package br.senac.alocacao.bloqueio;

import java.time.LocalDateTime;
import java.util.List;

public record BloqueioResponse(String id, TipoRecurso tipoRecurso, String recursoId, LocalDateTime inicio,
                               LocalDateTime fim, String motivo, List<String> reservasAfetadas) {
    public static BloqueioResponse de(Bloqueio b, List<String> afetadas) {
        return new BloqueioResponse(b.getId(), b.getTipoRecurso(), b.getRecursoId(), b.getInicio(), b.getFim(),
                b.getMotivo(), afetadas);
    }
}
