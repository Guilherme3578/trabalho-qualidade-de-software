package br.senac.alocacao.notificacao;

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
@Table(name = "notificacoes")
public class Notificacao {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private String id;

    private String destinatarioId;

    private String destinatarioEmail;

    private String tipo;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    private String mensagem;

    private String reservaId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private StatusNotificacao status;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    private String erro;

    private boolean lida;

    private LocalDateTime criadoEm;

    protected Notificacao() {
    }

    public Notificacao(String destinatarioId, String destinatarioEmail, String tipo, String mensagem,
                       String reservaId, LocalDateTime criadoEm) {
        this.destinatarioId = destinatarioId;
        this.destinatarioEmail = destinatarioEmail;
        this.tipo = tipo;
        this.mensagem = mensagem;
        this.reservaId = reservaId;
        this.criadoEm = criadoEm;
    }

    public void registrarResultado(StatusNotificacao status, String erro) {
        this.status = status;
        this.erro = erro;
    }

    public void marcarLida() {
        this.lida = true;
    }

    public String getId() {
        return id;
    }

    public String getDestinatarioId() {
        return destinatarioId;
    }

    public String getDestinatarioEmail() {
        return destinatarioEmail;
    }

    public String getTipo() {
        return tipo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public String getReservaId() {
        return reservaId;
    }

    public StatusNotificacao getStatus() {
        return status;
    }

    public String getErro() {
        return erro;
    }

    public boolean isLida() {
        return lida;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
