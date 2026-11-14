package br.senac.alocacao.sala;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "salas")
public class Sala {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private String id;

    private String codigo;

    private String nome;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private TipoSala tipo;

    private int capacidade;

    private String localizacao;

    private boolean restrita;

    private boolean ativa = true;

    protected Sala() {
    }

    public Sala(String codigo, String nome, TipoSala tipo, int capacidade, String localizacao, boolean restrita) {
        preencher(codigo, nome, tipo, capacidade, localizacao, restrita);
    }

    public void atualizar(String codigo, String nome, TipoSala tipo, int capacidade,
                          String localizacao, boolean restrita) {
        preencher(codigo, nome, tipo, capacidade, localizacao, restrita);
    }

    private void preencher(String codigo, String nome, TipoSala tipo, int capacidade,
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
}
