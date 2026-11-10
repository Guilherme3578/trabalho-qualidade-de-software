package br.senac.alocacao.reserva;

import br.senac.alocacao.material.Material;
import br.senac.alocacao.usuario.Usuario;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "reserva_movimentacoes")
public class MovimentacaoMaterial {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "material_id")
    private Material material;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private TipoMovimentacao tipo;

    private int quantidade;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "registrado_por_id")
    private Usuario registradoPor;

    private LocalDateTime registradoEm;

    private String observacao;

    protected MovimentacaoMaterial() {
    }

    public MovimentacaoMaterial(Material material, TipoMovimentacao tipo, int quantidade, Usuario registradoPor,
                                LocalDateTime registradoEm, String observacao) {
        this.material = material;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.registradoPor = registradoPor;
        this.registradoEm = registradoEm;
        this.observacao = observacao;
    }

    public String materialId() {
        return material.getId();
    }

    public TipoMovimentacao tipo() {
        return tipo;
    }

    public int quantidade() {
        return quantidade;
    }

    public String registradoPorId() {
        return registradoPor.getId();
    }

    public String registradoPorNome() {
        return registradoPor.getNome();
    }

    public LocalDateTime registradoEm() {
        return registradoEm;
    }

    public String observacao() {
        return observacao;
    }
}
