import { EnvironmentProviders, Provider, importProvidersFrom, signal } from '@angular/core';
import { provideRouter } from '@angular/router';
import { LucideAngularModule, icons } from 'lucide-angular';
import { Perfil, Usuario } from '../app/models/usuario.model';
import { AuthService } from '../app/services/auth.service';

export function usuarioDeTeste(perfil: Perfil, extras: Partial<Usuario> = {}): Usuario {
  return { id: 'u1', nome: 'Ana Souza', email: 'ana@senac.br', perfil, professorId: perfil === 'PROFESSOR' ? 'p1' : null, ativo: true, ...extras } as Usuario;
}

export function authFalso(perfil: Perfil, extras: Partial<Usuario> = {}) {
  const usuario = signal<Usuario | null>(usuarioDeTeste(perfil, extras));
  return {
    usuario: usuario.asReadonly(),
    autenticado: () => usuario() !== null,
    possuiPerfil: (...perfis: Perfil[]) => usuario() !== null && perfis.includes(usuario()!.perfil),
    sair: () => usuario.set(null)
  };
}

export function provedoresDeTela(perfil: Perfil, extras: Partial<Usuario> = {}): (Provider | EnvironmentProviders)[] {
  return [
    provideRouter([{ path: '**', children: [] }]),
    importProvidersFrom(LucideAngularModule.pick(icons)),
    { provide: AuthService, useValue: authFalso(perfil, extras) }
  ];
}
