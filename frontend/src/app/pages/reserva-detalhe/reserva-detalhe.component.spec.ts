import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { provedoresDeTela } from '../../../testing/apoio';
import { StatusReserva } from '../../models/reserva.model';
import { Perfil } from '../../models/usuario.model';
import { MensagemService } from '../../services/mensagem.service';
import { ReservaService } from '../../services/reserva.service';
import { ReservaDetalheComponent } from './reserva-detalhe.component';

function reserva(status: StatusReserva, solicitanteId = 'u1') {
  return {
    id: 'r1', salaId: 's1', salaCodigo: 'LAB-1', professorId: 'p1', professorNome: 'Ana Souza',
    solicitanteId, solicitanteNome: 'Ana Souza', disciplina: 'Qualidade de Software',
    inicio: '2030-01-10T08:00:00', fim: '2030-01-10T10:30:00', quantidadeAlunos: 20, status,
    motivosAprovacao: [], observacao: null, versao: 1,
    materiais: [{ materialId: 'm1', materialNome: 'Projetor', quantidade: 3 }],
    movimentacoes: [
      { materialId: 'm1', tipo: 'RETIRADA', quantidade: 3 },
      { materialId: 'm1', tipo: 'DEVOLUCAO', quantidade: 1 },
      { materialId: 'm1', tipo: 'DEVOLUCAO', quantidade: 1 }
    ],
    criadoEm: '2030-01-01T00:00:00', atualizadoEm: '2030-01-01T00:00:00'
  };
}

describe('ReservaDetalheComponent', () => {
  let servico: Record<string, ReturnType<typeof vi.fn>>;

  function montar(perfil: Perfil, status: StatusReserva, solicitanteId = 'u1') {
    servico['obter'].mockReturnValue(of(reserva(status, solicitanteId)));
    TestBed.configureTestingModule({
      providers: [...provedoresDeTela(perfil), { provide: ReservaService, useValue: servico }]
    });
    const fixture = TestBed.createComponent(ReservaDetalheComponent);
    fixture.componentRef.setInput('id', 'r1');
    fixture.detectChanges();
    return { fixture, tela: fixture.componentInstance as any, mensagens: TestBed.inject(MensagemService) };
  }

  beforeEach(() => {
    const atualizada = (status: StatusReserva) => vi.fn(() => of(reserva(status)));
    servico = {
      obter: vi.fn(),
      historico: vi.fn(() => of([{ id: 'e1' }, { id: 'e2' }])),
      cancelar: atualizada('CANCELADA'),
      aprovar: atualizada('APROVADA'),
      rejeitar: atualizada('REJEITADA'),
      iniciar: atualizada('EM_USO'),
      concluir: atualizada('CONCLUIDA'),
      registrarNaoComparecimento: atualizada('NAO_COMPARECEU'),
      registrarRetirada: atualizada('EM_USO'),
      registrarDevolucao: atualizada('EM_USO'),
      excluir: vi.fn(() => of(undefined))
    };
  });

  it('carrega a reserva e mostra o histórico do mais recente para o mais antigo', () => {
    const { tela } = montar('PROFESSOR', 'SOLICITADA');
    expect(tela.reserva().id).toBe('r1');
    expect(tela.historico().map((e: { id: string }) => e.id)).toEqual(['e2', 'e1']);
    expect(tela.duracao()).toBe(2.5);
  });

  it('soma retiradas e devoluções por material [RF09]', () => {
    const { tela } = montar('RESPONSAVEL', 'EM_USO');
    expect(tela.situacaoMateriais()[0]).toMatchObject({ retirado: 3, devolvido: 2 });
  });

  it.each<[Perfil, StatusReserva, string, Record<string, boolean>]>([
    ['PROFESSOR', 'SOLICITADA', 'u1', { editar: true, cancelar: true, aprovar: false, iniciar: false, excluir: true }],
    ['PROFESSOR', 'APROVADA', 'u1', { editar: true, cancelar: true, iniciar: true, naoCompareceu: false }],
    ['PROFESSOR', 'APROVADA', 'outro', { editar: false, cancelar: false, iniciar: false, excluir: false }],
    ['PROFESSOR', 'EM_USO', 'u1', { concluir: true, movimentar: false, excluir: false }],
    ['RESPONSAVEL', 'SOLICITADA', 'outro', { aprovar: true, editar: false, excluir: false }],
    ['RESPONSAVEL', 'APROVADA', 'outro', { iniciar: true, naoCompareceu: true, aprovar: false }],
    ['RESPONSAVEL', 'EM_USO', 'outro', { concluir: true, movimentar: true }],
    ['ADMIN', 'APROVADA', 'outro', { excluir: true, editar: false }],
    ['ADMIN', 'CONCLUIDA', 'outro', { excluir: false }]
  ])('%s com reserva %s (solicitante %s) vê as ações certas [RN05][RN06][RN07]', (perfil, status, dono, esperado) => {
    const { tela } = montar(perfil, status, dono);
    expect(tela.pode()).toMatchObject(esperado);
  });

  it.each([
    ['cancelar', 'Reserva cancelada.', 'CANCELADA'],
    ['aprovar', 'Reserva aprovada.', 'APROVADA']
  ])('confirma %s com observação opcional', (acao, texto, status) => {
    const { tela, mensagens } = montar('RESPONSAVEL', 'SOLICITADA');
    tela.abrirDialogo(acao);
    expect(tela.configDialogo().obrigatorio).toBe(false);
    tela.confirmarDialogo();
    expect(servico[acao]).toHaveBeenCalledWith('r1', null);
    expect(tela.reserva().status).toBe(status);
    expect(tela.dialogo()).toBeNull();
    expect(mensagens.mensagens()[0].texto).toBe(texto);
  });

  it('exige motivo para rejeitar', () => {
    const { tela, mensagens } = montar('RESPONSAVEL', 'SOLICITADA');
    tela.abrirDialogo('rejeitar');
    tela.texto = '   ';
    tela.confirmarDialogo();
    expect(servico['rejeitar']).not.toHaveBeenCalled();
    expect(mensagens.mensagens()[0].texto).toBe('Informe o motivo.');
    tela.texto = ' sala em obra ';
    tela.confirmarDialogo();
    expect(servico['rejeitar']).toHaveBeenCalledWith('r1', 'sala em obra');
  });

  it('não faz nada ao confirmar sem diálogo aberto', () => {
    const { tela } = montar('RESPONSAVEL', 'SOLICITADA');
    tela.confirmarDialogo();
    expect(servico['aprovar']).not.toHaveBeenCalled();
  });

  it.each([
    ['iniciar', 'iniciar', 'Uso iniciado. Bom trabalho!'],
    ['concluir', 'concluir', 'Reserva concluída.'],
    ['naoCompareceu', 'registrarNaoComparecimento', 'Não comparecimento registrado.']
  ])('%s chama a API e recarrega o histórico', (metodo, chamada, texto) => {
    const { tela, mensagens } = montar('RESPONSAVEL', 'APROVADA');
    tela[metodo]();
    expect(servico[chamada]).toHaveBeenCalledWith('r1');
    expect(servico['historico']).toHaveBeenCalledTimes(2);
    expect(mensagens.mensagens()[0].texto).toBe(texto);
    expect(tela.processando()).toBe(false);
  });

  it('mostra o erro da API e libera os botões se a ação falhar', () => {
    servico['iniciar'].mockReturnValue(throwError(() => new Error('409')));
    const { tela, mensagens } = montar('RESPONSAVEL', 'APROVADA');
    tela.iniciar();
    expect(tela.processando()).toBe(false);
    expect(mensagens.mensagens()[0].tipo).toBe('erro');
  });

  it.each([
    ['RETIRADA', 'registrarRetirada', 'Retirada registrada.'],
    ['DEVOLUCAO', 'registrarDevolucao', 'Devolução registrada.']
  ])('registra %s do material e zera o campo [RF09]', (tipo, chamada, texto) => {
    const { tela, mensagens } = montar('RESPONSAVEL', 'EM_USO');
    const situacao = tela.situacaoMateriais()[0];
    tela.movimentar(situacao, tipo);
    expect(servico[chamada]).not.toHaveBeenCalled();
    expect(mensagens.mensagens()[0].texto).toBe('Informe a quantidade.');
    tela.movimentacao['m1'] = 2;
    tela.movimentar(situacao, tipo);
    expect(servico[chamada]).toHaveBeenCalledWith('r1', { materialId: 'm1', quantidade: 2, observacao: null });
    expect(tela.movimentacao['m1']).toBe(0);
    expect(mensagens.mensagens().some((m: { texto: string }) => m.texto === texto)).toBe(true);
  });

  it('apaga a reserva e volta para a lista', () => {
    const { tela } = montar('PROFESSOR', 'SOLICITADA');
    const navegar = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    tela.excluir();
    expect(servico['excluir']).toHaveBeenCalledWith('r1');
    expect(navegar).toHaveBeenCalledWith(['/reservas']);
  });

  it('fecha a confirmação se a exclusão for recusada', () => {
    servico['excluir'].mockReturnValue(throwError(() => new Error('409')));
    const { tela, mensagens } = montar('PROFESSOR', 'SOLICITADA');
    tela.confirmarExclusao.set(true);
    tela.excluir();
    expect(tela.confirmarExclusao()).toBe(false);
    expect(tela.processando()).toBe(false);
    expect(mensagens.mensagens()[0].tipo).toBe('erro');
  });

  it('volta para a lista se a reserva não existir', () => {
    servico['obter'] = vi.fn(() => throwError(() => new Error('404')));
    servico['historico'].mockReturnValue(throwError(() => new Error('404')));
    TestBed.configureTestingModule({
      providers: [...provedoresDeTela('PROFESSOR'), { provide: ReservaService, useValue: servico }]
    });
    const navegar = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    const fixture = TestBed.createComponent(ReservaDetalheComponent);
    fixture.componentRef.setInput('id', 'r1');
    fixture.detectChanges();
    expect(navegar).toHaveBeenCalledWith(['/reservas']);
    expect((fixture.componentInstance as any).historico()).toEqual([]);
  });

  it('traduz o status no histórico e mantém valores desconhecidos', () => {
    const { tela } = montar('PROFESSOR', 'SOLICITADA');
    expect(tela.rotuloEstado('EM_USO')).toBe('Em uso');
    expect(tela.rotuloEstado('OUTRO')).toBe('OUTRO');
    expect(tela.rotuloEstado(null)).toBe('');
  });
});
