package br.senac.alocacao.professor;

import java.util.Set;

public record ProfessorResponse(String id, String nome, String email, String departamento,
                                Set<String> competencias, boolean ativo) {
    public static ProfessorResponse de(Professor p) {
        return new ProfessorResponse(p.getId(), p.getNome(), p.getEmail(), p.getDepartamento(),
                p.getCompetencias(), p.isAtivo());
    }
}
