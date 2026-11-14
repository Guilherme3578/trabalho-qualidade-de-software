import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { provedoresDeTela } from '../../../testing/apoio';
import { Perfil } from '../../models/usuario.model';
import { dataIso, somarDias } from '../../services/datas';
import { MaterialService } from '../../services/material.service';
import { MensagemService } from '../../services/mensagem.service';
import { ProfessorService } from '../../services/professor.service';
import { ReservaService } from '../../services/reserva.service';
import { SalaService } from '../../services/sala.service';
import { ReservaFormComponent } from './reserva-form.component';

const AMANHA = dataIso(somarDias(new Date(), 1));
const SALA = { id: 's1', codigo: 'LAB-1', capacidade: 30, restrita: false };
const SALA_RESTRITA = { id: 's2', codigo: 'AUD-1', capacidade: 200, restrita: true };
const PROFESSOR = { id: 'p1', nome: 'Ana Souza', competencias: ['Qualidade de Software'] };
const PROJETOR = { id: 'm1', nome: 'Projetor', quantidadeDisponivel: 2, restrito: false };
const DRONE = { id: 'm2', nome: 'Drone', quantidadeDisponivel: 1, restrito: true };

function reservaExistente() {
  return {
    id: 'r1', salaId: 's1', professorId: 'p1', disciplina: 'Qualidade de Software',
    inicio: `${AMANHA}T08:00:00`, fim: `${AMANHA}T10:00:00`, quantidadeAlunos: 25, status: 'APROVADA',
    observacao: null, versao: 3, materiais: [{ materialId: 'm1', materialNome: 'Projetor', quantidade: 2 }]
  };
}

describe('ReservaFormComponent', () => {
  let reservas: Record<string, ReturnType<typeof vi.fn>>;
  let salas: { listar: ReturnType<typeof vi.fn> };
  let professores: { listar: ReturnType<typeof vi.fn> };
  let materiais: { listar: ReturnType<typeof vi.fn> };

  function montar(perfil: Perfil = 'COORDENADOR', id?: string) {
    TestBed.configureTestingModule({
      providers: [
        ...provedoresDeTela(perfil),
        { provide: ReservaService, useValue: reservas },
        { provide: SalaService, useValue: salas },
        { provide: ProfessorService, useValue: professores },
        { provide: MaterialService, useValue: materiais }
      ]
    });
    const fixture = TestBed.createComponent(ReservaFormComponent);
    if (id) {
      fixture.componentRef.setInput('id', id);
    }
    fixture.detectChanges();
    return { fixture, tela: fixture.componentInstance as any, mensagens: TestBed.inject(MensagemService) };
  }

  beforeEach(() => {
    reservas = {
      obter: vi.fn(() => of(reservaExistente())),
      criar: vi.fn(() => of({ id: 'nova', status: 'SOLICITADA' })),
      alterar: vi.fn(() => of({ id: 'r1', status: 'APROVADA' }))
    };
    salas = { listar: vi.fn(() => of([SALA, SALA_RESTRITA])) };
    professores = { listar: vi.fn(() => of([PROFESSOR])) };
    materiais = { listar: vi.fn(() => of([PROJETOR, DRONE])) };
  });

  it('começa amanhã das 08:00 às 10:00, sem erro de horário', () => {
    const { tela } = montar();
    expect(tela.formulario.getRawValue().data).toBe(AMANHA);
    expect(tela.erroHorario()).toBeNull();
    expect(tela.duracao()).toBe(2);
  });

  it.each([
    ['', '08:00', '10:00', 'Informe a data e o horário.'],
    [AMANHA, '06:30', '08:00', 'O horário de funcionamento é das 07:00 às 23:00.'],
    [AMANHA, '22:00', '23:30', 'O horário de funcionamento é das 07:00 às 23:00.'],
    [AMANHA, '10:00', '09:00', 'O término deve ser depois do início.'],
    [AMANHA, '10:00', '10:15', 'A reserva deve durar entre 30 minutos e 6 horas.'],
    [AMANHA, '08:00', '15:00', 'A reserva deve durar entre 30 minutos e 6 horas.'],
    [dataIso(somarDias(new Date(), -1)), '08:00', '10:00', 'Escolha um horário que ainda não passou.']
  ])('recusa data %s de %s a %s [RN01][RN10]', (data, inicio, fim, mensagem) => {
    const { tela } = montar();
    tela.formulario.patchValue({ data, inicio, fim });
    expect(tela.erroHorario()).toBe(mensagem);
    tela.consultar();
    expect(salas.listar).not.toHaveBeenCalled();
  });

  it('consulta salas, professores e materiais livres no intervalo escolhido', () => {
    const { tela } = montar();
    tela.consultar();
    const intervalo = { inicio: `${AMANHA}T08:00`, fim: `${AMANHA}T10:00` };
    expect(salas.listar).toHaveBeenCalledWith(intervalo);
    expect(professores.listar).toHaveBeenCalledWith(intervalo);
    expect(materiais.listar).toHaveBeenCalledWith({ ...intervalo, quantidadeMinima: 0 });
    expect(tela.horarioConsultado()).toBe(true);
    expect(tela.salas().length).toBe(2);
  });

  it('mostra o erro da API se a consulta falhar', () => {
    salas.listar.mockReturnValue(throwError(() => new Error('falhou')));
    const { tela, mensagens } = montar();
    tela.consultar();
    expect(tela.buscando()).toBe(false);
    expect(mensagens.mensagens()[0].tipo).toBe('erro');
  });

  it('limita a quantidade de material entre zero e o disponível', () => {
    const { tela } = montar();
    tela.consultar();
    tela.alterarQuantidade(PROJETOR, 1);
    tela.alterarQuantidade(PROJETOR, 1);
    tela.alterarQuantidade(PROJETOR, 1);
    expect(tela.quantidade(PROJETOR)).toBe(2);
    tela.alterarQuantidade(PROJETOR, -5);
    expect(tela.quantidade(PROJETOR)).toBe(0);
  });

  it('avisa que sala, material restrito e disciplina fora da competência exigem aprovação [RF07]', () => {
    const { tela } = montar();
    tela.consultar();
    tela.escolherSala(SALA_RESTRITA);
    tela.alterarQuantidade(DRONE, 1);
    tela.formulario.patchValue({ professorId: 'p1', disciplina: 'Robótica' });
    expect(tela.avisosAprovacao()).toEqual([
      'A sala AUD-1 é restrita.',
      'Drone é um material restrito.',
      'Ana Souza não tem "Robótica" entre as competências cadastradas.'
    ]);
    tela.formulario.patchValue({ disciplina: 'qualidade de software' });
    expect(tela.avisosAprovacao().some((a: string) => a.includes('competências'))).toBe(false);
  });

  it('não envia formulário inválido e avisa o usuário', () => {
    const { tela, mensagens } = montar();
    tela.salvar();
    expect(reservas['criar']).not.toHaveBeenCalled();
    expect(mensagens.mensagens()[0].tipo).toBe('aviso');
    expect(tela.formulario.controls.salaId.touched).toBe(true);
  });

  it('cria a reserva com os materiais escolhidos e abre o detalhe [RF04]', () => {
    const { tela, mensagens } = montar();
    const navegar = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    tela.consultar();
    tela.escolherSala(SALA);
    tela.alterarQuantidade(PROJETOR, 1);
    tela.formulario.patchValue({ professorId: 'p1', disciplina: '  Qualidade de Software  ', observacao: '   ' });
    tela.salvar();
    expect(reservas['criar']).toHaveBeenCalledWith({
      salaId: 's1', professorId: 'p1', disciplina: 'Qualidade de Software',
      inicio: `${AMANHA}T08:00`, fim: `${AMANHA}T10:00`, quantidadeAlunos: 20, observacao: null,
      materiais: [{ materialId: 'm1', quantidade: 1 }], versao: undefined
    });
    expect(mensagens.mensagens()[0].texto).toContain('aguarda a aprovação');
    expect(navegar).toHaveBeenCalledWith(['/reservas', 'nova']);
  });

  it('mantém o formulário editável se a API recusar a reserva', () => {
    reservas['criar'].mockReturnValue(throwError(() => new Error('409')));
    const { tela, mensagens } = montar();
    tela.consultar();
    tela.escolherSala(SALA);
    tela.formulario.patchValue({ professorId: 'p1', disciplina: 'Qualidade de Software' });
    tela.salvar();
    expect(tela.salvando()).toBe(false);
    expect(mensagens.mensagens()[0].tipo).toBe('erro');
  });

  it('professor reserva sempre em nome próprio', () => {
    const { tela } = montar('PROFESSOR');
    expect(tela.formulario.getRawValue().professorId).toBe('p1');
  });

  it('ao editar, preenche os dados e devolve ao estoque o que a própria reserva ocupa', () => {
    materiais.listar.mockReturnValue(of([{ ...PROJETOR, quantidadeDisponivel: 0 }]));
    const { tela } = montar('COORDENADOR', 'r1');
    expect(reservas['obter']).toHaveBeenCalledWith('r1');
    expect(tela.formulario.getRawValue()).toMatchObject({ data: AMANHA, inicio: '08:00', fim: '10:00', salaId: 's1', quantidadeAlunos: 25 });
    expect(tela.materiais()[0].quantidadeDisponivel).toBe(2);
    expect(tela.quantidade(PROJETOR)).toBe(2);
  });

  it('ao editar, mantém a sala e o professor da própria reserva mesmo fora da lista de livres', () => {
    salas.listar.mockImplementation((filtro: { incluirInativas?: boolean }) => of(filtro.incluirInativas ? [SALA] : [SALA_RESTRITA]));
    professores.listar.mockImplementation((filtro?: object) => of(filtro ? [] : [PROFESSOR]));
    const { tela } = montar('COORDENADOR', 'r1');
    expect(tela.salas().map((s: { id: string }) => s.id)).toEqual(['s1', 's2']);
    expect(tela.professores().map((p: { id: string }) => p.id)).toEqual(['p1']);
    expect(tela.formulario.getRawValue().salaId).toBe('s1');
  });

  it('ao editar, envia a versão para o controle de concorrência otimista', () => {
    const { tela } = montar('COORDENADOR', 'r1');
    vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    tela.salvar();
    expect(reservas['alterar']).toHaveBeenCalledWith('r1', expect.objectContaining({ versao: 3 }));
  });

  it('descarta escolhas que deixaram de estar livres ao trocar o horário', () => {
    const { tela } = montar();
    tela.consultar();
    tela.escolherSala(SALA);
    tela.formulario.patchValue({ professorId: 'p1' });
    tela.alterarQuantidade(PROJETOR, 2);
    salas.listar.mockReturnValue(of([SALA_RESTRITA]));
    professores.listar.mockReturnValue(of([]));
    materiais.listar.mockReturnValue(of([{ ...PROJETOR, quantidadeDisponivel: 1 }]));
    tela.formulario.patchValue({ inicio: '14:00', fim: '16:00' });
    tela.consultar();
    expect(tela.formulario.getRawValue().salaId).toBe('');
    expect(tela.formulario.getRawValue().professorId).toBe('');
    expect(tela.quantidade(PROJETOR)).toBe(1);
  });

  it('mostra o erro se a reserva a editar não puder ser carregada', () => {
    reservas['obter'].mockReturnValue(throwError(() => new Error('404')));
    const { mensagens } = montar('COORDENADOR', 'r1');
    expect(mensagens.mensagens()[0].tipo).toBe('erro');
  });
});
