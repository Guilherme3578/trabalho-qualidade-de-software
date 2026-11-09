import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Sala, SalaRequest, TipoSala } from '../models/sala.model';
import { parametros } from './parametros';

export interface FiltroSalas {
  tipo?: TipoSala;
  capacidadeMinima?: number;
  localizacao?: string;
  inicio?: string;
  fim?: string;
  incluirInativas?: boolean;
}

@Injectable({ providedIn: 'root' })
export class SalaService {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/salas';

  listar(filtro: FiltroSalas = {}): Observable<Sala[]> {
    return this.http.get<Sala[]>(this.url, { params: parametros(filtro) });
  }

  criar(dados: SalaRequest): Observable<Sala> {
    return this.http.post<Sala>(this.url, dados);
  }

  atualizar(id: string, dados: SalaRequest): Observable<Sala> {
    return this.http.put<Sala>(`${this.url}/${id}`, dados);
  }

  definirAtiva(id: string, ativa: boolean): Observable<Sala> {
    return this.http.patch<Sala>(`${this.url}/${id}/ativa`, null, { params: new HttpParams().set('valor', ativa) });
  }
}
