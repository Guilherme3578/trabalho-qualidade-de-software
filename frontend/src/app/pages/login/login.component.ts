import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';
import { AuthService } from '../../services/auth.service';
import { MensagemService } from '../../services/mensagem.service';

interface UsuarioDemo {
  perfil: string;
  email: string;
  icone: string;
}

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, LucideAngularModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  private readonly auth = inject(AuthService);
  private readonly mensagens = inject(MensagemService);
  private readonly router = inject(Router);

  protected readonly enviando = signal(false);
  protected readonly mostrarSenha = signal(false);
  protected readonly erro = signal<string | null>(null);

  protected readonly demos: UsuarioDemo[] = [
    { perfil: 'Professor', email: 'ana.souza@senac.br', icone: 'graduation-cap' },
    { perfil: 'Coordenador', email: 'coordenador@senac.br', icone: 'users' },
    { perfil: 'Responsável', email: 'responsavel@senac.br', icone: 'clipboard-check' },
    { perfil: 'Administrador', email: 'admin@senac.br', icone: 'shield-check' }
  ];

  protected readonly formulario = inject(FormBuilder).nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    senha: ['', Validators.required]
  });

  protected usarDemo(demo: UsuarioDemo): void {
    this.formulario.setValue({ email: demo.email, senha: 'Senha123' });
    this.erro.set(null);
  }

  protected entrar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    this.enviando.set(true);
    this.erro.set(null);
    this.auth.login(this.formulario.getRawValue()).subscribe({
      next: () => this.router.navigate(['/inicio']),
      error: falha => {
        this.enviando.set(false);
        this.erro.set(this.mensagens.converter(falha).mensagem);
      }
    });
  }
}
