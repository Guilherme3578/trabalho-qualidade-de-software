import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Professor, ProfessorRequest } from '../models/professor.model';
import { parametros } from './parametros';

export interface FiltroProfessores {
  competencia?: string;
  departamento?: string;
  nome?: string;
  inicio?: string;
  fim?: string;
}

@Injectable({ providedIn: 'root' })
export class ProfessorService {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/professores';

  listar(filtro: FiltroProfessores = {}): Observable<Professor[]> {
    return this.http.get<Professor[]>(this.url, { params: parametros(filtro) });
  }

  criar(dados: ProfessorRequest): Observable<Professor> {
    return this.http.post<Professor>(this.url, dados);
  }

  atualizar(id: string, dados: ProfessorRequest): Observable<Professor> {
    return this.http.put<Professor>(`${this.url}/${id}`, dados);
  }

  definirAtivo(id: string, ativo: boolean): Observable<Professor> {
    return this.http.patch<Professor>(`${this.url}/${id}/ativo`, null, { params: new HttpParams().set('valor', ativo) });
  }
}
