package br.senac.alocacao.relatorio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "conflitos_evitados")
public class ConflitoEvitado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo;

    private String recursoTipo;

    private String recursoId;

    private LocalDateTime inicio;

    private LocalDateTime fim;

    private String solicitanteId;

    private LocalDateTime registradoEm;

    protected ConflitoEvitado() {
    }

    public ConflitoEvitado(String tipo, String recursoTipo, String recursoId, LocalDateTime inicio,
                           LocalDateTime fim, String solicitanteId, LocalDateTime registradoEm) {
        this.tipo = tipo;
        this.recursoTipo = recursoTipo;
        this.recursoId = recursoId;
        this.inicio = inicio;
        this.fim = fim;
        this.solicitanteId = solicitanteId;
        this.registradoEm = registradoEm;
    }

    public Long id() {
        return id;
    }

    public String tipo() {
        return tipo;
    }

    public String recursoTipo() {
        return recursoTipo;
    }

    public String recursoId() {
        return recursoId;
    }

    public LocalDateTime inicio() {
        return inicio;
    }

    public LocalDateTime fim() {
        return fim;
    }

    public String solicitanteId() {
        return solicitanteId;
    }

    public LocalDateTime registradoEm() {
        return registradoEm;
    }
}
