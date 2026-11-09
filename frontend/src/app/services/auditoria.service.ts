import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { AcaoAuditoria, EventoAuditoria } from '../models/auditoria.model';
import { parametros } from './parametros';

export interface FiltroAuditoria {
  entidade?: string;
  entidadeId?: string;
  acao?: AcaoAuditoria;
}

@Injectable({ providedIn: 'root' })
export class AuditoriaService {
  private readonly http = inject(HttpClient);

  listar(filtro: FiltroAuditoria = {}): Observable<EventoAuditoria[]> {
    return this.http.get<EventoAuditoria[]>('/api/auditoria', { params: parametros(filtro) });
  }
}
