import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';
import { CabecalhoPaginaComponent } from '../../components/cabecalho-pagina/cabecalho-pagina.component';
import { EstadoVazioComponent } from '../../components/estado-vazio/estado-vazio.component';
import { Notificacao } from '../../models/notificacao.model';
import { MensagemService } from '../../services/mensagem.service';
import { NotificacaoService } from '../../services/notificacao.service';

const ICONES: Record<string, string> = {
  RESERVA_APROVADA: 'circle-check',
  RESERVA_REJEITADA: 'x',
  RESERVA_CANCELADA: 'ban',
  RESERVA_SOLICITADA: 'hourglass'
};

@Component({
  selector: 'app-notificacoes',
  imports: [DatePipe, RouterLink, LucideAngularModule, CabecalhoPaginaComponent, EstadoVazioComponent],
  templateUrl: './notificacoes.component.html',
  styleUrl: './notificacoes.component.css'
})
export class NotificacoesComponent implements OnInit {
  protected readonly servico = inject(NotificacaoService);
  private readonly mensagens = inject(MensagemService);
  protected readonly carregando = signal(true);

  ngOnInit(): void {
    this.servico.carregar().subscribe({
      next: () => this.carregando.set(false),
      error: falha => {
        this.carregando.set(false);
        this.mensagens.erro(falha);
      }
    });
  }

  protected icone(n: Notificacao): string {
    return ICONES[n.tipo] ?? 'bell';
  }

  protected marcarLida(n: Notificacao): void {
    if (!n.lida) {
      this.servico.marcarLida(n.id).subscribe({ error: falha => this.mensagens.erro(falha) });
    }
  }
}
