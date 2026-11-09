import { Routes } from '@angular/router';
import { LayoutComponent } from './components/layout/layout.component';
import { authGuard } from './guards/auth.guard';
import { visitanteGuard } from './guards/visitante.guard';
import { AprovacoesComponent } from './pages/aprovacoes/aprovacoes.component';
import { DisponibilidadeComponent } from './pages/disponibilidade/disponibilidade.component';
import { InicioComponent } from './pages/inicio/inicio.component';
import { LoginComponent } from './pages/login/login.component';
import { ReservaDetalheComponent } from './pages/reserva-detalhe/reserva-detalhe.component';
import { ReservaFormComponent } from './pages/reserva-form/reserva-form.component';
import { ReservasComponent } from './pages/reservas/reservas.component';
import { perfilGuard } from './guards/perfil.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent, canActivate: [visitanteGuard], title: 'Entrar · AlocaSENAC' },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'inicio', pathMatch: 'full' },
      { path: 'inicio', component: InicioComponent, title: 'Início · AlocaSENAC' },
      { path: 'disponibilidade', component: DisponibilidadeComponent, title: 'Disponibilidade · AlocaSENAC' },
      { path: 'reservas', component: ReservasComponent, title: 'Reservas · AlocaSENAC' },
      {
        path: 'aprovacoes',
        component: AprovacoesComponent,
        canActivate: [perfilGuard],
        data: { perfis: ['RESPONSAVEL'] },
        title: 'Aprovações · AlocaSENAC'
      },
      {
        path: 'reservas/nova',
        component: ReservaFormComponent,
        canActivate: [perfilGuard],
        data: { perfis: ['PROFESSOR', 'COORDENADOR'] },
        title: 'Nova reserva · AlocaSENAC'
      },
      {
        path: 'reservas/:id/editar',
        component: ReservaFormComponent,
        canActivate: [perfilGuard],
        data: { perfis: ['PROFESSOR', 'COORDENADOR'] },
        title: 'Alterar reserva · AlocaSENAC'
      },
      { path: 'reservas/:id', component: ReservaDetalheComponent, title: 'Reserva · AlocaSENAC' }
    ]
  },
  { path: '**', redirectTo: '' }
];
