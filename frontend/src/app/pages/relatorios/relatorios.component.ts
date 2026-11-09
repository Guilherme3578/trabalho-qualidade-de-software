import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { LucideAngularModule } from 'lucide-angular';
import { CabecalhoPaginaComponent } from '../../components/cabecalho-pagina/cabecalho-pagina.component';
import { EstadoVazioComponent } from '../../components/estado-vazio/estado-vazio.component';
import { CargaHoraria, RelatorioConflitos, RelatorioUtilizacao } from '../../models/relatorio.model';
import { dataIso, somarDias } from '../../services/datas';
import { MensagemService } from '../../services/mensagem.service';
import { RelatorioService } from '../../services/relatorio.service';

const ROTULOS_CONFLITO: Record<string, string> = {
  CONFLITO_SALA: 'Sala ocupada',
  CONFLITO_PROFESSOR: 'Professor ocupado',
  CONFLITO_MATERIAL: 'Material esgotado',
  RECURSO_EM_MANUTENCAO: 'Em manutenção'
};

@Component({
  selector: 'app-relatorios',
  imports: [DecimalPipe, FormsModule, LucideAngularModule, CabecalhoPaginaComponent, EstadoVazioComponent],
  templateUrl: './relatorios.component.html',
  styleUrl: './relatorios.component.css'
})
export class RelatoriosComponent implements OnInit {
  private readonly servico = inject(RelatorioService);
  private readonly mensagens = inject(MensagemService);

  protected inicio = dataIso(new Date());
  protected fim = dataIso(somarDias(new Date(), 30));

  protected readonly carregando = signal(true);
  protected readonly utilizacao = signal<RelatorioUtilizacao | null>(null);
  protected readonly carga = signal<CargaHoraria[]>([]);
  protected readonly conflitos = signal<RelatorioConflitos | null>(null);

  protected readonly totalReservas = computed(() => (this.utilizacao()?.salas ?? []).reduce((t, s) => t + s.reservas, 0));
  protected readonly totalHoras = computed(() => (this.utilizacao()?.salas ?? []).reduce((t, s) => t + s.horasReservadas, 0));
  protected readonly ocupacaoMedia = computed(() => {
    const salas = this.utilizacao()?.salas ?? [];
    return salas.length ? salas.reduce((t, s) => t + s.taxaOcupacaoPercentual, 0) / salas.length : 0;
  });
  protected readonly maiorCarga = computed(() => Math.max(1, ...this.carga().map(c => c.horasAlocadas)));
  protected readonly maiorOcupacao = computed(() =>
    Math.max(1, ...(this.utilizacao()?.salas ?? []).map(s => s.taxaOcupacaoPercentual))
  );
  protected readonly tiposConflito = computed(() =>
    Object.entries(this.conflitos()?.porTipo ?? {}).map(([tipo, quantidade]) => ({
      rotulo: ROTULOS_CONFLITO[tipo] ?? tipo,
      quantidade
    }))
  );

  ngOnInit(): void {
    this.gerar();
  }

  protected gerar(): void {
    if (!this.inicio || !this.fim || this.fim < this.inicio) {
      this.mensagens.aviso('Informe um período válido.');
      return;
    }
    this.carregando.set(true);
    forkJoin({
      utilizacao: this.servico.utilizacao(this.inicio, this.fim),
      carga: this.servico.cargaHoraria(this.inicio, this.fim),
      conflitos: this.servico.conflitosEvitados(this.inicio, this.fim)
    }).subscribe({
      next: dados => {
        this.utilizacao.set(dados.utilizacao);
        this.carga.set([...dados.carga].sort((a, b) => b.horasAlocadas - a.horasAlocadas));
        this.conflitos.set(dados.conflitos);
        this.carregando.set(false);
      },
      error: falha => {
        this.carregando.set(false);
        this.mensagens.erro(falha);
      }
    });
  }

  protected largura(valor: number, maximo: number): number {
    return maximo > 0 ? Math.max(2, (valor / maximo) * 100) : 0;
  }
}
