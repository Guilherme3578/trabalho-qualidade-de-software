import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { LucideAngularModule } from 'lucide-angular';
import { CabecalhoPaginaComponent } from '../../components/cabecalho-pagina/cabecalho-pagina.component';
import { DialogoComponent } from '../../components/dialogo/dialogo.component';
import { EstadoVazioComponent } from '../../components/estado-vazio/estado-vazio.component';
import { Sala, TIPOS_SALA, TipoSala } from '../../models/sala.model';
import { MensagemService } from '../../services/mensagem.service';
import { SalaService } from '../../services/sala.service';

@Component({
  selector: 'app-salas',
  imports: [FormsModule, ReactiveFormsModule, LucideAngularModule, CabecalhoPaginaComponent, DialogoComponent, EstadoVazioComponent],
  templateUrl: './salas.component.html',
  styleUrl: './salas.component.css'
})
export class SalasComponent implements OnInit {
  private readonly servico = inject(SalaService);
  private readonly mensagens = inject(MensagemService);

  protected readonly tipos = Object.entries(TIPOS_SALA) as [TipoSala, string][];
  protected readonly rotulosTipo = TIPOS_SALA;
  protected readonly salas = signal<Sala[]>([]);
  protected readonly carregando = signal(true);
  protected readonly busca = signal('');
  protected readonly editando = signal<Sala | null>(null);
  protected readonly dialogoAberto = signal(false);
  protected readonly salvando = signal(false);

  protected readonly filtradas = computed(() => {
    const termo = this.busca().trim().toLowerCase();
    return this.salas().filter(s => !termo || `${s.codigo} ${s.nome} ${s.localizacao}`.toLowerCase().includes(termo));
  });

  protected readonly formulario = inject(FormBuilder).nonNullable.group({
    codigo: ['', [Validators.required, Validators.pattern(/^[A-Za-z0-9-]{1,20}$/)]],
    nome: ['', [Validators.required, Validators.maxLength(120)]],
    tipo: ['SALA_AULA' as TipoSala, Validators.required],
    capacidade: [30, [Validators.required, Validators.min(1), Validators.max(1000)]],
    localizacao: ['', [Validators.required, Validators.maxLength(120)]],
    restrita: [false]
  });

  ngOnInit(): void {
    this.carregar();
  }

  protected novo(): void {
    this.editando.set(null);
    this.formulario.reset();
    this.dialogoAberto.set(true);
  }

  protected editar(sala: Sala): void {
    this.editando.set(sala);
    this.formulario.setValue({
      codigo: sala.codigo,
      nome: sala.nome,
      tipo: sala.tipo,
      capacidade: sala.capacidade,
      localizacao: sala.localizacao,
      restrita: sala.restrita
    });
    this.dialogoAberto.set(true);
  }

  protected salvar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    const dados = this.formulario.getRawValue();
    const atual = this.editando();
    this.salvando.set(true);
    (atual ? this.servico.atualizar(atual.id, dados) : this.servico.criar(dados)).subscribe({
      next: () => {
        this.salvando.set(false);
        this.dialogoAberto.set(false);
        this.mensagens.sucesso(atual ? 'Sala atualizada.' : 'Sala cadastrada.');
        this.carregar();
      },
      error: falha => {
        this.salvando.set(false);
        this.mensagens.erro(falha);
      }
    });
  }

  protected alternarAtiva(sala: Sala): void {
    this.servico.definirAtiva(sala.id, !sala.ativa).subscribe({
      next: atualizada => {
        this.salas.update(lista => lista.map(s => (s.id === atualizada.id ? atualizada : s)));
        this.mensagens.sucesso(atualizada.ativa ? 'Sala reativada.' : 'Sala desativada.');
      },
      error: falha => this.mensagens.erro(falha)
    });
  }

  private carregar(): void {
    this.servico.listar({ incluirInativas: true }).subscribe({
      next: lista => {
        this.salas.set(lista);
        this.carregando.set(false);
      },
      error: falha => {
        this.carregando.set(false);
        this.mensagens.erro(falha);
      }
    });
  }
}
