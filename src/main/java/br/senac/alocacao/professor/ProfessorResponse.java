package br.senac.alocacao.professor;

import java.util.Set;
import java.util.TreeSet;

public record ProfessorResponse(String id, String nome, String email, String departamento,
                                Set<String> competencias, boolean ativo) {
    public static ProfessorResponse de(Professor p) {
        return new ProfessorResponse(p.getId(), p.getNome(), p.getEmail(), p.getDepartamento(),
                new TreeSet<>(p.getCompetencias()), p.isAtivo());
    }
}
