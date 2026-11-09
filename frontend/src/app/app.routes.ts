import { Routes } from '@angular/router';
import { LayoutComponent } from './components/layout/layout.component';
import { authGuard } from './guards/auth.guard';
import { perfilGuard } from './guards/perfil.guard';
import { visitanteGuard } from './guards/visitante.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login.component').then(m => m.LoginComponent),
    canActivate: [visitanteGuard],
    title: 'Entrar · AlocaSENAC'
  },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'inicio', pathMatch: 'full' },
      {
        path: 'inicio',
        loadComponent: () => import('./pages/inicio/inicio.component').then(m => m.InicioComponent),
        title: 'Início · AlocaSENAC'
      },
      {
        path: 'disponibilidade',
        loadComponent: () => import('./pages/disponibilidade/disponibilidade.component').then(m => m.DisponibilidadeComponent),
        title: 'Disponibilidade · AlocaSENAC'
      },
      {
        path: 'reservas',
        loadComponent: () => import('./pages/reservas/reservas.component').then(m => m.ReservasComponent),
        title: 'Reservas · AlocaSENAC'
      },
      {
        path: 'reservas/nova',
        loadComponent: () => import('./pages/reserva-form/reserva-form.component').then(m => m.ReservaFormComponent),
        canActivate: [perfilGuard],
        data: { perfis: ['PROFESSOR', 'COORDENADOR'] },
        title: 'Nova reserva · AlocaSENAC'
      },
      {
        path: 'reservas/:id/editar',
        loadComponent: () => import('./pages/reserva-form/reserva-form.component').then(m => m.ReservaFormComponent),
        canActivate: [perfilGuard],
        data: { perfis: ['PROFESSOR', 'COORDENADOR'] },
        title: 'Alterar reserva · AlocaSENAC'
      },
      {
        path: 'reservas/:id',
        loadComponent: () => import('./pages/reserva-detalhe/reserva-detalhe.component').then(m => m.ReservaDetalheComponent),
        title: 'Reserva · AlocaSENAC'
      },
      {
        path: 'aprovacoes',
        loadComponent: () => import('./pages/aprovacoes/aprovacoes.component').then(m => m.AprovacoesComponent),
        canActivate: [perfilGuard],
        data: { perfis: ['RESPONSAVEL'] },
        title: 'Aprovações · AlocaSENAC'
      },
      {
        path: 'notificacoes',
        loadComponent: () => import('./pages/notificacoes/notificacoes.component').then(m => m.NotificacoesComponent),
        title: 'Notificações · AlocaSENAC'
      },
      {
        path: 'relatorios',
        loadComponent: () => import('./pages/relatorios/relatorios.component').then(m => m.RelatoriosComponent),
        canActivate: [perfilGuard],
        data: { perfis: ['COORDENADOR', 'RESPONSAVEL', 'ADMIN'] },
        title: 'Relatórios · AlocaSENAC'
      },
      {
        path: 'auditoria',
        loadComponent: () => import('./pages/auditoria/auditoria.component').then(m => m.AuditoriaComponent),
        canActivate: [perfilGuard],
        data: { perfis: ['RESPONSAVEL', 'ADMIN'] },
        title: 'Auditoria · AlocaSENAC'
      },
      {
        path: '',
        canActivate: [perfilGuard],
        data: { perfis: ['ADMIN'] },
        children: [
          {
            path: 'salas',
            loadComponent: () => import('./pages/salas/salas.component').then(m => m.SalasComponent),
            title: 'Salas · AlocaSENAC'
          },
          {
            path: 'professores',
            loadComponent: () => import('./pages/professores/professores.component').then(m => m.ProfessoresComponent),
            title: 'Professores · AlocaSENAC'
          },
          {
            path: 'materiais',
            loadComponent: () => import('./pages/materiais/materiais.component').then(m => m.MateriaisComponent),
            title: 'Materiais · AlocaSENAC'
          },
          {
            path: 'manutencoes',
            loadComponent: () => import('./pages/manutencoes/manutencoes.component').then(m => m.ManutencoesComponent),
            title: 'Manutenções · AlocaSENAC'
          },
          {
            path: 'usuarios',
            loadComponent: () => import('./pages/usuarios/usuarios.component').then(m => m.UsuariosComponent),
            title: 'Usuários · AlocaSENAC'
          }
        ]
      }
    ]
  },
  { path: '**', redirectTo: '' }
];
