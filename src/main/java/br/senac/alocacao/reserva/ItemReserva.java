package br.senac.alocacao.reserva;

import br.senac.alocacao.material.Material;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.Objects;

@Embeddable
public class ItemReserva {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "material_id")
    private Material material;

    private int quantidade;

    protected ItemReserva() {
    }

    public ItemReserva(Material material, int quantidade) {
        this.material = material;
        this.quantidade = quantidade;
    }

    public Material material() {
        return material;
    }

    public String materialId() {
        return material.getId();
    }

    public String materialNome() {
        return material.getNome();
    }

    public int quantidade() {
        return quantidade;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ItemReserva outro && quantidade == outro.quantidade
                && Objects.equals(materialId(), outro.materialId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(materialId(), quantidade);
    }
}
