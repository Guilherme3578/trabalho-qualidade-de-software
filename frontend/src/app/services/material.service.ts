import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Material, MaterialRequest } from '../models/material.model';
import { parametros } from './parametros';

export interface FiltroMateriais {
  categoria?: string;
  localizacao?: string;
  nome?: string;
  inicio?: string;
  fim?: string;
  quantidadeMinima?: number;
}

@Injectable({ providedIn: 'root' })
export class MaterialService {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/materiais';

  listar(filtro: FiltroMateriais = {}): Observable<Material[]> {
    return this.http.get<Material[]>(this.url, { params: parametros(filtro) });
  }

  criar(dados: MaterialRequest): Observable<Material> {
    return this.http.post<Material>(this.url, dados);
  }

  atualizar(id: string, dados: MaterialRequest): Observable<Material> {
    return this.http.put<Material>(`${this.url}/${id}`, dados);
  }

  definirAtivo(id: string, ativo: boolean): Observable<Material> {
    return this.http.patch<Material>(`${this.url}/${id}/ativo`, null, { params: new HttpParams().set('valor', ativo) });
  }
}
