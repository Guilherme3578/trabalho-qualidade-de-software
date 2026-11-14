package br.senac.alocacao.banco;

import br.senac.alocacao.material.Material;
import br.senac.alocacao.professor.Professor;
import br.senac.alocacao.sala.Sala;
import br.senac.alocacao.sala.TipoSala;
import br.senac.alocacao.suporte.IntegracaoBase;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[RNF07] Entidades JPA atualizadas por meio de proxy do Hibernate")
class AtualizacaoViaProxyIT extends IntegracaoBase {
    @Autowired EntityManager entityManager;
    @Autowired TransactionTemplate transacao;

    @Test
    @DisplayName("[RNF07] Sala carregada como proxy grava a atualização no banco")
    void salaViaProxy() {
        Sala sala = sala("A101", 40, false);
        transacao.executeWithoutResult(t -> entityManager.getReference(Sala.class, sala.getId())
                .atualizar("A101", "Sala renomeada", TipoSala.SALA_AULA, 50, "Bloco Z", false));
        assertThat(salas.findById(sala.getId()).orElseThrow().getNome()).isEqualTo("Sala renomeada");
    }

    @Test
    @DisplayName("[RNF07] Professor carregado como proxy grava a atualização no banco")
    void professorViaProxy() {
        Professor professor = professor("Ana Souza", "Qualidade de Software");
        transacao.executeWithoutResult(t -> entityManager.getReference(Professor.class, professor.getId())
                .atualizar("Ana Souza Lima", "ana.lima@senac.br", "Computação", Set.of("Banco de Dados")));
        assertThat(professores.findById(professor.getId()).orElseThrow().getNome()).isEqualTo("Ana Souza Lima");
    }

    @Test
    @DisplayName("[RNF07] Material carregado como proxy grava a atualização no banco")
    void materialViaProxy() {
        Material material = material("Projetor", 5, false);
        transacao.executeWithoutResult(t -> entityManager.getReference(Material.class, material.getId())
                .atualizar("Projetor 4K", "Audiovisual", null, 5, "Almoxarifado A", false));
        assertThat(materiais.findById(material.getId()).orElseThrow().getNome()).isEqualTo("Projetor 4K");
    }
}
