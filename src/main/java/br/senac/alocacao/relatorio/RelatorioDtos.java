package br.senac.alocacao.relatorio;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/** Estruturas devolvidas pelos relatórios (RF12). */
public final class RelatorioDtos {

    private RelatorioDtos() {
    }

    public record UtilizacaoRecurso(String recursoTipo, String recursoId, String descricao, long reservas,
                                    double horasReservadas, double taxaOcupacaoPercentual) {
    }

    public record RelatorioUtilizacao(LocalDate inicio, LocalDate fim, double horasDisponiveisPorRecurso,
                                      List<UtilizacaoRecurso> salas, List<UtilizacaoRecurso> materiais) {
    }

    public record CargaHorariaProfessor(String professorId, String professorNome, long reservas,
                                        double horasAlocadas) {
    }

    public record RelatorioConflitos(LocalDate inicio, LocalDate fim, long total, Map<String, Long> porTipo,
                                     List<ConflitosPorRecurso> porRecurso) {
    }

    public record ConflitosPorRecurso(String recursoTipo, String recursoId, String descricao, long quantidade) {
    }
}
