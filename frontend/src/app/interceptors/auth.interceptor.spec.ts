import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { AuthService } from '../services/auth.service';
import { authInterceptor } from './auth.interceptor';

describe('authInterceptor', () => {
  let http: HttpClient;
  let controle: HttpTestingController;
  const authFalso = { token: 'meu-token' as string | null, sair: vi.fn() };

  beforeEach(() => {
    authFalso.token = 'meu-token';
    authFalso.sair.mockClear();
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: authFalso }
      ]
    });
    http = TestBed.inject(HttpClient);
    controle = TestBed.inject(HttpTestingController);
  });

  afterEach(() => controle.verify());

  it('envia o token nas chamadas para a API', () => {
    http.get('/api/salas').subscribe();
    const req = controle.expectOne('/api/salas');
    expect(req.request.headers.get('Authorization')).toBe('Bearer meu-token');
    req.flush([]);
  });

  it('não envia token para endereços fora da API', () => {
    http.get('/assets/logo.svg').subscribe();
    const req = controle.expectOne('/assets/logo.svg');
    expect(req.request.headers.has('Authorization')).toBe(false);
    req.flush('');
  });

  it('não envia cabeçalho quando não há sessão', () => {
    authFalso.token = null;
    http.get('/api/salas').subscribe();
    const req = controle.expectOne('/api/salas');
    expect(req.request.headers.has('Authorization')).toBe(false);
    req.flush([]);
  });

  it('faz logout quando a API responde 401', () => {
    http.get('/api/reservas').subscribe({ error: () => undefined });
    controle.expectOne('/api/reservas').flush({}, { status: 401, statusText: 'Unauthorized' });
    expect(authFalso.sair).toHaveBeenCalledTimes(1);
  });

  it('não faz logout quando o próprio login falha', () => {
    http.post('/api/auth/login', {}).subscribe({ error: () => undefined });
    controle.expectOne('/api/auth/login').flush({}, { status: 401, statusText: 'Unauthorized' });
    expect(authFalso.sair).not.toHaveBeenCalled();
  });

  it('não faz logout em outros erros, como 403', () => {
    http.get('/api/usuarios').subscribe({ error: () => undefined });
    controle.expectOne('/api/usuarios').flush({}, { status: 403, statusText: 'Forbidden' });
    expect(authFalso.sair).not.toHaveBeenCalled();
  });
});
