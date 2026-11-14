import { TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { provedoresDeTela } from '../../../testing/apoio';
import { AuditoriaService } from '../../services/auditoria.service';
import { dataIso, somarDias } from '../../services/datas';
import { MensagemService } from '../../services/mensagem.service';
import { RelatorioService } from '../../services/relatorio.service';
import { AuditoriaComponent } from '../auditoria/auditoria.component';
import { RelatoriosComponent } from './relatorios.component';

const UTILIZACAO = {
  salas: [
    { salaId: 's1', reservas: 4, horasReservadas: 10, taxaOcupacaoPercentual: 20 },
    { salaId: 's2', reservas: 6, horasReservadas: 14, taxaOcupacaoPercentual: 40 }
  ]
};
const CARGA = [
  { professorId: 'p1', horasAlocadas: 4 },
  { professorId: 'p2', horasAlocadas: 12 }
];
const CONFLITOS = { total: 5, porTipo: { CONFLITO_SALA: 3, OUTRO_TIPO: 2 } };

describe('RelatoriosComponent', () => {
  let servico: Record<string, ReturnType<typeof vi.fn>>;

  function montar() {
    TestBed.configureTestingModule({
      providers: [...provedoresDeTela('COORDENADOR'), { provide: RelatorioService, useValue: servico }]
    });
    const fixture = TestBed.createComponent(RelatoriosComponent);
    fixture.detectChanges();
    return { tela: fixture.componentInstance as any, mensagens: TestBed.inject(MensagemService) };
  }

  beforeEach(() => {
    servico = {
      utilizacao: vi.fn(() => of(UTILIZACAO)),
      cargaHoraria: vi.fn(() => of(CARGA)),
      conflitosEvitados: vi.fn(() => of(CONFLITOS))
    };
  });

  it('gera os três relatórios dos próximos 30 dias e consolida os totais [RF12]', () => {
    const { tela } = montar();
    const periodo = [dataIso(new Date()), dataIso(somarDias(new Date(), 30))];
    expect(servico['utilizacao']).toHaveBeenCalledWith(...periodo);
    expect(servico['cargaHoraria']).toHaveBeenCalledWith(...periodo);
    expect(servico['conflitosEvitados']).toHaveBeenCalledWith(...periodo);
    expect(tela.totalReservas()).toBe(10);
    expect(tela.totalHoras()).toBe(24);
    expect(tela.ocupacaoMedia()).toBe(30);
    expect(tela.maiorOcupacao()).toBe(40);
    expect(tela.carga().map((c: { professorId: string }) => c.professorId)).toEqual(['p2', 'p1']);
    expect(tela.maiorCarga()).toBe(12);
    expect(tela.tiposConflito()).toEqual([
      { rotulo: 'Sala ocupada', quantidade: 3 },
      { rotulo: 'OUTRO_TIPO', quantidade: 2 }
    ]);
  });

  it('calcula a largura das barras com mínimo visível', () => {
    const { tela } = montar();
    expect(tela.largura(6, 12)).toBe(50);
    expect(tela.largura(0, 12)).toBe(2);
    expect(tela.largura(5, 0)).toBe(0);
  });

  it('recusa período invertido', () => {
    const { tela, mensagens } = montar();
    tela.inicio = '2030-02-01';
    tela.fim = '2030-01-01';
    tela.gerar();
    expect(servico['utilizacao']).toHaveBeenCalledTimes(1);
    expect(mensagens.mensagens()[0].texto).toBe('Informe um período válido.');
  });

  it('fica zerado sem dados e mostra o erro da API', () => {
    servico['utilizacao'].mockReturnValue(throwError(() => new Error('500')));
    const { tela, mensagens } = montar();
    expect(tela.carregando()).toBe(false);
    expect(tela.ocupacaoMedia()).toBe(0);
    expect(tela.maiorCarga()).toBe(1);
    expect(tela.tiposConflito()).toEqual([]);
    expect(mensagens.mensagens()[0].tipo).toBe('erro');
  });
});

describe('AuditoriaComponent', () => {
  let servico: { listar: ReturnType<typeof vi.fn> };

  function montar() {
    TestBed.configureTestingModule({
      providers: [...provedoresDeTela('ADMIN'), { provide: AuditoriaService, useValue: servico }]
    });
    const fixture = TestBed.createComponent(AuditoriaComponent);
    fixture.detectChanges();
    return { tela: fixture.componentInstance as any, mensagens: TestBed.inject(MensagemService) };
  }

  beforeEach(() => (servico = { listar: vi.fn(() => of([{ id: 'e1' }])) }));

  it('lista todos os eventos e filtra por entidade e ação [RF10]', () => {
    const { tela } = montar();
    expect(servico.listar).toHaveBeenCalledWith({ entidade: undefined, acao: undefined });
    tela.entidade = 'Reserva';
    tela.acao = 'EXCLUSAO';
    tela.carregar();
    expect(servico.listar).toHaveBeenLastCalledWith({ entidade: 'Reserva', acao: 'EXCLUSAO' });
    expect(tela.eventos().length).toBe(1);
  });

  it('traduz entidade, ação e status, mantendo valores desconhecidos', () => {
    const { tela } = montar();
    expect(tela.rotuloEntidade('Bloqueio')).toBe('Manutenção');
    expect(tela.rotuloEntidade('Outra')).toBe('Outra');
    expect(tela.cor('EXCLUSAO')).toBe('badge-perigo');
    expect(tela.cor('ATIVACAO')).toBe('badge-neutro');
    expect(tela.estado('APROVADA')).toBe('Aprovada');
    expect(tela.estado('X')).toBe('X');
    expect(tela.estado(null)).toBe('');
  });

  it('mostra o erro se a auditoria não carregar', () => {
    servico.listar.mockReturnValue(throwError(() => new Error('403')));
    const { tela, mensagens } = montar();
    expect(tela.carregando()).toBe(false);
    expect(mensagens.mensagens()[0].tipo).toBe('erro');
  });
});
