package br.senac.alocacao.reserva;

import jakarta.validation.constraints.Size;

public record AcaoRequest(
        @Size(max = 500, message = "A observação deve ter no máximo 500 caracteres.") String observacao) {
    public static String texto(AcaoRequest req) {
        return req == null ? null : req.observacao();
    }
}
