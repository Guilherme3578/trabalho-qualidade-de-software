package br.senac.alocacao.auditoria;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
public class EventoAuditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataHora;

    private String usuarioId;

    private String usuarioNome;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private AcaoAuditoria acao;

    private String entidade;

    private String entidadeId;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    private String estadoAnterior;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    private String estadoNovo;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    private String detalhes;

    private String correlationId;

    protected EventoAuditoria() {
    }

    @SuppressWarnings("java:S107")
    public EventoAuditoria(LocalDateTime dataHora, String usuarioId, String usuarioNome, AcaoAuditoria acao,
                           String entidade, String entidadeId, String estadoAnterior, String estadoNovo,
                           String detalhes, String correlationId) {
        this.dataHora = dataHora;
        this.usuarioId = usuarioId;
        this.usuarioNome = usuarioNome;
        this.acao = acao;
        this.entidade = entidade;
        this.entidadeId = entidadeId;
        this.estadoAnterior = estadoAnterior;
        this.estadoNovo = estadoNovo;
        this.detalhes = detalhes;
        this.correlationId = correlationId;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public String getUsuarioNome() {
        return usuarioNome;
    }

    public AcaoAuditoria getAcao() {
        return acao;
    }

    public String getEntidade() {
        return entidade;
    }

    public String getEntidadeId() {
        return entidadeId;
    }

    public String getEstadoAnterior() {
        return estadoAnterior;
    }

    public String getEstadoNovo() {
        return estadoNovo;
    }

    public String getDetalhes() {
        return detalhes;
    }

    public String getCorrelationId() {
        return correlationId;
    }
}
