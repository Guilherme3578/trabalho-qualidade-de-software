package br.senac.alocacao.sala;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SalaRequest(
        @NotBlank(message = "Informe o código da sala.")
        @Pattern(regexp = "^[A-Za-z0-9-]{1,20}$", message = "O código deve ter até 20 letras, números ou hífen.")
        String codigo,
        @NotBlank(message = "Informe o nome da sala.")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres.") String nome,
        @NotNull(message = "Informe o tipo da sala (SALA_AULA, LABORATORIO ou AUDITORIO).") TipoSala tipo,
        @Min(value = 1, message = "A capacidade deve ser de pelo menos 1 pessoa.")
        @Max(value = 1000, message = "A capacidade deve ser de no máximo 1000 pessoas.") int capacidade,
        @NotBlank(message = "Informe a localização.")
        @Size(max = 120, message = "A localização deve ter no máximo 120 caracteres.") String localizacao,
        boolean restrita) {
}
