import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { provedoresDeTela } from '../../../testing/apoio';
import { Perfil } from '../../models/usuario.model';
import { dataIso, somarDias } from '../../services/datas';
import { MensagemService } from '../../services/mensagem.service';
import { ReservaService } from '../../services/reserva.service';
import { AprovacoesComponent } from '../aprovacoes/aprovacoes.component';
import { ReservasComponent } from './reservas.component';

const LISTA = [
  { id: 'r1', disciplina: 'Qualidade de Software', salaCodigo: 'LAB-1', professorNome: 'Ana Souza', solicitanteNome: 'Ana Souza', status: 'SOLICITADA', inicio: '2030-01-10T08:00:00', fim: '2030-01-10T10:00:00', materiais: [] },
  { id: 'r2', disciplina: 'Banco de Dados', salaCodigo: 'AUD-1', professorNome: 'Bruno Lima', solicitanteNome: 'Carla Dias', status: 'SOLICITADA', inicio: '2030-01-11T08:00:00', fim: '2030-01-11T10:00:00', materiais: [] }
];

describe('ReservasComponent', () => {
  let servico: { listar: ReturnType<typeof vi.fn> };

  function montar(perfil: Perfil) {
    TestBed.configureTestingModule({
      providers: [...provedoresDeTela(perfil), { provide: ReservaService, useValue: servico }]
    });
    const fixture = TestBed.createComponent(ReservasComponent);
    fixture.detectChanges();
    return { tela: fixture.componentInstance as any, mensagens: TestBed.inject(MensagemService) };
  }

  beforeEach(() => (servico = { listar: vi.fn(() => of(LISTA)) }));

  it('professor vê só as próprias reservas dos próximos 30 dias', () => {
    const { tela } = montar('PROFESSOR');
    expect(servico.listar).toHaveBeenCalledWith({
      inicio: `${dataIso(new Date())}T00:00`,
      fim: `${dataIso(somarDias(new Date(), 30))}T23:59`,
      status: undefined,
      minhas: true
    });
    expect(tela.carregando()).toBe(false);
  });

  it('responsável vê todas e pode filtrar por status sem datas', () => {
    const { tela } = montar('RESPONSAVEL');
    tela.inicio = '';
    tela.fim = '';
    tela.status = 'APROVADA';
    tela.carregar();
    expect(servico.listar).toHaveBeenLastCalledWith({ inicio: undefined, fim: undefined, status: 'APROVADA', minhas: undefined });
  });

  it.each([
    ['', 2],
    ['banco', 1],
    ['AUD', 1],
    ['carla', 1],
    ['bruno', 1],
    ['inexistente', 0]
  ])('busca "%s" por disciplina, sala, professor ou solicitante', (termo, quantidade) => {
    const { tela } = montar('ADMIN');
    tela.busca.set(termo);
    expect(tela.filtradas().length).toBe(quantidade);
  });

  it('abre o detalhe da reserva', () => {
    const { tela } = montar('ADMIN');
    const navegar = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    tela.abrir(LISTA[1]);
    expect(navegar).toHaveBeenCalledWith(['/reservas', 'r2']);
  });

  it('mostra o erro se a lista não carregar', () => {
    servico.listar.mockReturnValue(throwError(() => new Error('500')));
    const { tela, mensagens } = montar('ADMIN');
    expect(tela.carregando()).toBe(false);
    expect(mensagens.mensagens()[0].tipo).toBe('erro');
  });
});

describe('AprovacoesComponent', () => {
  let servico: Record<string, ReturnType<typeof vi.fn>>;

  function montar() {
    TestBed.configureTestingModule({
      providers: [...provedoresDeTela('RESPONSAVEL'), { provide: ReservaService, useValue: servico }]
    });
    const fixture = TestBed.createComponent(AprovacoesComponent);
    fixture.detectChanges();
    return { tela: fixture.componentInstance as any, mensagens: TestBed.inject(MensagemService) };
  }

  beforeEach(() => {
    servico = {
      listar: vi.fn(() => of([...LISTA])),
      aprovar: vi.fn(() => of({})),
      rejeitar: vi.fn(() => of({}))
    };
  });

  it('lista só as reservas pendentes [RF07]', () => {
    const { tela } = montar();
    expect(servico['listar']).toHaveBeenCalledWith({ status: 'SOLICITADA' });
    expect(tela.pendentes().length).toBe(2);
  });

  it('aprova e tira a reserva da fila', () => {
    const { tela, mensagens } = montar();
    tela.aprovar(LISTA[0]);
    expect(servico['aprovar']).toHaveBeenCalledWith('r1', null);
    expect(tela.pendentes().map((r: { id: string }) => r.id)).toEqual(['r2']);
    expect(tela.processando()).toBeNull();
    expect(mensagens.mensagens()[0].texto).toBe('Reserva de Qualidade de Software aprovada.');
  });

  it('exige motivo para rejeitar e envia o texto sem espaços sobrando', () => {
    const { tela, mensagens } = montar();
    tela.rejeitar();
    expect(servico['rejeitar']).not.toHaveBeenCalled();
    tela.abrirRejeicao(LISTA[1]);
    tela.rejeitar();
    expect(mensagens.mensagens()[0].texto).toBe('Informe o motivo da rejeição.');
    tela.motivo = '  conflito com prova  ';
    tela.rejeitar();
    expect(servico['rejeitar']).toHaveBeenCalledWith('r2', 'conflito com prova');
    expect(tela.rejeitando()).toBeNull();
    expect(tela.pendentes().map((r: { id: string }) => r.id)).toEqual(['r1']);
  });

  it('mantém a reserva na fila se a aprovação falhar', () => {
    servico['aprovar'].mockReturnValue(throwError(() => new Error('409')));
    const { tela, mensagens } = montar();
    tela.aprovar(LISTA[0]);
    expect(tela.pendentes().length).toBe(2);
    expect(tela.processando()).toBeNull();
    expect(mensagens.mensagens()[0].tipo).toBe('erro');
  });

  it('mostra o erro se a fila não carregar', () => {
    servico['listar'].mockReturnValue(throwError(() => new Error('500')));
    const { tela, mensagens } = montar();
    expect(tela.carregando()).toBe(false);
    expect(mensagens.mensagens()[0].tipo).toBe('erro');
  });
});
