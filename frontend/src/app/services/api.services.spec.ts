import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Observable } from 'rxjs';
import { AuditoriaService } from './auditoria.service';
import { BloqueioService } from './bloqueio.service';
import { MaterialService } from './material.service';
import { NotificacaoService } from './notificacao.service';
import { ProfessorService } from './professor.service';
import { RelatorioService } from './relatorio.service';
import { ReservaService } from './reserva.service';
import { SalaService } from './sala.service';
import { UsuarioService } from './usuario.service';

interface Caso {
  nome: string;
  chamar: () => Observable<unknown>;
  metodo: string;
  url: string;
  params?: Record<string, string>;
  corpo?: unknown;
}

describe('Contrato dos services com a API', () => {
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  const reserva = () => TestBed.inject(ReservaService);
  const sala = () => TestBed.inject(SalaService);
  const professor = () => TestBed.inject(ProfessorService);
  const material = () => TestBed.inject(MaterialService);
  const usuario = () => TestBed.inject(UsuarioService);
  const relatorio = () => TestBed.inject(RelatorioService);
  const bloqueio = () => TestBed.inject(BloqueioService);
  const auditoria = () => TestBed.inject(AuditoriaService);

  const dadosReserva = { titulo: 'Aula', inicio: '2030-01-01T10:00', fim: '2030-01-01T11:00' } as never;
  const movimentacao = { materialId: 'm1', quantidade: 2 } as never;

  const casos: Caso[] = [
    { nome: 'lista reservas com filtro', chamar: () => reserva().listar({ status: 'APROVADA' } as never), metodo: 'GET', url: '/api/reservas', params: { status: 'APROVADA' } },
    { nome: 'obtém reserva', chamar: () => reserva().obter('r1'), metodo: 'GET', url: '/api/reservas/r1' },
    { nome: 'histórico da reserva', chamar: () => reserva().historico('r1'), metodo: 'GET', url: '/api/reservas/r1/historico' },
    { nome: 'cria reserva', chamar: () => reserva().criar(dadosReserva), metodo: 'POST', url: '/api/reservas', corpo: dadosReserva },
    { nome: 'altera reserva', chamar: () => reserva().alterar('r1', dadosReserva), metodo: 'PUT', url: '/api/reservas/r1', corpo: dadosReserva },
    { nome: 'exclui reserva', chamar: () => reserva().excluir('r1'), metodo: 'DELETE', url: '/api/reservas/r1' },
    { nome: 'cancela reserva', chamar: () => reserva().cancelar('r1', 'motivo'), metodo: 'POST', url: '/api/reservas/r1/cancelar', corpo: { observacao: 'motivo' } },
    { nome: 'aprova reserva', chamar: () => reserva().aprovar('r1', null), metodo: 'POST', url: '/api/reservas/r1/aprovar', corpo: { observacao: null } },
    { nome: 'rejeita reserva', chamar: () => reserva().rejeitar('r1', 'sem sala'), metodo: 'POST', url: '/api/reservas/r1/rejeitar', corpo: { observacao: 'sem sala' } },
    { nome: 'inicia reserva', chamar: () => reserva().iniciar('r1'), metodo: 'POST', url: '/api/reservas/r1/iniciar', corpo: null },
    { nome: 'conclui reserva', chamar: () => reserva().concluir('r1'), metodo: 'POST', url: '/api/reservas/r1/concluir', corpo: null },
    { nome: 'registra não comparecimento', chamar: () => reserva().registrarNaoComparecimento('r1'), metodo: 'POST', url: '/api/reservas/r1/nao-compareceu', corpo: null },
    { nome: 'registra retirada', chamar: () => reserva().registrarRetirada('r1', movimentacao), metodo: 'POST', url: '/api/reservas/r1/retiradas', corpo: movimentacao },
    { nome: 'registra devolução', chamar: () => reserva().registrarDevolucao('r1', movimentacao), metodo: 'POST', url: '/api/reservas/r1/devolucoes', corpo: movimentacao },

    { nome: 'lista salas sem filtros vazios', chamar: () => sala().listar({ capacidadeMinima: 30, localizacao: '' }), metodo: 'GET', url: '/api/salas', params: { capacidadeMinima: '30' } },
    { nome: 'cria sala', chamar: () => sala().criar({ nome: 'Lab 1' } as never), metodo: 'POST', url: '/api/salas', corpo: { nome: 'Lab 1' } },
    { nome: 'atualiza sala', chamar: () => sala().atualizar('s1', { nome: 'Lab 2' } as never), metodo: 'PUT', url: '/api/salas/s1', corpo: { nome: 'Lab 2' } },
    { nome: 'desativa sala', chamar: () => sala().definirAtiva('s1', false), metodo: 'PATCH', url: '/api/salas/s1/ativa', params: { valor: 'false' } },

    { nome: 'lista professores', chamar: () => professor().listar({ competencia: 'Java' }), metodo: 'GET', url: '/api/professores', params: { competencia: 'Java' } },
    { nome: 'cria professor', chamar: () => professor().criar({ nome: 'Ana' } as never), metodo: 'POST', url: '/api/professores', corpo: { nome: 'Ana' } },
    { nome: 'atualiza professor', chamar: () => professor().atualizar('p1', { nome: 'Ana S.' } as never), metodo: 'PUT', url: '/api/professores/p1', corpo: { nome: 'Ana S.' } },
    { nome: 'ativa professor', chamar: () => professor().definirAtivo('p1', true), metodo: 'PATCH', url: '/api/professores/p1/ativo', params: { valor: 'true' } },

    { nome: 'lista materiais', chamar: () => material().listar({ quantidadeMinima: 2 }), metodo: 'GET', url: '/api/materiais', params: { quantidadeMinima: '2' } },
    { nome: 'cria material', chamar: () => material().criar({ nome: 'Projetor' } as never), metodo: 'POST', url: '/api/materiais', corpo: { nome: 'Projetor' } },
    { nome: 'atualiza material', chamar: () => material().atualizar('m1', { nome: 'Projetor HD' } as never), metodo: 'PUT', url: '/api/materiais/m1', corpo: { nome: 'Projetor HD' } },
    { nome: 'desativa material', chamar: () => material().definirAtivo('m1', false), metodo: 'PATCH', url: '/api/materiais/m1/ativo', params: { valor: 'false' } },

    { nome: 'lista usuários', chamar: () => usuario().listar(), metodo: 'GET', url: '/api/usuarios' },
    { nome: 'cria usuário', chamar: () => usuario().criar({ email: 'a@senac.br' } as never), metodo: 'POST', url: '/api/usuarios', corpo: { email: 'a@senac.br' } },
    { nome: 'desativa usuário', chamar: () => usuario().definirAtivo('u1', false), metodo: 'PATCH', url: '/api/usuarios/u1/ativo', params: { valor: 'false' } },

    { nome: 'relatório de utilização', chamar: () => relatorio().utilizacao('2030-01-01', '2030-01-31'), metodo: 'GET', url: '/api/relatorios/utilizacao', params: { inicio: '2030-01-01', fim: '2030-01-31' } },
    { nome: 'carga horária', chamar: () => relatorio().cargaHoraria('2030-01-01', '2030-01-31'), metodo: 'GET', url: '/api/relatorios/carga-horaria', params: { inicio: '2030-01-01', fim: '2030-01-31' } },
    { nome: 'conflitos evitados', chamar: () => relatorio().conflitosEvitados('2030-01-01', '2030-01-31'), metodo: 'GET', url: '/api/relatorios/conflitos-evitados', params: { inicio: '2030-01-01', fim: '2030-01-31' } },

    { nome: 'lista bloqueios do recurso', chamar: () => bloqueio().listar('SALA', 's1'), metodo: 'GET', url: '/api/bloqueios', params: { tipoRecurso: 'SALA', recursoId: 's1' } },
    { nome: 'lista todos os bloqueios', chamar: () => bloqueio().listar(), metodo: 'GET', url: '/api/bloqueios', params: {} },
    { nome: 'cria bloqueio', chamar: () => bloqueio().criar({ motivo: 'Pintura' } as never), metodo: 'POST', url: '/api/bloqueios', corpo: { motivo: 'Pintura' } },
    { nome: 'remove bloqueio', chamar: () => bloqueio().remover('b1'), metodo: 'DELETE', url: '/api/bloqueios/b1' },

    { nome: 'lista auditoria', chamar: () => auditoria().listar({ entidade: 'Reserva', acao: 'CRIACAO' as never }), metodo: 'GET', url: '/api/auditoria', params: { entidade: 'Reserva', acao: 'CRIACAO' } }
  ];

  for (const caso of casos) {
    it(caso.nome, () => {
      let respondeu = false;
      caso.chamar().subscribe(() => (respondeu = true));
      const req = http.expectOne(r => r.url === caso.url);
      expect(req.request.method).toBe(caso.metodo);
      if (caso.params) {
        const recebidos = Object.fromEntries(req.request.params.keys().map(k => [k, req.request.params.get(k)]));
        expect(recebidos).toEqual(caso.params);
      }
      if ('corpo' in caso) {
        expect(req.request.body).toEqual(caso.corpo);
      }
      req.flush(caso.metodo === 'DELETE' ? null : {});
      expect(respondeu).toBe(true);
    });
  }
});

describe('NotificacaoService', () => {
  let servico: NotificacaoService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    servico = TestBed.inject(NotificacaoService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('carrega a lista e conta as não lidas', () => {
    servico.carregar().subscribe();
    http.expectOne('/api/notificacoes').flush([
      { id: 'n1', lida: false },
      { id: 'n2', lida: true },
      { id: 'n3', lida: false }
    ]);
    expect(servico.notificacoes().length).toBe(3);
    expect(servico.naoLidas()).toBe(2);
  });

  it('marca como lida e atualiza só a notificação alterada', () => {
    servico.carregar().subscribe();
    http.expectOne('/api/notificacoes').flush([
      { id: 'n1', lida: false },
      { id: 'n2', lida: false }
    ]);
    servico.marcarLida('n1').subscribe();
    const req = http.expectOne('/api/notificacoes/n1/lida');
    expect(req.request.method).toBe('PATCH');
    req.flush({ id: 'n1', lida: true });
    expect(servico.naoLidas()).toBe(1);
    expect(servico.notificacoes().find(n => n.id === 'n2')?.lida).toBe(false);
  });
});
