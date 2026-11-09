package br.senac.alocacao.bloqueio;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("bloqueios")
@CompoundIndex(name = "bloqueio_recurso_periodo", def = "{'tipoRecurso': 1, 'recursoId': 1, 'inicio': 1}")
public class Bloqueio {
    @Id
    private String id;

    private TipoRecurso tipoRecurso;

    private String recursoId;

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
        this.recursoId = recursoId;
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
        return recursoId;
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
