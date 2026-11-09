import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Usuario, UsuarioRequest } from '../models/usuario.model';

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/usuarios';

  listar(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(this.url);
  }

  criar(dados: UsuarioRequest): Observable<Usuario> {
    return this.http.post<Usuario>(this.url, dados);
  }

  definirAtivo(id: string, ativo: boolean): Observable<Usuario> {
    return this.http.patch<Usuario>(`${this.url}/${id}/ativo`, null, { params: new HttpParams().set('valor', ativo) });
  }
}
