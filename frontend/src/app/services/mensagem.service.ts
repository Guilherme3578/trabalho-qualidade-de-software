import { HttpErrorResponse } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { ErroApi } from '../models/erro-api.model';

export type TipoMensagem = 'sucesso' | 'erro' | 'aviso';

export interface Mensagem {
  id: number;
  tipo: TipoMensagem;
  titulo: string;
  texto: string;
  detalhes: string[];
}

@Injectable({ providedIn: 'root' })
export class MensagemService {
  private sequencia = 0;
  private readonly lista = signal<Mensagem[]>([]);

  readonly mensagens = this.lista.asReadonly();

  sucesso(texto: string): void {
    this.adicionar('sucesso', 'Tudo certo', texto, []);
  }

  aviso(texto: string): void {
    this.adicionar('aviso', 'Atenção', texto, []);
  }

  erro(falha: unknown): void {
    const erro = this.converter(falha);
    this.adicionar('erro', 'Não foi possível concluir', erro.mensagem, erro.campos.map(c => c.mensagem));
  }

  fechar(id: number): void {
    this.lista.update(lista => lista.filter(m => m.id !== id));
  }

  converter(falha: unknown): ErroApi {
    if (falha instanceof HttpErrorResponse) {
      const corpo = falha.error ?? {};
      if (falha.status === 0) {
        return this.erroSimples(0, 'SEM_CONEXAO', 'Não foi possível falar com o servidor. Verifique se a API está em execução.');
      }
      return {
        status: falha.status,
        codigo: corpo.codigo ?? 'ERRO',
        mensagem: corpo.detail ?? 'Ocorreu um erro inesperado. Tente novamente.',
        campos: Array.isArray(corpo.campos) ? corpo.campos : [],
        correlationId: corpo.correlationId ?? null
      };
    }
    return this.erroSimples(0, 'ERRO', 'Ocorreu um erro inesperado. Tente novamente.');
  }

  private erroSimples(status: number, codigo: string, mensagem: string): ErroApi {
    return { status, codigo, mensagem, campos: [], correlationId: null };
  }

  private adicionar(tipo: TipoMensagem, titulo: string, texto: string, detalhes: string[]): void {
    const id = ++this.sequencia;
    this.lista.update(lista => [...lista, { id, tipo, titulo, texto, detalhes }]);
    setTimeout(() => this.fechar(id), tipo === 'erro' ? 8000 : 4500);
  }
}
