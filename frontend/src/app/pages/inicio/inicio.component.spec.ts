import { TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { provedoresDeTela } from '../../../testing/apoio';
import { Perfil } from '../../models/usuario.model';
import { MensagemService } from '../../services/mensagem.service';
import { ReservaService } from '../../services/reserva.service';
import { InicioComponent } from './inicio.component';

const PASSADO = '2000-01-01T08:00:00';
const FUTURO = '2999-01-01T08:00:00';

const HOJE = [
  { id: 'h1', status: 'APROVADA', inicio: PASSADO, fim: FUTURO },
  { id: 'h2', status: 'EM_USO', inicio: PASSADO, fim: FUTURO },
  { id: 'h3', status: 'CANCELADA', inicio: PASSADO, fim: FUTURO },
  { id: 'h4', status: 'REJEITADA', inicio: PASSADO, fim: FUTURO }
];
const PROXIMAS = [
  { id: 'p1', status: 'APROVADA', inicio: FUTURO, fim: FUTURO },
  { id: 'p2', status: 'CONCLUIDA', inicio: FUTURO, fim: FUTURO },
  { id: 'p3', status: 'SOLICITADA', inicio: PASSADO, fim: PASSADO }
];

describe('InicioComponent', () => {
  let servico: { listar: ReturnType<typeof vi.fn> };

  function montar(perfil: Perfil) {
    TestBed.configureTestingModule({
      providers: [...provedoresDeTela(perfil), { provide: ReservaService, useValue: servico }]
    });
    const fixture = TestBed.createComponent(InicioComponent);
    fixture.detectChanges();
    return { tela: fixture.componentInstance as any, mensagens: TestBed.inject(MensagemService) };
  }

  beforeEach(() => {
    servico = {
      listar: vi.fn()
        .mockReturnValueOnce(of(HOJE))
        .mockReturnValueOnce(of(PROXIMAS))
        .mockReturnValueOnce(of([{ id: 'x' }]))
    };
  });

  it('calcula os indicadores ignorando reservas canceladas e rejeitadas', () => {
    const { tela } = montar('RESPONSAVEL');
    const valores = tela.indicadores().map((i: { valor: number }) => i.valor);
    expect(valores).toEqual([2, 1, 1, 1]);
    expect(tela.carregando()).toBe(false);
  });

  it('solicitante vê só as próprias reservas', () => {
    const { tela } = montar('PROFESSOR');
    for (const [filtro] of servico.listar.mock.calls) {
      expect(filtro.minhas).toBe(true);
    }
    expect(tela.indicadores()[3].dica).toBe('suas reservas');
  });

  it.each<[Perfil, string[]]>([
    ['PROFESSOR', ['/reservas/nova', '/disponibilidade']],
    ['COORDENADOR', ['/reservas/nova', '/disponibilidade', '/relatorios']],
    ['RESPONSAVEL', ['/disponibilidade', '/aprovacoes', '/relatorios']],
    ['ADMIN', ['/disponibilidade', '/relatorios', '/salas']]
  ])('mostra os atalhos de %s', (perfil, rotas) => {
    const { tela } = montar(perfil);
    expect(tela.atalhos().map((a: { rota: string }) => a.rota)).toEqual(rotas);
  });

  it('cumprimenta conforme a hora e marca reservas acontecendo agora', () => {
    const { tela } = montar('ADMIN');
    expect(['Bom dia', 'Boa tarde', 'Boa noite']).toContain(tela.saudacao());
    expect(tela.acontecendo(HOJE[0])).toBe(true);
    expect(tela.acontecendo(PROXIMAS[0])).toBe(false);
  });

  it('mostra o erro se o painel não carregar', () => {
    servico.listar = vi.fn(() => throwError(() => new Error('500')));
    const { tela, mensagens } = montar('ADMIN');
    expect(tela.carregando()).toBe(false);
    expect(mensagens.mensagens()[0].tipo).toBe('erro');
  });
});
