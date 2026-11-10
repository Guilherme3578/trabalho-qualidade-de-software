package br.senac.alocacao.comum;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Utilitários de filtro e JSON")
class UtilitariosTest {
    @ParameterizedTest(name = "[RNF03] entrada \"{0}\" vira padrão LIKE \"{1}\"")
    @CsvSource(delimiter = '|', quoteCharacter = '"', value = {
            "bloco a|%bloco a%",
            "  bloco  |%bloco%",
            "100%|%100\\%%",
            "a_b|%a\\_b%",
            "c:\\temp|%c:\\\\temp%",
            "'; DROP TABLE salas; --|%'; DROP TABLE salas; --%"})
    @DisplayName("[RNF03] Filtros de texto escapam curingas do LIKE (o valor segue como parâmetro, sem SQL injection)")
    void filtroEscapado(String entrada, String esperado) {
        assertThat(Filtros.contem(entrada)).isEqualTo(esperado);
    }

    @ParameterizedTest(name = "[RF03] filtro vazio \"{0}\" é ignorado")
    @ValueSource(strings = {"", "   "})
    @DisplayName("[RF03] Filtros vazios não restringem a pesquisa")
    void filtroVazio(String entrada) {
        assertThat(Filtros.contem(entrada)).isNull();
        assertThat(Filtros.igual(entrada)).isNull();
        assertThat(Filtros.contem(null)).isNull();
    }

    @Test
    @DisplayName("[RF03] Igualdade remove espaços nas pontas (comparação sem maiúsculas é feita na consulta)")
    void igual() {
        assertThat(Filtros.igual("  Banco de Dados ")).isEqualTo("Banco de Dados");
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
