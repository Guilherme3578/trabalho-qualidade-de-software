package br.senac.alocacao.professor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record ProfessorRequest(
        @NotBlank(message = "Informe o nome do professor.")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres.") String nome,
        @NotBlank(message = "Informe o e-mail.")
        @Email(message = "Informe um e-mail válido.")
        @Size(max = 160, message = "O e-mail deve ter no máximo 160 caracteres.") String email,
        @Size(max = 80, message = "O departamento deve ter no máximo 80 caracteres.") String departamento,
        @Size(max = 30, message = "No máximo 30 competências.")
        Set<@NotBlank(message = "Competência não pode ser vazia.")
        @Size(max = 80, message = "Cada competência deve ter no máximo 80 caracteres.") String> competencias) {
}
