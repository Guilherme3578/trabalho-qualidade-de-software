package br.senac.alocacao.bloqueio;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "bloqueios")
public class Bloqueio {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private String id;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private TipoRecurso tipoRecurso;

    private String salaId;

    private String professorId;

    private String materialId;

    private LocalDateTime inicio;

    private LocalDateTime fim;

    private String motivo;

    private String criadoPorId;

    private LocalDateTime criadoEm;

    protected Bloqueio() {
    }

    public Bloqueio(TipoRecurso tipoRecurso, String recursoId, LocalDateTime inicio, LocalDateTime fim,
                    String motivo, String criadoPorId, LocalDateTime criadoEm) {
        this.tipoRecurso = tipoRecurso;
        switch (tipoRecurso) {
            case SALA -> this.salaId = recursoId;
            case PROFESSOR -> this.professorId = recursoId;
            case MATERIAL -> this.materialId = recursoId;
        }
        this.inicio = inicio;
        this.fim = fim;
        this.motivo = motivo;
        this.criadoPorId = criadoPorId;
        this.criadoEm = criadoEm;
    }

    public String getId() {
        return id;
    }

    public TipoRecurso getTipoRecurso() {
        return tipoRecurso;
    }

    public String getRecursoId() {
        return switch (tipoRecurso) {
            case SALA -> salaId;
            case PROFESSOR -> professorId;
            case MATERIAL -> materialId;
        };
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public LocalDateTime getFim() {
        return fim;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getCriadoPorId() {
        return criadoPorId;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
