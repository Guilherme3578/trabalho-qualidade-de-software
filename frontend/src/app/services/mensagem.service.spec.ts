import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { MensagemService } from './mensagem.service';

describe('MensagemService', () => {
  let servico: MensagemService;

  beforeEach(() => {
    vi.useFakeTimers();
    servico = TestBed.inject(MensagemService);
  });

  afterEach(() => vi.useRealTimers());

  it('converte o Problem Details da API em mensagem com campos inválidos', () => {
    const erro = new HttpErrorResponse({
      status: 400,
      error: {
        codigo: 'VALIDACAO',
        detail: 'Existem campos inválidos.',
        correlationId: 'abc',
        campos: [{ campo: 'nome', mensagem: 'Informe o nome da sala.' }]
      }
    });

    expect(servico.converter(erro)).toEqual({
      status: 400,
      codigo: 'VALIDACAO',
      mensagem: 'Existem campos inválidos.',
      campos: [{ campo: 'nome', mensagem: 'Informe o nome da sala.' }],
      correlationId: 'abc'
    });
  });

  it('explica quando o servidor está fora do ar', () => {
    const erro = new HttpErrorResponse({ status: 0 });
    expect(servico.converter(erro).codigo).toBe('SEM_CONEXAO');
  });

  it('usa uma mensagem genérica para erros desconhecidos', () => {
    expect(servico.converter(new Error('x')).mensagem).toContain('erro inesperado');
  });

  it('exibe o erro com os detalhes dos campos e remove depois de alguns segundos', () => {
    servico.erro(new HttpErrorResponse({ status: 400, error: { detail: 'Inválido', campos: [{ campo: 'a', mensagem: 'Campo A' }] } }));

    expect(servico.mensagens()).toHaveLength(1);
    expect(servico.mensagens()[0]).toMatchObject({ tipo: 'erro', texto: 'Inválido', detalhes: ['Campo A'] });

    vi.advanceTimersByTime(8000);
    expect(servico.mensagens()).toHaveLength(0);
  });

  it('fecha uma mensagem pelo id', () => {
    servico.sucesso('Salvo');
    servico.aviso('Atenção');
    servico.fechar(servico.mensagens()[0].id);
    expect(servico.mensagens().map(m => m.texto)).toEqual(['Atenção']);
  });

  it('desativação sem reservas afetadas é um sucesso simples', () => {
    servico.desativacao('Sala desativada.', []);
    expect(servico.mensagens()[0].tipo).toBe('sucesso');
  });

  it('desativação com reservas afetadas vira aviso com a quantidade', () => {
    servico.desativacao('Sala desativada.', ['a', 'b']);
    const m = servico.mensagens()[0];
    expect(m.tipo).toBe('aviso');
    expect(m.texto).toContain('2 reservas futuras continuam marcadas');
  });
});
