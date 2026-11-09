package br.senac.alocacao.notificacao;

import br.senac.alocacao.comum.Json;
import br.senac.alocacao.config.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class HttpNotificacaoGateway implements NotificacaoGateway {
    private static final Logger log = LoggerFactory.getLogger(HttpNotificacaoGateway.class);

    private final AppProperties.Notificacao config;
    private final HttpClient cliente;

    public HttpNotificacaoGateway(AppProperties props) {
        this.config = props.notificacao();
        this.cliente = HttpClient.newBuilder().connectTimeout(config.timeout()).build();
    }

    @Override
    public StatusNotificacao enviar(Notificacao n) {
        if (!config.habilitada()) {
            log.info("[NOTIFICAÇÃO SIMULADA] para={} tipo={} mensagem={}", n.getDestinatarioEmail(), n.getTipo(),
                    n.getMensagem());
            return StatusNotificacao.SIMULADA;
        }
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("destinatario", n.getDestinatarioEmail());
        corpo.put("tipo", n.getTipo());
        corpo.put("mensagem", n.getMensagem());
        corpo.put("reservaId", n.getReservaId());

        HttpRequest req = HttpRequest.newBuilder(URI.create(config.url()))
                .timeout(config.timeout())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(Json.objeto(corpo)))
                .build();
        try {
            HttpResponse<Void> resp = cliente.send(req, HttpResponse.BodyHandlers.discarding());
            if (resp.statusCode() / 100 == 2) {
                return StatusNotificacao.ENVIADA;
            }
            log.warn("API de notificação respondeu HTTP {}", resp.statusCode());
            throw new FalhaNotificacaoException("HTTP " + resp.statusCode());
        } catch (IOException e) {
            log.warn("Falha ao chamar API de notificação: {}", e.toString());
            throw new FalhaNotificacaoException(e.getClass().getSimpleName());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FalhaNotificacaoException("interrompido");
        }
    }

    public static class FalhaNotificacaoException extends RuntimeException {
        public FalhaNotificacaoException(String motivo) {
            super(motivo);
        }
    }
}
