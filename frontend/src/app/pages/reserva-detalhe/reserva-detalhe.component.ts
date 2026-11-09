import { Component, OnInit, computed, inject, input, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Observable } from 'rxjs';
import { LucideAngularModule } from 'lucide-angular';
import { CabecalhoPaginaComponent } from '../../components/cabecalho-pagina/cabecalho-pagina.component';
import { DialogoComponent } from '../../components/dialogo/dialogo.component';
import { StatusBadgeComponent } from '../../components/status-badge/status-badge.component';
import { ACOES_AUDITORIA, EventoAuditoria } from '../../models/auditoria.model';
import { ItemReserva, Reserva, STATUS_RESERVA, StatusReserva } from '../../models/reserva.model';
import { AuthService } from '../../services/auth.service';
import { horas } from '../../services/datas';
import { MensagemService } from '../../services/mensagem.service';
import { ReservaService } from '../../services/reserva.service';

type AcaoComTexto = 'cancelar' | 'aprovar' | 'rejeitar';

interface ConfigDialogo {
  titulo: string;
  texto: string;
  rotulo: string;
  botao: string;
  classe: string;
  obrigatorio: boolean;
}

const DIALOGOS: Record<AcaoComTexto, ConfigDialogo> = {
  cancelar: {
    titulo: 'Cancelar reserva',
    texto: 'O horário e os recursos voltam a ficar livres para outras pessoas.',
    rotulo: 'Motivo do cancelamento',
    botao: 'Cancelar reserva',
    classe: 'btn-perigo',
    obrigatorio: false
  },
  aprovar: {
    titulo: 'Aprovar reserva',
    texto: 'O solicitante será notificado da aprovação.',
    rotulo: 'Observação (opcional)',
    botao: 'Aprovar',
    classe: 'btn-sucesso',
    obrigatorio: false
  },
  rejeitar: {
    titulo: 'Rejeitar reserva',
    texto: 'Explique o motivo para o solicitante poder ajustar o pedido.',
    rotulo: 'Motivo da rejeição',
    botao: 'Rejeitar',
    classe: 'btn-perigo',
    obrigatorio: true
  }
};

interface SituacaoMaterial {
  item: ItemReserva;
  retirado: number;
  devolvido: number;
}

@Component({
  selector: 'app-reserva-detalhe',
  imports: [DatePipe, FormsModule, RouterLink, LucideAngularModule, CabecalhoPaginaComponent, DialogoComponent, StatusBadgeComponent],
  templateUrl: './reserva-detalhe.component.html',
  styleUrl: './reserva-detalhe.component.css'
})
export class ReservaDetalheComponent implements OnInit {
  readonly id = input.required<string>();

  private readonly auth = inject(AuthService);
  private readonly reservaService = inject(ReservaService);
  private readonly mensagens = inject(MensagemService);
  private readonly router = inject(Router);

  protected readonly acoesAuditoria = ACOES_AUDITORIA;
  protected readonly statusReserva = STATUS_RESERVA;

  protected readonly reserva = signal<Reserva | null>(null);
  protected readonly historico = signal<EventoAuditoria[]>([]);
  protected readonly processando = signal(false);
  protected readonly dialogo = signal<AcaoComTexto | null>(null);
  protected readonly confirmarExclusao = signal(false);
  protected texto = '';
  protected movimentacao: Record<string, number> = {};

  protected readonly configDialogo = computed(() => {
    const acao = this.dialogo();
    return acao ? DIALOGOS[acao] : null;
  });

  private readonly ehDono = computed(() => this.reserva()?.solicitanteId === this.auth.usuario()?.id);
  private readonly ehSolicitante = this.auth.possuiPerfil('PROFESSOR', 'COORDENADOR');
  private readonly ehResponsavel = this.auth.possuiPerfil('RESPONSAVEL');

  protected readonly pode = computed(() => {
    const r = this.reserva();
    const status = r?.status;
    const dono = this.ehDono() && this.ehSolicitante;
    const iniciada = status === 'EM_USO' || status === 'CONCLUIDA' || status === 'NAO_COMPARECEU';
    return {
      editar: dono && (status === 'SOLICITADA' || status === 'APROVADA'),
      cancelar: dono && (status === 'SOLICITADA' || status === 'APROVADA'),
      aprovar: this.ehResponsavel && status === 'SOLICITADA',
      iniciar: (dono || this.ehResponsavel) && status === 'APROVADA',
      concluir: (dono || this.ehResponsavel) && status === 'EM_USO',
      naoCompareceu: this.ehResponsavel && status === 'APROVADA',
      movimentar: this.ehResponsavel && status === 'EM_USO',
      excluir: !iniciada && ((this.ehDono() && this.ehSolicitante) || this.auth.possuiPerfil('ADMIN'))
    };
  });

  protected readonly situacaoMateriais = computed<SituacaoMaterial[]>(() => {
    const r = this.reserva();
    if (!r) {
      return [];
    }
    return r.materiais.map(item => ({
      item,
      retirado: this.somar(r, item.materialId, 'RETIRADA'),
      devolvido: this.somar(r, item.materialId, 'DEVOLUCAO')
    }));
  });

  protected readonly duracao = computed(() => {
    const r = this.reserva();
    return r ? horas(r.inicio, r.fim) : 0;
  });

  ngOnInit(): void {
    this.carregar();
  }

  protected abrirDialogo(acao: AcaoComTexto): void {
    this.texto = '';
    this.dialogo.set(acao);
  }

  protected confirmarDialogo(): void {
    const acao = this.dialogo();
    const r = this.reserva();
    if (!acao || !r) {
      return;
    }
    if (DIALOGOS[acao].obrigatorio && !this.texto.trim()) {
      this.mensagens.aviso('Informe o motivo.');
      return;
    }
    const texto = this.texto.trim() || null;
    const chamadas: Record<AcaoComTexto, Observable<Reserva>> = {
      cancelar: this.reservaService.cancelar(r.id, texto),
      aprovar: this.reservaService.aprovar(r.id, texto),
      rejeitar: this.reservaService.rejeitar(r.id, texto)
    };
    const sucesso: Record<AcaoComTexto, string> = {
      cancelar: 'Reserva cancelada.',
      aprovar: 'Reserva aprovada.',
      rejeitar: 'Reserva rejeitada.'
    };
    this.executar(chamadas[acao], sucesso[acao]);
  }

  protected iniciar(): void {
    this.executar(this.reservaService.iniciar(this.id()), 'Uso iniciado. Bom trabalho!');
  }

  protected concluir(): void {
    this.executar(this.reservaService.concluir(this.id()), 'Reserva concluída.');
  }

  protected naoCompareceu(): void {
    this.executar(this.reservaService.registrarNaoComparecimento(this.id()), 'Não comparecimento registrado.');
  }

  protected movimentar(situacao: SituacaoMaterial, tipo: 'RETIRADA' | 'DEVOLUCAO'): void {
    const quantidade = this.movimentacao[situacao.item.materialId] ?? 0;
    if (quantidade < 1) {
      this.mensagens.aviso('Informe a quantidade.');
      return;
    }
    const dados = { materialId: situacao.item.materialId, quantidade, observacao: null };
    const chamada = tipo === 'RETIRADA'
      ? this.reservaService.registrarRetirada(this.id(), dados)
      : this.reservaService.registrarDevolucao(this.id(), dados);
    this.executar(chamada, tipo === 'RETIRADA' ? 'Retirada registrada.' : 'Devolução registrada.');
    this.movimentacao[situacao.item.materialId] = 0;
  }

  protected excluir(): void {
    this.processando.set(true);
    this.reservaService.excluir(this.id()).subscribe({
      next: () => {
        this.mensagens.sucesso('Reserva apagada.');
        this.router.navigate(['/reservas']);
      },
      error: falha => {
        this.processando.set(false);
        this.confirmarExclusao.set(false);
        this.mensagens.erro(falha);
      }
    });
  }

  protected rotuloEstado(valor: string | null): string {
    return valor && valor in STATUS_RESERVA ? STATUS_RESERVA[valor as StatusReserva] : valor ?? '';
  }

  private executar(chamada: Observable<Reserva>, textoSucesso: string): void {
    this.processando.set(true);
    chamada.subscribe({
      next: reserva => {
        this.reserva.set(reserva);
        this.dialogo.set(null);
        this.processando.set(false);
        this.mensagens.sucesso(textoSucesso);
        this.carregarHistorico();
      },
      error: falha => {
        this.processando.set(false);
        this.mensagens.erro(falha);
      }
    });
  }

  private carregar(): void {
    this.reservaService.obter(this.id()).subscribe({
      next: reserva => this.reserva.set(reserva),
      error: falha => {
        this.mensagens.erro(falha);
        this.router.navigate(['/reservas']);
      }
    });
    this.carregarHistorico();
  }

  private carregarHistorico(): void {
    this.reservaService.historico(this.id()).subscribe({
      next: eventos => this.historico.set([...eventos].reverse()),
      error: () => this.historico.set([])
    });
  }

  private somar(r: Reserva, materialId: string, tipo: 'RETIRADA' | 'DEVOLUCAO'): number {
    return r.movimentacoes.filter(m => m.materialId === materialId && m.tipo === tipo).reduce((t, m) => t + m.quantidade, 0);
  }
}
