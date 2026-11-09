import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { TokenResponse } from '../models/usuario.model';
import { AuthService } from './auth.service';

const RESPOSTA: TokenResponse = {
  token: 'token-jwt',
  tipo: 'Bearer',
  expiraEm: '2030-01-01T00:00:00Z',
  usuario: { id: 'u1', nome: 'Ana Souza', email: 'ana@senac.br', perfil: 'PROFESSOR', professorId: 'p1', ativo: true }
};

describe('AuthService', () => {
  let servico: AuthService;
  let http: HttpTestingController;

  beforeEach(() => {
    sessionStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([{ path: 'login', children: [] }])]
    });
    servico = TestBed.inject(AuthService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('começa deslogado', () => {
    expect(servico.autenticado()).toBe(false);
    expect(servico.token).toBeNull();
  });

  it('guarda token e usuário depois do login', () => {
    servico.login({ email: 'ana@senac.br', senha: 'Senha123' }).subscribe();
    const req = http.expectOne('/api/auth/login');
    expect(req.request.method).toBe('POST');
    req.flush(RESPOSTA);

    expect(servico.autenticado()).toBe(true);
    expect(servico.usuario()?.nome).toBe('Ana Souza');
    expect(sessionStorage.getItem('alocacao.token')).toBe('token-jwt');
  });

  it('confere o perfil do usuário logado', () => {
    servico.login({ email: 'ana@senac.br', senha: 'Senha123' }).subscribe();
    http.expectOne('/api/auth/login').flush(RESPOSTA);

    expect(servico.possuiPerfil('PROFESSOR', 'COORDENADOR')).toBe(true);
    expect(servico.possuiPerfil('ADMIN')).toBe(false);
  });

  it('apaga a sessão ao sair', () => {
    servico.login({ email: 'ana@senac.br', senha: 'Senha123' }).subscribe();
    http.expectOne('/api/auth/login').flush(RESPOSTA);

    servico.sair();

    expect(servico.autenticado()).toBe(false);
    expect(sessionStorage.getItem('alocacao.token')).toBeNull();
  });
});
