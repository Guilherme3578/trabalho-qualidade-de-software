import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { LoginRequest, Perfil, TokenResponse, Usuario } from '../models/usuario.model';

const CHAVE_TOKEN = 'alocacao.token';
const CHAVE_USUARIO = 'alocacao.usuario';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  private readonly tokenAtual = signal<string | null>(sessionStorage.getItem(CHAVE_TOKEN));
  private readonly usuarioAtual = signal<Usuario | null>(this.lerUsuario());

  readonly usuario = this.usuarioAtual.asReadonly();
  readonly autenticado = computed(() => this.tokenAtual() !== null && this.usuarioAtual() !== null);

  get token(): string | null {
    return this.tokenAtual();
  }

  login(dados: LoginRequest): Observable<TokenResponse> {
    return this.http.post<TokenResponse>('/api/auth/login', dados).pipe(
      tap(resposta => {
        sessionStorage.setItem(CHAVE_TOKEN, resposta.token);
        sessionStorage.setItem(CHAVE_USUARIO, JSON.stringify(resposta.usuario));
        this.tokenAtual.set(resposta.token);
        this.usuarioAtual.set(resposta.usuario);
      })
    );
  }

  sair(): void {
    sessionStorage.removeItem(CHAVE_TOKEN);
    sessionStorage.removeItem(CHAVE_USUARIO);
    this.tokenAtual.set(null);
    this.usuarioAtual.set(null);
    this.router.navigate(['/login']);
  }

  possuiPerfil(...perfis: Perfil[]): boolean {
    const usuario = this.usuarioAtual();
    return usuario !== null && perfis.includes(usuario.perfil);
  }

  private lerUsuario(): Usuario | null {
    const salvo = sessionStorage.getItem(CHAVE_USUARIO);
    if (!salvo) {
      return null;
    }
    try {
      return JSON.parse(salvo) as Usuario;
    } catch {
      return null;
    }
  }
}
