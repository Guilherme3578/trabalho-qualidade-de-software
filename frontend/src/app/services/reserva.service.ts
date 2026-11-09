import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { EventoAuditoria } from '../models/auditoria.model';
import { FiltroReservas, MovimentacaoRequest, Reserva, ReservaRequest } from '../models/reserva.model';
import { parametros } from './parametros';

@Injectable({ providedIn: 'root' })
export class ReservaService {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/reservas';

  listar(filtro: FiltroReservas = {}): Observable<Reserva[]> {
    return this.http.get<Reserva[]>(this.url, { params: parametros(filtro) });
  }

  obter(id: string): Observable<Reserva> {
    return this.http.get<Reserva>(`${this.url}/${id}`);
  }

  historico(id: string): Observable<EventoAuditoria[]> {
    return this.http.get<EventoAuditoria[]>(`${this.url}/${id}/historico`);
  }

  criar(dados: ReservaRequest): Observable<Reserva> {
    return this.http.post<Reserva>(this.url, dados);
  }

  alterar(id: string, dados: ReservaRequest): Observable<Reserva> {
    return this.http.put<Reserva>(`${this.url}/${id}`, dados);
  }

  excluir(id: string): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }

  cancelar(id: string, observacao: string | null): Observable<Reserva> {
    return this.http.post<Reserva>(`${this.url}/${id}/cancelar`, { observacao });
  }

  aprovar(id: string, observacao: string | null): Observable<Reserva> {
    return this.http.post<Reserva>(`${this.url}/${id}/aprovar`, { observacao });
  }

  rejeitar(id: string, observacao: string | null): Observable<Reserva> {
    return this.http.post<Reserva>(`${this.url}/${id}/rejeitar`, { observacao });
  }

  iniciar(id: string): Observable<Reserva> {
    return this.http.post<Reserva>(`${this.url}/${id}/iniciar`, null);
  }

  concluir(id: string): Observable<Reserva> {
    return this.http.post<Reserva>(`${this.url}/${id}/concluir`, null);
  }

  registrarNaoComparecimento(id: string): Observable<Reserva> {
    return this.http.post<Reserva>(`${this.url}/${id}/nao-compareceu`, null);
  }

  registrarRetirada(id: string, dados: MovimentacaoRequest): Observable<Reserva> {
    return this.http.post<Reserva>(`${this.url}/${id}/retiradas`, dados);
  }

  registrarDevolucao(id: string, dados: MovimentacaoRequest): Observable<Reserva> {
    return this.http.post<Reserva>(`${this.url}/${id}/devolucoes`, dados);
  }
}
