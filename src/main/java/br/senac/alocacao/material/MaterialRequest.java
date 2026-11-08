package br.senac.alocacao.material;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MaterialRequest(
        @NotBlank(message = "Informe o nome do material.")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres.") String nome,
        @NotBlank(message = "Informe a categoria.")
        @Size(max = 60, message = "A categoria deve ter no máximo 60 caracteres.") String categoria,
        @Size(max = 40, message = "O patrimônio deve ter no máximo 40 caracteres.") String patrimonio,
        @Min(value = 0, message = "A quantidade total não pode ser negativa.")
        @Max(value = 10000, message = "A quantidade total deve ser no máximo 10000.") int quantidadeTotal,
        @Size(max = 120, message = "A localização deve ter no máximo 120 caracteres.") String localizacao,
        boolean restrito) {
}
