package br.senac.alocacao.reserva;

import java.time.LocalDateTime;

/**
 * Evento publicado a cada mudança de estado. É consumido APÓS o commit da transação
 * (NotificacaoService), garantindo que nunca se notifique algo que foi desfeito.
 */
public record ReservaStatusAlteradoEvent(String reservaId, String solicitanteId, String disciplina,
                                         String salaCodigo, LocalDateTime inicio,
                                         StatusReserva anterior, StatusReserva novo, String motivo) {
}
