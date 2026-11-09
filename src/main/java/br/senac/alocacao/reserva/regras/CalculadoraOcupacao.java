package br.senac.alocacao.reserva.regras;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class CalculadoraOcupacao {
    private CalculadoraOcupacao() {
    }

    public record Uso(LocalDateTime inicio, LocalDateTime fim, int quantidade) {
    }

    private record Evento(LocalDateTime instante, int delta) {
    }

    public static int picoDeUso(List<Uso> usos, Intervalo janela) {
        List<Evento> eventos = new ArrayList<>();
        for (Uso uso : usos) {
            if (!janela.sobrepoe(uso.inicio(), uso.fim())) {
                continue;
            }
            LocalDateTime ini = uso.inicio().isBefore(janela.inicio()) ? janela.inicio() : uso.inicio();
            LocalDateTime fim = uso.fim().isAfter(janela.fim()) ? janela.fim() : uso.fim();
            eventos.add(new Evento(ini, uso.quantidade()));
            eventos.add(new Evento(fim, -uso.quantidade()));
        }
        eventos.sort(Comparator.comparing(Evento::instante).thenComparingInt(Evento::delta));

        int atual = 0;
        int pico = 0;
        for (Evento e : eventos) {
            atual += e.delta();
            pico = Math.max(pico, atual);
        }
        return pico;
    }
}
