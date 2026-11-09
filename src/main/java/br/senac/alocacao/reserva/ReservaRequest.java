package br.senac.alocacao.reserva;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record ReservaRequest(
        @NotBlank(message = "Informe a sala.") String salaId,
        @NotBlank(message = "Informe o professor.") String professorId,
        @NotBlank(message = "Informe a disciplina.")
        @Size(max = 120, message = "A disciplina deve ter no máximo 120 caracteres.") String disciplina,
        @NotNull(message = "Informe o início.") LocalDateTime inicio,
        @NotNull(message = "Informe o término.") LocalDateTime fim,
        @Positive(message = "A quantidade de alunos deve ser maior que zero.")
        @Max(value = 1000, message = "A quantidade de alunos deve ser no máximo 1000.") int quantidadeAlunos,
        @Size(max = 500, message = "A observação deve ter no máximo 500 caracteres.") String observacao,
        @Size(max = 20, message = "No máximo 20 materiais por reserva.") List<@Valid ItemRequest> materiais,
        Long versao) {
    public record ItemRequest(
            @NotBlank(message = "Informe o material.") String materialId,
            @Positive(message = "A quantidade do material deve ser maior que zero.")
            @Max(value = 500, message = "A quantidade do material deve ser no máximo 500.") int quantidade) {
    }

    public List<ItemRequest> materiaisOuVazio() {
        return materiais == null ? List.of() : materiais;
    }
}
