import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';
import { CabecalhoPaginaComponent } from '../../components/cabecalho-pagina/cabecalho-pagina.component';
import { DialogoComponent } from '../../components/dialogo/dialogo.component';
import { EstadoVazioComponent } from '../../components/estado-vazio/estado-vazio.component';
import { Reserva } from '../../models/reserva.model';
import { MensagemService } from '../../services/mensagem.service';
import { ReservaService } from '../../services/reserva.service';

@Component({
  selector: 'app-aprovacoes',
  imports: [DatePipe, FormsModule, RouterLink, LucideAngularModule, CabecalhoPaginaComponent, DialogoComponent, EstadoVazioComponent],
  templateUrl: './aprovacoes.component.html',
  styleUrl: './aprovacoes.component.css'
})
export class AprovacoesComponent implements OnInit {
  private readonly reservaService = inject(ReservaService);
  private readonly mensagens = inject(MensagemService);

  protected readonly carregando = signal(true);
  protected readonly pendentes = signal<Reserva[]>([]);
  protected readonly processando = signal<string | null>(null);
  protected readonly rejeitando = signal<Reserva | null>(null);
  protected motivo = '';

  ngOnInit(): void {
    this.carregar();
  }

  protected aprovar(reserva: Reserva): void {
    this.processando.set(reserva.id);
    this.reservaService.aprovar(reserva.id, null).subscribe({
      next: () => this.concluir(reserva, `Reserva de ${reserva.disciplina} aprovada.`),
      error: falha => this.falhar(falha)
    });
  }

  protected abrirRejeicao(reserva: Reserva): void {
    this.motivo = '';
    this.rejeitando.set(reserva);
  }

  protected rejeitar(): void {
    const reserva = this.rejeitando();
    if (!reserva) {
      return;
    }
    if (!this.motivo.trim()) {
      this.mensagens.aviso('Informe o motivo da rejeição.');
      return;
    }
    this.processando.set(reserva.id);
    this.reservaService.rejeitar(reserva.id, this.motivo.trim()).subscribe({
      next: () => {
        this.rejeitando.set(null);
        this.concluir(reserva, `Reserva de ${reserva.disciplina} rejeitada.`);
      },
      error: falha => this.falhar(falha)
    });
  }

  private carregar(): void {
    this.reservaService.listar({ status: 'SOLICITADA' }).subscribe({
      next: lista => {
        this.pendentes.set(lista);
        this.carregando.set(false);
      },
      error: falha => {
        this.carregando.set(false);
        this.mensagens.erro(falha);
      }
    });
  }

  private concluir(reserva: Reserva, texto: string): void {
    this.processando.set(null);
    this.pendentes.update(lista => lista.filter(r => r.id !== reserva.id));
    this.mensagens.sucesso(texto);
  }

  private falhar(falha: unknown): void {
    this.processando.set(null);
    this.mensagens.erro(falha);
  }
}
