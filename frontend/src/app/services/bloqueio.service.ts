import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Bloqueio, BloqueioRequest, TipoRecurso } from '../models/bloqueio.model';
import { parametros } from './parametros';

@Injectable({ providedIn: 'root' })
export class BloqueioService {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/bloqueios';

  listar(tipoRecurso?: TipoRecurso, recursoId?: string): Observable<Bloqueio[]> {
    return this.http.get<Bloqueio[]>(this.url, { params: parametros({ tipoRecurso, recursoId }) });
  }

  criar(dados: BloqueioRequest): Observable<Bloqueio> {
    return this.http.post<Bloqueio>(this.url, dados);
  }

  remover(id: string): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
