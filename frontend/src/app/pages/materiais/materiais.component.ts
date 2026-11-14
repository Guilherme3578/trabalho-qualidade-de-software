import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { LucideAngularModule } from 'lucide-angular';
import { CabecalhoPaginaComponent } from '../../components/cabecalho-pagina/cabecalho-pagina.component';
import { DialogoComponent } from '../../components/dialogo/dialogo.component';
import { EstadoVazioComponent } from '../../components/estado-vazio/estado-vazio.component';
import { Material } from '../../models/material.model';
import { MaterialService } from '../../services/material.service';
import { MensagemService } from '../../services/mensagem.service';

@Component({
  selector: 'app-materiais',
  imports: [FormsModule, ReactiveFormsModule, LucideAngularModule, CabecalhoPaginaComponent, DialogoComponent, EstadoVazioComponent],
  templateUrl: './materiais.component.html',
  styleUrl: './materiais.component.css'
})
export class MateriaisComponent implements OnInit {
  private readonly servico = inject(MaterialService);
  private readonly mensagens = inject(MensagemService);

  protected readonly materiais = signal<Material[]>([]);
  protected readonly carregando = signal(true);
  protected readonly busca = signal('');
  protected readonly editando = signal<Material | null>(null);
  protected readonly dialogoAberto = signal(false);
  protected readonly salvando = signal(false);

  protected readonly filtrados = computed(() => {
    const termo = this.busca().trim().toLowerCase();
    return this.materiais().filter(
      m => !termo || `${m.nome} ${m.categoria} ${m.localizacao ?? ''} ${m.patrimonio ?? ''}`.toLowerCase().includes(termo)
    );
  });

  protected readonly formulario = inject(FormBuilder).nonNullable.group({
    nome: ['', [Validators.required, Validators.maxLength(120)]],
    categoria: ['', [Validators.required, Validators.maxLength(60)]],
    patrimonio: [''],
    quantidadeTotal: [1, [Validators.required, Validators.min(1)]],
    localizacao: [''],
    restrito: [false]
  });

  ngOnInit(): void {
    this.carregar();
  }

  protected novo(): void {
    this.editando.set(null);
    this.formulario.reset();
    this.dialogoAberto.set(true);
  }

  protected editar(material: Material): void {
    this.editando.set(material);
    this.formulario.setValue({
      nome: material.nome,
      categoria: material.categoria,
      patrimonio: material.patrimonio ?? '',
      quantidadeTotal: material.quantidadeTotal,
      localizacao: material.localizacao ?? '',
      restrito: material.restrito
    });
    this.dialogoAberto.set(true);
  }

  protected salvar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    const v = this.formulario.getRawValue();
    const dados = { ...v, patrimonio: v.patrimonio.trim() || null };
    const atual = this.editando();
    this.salvando.set(true);
    (atual ? this.servico.atualizar(atual.id, dados) : this.servico.criar(dados)).subscribe({
      next: () => {
        this.salvando.set(false);
        this.dialogoAberto.set(false);
        this.mensagens.sucesso(atual ? 'Material atualizado.' : 'Material cadastrado.');
        this.carregar();
      },
      error: falha => {
        this.salvando.set(false);
        this.mensagens.erro(falha);
      }
    });
  }

  protected alternarAtivo(material: Material): void {
    this.servico.definirAtivo(material.id, !material.ativo).subscribe({
      next: atualizado => {
        this.materiais.update(lista => lista.map(m => (m.id === atualizado.id ? atualizado : m)));
        if (atualizado.ativo) {
          this.mensagens.sucesso('Material reativado.');
        } else {
          this.mensagens.desativacao('Material desativado.', atualizado.reservasAfetadas);
        }
      },
      error: falha => this.mensagens.erro(falha)
    });
  }

  private carregar(): void {
    this.servico.listar().subscribe({
      next: lista => {
        this.materiais.set(lista);
        this.carregando.set(false);
      },
      error: falha => {
        this.carregando.set(false);
        this.mensagens.erro(falha);
      }
    });
  }
}
