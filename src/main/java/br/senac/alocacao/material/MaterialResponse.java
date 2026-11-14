package br.senac.alocacao.material;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public record MaterialResponse(String id, String nome, String categoria, String patrimonio, int quantidadeTotal,
                               Integer quantidadeDisponivel, String localizacao, boolean restrito, boolean ativo,
                               @JsonInclude(JsonInclude.Include.NON_NULL) List<String> reservasAfetadas) {
    public static MaterialResponse de(Material m, Integer disponivel) {
        return de(m, disponivel, null);
    }

    public static MaterialResponse de(Material m, Integer disponivel, List<String> reservasAfetadas) {
        return new MaterialResponse(m.getId(), m.getNome(), m.getCategoria(), m.getPatrimonio(),
                m.getQuantidadeTotal(), disponivel, m.getLocalizacao(), m.isRestrito(), m.isAtivo(),
                reservasAfetadas);
    }
}
