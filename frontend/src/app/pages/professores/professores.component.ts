import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { LucideAngularModule } from 'lucide-angular';
import { CabecalhoPaginaComponent } from '../../components/cabecalho-pagina/cabecalho-pagina.component';
import { DialogoComponent } from '../../components/dialogo/dialogo.component';
import { EstadoVazioComponent } from '../../components/estado-vazio/estado-vazio.component';
import { Professor } from '../../models/professor.model';
import { MensagemService } from '../../services/mensagem.service';
import { ProfessorService } from '../../services/professor.service';

@Component({
  selector: 'app-professores',
  imports: [FormsModule, ReactiveFormsModule, LucideAngularModule, CabecalhoPaginaComponent, DialogoComponent, EstadoVazioComponent],
  templateUrl: './professores.component.html',
  styleUrl: './professores.component.css'
})
export class ProfessoresComponent implements OnInit {
  private readonly servico = inject(ProfessorService);
  private readonly mensagens = inject(MensagemService);

  protected readonly professores = signal<Professor[]>([]);
  protected readonly carregando = signal(true);
  protected readonly busca = signal('');
  protected readonly editando = signal<Professor | null>(null);
  protected readonly dialogoAberto = signal(false);
  protected readonly salvando = signal(false);

  protected readonly filtrados = computed(() => {
    const termo = this.busca().trim().toLowerCase();
    return this.professores().filter(
      p => !termo || `${p.nome} ${p.email} ${p.departamento} ${p.competencias.join(' ')}`.toLowerCase().includes(termo)
    );
  });

  protected readonly formulario = inject(FormBuilder).nonNullable.group({
    nome: ['', [Validators.required, Validators.maxLength(120)]],
    email: ['', [Validators.required, Validators.email]],
    departamento: [''],
    competencias: ['']
  });

  ngOnInit(): void {
    this.carregar();
  }

  protected novo(): void {
    this.editando.set(null);
    this.formulario.reset();
    this.dialogoAberto.set(true);
  }

  protected editar(professor: Professor): void {
    this.editando.set(professor);
    this.formulario.setValue({
      nome: professor.nome,
      email: professor.email,
      departamento: professor.departamento ?? '',
      competencias: professor.competencias.join(', ')
    });
    this.dialogoAberto.set(true);
  }

  protected salvar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    const v = this.formulario.getRawValue();
    const dados = {
      nome: v.nome.trim(),
      email: v.email.trim(),
      departamento: v.departamento.trim(),
      competencias: v.competencias.split(',').map(c => c.trim()).filter(Boolean)
    };
    const atual = this.editando();
    this.salvando.set(true);
    (atual ? this.servico.atualizar(atual.id, dados) : this.servico.criar(dados)).subscribe({
      next: () => {
        this.salvando.set(false);
        this.dialogoAberto.set(false);
        this.mensagens.sucesso(atual ? 'Professor atualizado.' : 'Professor cadastrado.');
        this.carregar();
      },
      error: falha => {
        this.salvando.set(false);
        this.mensagens.erro(falha);
      }
    });
  }

  protected alternarAtivo(professor: Professor): void {
    this.servico.definirAtivo(professor.id, !professor.ativo).subscribe({
      next: atualizado => {
        this.professores.update(lista => lista.map(p => (p.id === atualizado.id ? atualizado : p)));
        if (atualizado.ativo) {
          this.mensagens.sucesso('Professor reativado.');
        } else {
          this.mensagens.desativacao('Professor desativado.', atualizado.reservasAfetadas);
        }
      },
      error: falha => this.mensagens.erro(falha)
    });
  }

  private carregar(): void {
    this.servico.listar().subscribe({
      next: lista => {
        this.professores.set(lista);
        this.carregando.set(false);
      },
      error: falha => {
        this.carregando.set(false);
        this.mensagens.erro(falha);
      }
    });
  }
}
