package br.senac.alocacao.reserva;

import java.time.LocalDateTime;

/** Registro de retirada ou devolução de material (documento embutido em Reserva) — RF09. */
public record MovimentacaoMaterial(String materialId, TipoMovimentacao tipo, int quantidade,
                                   String registradoPorId, String registradoPorNome,
                                   LocalDateTime registradoEm, String observacao) {
}
