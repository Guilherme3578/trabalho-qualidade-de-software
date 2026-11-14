package br.senac.alocacao.reserva.regras;

import br.senac.alocacao.material.Material;
import br.senac.alocacao.professor.Professor;
import br.senac.alocacao.sala.Sala;
import br.senac.alocacao.sala.TipoSala;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[RF07] Avaliação da necessidade de aprovação")
class AvaliadorAprovacaoTest {
    private final Sala salaComum = new Sala("A101", "Sala A101", TipoSala.SALA_AULA, 40, "Bloco A", false);
    private final Sala salaRestrita = new Sala("LAB-RED", "Lab Redes", TipoSala.LABORATORIO, 20, "Bloco B", true);
    private final Material projetor = new Material("Projetor", "Audiovisual", null, 5, "Almox", false);
    private final Material kitRobotica = new Material("Kit de robótica", "Lab", null, 4, "Almox", true);
    private final Professor ana = new Professor("Ana", "ana@x.com", "Comp", Set.of("Qualidade de Software"));

    @Nested
    @DisplayName("Ciclo 1 — recursos comuns e docente habilitado")
    class Ciclo1 {
        @Test
        @DisplayName("[RF07] Sem recurso restrito e docente habilitado → aprovação automática")
        void semMotivos() {
            assertThat(AvaliadorAprovacao.motivos(salaComum, List.of(projetor), ana, "Qualidade de Software")).isEmpty();
        }
    }

    @Nested
    @DisplayName("Ciclo 2 — recursos restritos")
    class Ciclo2 {
        @Test
        @DisplayName("[RF07][RN06] Sala restrita exige aprovação")
        void salaRestrita() {
            assertThat(AvaliadorAprovacao.motivos(salaRestrita, List.of(), ana, "Qualidade de Software"))
                    .containsExactly("Sala restrita: LAB-RED");
        }

        @Test
        @DisplayName("[RF07][RN06] Material restrito exige aprovação")
        void materialRestrito() {
            assertThat(AvaliadorAprovacao.motivos(salaComum, List.of(projetor, kitRobotica), ana, "Qualidade de Software"))
                    .containsExactly("Material restrito: Kit de robótica");
        }
    }

    @Nested
    @DisplayName("Ciclo 3 — validação da alocação docente")
    class Ciclo3 {
        @Test
        @DisplayName("[RF07] Professor sem a competência da disciplina exige validação do responsável")
        void semCompetencia() {
            assertThat(AvaliadorAprovacao.motivos(salaComum, List.of(), ana, "Banco de Dados"))
                    .containsExactly("Professor sem competência cadastrada em 'Banco de Dados'");
        }

        @ParameterizedTest(name = "[RF07] \"{0}\" é reconhecida como a mesma competência")
        @ValueSource(strings = {"Qualidade de Software", "qualidade de software", "  QUALIDADE DE SOFTWARE  "})
        @DisplayName("[RF07] Comparação ignora maiúsculas e espaços (refatoração do ciclo 3)")
        void normalizacao(String disciplina) {
            assertThat(AvaliadorAprovacao.motivos(salaComum, List.of(), ana, disciplina)).isEmpty();
        }

        @Test
        @DisplayName("[RF07] Disciplina não informada exige validação do responsável em vez de falhar")
        void disciplinaNula() {
            assertThat(AvaliadorAprovacao.motivos(salaComum, List.of(), ana, null))
                    .containsExactly("Professor sem competência cadastrada em ''");
        }
    }

    @Test
    @DisplayName("[RF07] Todos os motivos são acumulados")
    void acumulaMotivos() {
        assertThat(AvaliadorAprovacao.motivos(salaRestrita, List.of(kitRobotica), ana, "Robótica")).hasSize(3);
    }
}
