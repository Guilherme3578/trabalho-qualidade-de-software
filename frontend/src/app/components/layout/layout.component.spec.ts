import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { authFalso, provedoresDeTela } from '../../../testing/apoio';
import { Perfil } from '../../models/usuario.model';
import { AuthService } from '../../services/auth.service';
import { MensagemService } from '../../services/mensagem.service';
import { NotificacaoService } from '../../services/notificacao.service';
import { LoginComponent } from '../../pages/login/login.component';
import { NotificacoesComponent } from '../../pages/notificacoes/notificacoes.component';
import { CabecalhoPaginaComponent } from '../cabecalho-pagina/cabecalho-pagina.component';
import { DialogoComponent } from '../dialogo/dialogo.component';
import { EstadoVazioComponent } from '../estado-vazio/estado-vazio.component';
import { MensagensComponent } from '../mensagens/mensagens.component';
import { LayoutComponent } from './layout.component';

function notificacoesFalsas() {
  return {
    notificacoes: () => [],
    naoLidas: () => 0,
    carregar: vi.fn(() => of([])),
    marcarLida: vi.fn(() => of({}))
  };
}

describe('LayoutComponent', () => {
  function montar(perfil: Perfil, nome = 'Ana Maria Souza') {
    const notificacoes = notificacoesFalsas();
    TestBed.configureTestingModule({
      providers: [...provedoresDeTela(perfil, { nome }), { provide: NotificacaoService, useValue: notificacoes }]
    });
    const fixture = TestBed.createComponent(LayoutComponent);
    fixture.detectChanges();
    return { tela: fixture.componentInstance as any, notificacoes };
  }

  it.each<[Perfil, string[]]>([
    ['PROFESSOR', ['/inicio', '/disponibilidade', '/reservas/nova', '/reservas']],
    ['RESPONSAVEL', ['/inicio', '/disponibilidade', '/reservas', '/aprovacoes', '/relatorios', '/auditoria']],
    ['ADMIN', ['/inicio', '/disponibilidade', '/reservas', '/salas', '/professores', '/materiais', '/manutencoes', '/usuarios', '/relatorios', '/auditoria']]
  ])('mostra no menu só o que %s pode acessar [RF01]', (perfil, rotas) => {
    const { tela } = montar(perfil);
    const visiveis = tela.grupos().flatMap((g: { itens: { rota: string }[] }) => g.itens.map(i => i.rota));
    expect(visiveis).toEqual(rotas);
  });

  it('esconde grupos sem nenhum item permitido', () => {
    const { tela } = montar('PROFESSOR');
    expect(tela.grupos().length).toBe(1);
  });

  it('mostra as iniciais do nome e carrega as notificações', () => {
    const { tela, notificacoes } = montar('ADMIN');
    expect(tela.iniciais()).toBe('AM');
    expect(notificacoes.carregar).toHaveBeenCalled();
  });

  it('fecha o menu do celular ao navegar', async () => {
    const { tela } = montar('ADMIN');
    tela.alternarMenu();
    expect(tela.menuAberto()).toBe(true);
    await TestBed.inject(Router).navigateByUrl('/qualquer');
    expect(tela.menuAberto()).toBe(false);
  });
});

describe('NotificacoesComponent', () => {
  function montar(notificacoes = notificacoesFalsas()) {
    TestBed.configureTestingModule({
      providers: [...provedoresDeTela('PROFESSOR'), { provide: NotificacaoService, useValue: notificacoes }]
    });
    const fixture = TestBed.createComponent(NotificacoesComponent);
    fixture.detectChanges();
    return { tela: fixture.componentInstance as any, notificacoes, mensagens: TestBed.inject(MensagemService) };
  }

  it('carrega e marca como lida só o que ainda não foi lido [RF11]', () => {
    const { tela, notificacoes } = montar();
    expect(tela.carregando()).toBe(false);
    tela.marcarLida({ id: 'n1', lida: true });
    expect(notificacoes.marcarLida).not.toHaveBeenCalled();
    tela.marcarLida({ id: 'n2', lida: false });
    expect(notificacoes.marcarLida).toHaveBeenCalledWith('n2');
  });

  it('escolhe o ícone pelo tipo da notificação', () => {
    const { tela } = montar();
    expect(tela.icone({ tipo: 'RESERVA_APROVADA' })).toBe('circle-check');
    expect(tela.icone({ tipo: 'DESCONHECIDO' })).toBe('bell');
  });

  it('mostra os erros da API', () => {
    const falsas = notificacoesFalsas();
    falsas.carregar.mockReturnValue(throwError(() => new Error('500')));
    falsas.marcarLida.mockReturnValue(throwError(() => new Error('500')));
    const { tela, mensagens } = montar(falsas);
    tela.marcarLida({ id: 'n2', lida: false });
    expect(tela.carregando()).toBe(false);
    expect(mensagens.mensagens().length).toBe(2);
  });
});

describe('LoginComponent', () => {
  function montar(login: ReturnType<typeof vi.fn>) {
    TestBed.configureTestingModule({
      providers: [...provedoresDeTela('PROFESSOR'), { provide: AuthService, useValue: { ...authFalso('PROFESSOR'), login } }]
    });
    const fixture = TestBed.createComponent(LoginComponent);
    fixture.detectChanges();
    return fixture.componentInstance as any;
  }

  it('não envia sem e-mail válido e senha', () => {
    const login = vi.fn();
    const tela = montar(login);
    tela.formulario.setValue({ email: 'invalido', senha: '' });
    tela.entrar();
    expect(login).not.toHaveBeenCalled();
    expect(tela.formulario.touched).toBe(true);
  });

  it('preenche um usuário de demonstração e entra [RF01]', () => {
    const login = vi.fn(() => of({}));
    const tela = montar(login);
    const navegar = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    tela.usarDemo(tela.demos[3]);
    tela.entrar();
    expect(login).toHaveBeenCalledWith({ email: 'admin@senac.br', senha: 'Senha123' });
    expect(navegar).toHaveBeenCalledWith(['/inicio']);
  });

  it('mostra a mensagem da API quando o login falha', () => {
    const falha = new HttpErrorResponse({ status: 401, error: { detail: 'E-mail ou senha inválidos.' } });
    const tela = montar(vi.fn(() => throwError(() => falha)));
    tela.formulario.setValue({ email: 'ana@senac.br', senha: 'errada' });
    tela.entrar();
    expect(tela.enviando()).toBe(false);
    expect(tela.erro()).toBe('E-mail ou senha inválidos.');
  });
});

describe('Componentes de apresentação', () => {
  beforeEach(() => TestBed.configureTestingModule({ providers: provedoresDeTela('PROFESSOR') }));

  it('diálogo fecha com Esc', () => {
    const fixture = TestBed.createComponent(DialogoComponent);
    fixture.componentRef.setInput('titulo', 'Confirmar');
    let fechou = false;
    fixture.componentInstance.fechar.subscribe(() => (fechou = true));
    fixture.detectChanges();
    document.dispatchEvent(new KeyboardEvent('keydown', { key: 'Escape' }));
    expect(fechou).toBe(true);
  });

  it('cabeçalho e estado vazio mostram o título', () => {
    const cabecalho = TestBed.createComponent(CabecalhoPaginaComponent);
    cabecalho.componentRef.setInput('titulo', 'Reservas');
    cabecalho.componentRef.setInput('icone', 'calendar-days');
    cabecalho.detectChanges();
    expect(cabecalho.nativeElement.textContent).toContain('Reservas');
    const vazio = TestBed.createComponent(EstadoVazioComponent);
    vazio.componentRef.setInput('titulo', 'Nada por aqui');
    vazio.detectChanges();
    expect(vazio.nativeElement.textContent).toContain('Nada por aqui');
  });

  it('lista as mensagens do serviço', () => {
    TestBed.inject(MensagemService).sucesso('Salvo.');
    const fixture = TestBed.createComponent(MensagensComponent);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Salvo.');
  });
});
