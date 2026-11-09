package br.senac.alocacao.reserva.regras;

import br.senac.alocacao.comum.CodigoErro;
import br.senac.alocacao.comum.NegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Intervalo de tempo")
class IntervaloTest {
    private static final String DIA = "2030-03-11T";

    private static Intervalo i(String ini, String fim) {
        return new Intervalo(LocalDateTime.parse(DIA + ini), LocalDateTime.parse(DIA + fim));
    }

    @ParameterizedTest(name = "[RN02] {0}-{1} x {2}-{3} → sobrepõe={4}")
    @DisplayName("[RN02] Detecção de sobreposição entre intervalos")
    @CsvSource({
            "08:00,10:00, 09:00,11:00, true",
            "09:00,11:00, 08:00,10:00, true",
            "08:00,12:00, 09:00,10:00, true",
            "09:00,10:00, 08:00,12:00, true",
            "08:00,10:00, 08:00,10:00, true",
            "08:00,10:00, 10:00,12:00, false",
            "10:00,12:00, 08:00,10:00, false",
            "08:00,09:00, 11:00,12:00, false",
            "08:00,10:00, 09:59,10:30, true"
    })
    void sobreposicao(String ai, String af, String bi, String bf, boolean esperado) {
        Intervalo a = i(ai, af);
        Intervalo b = i(bi, bf);
        assertThat(a.sobrepoe(b)).isEqualTo(esperado);
        assertThat(b.sobrepoe(a)).as("a sobreposição deve ser simétrica").isEqualTo(esperado);
    }

    @ParameterizedTest(name = "[RN01] início {0} e término {1} → recusado")
    @DisplayName("[RN01] O término deve ser posterior ao início")
    @CsvSource({"10:00,10:00", "10:00,09:59", "23:00,07:00"})
    void terminoNaoPosterior(String ini, String fim) {
        assertThatThrownBy(() -> i(ini, fim))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("término deve ser posterior")
                .extracting("codigo").isEqualTo(CodigoErro.HORARIO_INVALIDO);
    }

    @Test
    @DisplayName("[RN01] Início ou término ausente é recusado")
    void nulos() {
        assertThatThrownBy(() -> new Intervalo(null, LocalDateTime.now()))
                .isInstanceOf(NegocioException.class);
        assertThatThrownBy(() -> new Intervalo(LocalDateTime.now(), null))
                .isInstanceOf(NegocioException.class);
    }

    @Test
    @DisplayName("Duração é calculada corretamente")
    void duracao() {
        assertThat(i("08:00", "09:30").duracao().toMinutes()).isEqualTo(90);
    }
}
