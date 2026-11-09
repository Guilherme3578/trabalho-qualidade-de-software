import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { CargaHoraria, RelatorioConflitos, RelatorioUtilizacao } from '../models/relatorio.model';
import { parametros } from './parametros';

@Injectable({ providedIn: 'root' })
export class RelatorioService {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/relatorios';

  utilizacao(inicio: string, fim: string): Observable<RelatorioUtilizacao> {
    return this.http.get<RelatorioUtilizacao>(`${this.url}/utilizacao`, { params: parametros({ inicio, fim }) });
  }

  cargaHoraria(inicio: string, fim: string): Observable<CargaHoraria[]> {
    return this.http.get<CargaHoraria[]>(`${this.url}/carga-horaria`, { params: parametros({ inicio, fim }) });
  }

  conflitosEvitados(inicio: string, fim: string): Observable<RelatorioConflitos> {
    return this.http.get<RelatorioConflitos>(`${this.url}/conflitos-evitados`, { params: parametros({ inicio, fim }) });
  }
}
