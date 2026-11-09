package br.senac.alocacao.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("app")
public record AppProperties(Jwt jwt, Notificacao notificacao, Login login) {
    public record Jwt(String segredo, Duration expiracao) {
    }

    public record Notificacao(boolean habilitada, String url, Duration timeout) {
    }

    public record Login(int maxTentativas, Duration janelaBloqueio) {
    }
}
