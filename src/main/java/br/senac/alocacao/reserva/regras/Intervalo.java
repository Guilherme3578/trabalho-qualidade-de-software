package br.senac.alocacao.reserva.regras;

import br.senac.alocacao.comum.CodigoErro;
import br.senac.alocacao.comum.NegocioException;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Intervalo de tempo semiaberto [inicio, fim).
 * Por ser semiaberto, uma reserva que termina às 10:00 NÃO conflita com outra que começa às 10:00.
 *
 * Regra RN01: o término deve ser posterior ao início (garantido na construção).
 */
public record Intervalo(LocalDateTime inicio, LocalDateTime fim) {

    public Intervalo {
        if (inicio == null || fim == null) {
            throw NegocioException.regra(CodigoErro.HORARIO_INVALIDO, "Informe o início e o término.");
        }
        if (!fim.isAfter(inicio)) {
            throw NegocioException.regra(CodigoErro.HORARIO_INVALIDO,
                    "O término deve ser posterior ao início.");
        }
    }

    /** Dois intervalos se sobrepõem quando cada um começa antes do outro terminar. */
    public boolean sobrepoe(Intervalo outro) {
        return inicio.isBefore(outro.fim) && outro.inicio.isBefore(fim);
    }

    public boolean sobrepoe(LocalDateTime outroInicio, LocalDateTime outroFim) {
        return inicio.isBefore(outroFim) && outroInicio.isBefore(fim);
    }

    public Duration duracao() {
        return Duration.between(inicio, fim);
    }
}
