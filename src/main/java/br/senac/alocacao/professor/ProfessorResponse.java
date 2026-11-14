package br.senac.alocacao.professor;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public record ProfessorResponse(String id, String nome, String email, String departamento,
                                Set<String> competencias, boolean ativo,
                                @JsonInclude(JsonInclude.Include.NON_NULL) List<String> reservasAfetadas) {
    public static ProfessorResponse de(Professor p) {
        return de(p, null);
    }

    public static ProfessorResponse de(Professor p, List<String> reservasAfetadas) {
        return new ProfessorResponse(p.getId(), p.getNome(), p.getEmail(), p.getDepartamento(),
                new TreeSet<>(p.getCompetencias()), p.isAtivo(), reservasAfetadas);
    }
}
