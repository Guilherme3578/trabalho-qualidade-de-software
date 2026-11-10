package br.senac.alocacao.suporte;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public final class LimpezaBanco {
    private static final List<String> TABELAS = List.of(
            "notificacoes", "reserva_movimentacoes", "reserva_itens", "reserva_motivos_aprovacao", "reservas",
            "bloqueios", "conflitos_evitados", "auditoria", "usuarios", "professor_competencias", "professores",
            "salas", "materiais");

    private LimpezaBanco() {
    }

    public static void limpar(JdbcTemplate jdbc) {
        TABELAS.forEach(t -> jdbc.update("DELETE FROM " + t));
    }
}
