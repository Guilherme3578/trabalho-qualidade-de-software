package br.senac.alocacao.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Configurações próprias da aplicação (prefixo "app" no application.yml).
 */
@ConfigurationProperties("app")
public record AppProperties(Jwt jwt, Notificacao notificacao, Login login) {

    /** segredo: chave HMAC (mín. 32 caracteres); expiracao: validade do token. */
    public record Jwt(String segredo, Duration expiracao) {
    }

    /** habilitada=false: notificações são apenas simuladas (registradas no banco e no log). */
    public record Notificacao(boolean habilitada, String url, Duration timeout) {
    }

    /** Proteção contra força bruta no login. */
    public record Login(int maxTentativas, Duration janelaBloqueio) {
    }
}
