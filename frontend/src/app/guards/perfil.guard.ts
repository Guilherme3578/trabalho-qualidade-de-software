import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Perfil } from '../models/usuario.model';
import { AuthService } from '../services/auth.service';

export const perfilGuard: CanActivateFn = route => {
  const perfis = (route.data['perfis'] ?? []) as Perfil[];
  const auth = inject(AuthService);
  return perfis.length === 0 || auth.possuiPerfil(...perfis) ? true : inject(Router).createUrlTree(['/inicio']);
};
