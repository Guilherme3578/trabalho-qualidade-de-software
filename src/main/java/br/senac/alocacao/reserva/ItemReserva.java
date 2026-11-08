package br.senac.alocacao.reserva;

/** Material incluído na reserva (documento embutido em Reserva). */
public record ItemReserva(String materialId, String materialNome, int quantidade) {
}
