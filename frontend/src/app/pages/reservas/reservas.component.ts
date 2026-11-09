import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';
import { CabecalhoPaginaComponent } from '../../components/cabecalho-pagina/cabecalho-pagina.component';
import { EstadoVazioComponent } from '../../components/estado-vazio/estado-vazio.component';
import { StatusBadgeComponent } from '../../components/status-badge/status-badge.component';
import { Reserva, STATUS_RESERVA, StatusReserva } from '../../models/reserva.model';
import { AuthService } from '../../services/auth.service';
import { dataIso, somarDias } from '../../services/datas';
import { MensagemService } from '../../services/mensagem.service';
import { ReservaService } from '../../services/reserva.service';

@Component({
  selector: 'app-reservas',
  imports: [DatePipe, FormsModule, RouterLink, LucideAngularModule, CabecalhoPaginaComponent, EstadoVazioComponent, StatusBadgeComponent],
  templateUrl: './reservas.component.html',
  styleUrl: './reservas.component.css'
})
export class ReservasComponent implements OnInit {
  protected readonly auth = inject(AuthService);
  private readonly reservaService = inject(ReservaService);
  private readonly mensagens = inject(MensagemService);
  private readonly router = inject(Router);

  protected readonly podeReservar = this.auth.possuiPerfil('PROFESSOR', 'COORDENADOR');
  protected readonly statusDisponiveis = Object.entries(STATUS_RESERVA) as [StatusReserva, string][];

  protected readonly carregando = signal(true);
  protected readonly reservas = signal<Reserva[]>([]);
  protected readonly busca = signal('');

  protected inicio = dataIso(new Date());
  protected fim = dataIso(somarDias(new Date(), 30));
  protected status: StatusReserva | '' = '';
  protected minhas = this.podeReservar;

  protected readonly filtradas = computed(() => {
    const termo = this.busca().trim().toLowerCase();
    if (!termo) {
      return this.reservas();
    }
    return this.reservas().filter(r =>
      [r.disciplina, r.salaCodigo, r.professorNome, r.solicitanteNome].some(v => v.toLowerCase().includes(termo))
    );
  });

  ngOnInit(): void {
    this.carregar();
  }

  protected carregar(): void {
    this.carregando.set(true);
    this.reservaService
      .listar({
        inicio: this.inicio ? `${this.inicio}T00:00` : undefined,
        fim: this.fim ? `${this.fim}T23:59` : undefined,
        status: this.status || undefined,
        minhas: this.minhas || undefined
      })
      .subscribe({
        next: lista => {
          this.reservas.set(lista);
          this.carregando.set(false);
        },
        error: falha => {
          this.carregando.set(false);
          this.mensagens.erro(falha);
        }
      });
  }

  protected abrir(reserva: Reserva): void {
    this.router.navigate(['/reservas', reserva.id]);
  }
}
