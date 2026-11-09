package br.senac.alocacao.comum;

import br.senac.alocacao.reserva.regras.Intervalo;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

public final class Filtros {
    private Filtros() {
    }

    public static Pattern contem(String texto) {
        return Pattern.compile(Pattern.quote(texto.trim()), Pattern.CASE_INSENSITIVE);
    }

    public static Pattern igual(String texto) {
        return Pattern.compile("^" + Pattern.quote(texto.trim()) + "$", Pattern.CASE_INSENSITIVE);
    }

    public static Optional<Intervalo> intervaloOpcional(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null && fim == null) {
            return Optional.empty();
        }
        if (inicio == null || fim == null) {
            throw NegocioException.regra(CodigoErro.VALIDACAO,
                    "Para pesquisar por disponibilidade, informe início e término.");
        }
        return Optional.of(new Intervalo(inicio, fim));
    }

    public static boolean preenchido(String s) {
        return s != null && !s.isBlank();
    }
}
