package br.senac.alocacao.material;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("materiais")
public class Material {
    @Id
    private String id;

    private String nome;

    @Indexed
    private String categoria;

    @Indexed(unique = true, sparse = true)
    private String patrimonio;

    private int quantidadeTotal;

    private String localizacao;

    private boolean restrito;

    private boolean ativo = true;

    private long versaoAgenda;

    protected Material() {
    }

    public Material(String nome, String categoria, String patrimonio, int quantidadeTotal,
                    String localizacao, boolean restrito) {
        atualizar(nome, categoria, patrimonio, quantidadeTotal, localizacao, restrito);
    }

    public final void atualizar(String nome, String categoria, String patrimonio, int quantidadeTotal,
                                String localizacao, boolean restrito) {
        this.nome = nome;
        this.categoria = categoria;
        this.patrimonio = patrimonio == null || patrimonio.isBlank() ? null : patrimonio;
        this.quantidadeTotal = quantidadeTotal;
        this.localizacao = localizacao;
        this.restrito = restrito;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getPatrimonio() {
        return patrimonio;
    }

    public int getQuantidadeTotal() {
        return quantidadeTotal;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public boolean isRestrito() {
        return restrito;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public long getVersaoAgenda() {
        return versaoAgenda;
    }
}
