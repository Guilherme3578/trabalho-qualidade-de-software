import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';
import { LucideAngularModule } from 'lucide-angular';
import { PERFIS, Perfil } from '../../models/usuario.model';
import { AuthService } from '../../services/auth.service';
import { NotificacaoService } from '../../services/notificacao.service';

interface ItemMenu {
  rota: string;
  rotulo: string;
  icone: string;
  perfis?: Perfil[];
}

interface GrupoMenu {
  titulo: string;
  itens: ItemMenu[];
}

const MENU: GrupoMenu[] = [
  {
    titulo: 'Geral',
    itens: [
      { rota: '/inicio', rotulo: 'Início', icone: 'layout-dashboard' },
      { rota: '/disponibilidade', rotulo: 'Disponibilidade', icone: 'search' },
      { rota: '/reservas/nova', rotulo: 'Nova reserva', icone: 'calendar-plus', perfis: ['PROFESSOR', 'COORDENADOR'] },
      { rota: '/reservas', rotulo: 'Reservas', icone: 'calendar-days' },
      { rota: '/aprovacoes', rotulo: 'Aprovações', icone: 'clipboard-check', perfis: ['RESPONSAVEL'] }
    ]
  },
  {
    titulo: 'Cadastros',
    itens: [
      { rota: '/salas', rotulo: 'Salas', icone: 'door-open', perfis: ['ADMIN'] },
      { rota: '/professores', rotulo: 'Professores', icone: 'graduation-cap', perfis: ['ADMIN'] },
      { rota: '/materiais', rotulo: 'Materiais', icone: 'package', perfis: ['ADMIN'] },
      { rota: '/manutencoes', rotulo: 'Manutenções', icone: 'wrench', perfis: ['ADMIN'] },
      { rota: '/usuarios', rotulo: 'Usuários', icone: 'user-cog', perfis: ['ADMIN'] }
    ]
  },
  {
    titulo: 'Gestão',
    itens: [
      { rota: '/relatorios', rotulo: 'Relatórios', icone: 'chart-column', perfis: ['COORDENADOR', 'RESPONSAVEL', 'ADMIN'] },
      { rota: '/auditoria', rotulo: 'Auditoria', icone: 'history', perfis: ['RESPONSAVEL', 'ADMIN'] }
    ]
  }
];

@Component({
  selector: 'app-layout',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, LucideAngularModule],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.css'
})
export class LayoutComponent implements OnInit {
  protected readonly auth = inject(AuthService);
  protected readonly notificacoes = inject(NotificacaoService);
  private readonly router = inject(Router);

  protected readonly menuAberto = signal(false);
  protected readonly perfis = PERFIS;

  protected readonly grupos = computed(() =>
    MENU.map(grupo => ({
      titulo: grupo.titulo,
      itens: grupo.itens.filter(item => !item.perfis || this.auth.possuiPerfil(...item.perfis))
    })).filter(grupo => grupo.itens.length > 0)
  );

  protected readonly iniciais = computed(() => {
    const nome = this.auth.usuario()?.nome ?? '';
    return nome.split(' ').filter(Boolean).slice(0, 2).map(p => p[0].toUpperCase()).join('');
  });

  ngOnInit(): void {
    this.notificacoes.carregar().subscribe({ error: () => undefined });
    this.router.events.pipe(filter(e => e instanceof NavigationEnd)).subscribe(() => this.menuAberto.set(false));
  }

  protected alternarMenu(): void {
    this.menuAberto.update(aberto => !aberto);
  }
}
