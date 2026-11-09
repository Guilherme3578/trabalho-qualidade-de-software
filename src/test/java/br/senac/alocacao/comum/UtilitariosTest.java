package br.senac.alocacao.comum;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Utilitários de filtro e JSON")
class UtilitariosTest {
    @ParameterizedTest(name = "[RNF03] entrada maliciosa \"{0}\" é tratada literalmente")
    @ValueSource(strings = {".*", "^$", "a|b", "(.*)+$", "{\"$gt\": \"\"}", "'; db.dropDatabase(); '"})
    @DisplayName("[RNF03] Filtros escapam expressões regulares (evita injeção NoSQL/ReDoS)")
    void filtroEscapado(String entrada) {
        assertThat(Filtros.contem(entrada).matcher("texto qualquer sem relação").find()).isFalse();
        assertThat(Filtros.contem(entrada).matcher("prefixo " + entrada + " sufixo").find()).isTrue();
    }

    @Test
    @DisplayName("[RF03] Igualdade ignora maiúsculas")
    void igual() {
        assertThat(Filtros.igual("banco de dados").matcher("Banco de Dados").matches()).isTrue();
        assertThat(Filtros.igual("banco").matcher("Banco de Dados").matches()).isFalse();
    }

    @Test
    @DisplayName("[RF03] Disponibilidade exige início e término juntos")
    void intervaloOpcional() {
        assertThat(Filtros.intervaloOpcional(null, null)).isEmpty();
        assertThat(Filtros.intervaloOpcional(LocalDateTime.now(), LocalDateTime.now().plusHours(1))).isPresent();
        assertThatThrownBy(() -> Filtros.intervaloOpcional(LocalDateTime.now(), null))
                .isInstanceOf(NegocioException.class);
    }

    @Test
    @DisplayName("[RNF04] JSON gerado escapa caracteres especiais")
    void json() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("texto", "aspas \" barra \\ quebra\n tab\t <script>");
        m.put("numero", 10);
        m.put("nulo", null);
        m.put("controle", "\u0001");
        assertThat(Json.objeto(m)).isEqualTo(
                "{\"texto\":\"aspas \\\" barra \\\\ quebra\\n tab\\t <script>\",\"numero\":10,\"nulo\":null,"
                        + "\"controle\":\"\\u0001\"}");
    }
}
