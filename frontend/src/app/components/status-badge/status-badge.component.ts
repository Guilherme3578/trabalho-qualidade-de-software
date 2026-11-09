import { Component, computed, input } from '@angular/core';
import { STATUS_RESERVA, StatusReserva } from '../../models/reserva.model';

const CORES: Record<StatusReserva, string> = {
  SOLICITADA: 'badge-aviso',
  APROVADA: 'badge-info',
  EM_USO: 'badge-primario',
  CONCLUIDA: 'badge-sucesso',
  REJEITADA: 'badge-perigo',
  CANCELADA: 'badge-neutro',
  NAO_COMPARECEU: 'badge-perigo'
};

@Component({
  selector: 'app-status-badge',
  templateUrl: './status-badge.component.html',
  styleUrl: './status-badge.component.css'
})
export class StatusBadgeComponent {
  readonly status = input.required<StatusReserva>();

  protected readonly rotulo = computed(() => STATUS_RESERVA[this.status()]);
  protected readonly classe = computed(() => CORES[this.status()]);
  protected readonly ativo = computed(() => this.status() === 'EM_USO' || this.status() === 'SOLICITADA');
}
