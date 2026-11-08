package br.senac.alocacao.notificacao;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("notificacoes")
public class Notificacao {

    @Id
    private String id;

    @Indexed
    private String destinatarioId;

    private String destinatarioEmail;

    private String tipo;

    private String mensagem;

    private String reservaId;

    private StatusNotificacao status;

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
