package br.senac.alocacao.reserva.regras;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Calcula quantas unidades de um material estão em uso simultaneamente dentro de uma janela.
 *
 * Não basta somar todas as reservas que tocam a janela: duas reservas podem tocar a janela
 * sem se sobreporem entre si (ex.: 08–09 e 09–10 dentro da janela 08–10). O correto é o
 * PICO de uso, obtido por varredura de eventos ("sweep line"):
 * cada início soma a quantidade e cada fim subtrai; o maior valor acumulado é o pico.
 * Fins são processados antes de inícios no mesmo instante (intervalos semiabertos).
 */
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
