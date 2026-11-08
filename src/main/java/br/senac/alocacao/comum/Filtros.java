package br.senac.alocacao.comum;

import br.senac.alocacao.reserva.regras.Intervalo;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

/** Utilitários para filtros de pesquisa. */
public final class Filtros {

    private Filtros() {
    }

    /**
     * Expressão regular "contém", sem diferenciar maiúsculas.
     * O texto do usuário é escapado com Pattern.quote: caracteres como .* ou $where
     * são tratados literalmente (evita injeção de regex/NoSQL e consultas lentas).
     */
    public static Pattern contem(String texto) {
        return Pattern.compile(Pattern.quote(texto.trim()), Pattern.CASE_INSENSITIVE);
    }

    /** Igualdade sem diferenciar maiúsculas, também com escape. */
    public static Pattern igual(String texto) {
        return Pattern.compile("^" + Pattern.quote(texto.trim()) + "$", Pattern.CASE_INSENSITIVE);
    }

    /** Filtro de disponibilidade: exige início e término juntos. */
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
