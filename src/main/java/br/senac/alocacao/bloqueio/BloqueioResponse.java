package br.senac.alocacao.bloqueio;

import java.time.LocalDateTime;
import java.util.List;

/**
 * reservasAfetadas: reservas ativas que já existiam no período bloqueado. O bloqueio é aceito
 * (manutenção pode ser urgente), mas o administrador é avisado para tratá-las.
 */
public record BloqueioResponse(String id, TipoRecurso tipoRecurso, String recursoId, LocalDateTime inicio,
                               LocalDateTime fim, String motivo, List<String> reservasAfetadas) {

    public static BloqueioResponse de(Bloqueio b, List<String> afetadas) {
        return new BloqueioResponse(b.getId(), b.getTipoRecurso(), b.getRecursoId(), b.getInicio(), b.getFim(),
                b.getMotivo(), afetadas);
    }
}
