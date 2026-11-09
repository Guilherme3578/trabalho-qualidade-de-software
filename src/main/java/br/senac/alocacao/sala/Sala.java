package br.senac.alocacao.sala;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("salas")
public class Sala {
    @Id
    private String id;

    @Indexed(unique = true)
    private String codigo;

    private String nome;

    @Indexed
    private TipoSala tipo;

    private int capacidade;

    private String localizacao;

    private boolean restrita;

    private boolean ativa = true;

    private long versaoAgenda;

    protected Sala() {
    }

    public Sala(String codigo, String nome, TipoSala tipo, int capacidade, String localizacao, boolean restrita) {
        atualizar(codigo, nome, tipo, capacidade, localizacao, restrita);
    }

    public final void atualizar(String codigo, String nome, TipoSala tipo, int capacidade,
                                String localizacao, boolean restrita) {
        this.codigo = codigo;
        this.nome = nome;
        this.tipo = tipo;
        this.capacidade = capacidade;
        this.localizacao = localizacao;
        this.restrita = restrita;
    }

    public String getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public TipoSala getTipo() {
        return tipo;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public boolean isRestrita() {
        return restrita;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public long getVersaoAgenda() {
        return versaoAgenda;
    }
}
