import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { LucideAngularModule } from 'lucide-angular';
import { EstadoVazioComponent } from '../../components/estado-vazio/estado-vazio.component';
import { StatusBadgeComponent } from '../../components/status-badge/status-badge.component';
import { Reserva } from '../../models/reserva.model';
import { AuthService } from '../../services/auth.service';
import { fimDoDia, inicioDoDia, somarDias } from '../../services/datas';
import { MensagemService } from '../../services/mensagem.service';
import { ReservaService } from '../../services/reserva.service';

interface Indicador {
  rotulo: string;
  valor: number;
  icone: string;
  cor: string;
  dica: string;
}

interface Atalho {
  rota: string;
  rotulo: string;
  texto: string;
  icone: string;
}

@Component({
  selector: 'app-inicio',
  imports: [DatePipe, RouterLink, LucideAngularModule, StatusBadgeComponent, EstadoVazioComponent],
  templateUrl: './inicio.component.html',
  styleUrl: './inicio.component.css'
})
export class InicioComponent implements OnInit {
  protected readonly auth = inject(AuthService);
  private readonly reservas = inject(ReservaService);
  private readonly mensagens = inject(MensagemService);

  protected readonly carregando = signal(true);
  protected readonly hoje = signal<Reserva[]>([]);
  protected readonly proximas = signal<Reserva[]>([]);
  protected readonly pendentes = signal<Reserva[]>([]);
  protected readonly agora = new Date();

  private readonly solicitante = this.auth.possuiPerfil('PROFESSOR', 'COORDENADOR');

  protected readonly saudacao = computed(() => {
    const hora = this.agora.getHours();
    return hora < 12 ? 'Bom dia' : hora < 18 ? 'Boa tarde' : 'Boa noite';
  });

  protected readonly indicadores = computed<Indicador[]>(() => {
    const hoje = this.hoje().filter(r => r.status !== 'CANCELADA' && r.status !== 'REJEITADA');
    return [
      { rotulo: 'Reservas hoje', valor: hoje.length, icone: 'calendar-days', cor: 'violeta', dica: 'ativas no dia' },
      {
        rotulo: 'Em uso agora',
        valor: hoje.filter(r => r.status === 'EM_USO').length,
        icone: 'play',
        cor: 'ciano',
        dica: 'salas ocupadas'
      },
      {
        rotulo: 'Aguardando aprovação',
        valor: this.pendentes().length,
        icone: 'hourglass',
        cor: 'ambar',
        dica: 'recursos restritos'
      },
      {
        rotulo: 'Próximos 7 dias',
        valor: this.proximas().length,
        icone: 'clock',
        cor: 'verde',
        dica: this.solicitante ? 'suas reservas' : 'reservas agendadas'
      }
    ];
  });

  protected readonly atalhos = computed<Atalho[]>(() => {
    const lista: Atalho[] = [];
    if (this.solicitante) {
      lista.push({ rota: '/reservas/nova', rotulo: 'Nova reserva', texto: 'Escolha sala, horário e materiais', icone: 'calendar-plus' });
    }
    lista.push({ rota: '/disponibilidade', rotulo: 'Ver disponibilidade', texto: 'Salas, professores e materiais livres', icone: 'search' });
    if (this.auth.possuiPerfil('RESPONSAVEL')) {
      lista.push({ rota: '/aprovacoes', rotulo: 'Aprovações', texto: 'Pedidos com recursos restritos', icone: 'clipboard-check' });
    }
    if (this.auth.possuiPerfil('COORDENADOR', 'RESPONSAVEL', 'ADMIN')) {
      lista.push({ rota: '/relatorios', rotulo: 'Relatórios', texto: 'Ocupação e carga horária', icone: 'chart-column' });
    }
    if (this.auth.possuiPerfil('ADMIN')) {
      lista.push({ rota: '/salas', rotulo: 'Cadastros', texto: 'Salas, professores e materiais', icone: 'door-open' });
    }
    return lista;
  });

  ngOnInit(): void {
    const minhas = this.solicitante;
    forkJoin({
      hoje: this.reservas.listar({ inicio: inicioDoDia(), fim: fimDoDia(), minhas }),
      proximas: this.reservas.listar({ inicio: inicioDoDia(), fim: fimDoDia(somarDias(this.agora, 7)), minhas }),
      pendentes: this.reservas.listar({ status: 'SOLICITADA', minhas })
    }).subscribe({
      next: dados => {
        this.hoje.set(dados.hoje);
        this.proximas.set(
          dados.proximas.filter(r => ['SOLICITADA', 'APROVADA', 'EM_USO'].includes(r.status) && new Date(r.fim) > this.agora)
        );
        this.pendentes.set(dados.pendentes);
        this.carregando.set(false);
      },
      error: falha => {
        this.carregando.set(false);
        this.mensagens.erro(falha);
      }
    });
  }

  protected acontecendo(reserva: Reserva): boolean {
    return new Date(reserva.inicio) <= this.agora && this.agora < new Date(reserva.fim);
  }
}
