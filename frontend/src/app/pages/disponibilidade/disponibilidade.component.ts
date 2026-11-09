import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { LucideAngularModule } from 'lucide-angular';
import { CabecalhoPaginaComponent } from '../../components/cabecalho-pagina/cabecalho-pagina.component';
import { EstadoVazioComponent } from '../../components/estado-vazio/estado-vazio.component';
import { Material } from '../../models/material.model';
import { Professor } from '../../models/professor.model';
import { Reserva, STATUS_RESERVA } from '../../models/reserva.model';
import { Sala, TIPOS_SALA, TipoSala } from '../../models/sala.model';
import { AuthService } from '../../services/auth.service';
import { dataIso, horas, somarDias } from '../../services/datas';
import { MaterialService } from '../../services/material.service';
import { MensagemService } from '../../services/mensagem.service';
import { ProfessorService } from '../../services/professor.service';
import { ReservaService } from '../../services/reserva.service';
import { SalaService } from '../../services/sala.service';

const PRIMEIRA_HORA = 7;
const ULTIMA_HORA = 23;
const OCUPAM = ['SOLICITADA', 'APROVADA', 'EM_USO'];

interface Bloco {
  reserva: Reserva;
  esquerda: number;
  largura: number;
}

@Component({
  selector: 'app-disponibilidade',
  imports: [DatePipe, FormsModule, RouterLink, LucideAngularModule, CabecalhoPaginaComponent, EstadoVazioComponent],
  templateUrl: './disponibilidade.component.html',
  styleUrl: './disponibilidade.component.css'
})
export class DisponibilidadeComponent implements OnInit {
  private readonly salaService = inject(SalaService);
  private readonly professorService = inject(ProfessorService);
  private readonly materialService = inject(MaterialService);
  private readonly reservaService = inject(ReservaService);
  private readonly mensagens = inject(MensagemService);
  protected readonly podeReservar = inject(AuthService).possuiPerfil('PROFESSOR', 'COORDENADOR');

  protected readonly tiposSala = Object.entries(TIPOS_SALA) as [TipoSala, string][];
  protected readonly rotulosTipo = TIPOS_SALA;
  protected readonly rotulosStatus = STATUS_RESERVA;
  protected readonly horasDoDia = Array.from({ length: ULTIMA_HORA - PRIMEIRA_HORA }, (_, i) => PRIMEIRA_HORA + i);

  protected readonly dia = signal(dataIso(new Date()));
  protected readonly todasSalas = signal<Sala[]>([]);
  protected readonly reservasDoDia = signal<Reserva[]>([]);
  protected readonly carregandoMapa = signal(true);

  protected inicio = '08:00';
  protected fim = '10:00';
  protected tipo: TipoSala | '' = '';
  protected capacidadeMinima: number | null = null;
  protected competencia = '';

  protected readonly buscando = signal(false);
  protected readonly consultado = signal(false);
  protected readonly salasLivres = signal<Sala[]>([]);
  protected readonly professoresLivres = signal<Professor[]>([]);
  protected readonly materiais = signal<Material[]>([]);

  protected readonly linhas = computed(() =>
    this.todasSalas().map(sala => ({
      sala,
      blocos: this.reservasDoDia()
        .filter(r => r.salaId === sala.id && OCUPAM.includes(r.status))
        .map(r => this.bloco(r))
    }))
  );

  protected readonly agoraPercentual = computed(() => {
    if (this.dia() !== dataIso(new Date())) {
      return null;
    }
    const agora = new Date();
    const valor = ((agora.getHours() + agora.getMinutes() / 60 - PRIMEIRA_HORA) / (ULTIMA_HORA - PRIMEIRA_HORA)) * 100;
    return valor >= 0 && valor <= 100 ? valor : null;
  });

  ngOnInit(): void {
    this.salaService.listar({ incluirInativas: false }).subscribe({
      next: salas => this.todasSalas.set(salas),
      error: falha => this.mensagens.erro(falha)
    });
    this.carregarMapa();
  }

  protected mudarDia(dias: number): void {
    const atual = new Date(`${this.dia()}T12:00`);
    this.dia.set(dataIso(somarDias(atual, dias)));
    this.carregarMapa();
    if (this.consultado()) {
      this.buscar();
    }
  }

  protected definirDia(valor: string): void {
    if (valor) {
      this.dia.set(valor);
      this.carregarMapa();
    }
  }

  protected buscar(): void {
    if (!this.inicio || !this.fim || this.fim <= this.inicio) {
      this.mensagens.aviso('Informe um intervalo válido: o término deve ser depois do início.');
      return;
    }
    const intervalo = { inicio: `${this.dia()}T${this.inicio}`, fim: `${this.dia()}T${this.fim}` };
    this.buscando.set(true);
    forkJoin({
      salas: this.salaService.listar({ ...intervalo, tipo: this.tipo || undefined, capacidadeMinima: this.capacidadeMinima ?? undefined }),
      professores: this.professorService.listar({ ...intervalo, competencia: this.competencia.trim() || undefined }),
      materiais: this.materialService.listar({ ...intervalo, quantidadeMinima: 0 })
    }).subscribe({
      next: dados => {
        this.salasLivres.set(dados.salas);
        this.professoresLivres.set(dados.professores);
        this.materiais.set(dados.materiais);
        this.buscando.set(false);
        this.consultado.set(true);
      },
      error: falha => {
        this.buscando.set(false);
        this.mensagens.erro(falha);
      }
    });
  }

  private carregarMapa(): void {
    this.carregandoMapa.set(true);
    this.reservaService.listar({ inicio: `${this.dia()}T00:00`, fim: `${this.dia()}T23:59` }).subscribe({
      next: reservas => {
        this.reservasDoDia.set(reservas);
        this.carregandoMapa.set(false);
      },
      error: falha => {
        this.carregandoMapa.set(false);
        this.mensagens.erro(falha);
      }
    });
  }

  private bloco(reserva: Reserva): Bloco {
    const total = ULTIMA_HORA - PRIMEIRA_HORA;
    const inicio = new Date(reserva.inicio);
    const deslocamento = inicio.getHours() + inicio.getMinutes() / 60 - PRIMEIRA_HORA;
    return {
      reserva,
      esquerda: (deslocamento / total) * 100,
      largura: (horas(reserva.inicio, reserva.fim) / total) * 100
    };
  }
}
