import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { Notificacao } from '../models/notificacao.model';

@Injectable({ providedIn: 'root' })
export class NotificacaoService {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/notificacoes';
  private readonly lista = signal<Notificacao[]>([]);

  readonly notificacoes = this.lista.asReadonly();
  readonly naoLidas = computed(() => this.lista().filter(n => !n.lida).length);

  carregar(): Observable<Notificacao[]> {
    return this.http.get<Notificacao[]>(this.url).pipe(tap(lista => this.lista.set(lista)));
  }

  marcarLida(id: string): Observable<Notificacao> {
    return this.http.patch<Notificacao>(`${this.url}/${id}/lida`, null).pipe(
      tap(atualizada => this.lista.update(lista => lista.map(n => (n.id === id ? atualizada : n))))
    );
  }
}
