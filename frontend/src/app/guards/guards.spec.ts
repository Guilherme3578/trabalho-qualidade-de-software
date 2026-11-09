import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree, provideRouter } from '@angular/router';
import { Perfil } from '../models/usuario.model';
import { AuthService } from '../services/auth.service';
import { authGuard } from './auth.guard';
import { perfilGuard } from './perfil.guard';
import { visitanteGuard } from './visitante.guard';

describe('guards', () => {
  let logado = false;
  let perfil: Perfil = 'PROFESSOR';

  const authFalso = {
    autenticado: () => logado,
    possuiPerfil: (...perfis: Perfil[]) => logado && perfis.includes(perfil)
  };

  const executar = (guarda: typeof authGuard, perfis: Perfil[] = []) =>
    TestBed.runInInjectionContext(() =>
      guarda({ data: { perfis } } as unknown as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
    );

  const destino = (resultado: unknown) => TestBed.inject(Router).serializeUrl(resultado as UrlTree);

  beforeEach(() => {
    logado = false;
    perfil = 'PROFESSOR';
    TestBed.configureTestingModule({
      providers: [provideRouter([]), { provide: AuthService, useValue: authFalso }]
    });
  });

  it('authGuard manda para o login quem não está autenticado', () => {
    expect(destino(executar(authGuard))).toBe('/login');
  });

  it('authGuard libera quem está autenticado', () => {
    logado = true;
    expect(executar(authGuard)).toBe(true);
  });

  it('visitanteGuard manda quem já está logado para o início', () => {
    logado = true;
    expect(destino(executar(visitanteGuard))).toBe('/inicio');
  });

  it('visitanteGuard libera o login para quem não está autenticado', () => {
    expect(executar(visitanteGuard)).toBe(true);
  });

  it.each<[Perfil, Perfil[], boolean]>([
    ['RESPONSAVEL', ['RESPONSAVEL'], true],
    ['ADMIN', ['RESPONSAVEL', 'ADMIN'], true],
    ['PROFESSOR', ['ADMIN'], false],
    ['COORDENADOR', ['RESPONSAVEL'], false]
  ])('perfilGuard: %s acessando rota de %j → %s', (atual, exigidos, permitido) => {
    logado = true;
    perfil = atual;
    const resultado = executar(perfilGuard, exigidos);
    if (permitido) {
      expect(resultado).toBe(true);
    } else {
      expect(destino(resultado)).toBe('/inicio');
    }
  });

  it('perfilGuard libera rotas sem perfis definidos', () => {
    logado = true;
    expect(executar(perfilGuard, [])).toBe(true);
  });
});
