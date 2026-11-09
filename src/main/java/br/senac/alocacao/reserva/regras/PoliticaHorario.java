package br.senac.alocacao.reserva.regras;

import br.senac.alocacao.comum.CodigoErro;
import br.senac.alocacao.comum.NegocioException;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class PoliticaHorario {
    public static final LocalTime ABERTURA = LocalTime.of(7, 0);
    public static final LocalTime FECHAMENTO = LocalTime.of(23, 0);
    public static final Duration DURACAO_MINIMA = Duration.ofMinutes(30);
    public static final Duration DURACAO_MAXIMA = Duration.ofHours(6);

    private final Clock clock;

    public PoliticaHorario(Clock clock) {
        this.clock = clock;
    }

    public Intervalo validar(LocalDateTime inicio, LocalDateTime fim) {
        Intervalo intervalo = new Intervalo(inicio, fim);

        if (inicio.isBefore(LocalDateTime.now(clock))) {
            throw NegocioException.regra(CodigoErro.HORARIO_INVALIDO,
                    "Não é possível criar ou alterar reservas para um horário que já passou.");
        }
        if (!inicio.toLocalDate().equals(fim.toLocalDate())) {
            throw NegocioException.regra(CodigoErro.HORARIO_INVALIDO,
                    "A reserva deve começar e terminar no mesmo dia.");
        }
        if (inicio.toLocalTime().isBefore(ABERTURA) || fim.toLocalTime().isAfter(FECHAMENTO)) {
            throw NegocioException.regra(CodigoErro.HORARIO_INVALIDO,
                    "As reservas devem estar dentro do horário de funcionamento (07:00 às 23:00).");
        }
        Duration duracao = intervalo.duracao();
        if (duracao.compareTo(DURACAO_MINIMA) < 0 || duracao.compareTo(DURACAO_MAXIMA) > 0) {
            throw NegocioException.regra(CodigoErro.HORARIO_INVALIDO,
                    "A reserva deve durar entre 30 minutos e 6 horas.");
        }
        return intervalo;
    }
}
