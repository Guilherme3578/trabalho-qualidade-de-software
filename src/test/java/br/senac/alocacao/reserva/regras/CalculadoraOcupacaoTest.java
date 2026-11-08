package br.senac.alocacao.reserva.regras;

import br.senac.alocacao.reserva.regras.CalculadoraOcupacao.Uso;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[RN02] Pico de uso simultâneo de materiais")
class CalculadoraOcupacaoTest {

    private static LocalDateTime h(String hhmm) {
        return LocalDateTime.parse("2030-03-11T" + hhmm);
    }

    private static Uso uso(String ini, String fim, int qtd) {
        return new Uso(h(ini), h(fim), qtd);
    }

    private static final Intervalo JANELA = new Intervalo(h("08:00"), h("12:00"));

    static Stream<Arguments> cenarios() {
        return Stream.of(
                Arguments.of("sem reservas", List.of(), 0),
                Arguments.of("uma reserva", List.of(uso("09:00", "10:00", 3)), 3),
                Arguments.of("sobrepostas somam", List.of(uso("08:00", "10:00", 3), uso("09:00", "11:00", 2)), 5),
                Arguments.of("sequenciais NÃO somam (fim = início)",
                        List.of(uso("08:00", "10:00", 3), uso("10:00", "12:00", 4)), 4),
                Arguments.of("tocam a janela mas não se sobrepõem entre si",
                        List.of(uso("08:00", "09:00", 5), uso("11:00", "12:00", 5)), 5),
                Arguments.of("fora da janela é ignorada",
                        List.of(uso("13:00", "14:00", 10), uso("09:00", "10:00", 1)), 1),
                Arguments.of("três camadas",
                        List.of(uso("08:00", "12:00", 1), uso("09:00", "11:00", 2), uso("10:00", "10:30", 3)), 6)
        );
    }

    @ParameterizedTest(name = "[RN02] {0} → pico {2}")
    @MethodSource("cenarios")
    @DisplayName("[RN02] Cálculo do pico por varredura de eventos")
    void pico(String cenario, List<Uso> usos, int esperado) {
        assertThat(CalculadoraOcupacao.picoDeUso(usos, JANELA)).isEqualTo(esperado);
    }

    @Test
    @DisplayName("[RN02] Reserva que começa antes da janela é recortada")
    void recorte() {
        assertThat(CalculadoraOcupacao.picoDeUso(List.of(uso("07:00", "08:30", 2)), JANELA)).isEqualTo(2);
    }
}
