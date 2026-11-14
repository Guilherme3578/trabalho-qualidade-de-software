package br.senac.alocacao.sala;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public record SalaResponse(String id, String codigo, String nome, TipoSala tipo, int capacidade,
                           String localizacao, boolean restrita, boolean ativa,
                           @JsonInclude(JsonInclude.Include.NON_NULL) List<String> reservasAfetadas) {
    public static SalaResponse de(Sala s) {
        return de(s, null);
    }

    public static SalaResponse de(Sala s, List<String> reservasAfetadas) {
        return new SalaResponse(s.getId(), s.getCodigo(), s.getNome(), s.getTipo(), s.getCapacidade(),
                s.getLocalizacao(), s.isRestrita(), s.isAtiva(), reservasAfetadas);
    }
}
