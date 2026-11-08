package br.senac.alocacao.notificacao;

/**
 * Porta de saída para o serviço externo de notificações (e-mail/SMS/push).
 * Isolar a integração atrás de uma interface permite simulá-la (WireMock nos testes,
 * modo SIMULADA em desenvolvimento) sem alterar as regras de negócio.
 */
public interface NotificacaoGateway {

    StatusNotificacao enviar(Notificacao notificacao);
}
