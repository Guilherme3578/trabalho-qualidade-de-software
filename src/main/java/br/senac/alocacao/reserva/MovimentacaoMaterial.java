package br.senac.alocacao.reserva;

import java.time.LocalDateTime;

public record MovimentacaoMaterial(String materialId, TipoMovimentacao tipo, int quantidade,
                                   String registradoPorId, String registradoPorNome,
                                   LocalDateTime registradoEm, String observacao) {
}
