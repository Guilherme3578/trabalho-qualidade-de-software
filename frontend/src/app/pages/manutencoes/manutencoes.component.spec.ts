import { TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { provedoresDeTela } from '../../../testing/apoio';
import { BloqueioService } from '../../services/bloqueio.service';
import { MaterialService } from '../../services/material.service';
import { MensagemService } from '../../services/mensagem.service';
import { ProfessorService } from '../../services/professor.service';
import { SalaService } from '../../services/sala.service';
import { ManutencoesComponent } from './manutencoes.component';

const VIGENTE = { id: 'b1', tipoRecurso: 'SALA', recursoId: 's1', inicio: '2000-01-01T00:00:00', fim: '2999-01-01T00:00:00', motivo: 'Pintura', reservasAfetadas: [] };
const ENCERRADO = { id: 'b2', tipoRecurso: 'MATERIAL', recursoId: 'm9', inicio: '2000-01-01T00:00:00', fim: '2000-01-02T00:00:00', motivo: 'Conserto', reservasAfetadas: [] };

describe('ManutencoesComponent', () => {
  let bloqueios: Record<string, ReturnType<typeof vi.fn>>;

  function montar() {
    TestBed.configureTestingModule({
      providers: [
        ...provedoresDeTela('ADMIN'),
        { provide: BloqueioService, useValue: bloqueios },
        { provide: SalaService, useValue: { listar: vi.fn(() => of([{ id: 's1', codigo: 'LAB-1', nome: 'Laboratório' }])) } },
        { provide: ProfessorService, useValue: { listar: vi.fn(() => of([{ id: 'p1', nome: 'Ana Souza' }])) } },
        { provide: MaterialService, useValue: { listar: vi.fn(() => of([{ id: 'm1', nome: 'Projetor' }])) } }
      ]
    });
    const fixture = TestBed.createComponent(ManutencoesComponent);
    fixture.detectChanges();
    return { tela: fixture.componentInstance as any, mensagens: TestBed.inject(MensagemService) };
  }

  beforeEach(() => {
    bloqueios = {
      listar: vi.fn(() => of([VIGENTE, ENCERRADO])),
      criar: vi.fn((dados: object) => of({ ...dados, id: 'b3', reservasAfetadas: ['r1', 'r2'] })),
      remover: vi.fn(() => of(undefined))
    };
  });

  it('carrega bloqueios e nomes dos recursos de cada tipo [RF08]', () => {
    const { tela } = montar();
    expect(tela.bloqueios().length).toBe(2);
    expect(tela.nomeRecurso(VIGENTE)).toBe('LAB-1 · Laboratório');
    expect(tela.nomeRecurso(ENCERRADO)).toBe('m9');
    expect(tela.vigente(VIGENTE)).toBe(true);
    expect(tela.encerrado(ENCERRADO)).toBe(true);
    expect(tela.vigente(ENCERRADO)).toBe(false);
  });

  it('troca a lista de recursos ao mudar o tipo e limpa a escolha', () => {
    const { tela } = montar();
    tela.formulario.patchValue({ recursoId: 's1' });
    tela.formulario.controls.tipoRecurso.setValue('PROFESSOR');
    expect(tela.opcoes()).toEqual([{ id: 'p1', nome: 'Ana Souza' }]);
    expect(tela.formulario.getRawValue().recursoId).toBe('');
  });

  it('abre o formulário com um intervalo de 4 horas começando na próxima hora cheia', () => {
    const { tela } = montar();
    tela.novo();
    const { inicio, fim } = tela.formulario.getRawValue();
    expect(inicio.endsWith(':00')).toBe(true);
    expect((new Date(fim).getTime() - new Date(inicio).getTime()) / 3_600_000).toBe(4);
    expect(tela.dialogoAberto()).toBe(true);
  });

  it('não salva formulário incompleto nem intervalo invertido', () => {
    const { tela, mensagens } = montar();
    tela.novo();
    tela.salvar();
    expect(bloqueios['criar']).not.toHaveBeenCalled();
    tela.formulario.patchValue({ recursoId: 's1', motivo: 'Pintura', inicio: '2030-01-01T10:00', fim: '2030-01-01T09:00' });
    tela.salvar();
    expect(bloqueios['criar']).not.toHaveBeenCalled();
    expect(mensagens.mensagens()[0].texto).toBe('O término deve ser depois do início.');
  });

  it('registra a manutenção e lista as reservas afetadas [RN04]', () => {
    const { tela, mensagens } = montar();
    tela.novo();
    tela.formulario.patchValue({ recursoId: 's1', motivo: '  Troca de piso ', inicio: '2030-01-01T08:00', fim: '2030-01-01T12:00' });
    tela.salvar();
    expect(bloqueios['criar']).toHaveBeenCalledWith(expect.objectContaining({ recursoId: 's1', motivo: 'Troca de piso' }));
    expect(tela.bloqueios()[0].id).toBe('b3');
    expect(tela.afetadas()).toEqual(['r1', 'r2']);
    expect(tela.dialogoAberto()).toBe(false);
    expect(mensagens.mensagens()[0].tipo).toBe('sucesso');
  });

  it('mantém o diálogo aberto se a API recusar a manutenção', () => {
    bloqueios['criar'].mockReturnValue(throwError(() => new Error('400')));
    const { tela, mensagens } = montar();
    tela.novo();
    tela.formulario.patchValue({ recursoId: 's1', motivo: 'Pintura', inicio: '2030-01-01T08:00', fim: '2030-01-01T12:00' });
    tela.salvar();
    expect(tela.dialogoAberto()).toBe(true);
    expect(tela.salvando()).toBe(false);
    expect(mensagens.mensagens()[0].tipo).toBe('erro');
  });

  it('remove a manutenção confirmada', () => {
    const { tela, mensagens } = montar();
    tela.remover();
    expect(bloqueios['remover']).not.toHaveBeenCalled();
    tela.removendo.set(VIGENTE);
    tela.remover();
    expect(bloqueios['remover']).toHaveBeenCalledWith('b1');
    expect(tela.bloqueios().map((b: { id: string }) => b.id)).toEqual(['b2']);
    expect(tela.removendo()).toBeNull();
    expect(mensagens.mensagens()[0].tipo).toBe('sucesso');
  });

  it('mostra o erro se a remoção falhar ou a tela não carregar', () => {
    bloqueios['remover'].mockReturnValue(throwError(() => new Error('500')));
    bloqueios['listar'].mockReturnValue(throwError(() => new Error('500')));
    const { tela, mensagens } = montar();
    expect(tela.carregando()).toBe(false);
    tela.removendo.set(VIGENTE);
    tela.remover();
    expect(mensagens.mensagens().length).toBe(2);
  });
});
