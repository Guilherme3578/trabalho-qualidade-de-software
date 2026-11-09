import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';
import { CabecalhoPaginaComponent } from '../../components/cabecalho-pagina/cabecalho-pagina.component';
import { EstadoVazioComponent } from '../../components/estado-vazio/estado-vazio.component';
import { ACOES_AUDITORIA, AcaoAuditoria, EventoAuditoria } from '../../models/auditoria.model';
import { STATUS_RESERVA, StatusReserva } from '../../models/reserva.model';
import { AuditoriaService } from '../../services/auditoria.service';
import { MensagemService } from '../../services/mensagem.service';

const CORES: Partial<Record<AcaoAuditoria, string>> = {
  CRIACAO: 'badge-sucesso',
  ALTERACAO: 'badge-info',
  TRANSICAO_ESTADO: 'badge-primario',
  EXCLUSAO: 'badge-perigo',
  DESATIVACAO: 'badge-neutro',
  LOGIN_FALHA: 'badge-perigo',
  RETIRADA_MATERIAL: 'badge-aviso',
  DEVOLUCAO_MATERIAL: 'badge-sucesso'
};

@Component({
  selector: 'app-auditoria',
  imports: [DatePipe, FormsModule, RouterLink, LucideAngularModule, CabecalhoPaginaComponent, EstadoVazioComponent],
  templateUrl: './auditoria.component.html',
  styleUrl: './auditoria.component.css'
})
export class AuditoriaComponent implements OnInit {
  private readonly servico = inject(AuditoriaService);
  private readonly mensagens = inject(MensagemService);

  protected readonly acoes = Object.entries(ACOES_AUDITORIA) as [AcaoAuditoria, string][];
  protected readonly rotulos = ACOES_AUDITORIA;
  protected readonly entidades: Record<string, string> = {
    Reserva: 'Reserva',
    Sala: 'Sala',
    Professor: 'Professor',
    Material: 'Material',
    Bloqueio: 'Manutenção',
    Usuario: 'Usuário'
  };
  protected readonly listaEntidades = Object.entries(this.entidades);
  protected readonly eventos = signal<EventoAuditoria[]>([]);
  protected readonly carregando = signal(true);

  protected entidade = '';
  protected acao: AcaoAuditoria | '' = '';

  ngOnInit(): void {
    this.carregar();
  }

  protected carregar(): void {
    this.carregando.set(true);
    this.servico.listar({ entidade: this.entidade || undefined, acao: this.acao || undefined }).subscribe({
      next: lista => {
        this.eventos.set(lista);
        this.carregando.set(false);
      },
      error: falha => {
        this.carregando.set(false);
        this.mensagens.erro(falha);
      }
    });
  }

  protected cor(acao: AcaoAuditoria): string {
    return CORES[acao] ?? 'badge-neutro';
  }

  protected estado(valor: string | null): string {
    return valor && valor in STATUS_RESERVA ? STATUS_RESERVA[valor as StatusReserva] : valor ?? '';
  }
}
