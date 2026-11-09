import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { LucideAngularModule } from 'lucide-angular';
import { CabecalhoPaginaComponent } from '../../components/cabecalho-pagina/cabecalho-pagina.component';
import { DialogoComponent } from '../../components/dialogo/dialogo.component';
import { EstadoVazioComponent } from '../../components/estado-vazio/estado-vazio.component';
import { Professor } from '../../models/professor.model';
import { PERFIS, Perfil, Usuario } from '../../models/usuario.model';
import { AuthService } from '../../services/auth.service';
import { MensagemService } from '../../services/mensagem.service';
import { ProfessorService } from '../../services/professor.service';
import { UsuarioService } from '../../services/usuario.service';

@Component({
  selector: 'app-usuarios',
  imports: [FormsModule, ReactiveFormsModule, LucideAngularModule, CabecalhoPaginaComponent, DialogoComponent, EstadoVazioComponent],
  templateUrl: './usuarios.component.html',
  styleUrl: './usuarios.component.css'
})
export class UsuariosComponent implements OnInit {
  private readonly servico = inject(UsuarioService);
  private readonly professorService = inject(ProfessorService);
  private readonly mensagens = inject(MensagemService);
  protected readonly idAtual = inject(AuthService).usuario()?.id;

  protected readonly perfis = Object.entries(PERFIS) as [Perfil, string][];
  protected readonly rotulosPerfil = PERFIS;
  protected readonly usuarios = signal<Usuario[]>([]);
  protected readonly professores = signal<Professor[]>([]);
  protected readonly carregando = signal(true);
  protected readonly busca = signal('');
  protected readonly dialogoAberto = signal(false);
  protected readonly salvando = signal(false);

  protected readonly filtrados = computed(() => {
    const termo = this.busca().trim().toLowerCase();
    return this.usuarios().filter(u => !termo || `${u.nome} ${u.email} ${PERFIS[u.perfil]}`.toLowerCase().includes(termo));
  });

  protected readonly formulario = inject(FormBuilder).nonNullable.group({
    nome: ['', [Validators.required, Validators.maxLength(120)]],
    email: ['', [Validators.required, Validators.email]],
    senha: ['', [Validators.required, Validators.minLength(8), Validators.pattern(/^(?=.*[A-Za-z])(?=.*\d).+$/)]],
    perfil: ['PROFESSOR' as Perfil, Validators.required],
    professorId: ['']
  });

  ngOnInit(): void {
    forkJoin({ usuarios: this.servico.listar(), professores: this.professorService.listar() }).subscribe({
      next: dados => {
        this.usuarios.set(dados.usuarios);
        this.professores.set(dados.professores);
        this.carregando.set(false);
      },
      error: falha => {
        this.carregando.set(false);
        this.mensagens.erro(falha);
      }
    });
  }

  protected novo(): void {
    this.formulario.reset();
    this.dialogoAberto.set(true);
  }

  protected nomeProfessor(id: string | null): string {
    return this.professores().find(p => p.id === id)?.nome ?? '';
  }

  protected salvar(): void {
    const v = this.formulario.getRawValue();
    if (this.formulario.invalid || (v.perfil === 'PROFESSOR' && !v.professorId)) {
      this.formulario.markAllAsTouched();
      return;
    }
    this.salvando.set(true);
    this.servico
      .criar({
        nome: v.nome.trim(),
        email: v.email.trim(),
        senha: v.senha,
        perfil: v.perfil,
        professorId: v.perfil === 'PROFESSOR' ? v.professorId : null
      })
      .subscribe({
        next: usuario => {
          this.salvando.set(false);
          this.dialogoAberto.set(false);
          this.usuarios.update(lista => [...lista, usuario].sort((a, b) => a.nome.localeCompare(b.nome)));
          this.mensagens.sucesso('Usuário cadastrado.');
        },
        error: falha => {
          this.salvando.set(false);
          this.mensagens.erro(falha);
        }
      });
  }

  protected alternarAtivo(usuario: Usuario): void {
    this.servico.definirAtivo(usuario.id, !usuario.ativo).subscribe({
      next: atualizado => {
        this.usuarios.update(lista => lista.map(u => (u.id === atualizado.id ? atualizado : u)));
        this.mensagens.sucesso(atualizado.ativo ? 'Usuário reativado.' : 'Usuário desativado. O acesso dele foi bloqueado.');
      },
      error: falha => this.mensagens.erro(falha)
    });
  }
}
