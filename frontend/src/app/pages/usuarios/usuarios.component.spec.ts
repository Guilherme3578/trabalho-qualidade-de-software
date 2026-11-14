import { TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { provedoresDeTela } from '../../../testing/apoio';
import { MensagemService } from '../../services/mensagem.service';
import { ProfessorService } from '../../services/professor.service';
import { UsuarioService } from '../../services/usuario.service';
import { UsuariosComponent } from './usuarios.component';

const USUARIOS = [
  { id: 'u1', nome: 'Ana Souza', email: 'ana@senac.br', perfil: 'PROFESSOR', professorId: 'p1', ativo: true },
  { id: 'u2', nome: 'Carlos Admin', email: 'admin@senac.br', perfil: 'ADMIN', professorId: null, ativo: true }
];

describe('UsuariosComponent', () => {
  let servico: Record<string, ReturnType<typeof vi.fn>>;

  function montar() {
    TestBed.configureTestingModule({
      providers: [
        ...provedoresDeTela('ADMIN', { id: 'u2' }),
        { provide: UsuarioService, useValue: servico },
        { provide: ProfessorService, useValue: { listar: vi.fn(() => of([{ id: 'p1', nome: 'Ana Souza' }])) } }
      ]
    });
    const fixture = TestBed.createComponent(UsuariosComponent);
    fixture.detectChanges();
    return { tela: fixture.componentInstance as any, mensagens: TestBed.inject(MensagemService) };
  }

  beforeEach(() => {
    servico = {
      listar: vi.fn(() => of([...USUARIOS])),
      criar: vi.fn((dados: object) => of({ ...dados, id: 'u3', ativo: true })),
      definirAtivo: vi.fn((id: string, ativo: boolean) => of({ ...USUARIOS.find(u => u.id === id), ativo }))
    };
  });

  it('lista usuários, identifica o usuário logado e o professor vinculado', () => {
    const { tela } = montar();
    expect(tela.usuarios().length).toBe(2);
    expect(tela.idAtual).toBe('u2');
    expect(tela.nomeProfessor('p1')).toBe('Ana Souza');
    expect(tela.nomeProfessor(null)).toBe('');
  });

  it.each([
    ['', 2],
    ['ana', 1],
    ['administrador', 1],
    ['@senac.br', 2]
  ])('busca "%s" por nome, e-mail ou perfil', (termo, quantidade) => {
    const { tela } = montar();
    tela.busca.set(termo);
    expect(tela.filtrados().length).toBe(quantidade);
  });

  it.each([
    [{ nome: 'Bia', email: 'bia@senac.br', senha: 'curta1', perfil: 'ADMIN' }],
    [{ nome: 'Bia', email: 'bia@senac.br', senha: 'semnumeros', perfil: 'ADMIN' }],
    [{ nome: 'Bia', email: 'invalido', senha: 'Senha123', perfil: 'ADMIN' }],
    [{ nome: 'Bia', email: 'bia@senac.br', senha: 'Senha123', perfil: 'PROFESSOR', professorId: '' }]
  ])('não cadastra usuário inválido [RNF03]: %o', dados => {
    const { tela } = montar();
    tela.novo();
    tela.formulario.patchValue(dados);
    tela.salvar();
    expect(servico['criar']).not.toHaveBeenCalled();
  });

  it('cadastra e mantém a lista em ordem alfabética, sem professor para quem não é professor', () => {
    const { tela, mensagens } = montar();
    tela.novo();
    expect(tela.dialogoAberto()).toBe(true);
    tela.formulario.patchValue({ nome: ' Bia Coordenadora ', email: 'bia@senac.br', senha: 'Senha123', perfil: 'COORDENADOR', professorId: 'p1' });
    tela.salvar();
    expect(servico['criar']).toHaveBeenCalledWith({ nome: 'Bia Coordenadora', email: 'bia@senac.br', senha: 'Senha123', perfil: 'COORDENADOR', professorId: null });
    expect(tela.usuarios().map((u: { nome: string }) => u.nome)).toEqual(['Ana Souza', 'Bia Coordenadora', 'Carlos Admin']);
    expect(tela.dialogoAberto()).toBe(false);
    expect(mensagens.mensagens()[0].texto).toBe('Usuário cadastrado.');
  });

  it('mostra o erro se o cadastro for recusado', () => {
    servico['criar'].mockReturnValue(throwError(() => new Error('409')));
    const { tela, mensagens } = montar();
    tela.formulario.patchValue({ nome: 'Bia', email: 'bia@senac.br', senha: 'Senha123', perfil: 'ADMIN' });
    tela.salvar();
    expect(tela.salvando()).toBe(false);
    expect(mensagens.mensagens()[0].tipo).toBe('erro');
  });

  it('desativa e reativa usuários [RNF02]', () => {
    const { tela, mensagens } = montar();
    tela.alternarAtivo(USUARIOS[0]);
    expect(servico['definirAtivo']).toHaveBeenCalledWith('u1', false);
    expect(tela.usuarios()[0].ativo).toBe(false);
    expect(mensagens.mensagens()[0].texto).toContain('desativado');
    tela.alternarAtivo(tela.usuarios()[0]);
    expect(mensagens.mensagens()[1].texto).toBe('Usuário reativado.');
  });

  it('mostra os erros da API', () => {
    servico['listar'].mockReturnValue(throwError(() => new Error('500')));
    servico['definirAtivo'].mockReturnValue(throwError(() => new Error('500')));
    const { tela, mensagens } = montar();
    expect(tela.carregando()).toBe(false);
    tela.alternarAtivo(USUARIOS[0]);
    expect(mensagens.mensagens().length).toBe(2);
  });
});
