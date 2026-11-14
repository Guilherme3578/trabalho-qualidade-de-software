import { TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { provedoresDeTela } from '../../../testing/apoio';
import { dataIso, somarDias } from '../../services/datas';
import { MaterialService } from '../../services/material.service';
import { MensagemService } from '../../services/mensagem.service';
import { ProfessorService } from '../../services/professor.service';
import { ReservaService } from '../../services/reserva.service';
import { SalaService } from '../../services/sala.service';
import { DisponibilidadeComponent } from './disponibilidade.component';

const HOJE = dataIso(new Date());
const SALAS = [
  { id: 's1', codigo: 'LAB-1', nome: 'Laboratório 1', tipo: 'LABORATORIO', capacidade: 30 },
  { id: 's2', codigo: 'AUD-1', nome: 'Auditório', tipo: 'AUDITORIO', capacidade: 200 }
];
const RESERVAS = [
  { id: 'r1', salaId: 's1', status: 'APROVADA', inicio: `${HOJE}T09:00:00`, fim: `${HOJE}T11:00:00` },
  { id: 'r2', salaId: 's1', status: 'CANCELADA', inicio: `${HOJE}T13:00:00`, fim: `${HOJE}T14:00:00` },
  { id: 'r3', salaId: 's2', status: 'EM_USO', inicio: `${HOJE}T07:00:00`, fim: `${HOJE}T23:00:00` }
];

describe('DisponibilidadeComponent', () => {
  let salas: { listar: ReturnType<typeof vi.fn> };
  let professores: { listar: ReturnType<typeof vi.fn> };
  let materiais: { listar: ReturnType<typeof vi.fn> };
  let reservas: { listar: ReturnType<typeof vi.fn> };

  function montar() {
    TestBed.configureTestingModule({
      providers: [
        ...provedoresDeTela('PROFESSOR'),
        { provide: SalaService, useValue: salas },
        { provide: ProfessorService, useValue: professores },
        { provide: MaterialService, useValue: materiais },
        { provide: ReservaService, useValue: reservas }
      ]
    });
    const fixture = TestBed.createComponent(DisponibilidadeComponent);
    fixture.detectChanges();
    return { tela: fixture.componentInstance as any, mensagens: TestBed.inject(MensagemService) };
  }

  beforeEach(() => {
    salas = { listar: vi.fn(() => of(SALAS)) };
    professores = { listar: vi.fn(() => of([{ id: 'p1' }])) };
    materiais = { listar: vi.fn(() => of([{ id: 'm1' }])) };
    reservas = { listar: vi.fn(() => of(RESERVAS)) };
  });

  it('monta o mapa do dia só com reservas que ocupam a sala, na escala de 07h a 23h [RF03]', () => {
    const { tela } = montar();
    expect(reservas.listar).toHaveBeenCalledWith({ inicio: `${HOJE}T00:00`, fim: `${HOJE}T23:59` });
    const [lab, auditorio] = tela.linhas();
    expect(lab.blocos.map((b: { reserva: { id: string } }) => b.reserva.id)).toEqual(['r1']);
    expect(lab.blocos[0].esquerda).toBeCloseTo(12.5);
    expect(lab.blocos[0].largura).toBeCloseTo(12.5);
    expect(auditorio.blocos[0]).toMatchObject({ esquerda: 0, largura: 100 });
    expect(tela.carregandoMapa()).toBe(false);
  });

  it('mostra a linha do "agora" só no dia de hoje', () => {
    const { tela } = montar();
    const horaAtual = new Date().getHours();
    if (horaAtual >= 7 && horaAtual < 23) {
      expect(tela.agoraPercentual()).not.toBeNull();
    }
    tela.mudarDia(1);
    expect(tela.agoraPercentual()).toBeNull();
  });

  it('navega entre os dias e recarrega o mapa', () => {
    const { tela } = montar();
    tela.mudarDia(1);
    expect(tela.dia()).toBe(dataIso(somarDias(new Date(), 1)));
    tela.mudarDia(-2);
    expect(tela.dia()).toBe(dataIso(somarDias(new Date(), -1)));
    tela.definirDia('');
    expect(tela.dia()).toBe(dataIso(somarDias(new Date(), -1)));
    tela.definirDia('2030-05-20');
    expect(reservas.listar).toHaveBeenLastCalledWith({ inicio: '2030-05-20T00:00', fim: '2030-05-20T23:59' });
  });

  it('recusa intervalo com término antes do início [RN01]', () => {
    const { tela, mensagens } = montar();
    tela.inicio = '10:00';
    tela.fim = '09:00';
    tela.buscar();
    expect(professores.listar).not.toHaveBeenCalled();
    expect(mensagens.mensagens()[0].tipo).toBe('aviso');
  });

  it('pesquisa salas por tipo e capacidade e professores por competência [RF03]', () => {
    const { tela } = montar();
    tela.tipo = 'LABORATORIO';
    tela.capacidadeMinima = 25;
    tela.competencia = '  Java ';
    tela.buscar();
    const intervalo = { inicio: `${HOJE}T08:00`, fim: `${HOJE}T10:00` };
    expect(salas.listar).toHaveBeenLastCalledWith({ ...intervalo, tipo: 'LABORATORIO', capacidadeMinima: 25 });
    expect(professores.listar).toHaveBeenCalledWith({ ...intervalo, competencia: 'Java' });
    expect(materiais.listar).toHaveBeenCalledWith({ ...intervalo, quantidadeMinima: 0 });
    expect(tela.consultado()).toBe(true);
    expect(tela.salasLivres().length).toBe(2);
  });

  it('refaz a pesquisa ao trocar de dia depois de consultar', () => {
    const { tela } = montar();
    tela.buscar();
    tela.mudarDia(1);
    expect(professores.listar).toHaveBeenCalledTimes(2);
  });

  it('mostra os erros da API sem travar a tela', () => {
    salas.listar.mockReturnValue(throwError(() => new Error('500')));
    reservas.listar.mockReturnValue(throwError(() => new Error('500')));
    const { tela, mensagens } = montar();
    expect(tela.carregandoMapa()).toBe(false);
    tela.buscar();
    expect(tela.buscando()).toBe(false);
    expect(mensagens.mensagens().length).toBe(3);
  });
});
