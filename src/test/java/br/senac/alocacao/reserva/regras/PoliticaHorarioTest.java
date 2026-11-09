package br.senac.alocacao.reserva.regras;

import br.senac.alocacao.comum.NegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Política de horário das reservas")
class PoliticaHorarioTest {
    private static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");
    private final Clock relogio = Clock.fixed(LocalDateTime.parse("2030-03-10T12:00").atZone(ZONA).toInstant(), ZONA);
    private final PoliticaHorario politica = new PoliticaHorario(relogio);

    @ParameterizedTest(name = "[RN10] {0} até {1} → aceito")
    @DisplayName("[RN10] Horários válidos são aceitos")
    @CsvSource({
            "2030-03-11T07:00, 2030-03-11T08:00",
            "2030-03-11T22:00, 2030-03-11T23:00",
            "2030-03-11T08:00, 2030-03-11T08:30",
            "2030-03-11T08:00, 2030-03-11T14:00",
            "2030-03-10T12:30, 2030-03-10T14:00"
    })
    void validos(String inicio, String fim) {
        assertThatCode(() -> politica.validar(LocalDateTime.parse(inicio), LocalDateTime.parse(fim)))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest(name = "[{3}] {0} até {1} → \"{2}\"")
    @DisplayName("Horários inválidos são recusados com mensagem compreensível")
    @CsvSource({
            "2030-03-11T10:00, 2030-03-11T09:00, término deve ser posterior, RN01",
            "2030-03-11T10:00, 2030-03-11T10:00, término deve ser posterior, RN01",
            "2030-03-09T10:00, 2030-03-09T11:00, já passou,                  RN10",
            "2030-03-10T11:00, 2030-03-10T13:00, já passou,                  RN10",
            "2030-03-11T06:30, 2030-03-11T08:00, horário de funcionamento,   RN10",
            "2030-03-11T22:00, 2030-03-11T23:30, horário de funcionamento,   RN10",
            "2030-03-11T22:00, 2030-03-12T08:00, mesmo dia,                  RN10",
            "2030-03-11T08:00, 2030-03-11T08:20, entre 30 minutos e 6 horas, RN10",
            "2030-03-11T08:00, 2030-03-11T14:30, entre 30 minutos e 6 horas, RN10"
    })
    void invalidos(String inicio, String fim, String trechoMensagem, String regra) {
        assertThatThrownBy(() -> politica.validar(LocalDateTime.parse(inicio), LocalDateTime.parse(fim)))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining(trechoMensagem);
    }

    @Test
    @DisplayName("[RN10] Validação devolve o intervalo normalizado")
    void devolveIntervalo() {
        Intervalo i = politica.validar(LocalDateTime.parse("2030-03-11T08:00"), LocalDateTime.parse("2030-03-11T10:00"));
        assertThat(i.duracao().toHours()).isEqualTo(2);
    }
}
