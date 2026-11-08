package br.senac.alocacao.notificacao;

public enum StatusNotificacao {
    /** Integração externa desligada: notificação apenas registrada (modo simulado). */
    SIMULADA,
    /** Entregue com sucesso à API externa. */
    ENVIADA,
    /** A API externa falhou ou não respondeu a tempo. A reserva NÃO é afetada. */
    FALHA
}
