package br.senac.alocacao.notificacao;

public interface NotificacaoGateway {
    StatusNotificacao enviar(Notificacao notificacao);
}
