package br.senac.alocacao.reserva;

import java.time.LocalDateTime;

public record ReservaStatusAlteradoEvent(String reservaId, String solicitanteId, String disciplina,
                                         String salaCodigo, LocalDateTime inicio,
                                         StatusReserva anterior, StatusReserva novo, String motivo) {
}
