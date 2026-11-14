package br.senac.alocacao.reserva.regras;

import br.senac.alocacao.comum.CodigoErro;
import br.senac.alocacao.comum.NegocioException;

import java.time.Duration;
import java.time.LocalDateTime;

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

    public boolean sobrepoe(Intervalo outro) {
        return inicio.isBefore(outro.fim) && outro.inicio.isBefore(fim);
    }

    public boolean sobrepoe(LocalDateTime outroInicio, LocalDateTime outroFim) {
        return inicio.isBefore(outroFim) && outroInicio.isBefore(fim);
    }

    @SuppressWarnings("java:S8700")
    public Duration duracao() {
        return Duration.between(inicio, fim);
    }
}
