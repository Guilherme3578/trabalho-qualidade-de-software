package br.senac.alocacao.reserva;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record MovimentacaoRequest(
        @NotBlank(message = "Informe o material.") String materialId,
        @Positive(message = "A quantidade deve ser maior que zero.") int quantidade,
        @Size(max = 255, message = "A observação deve ter no máximo 255 caracteres.") String observacao) {
}
