package br.senac.alocacao.material;

/** quantidadeDisponivel só é preenchida quando a pesquisa informa um intervalo. */
public record MaterialResponse(String id, String nome, String categoria, String patrimonio, int quantidadeTotal,
                               Integer quantidadeDisponivel, String localizacao, boolean restrito, boolean ativo) {

    public static MaterialResponse de(Material m, Integer disponivel) {
        return new MaterialResponse(m.getId(), m.getNome(), m.getCategoria(), m.getPatrimonio(),
                m.getQuantidadeTotal(), disponivel, m.getLocalizacao(), m.isRestrito(), m.isAtivo());
    }
}
