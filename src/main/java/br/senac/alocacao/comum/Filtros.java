package br.senac.alocacao.comum;

import br.senac.alocacao.reserva.regras.Intervalo;

import java.time.LocalDateTime;
import java.util.Optional;

public final class Filtros {
    private Filtros() {
    }

    public static String contem(String texto) {
        if (!preenchido(texto)) {
            return null;
        }
        String escapado = texto.trim()
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
        return "%" + escapado + "%";
    }

    public static String igual(String texto) {
        return preenchido(texto) ? texto.trim() : null;
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
