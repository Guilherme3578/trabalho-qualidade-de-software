package br.senac.alocacao.reserva.regras;

import br.senac.alocacao.material.Material;
import br.senac.alocacao.professor.Professor;
import br.senac.alocacao.sala.Sala;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Decide se uma reserva precisa de aprovação de um RESPONSAVEL (RF07 / RN06).
 *
 * Exigem aprovação:
 * <ol>
 *   <li>sala restrita;</li>
 *   <li>qualquer material restrito;</li>
 *   <li>professor sem a competência da disciplina informada (validação da alocação docente).</li>
 * </ol>
 * Se a lista de motivos for vazia, a reserva é aprovada automaticamente pelo sistema.
 */
public final class AvaliadorAprovacao {

    private AvaliadorAprovacao() {
    }

    public static List<String> motivos(Sala sala, Collection<Material> materiais, Professor professor,
                                       String disciplina) {
        List<String> motivos = new ArrayList<>();
        if (sala.isRestrita()) {
            motivos.add("Sala restrita: " + sala.getCodigo());
        }
        materiais.stream()
                .filter(Material::isRestrito)
                .forEach(m -> motivos.add("Material restrito: " + m.getNome()));
        if (!professor.possuiCompetencia(disciplina)) {
            motivos.add("Professor sem competência cadastrada em '" + disciplina.trim() + "'");
        }
        return motivos;
    }
}
