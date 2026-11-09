import { Routes } from '@angular/router';
import { LayoutComponent } from './components/layout/layout.component';
import { authGuard } from './guards/auth.guard';
import { visitanteGuard } from './guards/visitante.guard';
import { LoginComponent } from './pages/login/login.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent, canActivate: [visitanteGuard], title: 'Entrar · AlocaSENAC' },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: []
  },
  { path: '**', redirectTo: '' }
];
