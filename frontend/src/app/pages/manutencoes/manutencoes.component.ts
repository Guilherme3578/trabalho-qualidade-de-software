import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { LucideAngularModule } from 'lucide-angular';
import { CabecalhoPaginaComponent } from '../../components/cabecalho-pagina/cabecalho-pagina.component';
import { DialogoComponent } from '../../components/dialogo/dialogo.component';
import { EstadoVazioComponent } from '../../components/estado-vazio/estado-vazio.component';
import { Bloqueio, TIPOS_RECURSO, TipoRecurso } from '../../models/bloqueio.model';
import { BloqueioService } from '../../services/bloqueio.service';
import { dataHoraIso } from '../../services/datas';
import { MaterialService } from '../../services/material.service';
import { MensagemService } from '../../services/mensagem.service';
import { ProfessorService } from '../../services/professor.service';
import { SalaService } from '../../services/sala.service';

interface Recurso {
  id: string;
  nome: string;
}

@Component({
  selector: 'app-manutencoes',
  imports: [DatePipe, ReactiveFormsModule, RouterLink, LucideAngularModule, CabecalhoPaginaComponent, DialogoComponent, EstadoVazioComponent],
  templateUrl: './manutencoes.component.html',
  styleUrl: './manutencoes.component.css'
})
export class ManutencoesComponent implements OnInit {
  private readonly servico = inject(BloqueioService);
  private readonly salaService = inject(SalaService);
  private readonly professorService = inject(ProfessorService);
  private readonly materialService = inject(MaterialService);
  private readonly mensagens = inject(MensagemService);

  protected readonly tipos = Object.entries(TIPOS_RECURSO) as [TipoRecurso, string][];
  protected readonly rotulosTipo = TIPOS_RECURSO;
  protected readonly bloqueios = signal<Bloqueio[]>([]);
  protected readonly recursos = signal<Record<TipoRecurso, Recurso[]>>({ SALA: [], PROFESSOR: [], MATERIAL: [] });
  protected readonly carregando = signal(true);
  protected readonly dialogoAberto = signal(false);
  protected readonly salvando = signal(false);
  protected readonly afetadas = signal<string[] | null>(null);
  protected readonly removendo = signal<Bloqueio | null>(null);
  private readonly agora = new Date();

  protected readonly formulario = inject(FormBuilder).nonNullable.group({
    tipoRecurso: ['SALA' as TipoRecurso, Validators.required],
    recursoId: ['', Validators.required],
    inicio: ['', Validators.required],
    fim: ['', Validators.required],
    motivo: ['', [Validators.required, Validators.maxLength(255)]]
  });

  protected readonly tipoEscolhido = signal<TipoRecurso>('SALA');
  protected readonly opcoes = computed(() => this.recursos()[this.tipoEscolhido()]);

  ngOnInit(): void {
    this.formulario.controls.tipoRecurso.valueChanges.subscribe(tipo => {
      this.tipoEscolhido.set(tipo);
      this.formulario.controls.recursoId.setValue('');
    });
    forkJoin({
      salas: this.salaService.listar({ incluirInativas: true }),
      professores: this.professorService.listar(),
      materiais: this.materialService.listar(),
      bloqueios: this.servico.listar()
    }).subscribe({
      next: dados => {
        this.recursos.set({
          SALA: dados.salas.map(s => ({ id: s.id, nome: `${s.codigo} · ${s.nome}` })),
          PROFESSOR: dados.professores.map(p => ({ id: p.id, nome: p.nome })),
          MATERIAL: dados.materiais.map(m => ({ id: m.id, nome: m.nome }))
        });
        this.bloqueios.set(dados.bloqueios);
        this.carregando.set(false);
      },
      error: falha => {
        this.carregando.set(false);
        this.mensagens.erro(falha);
      }
    });
  }

  protected nomeRecurso(b: Bloqueio): string {
    return this.recursos()[b.tipoRecurso].find(r => r.id === b.recursoId)?.nome ?? b.recursoId;
  }

  protected vigente(b: Bloqueio): boolean {
    return new Date(b.inicio) <= this.agora && this.agora < new Date(b.fim);
  }

  protected encerrado(b: Bloqueio): boolean {
    return new Date(b.fim) <= this.agora;
  }

  protected novo(): void {
    const inicio = new Date();
    inicio.setMinutes(0, 0, 0);
    inicio.setHours(inicio.getHours() + 1);
    const fim = new Date(inicio);
    fim.setHours(fim.getHours() + 4);
    this.formulario.reset({ tipoRecurso: 'SALA', recursoId: '', inicio: dataHoraIso(inicio), fim: dataHoraIso(fim), motivo: '' });
    this.dialogoAberto.set(true);
  }

  protected salvar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    const v = this.formulario.getRawValue();
    if (v.fim <= v.inicio) {
      this.mensagens.aviso('O término deve ser depois do início.');
      return;
    }
    this.salvando.set(true);
    this.servico.criar({ ...v, motivo: v.motivo.trim() }).subscribe({
      next: bloqueio => {
        this.salvando.set(false);
        this.dialogoAberto.set(false);
        this.bloqueios.update(lista => [bloqueio, ...lista]);
        this.mensagens.sucesso('Manutenção registrada. O recurso não poderá ser reservado nesse período.');
        if (bloqueio.reservasAfetadas.length) {
          this.afetadas.set(bloqueio.reservasAfetadas);
        }
      },
      error: falha => {
        this.salvando.set(false);
        this.mensagens.erro(falha);
      }
    });
  }

  protected remover(): void {
    const bloqueio = this.removendo();
    if (!bloqueio) {
      return;
    }
    this.servico.remover(bloqueio.id).subscribe({
      next: () => {
        this.bloqueios.update(lista => lista.filter(b => b.id !== bloqueio.id));
        this.removendo.set(null);
        this.mensagens.sucesso('Manutenção removida. O recurso voltou a ficar disponível.');
      },
      error: falha => this.mensagens.erro(falha)
    });
  }
}
