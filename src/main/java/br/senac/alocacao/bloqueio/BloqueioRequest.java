package br.senac.alocacao.bloqueio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record BloqueioRequest(
        @NotNull(message = "Informe o tipo do recurso (SALA, MATERIAL ou PROFESSOR).") TipoRecurso tipoRecurso,
        @NotBlank(message = "Informe o recurso.") String recursoId,
        @NotNull(message = "Informe o início do bloqueio.") LocalDateTime inicio,
        @NotNull(message = "Informe o término do bloqueio.") LocalDateTime fim,
        @NotBlank(message = "Informe o motivo (ex.: manutenção do ar-condicionado).")
        @Size(max = 255, message = "O motivo deve ter no máximo 255 caracteres.") String motivo) {
}
