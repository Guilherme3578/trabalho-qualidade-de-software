package br.senac.alocacao.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PoliticaConteudoFilter extends OncePerRequestFilter {
    public static final String ATRIBUTO_NONCE = PoliticaConteudoFilter.class.getName() + ".nonce";

    private final SecureRandom aleatorio = new SecureRandom();

    @Override
    protected void doFilterInternal(HttpServletRequest requisicao, HttpServletResponse resposta, FilterChain cadeia)
            throws ServletException, IOException {
        String nonce = novoNonce();
        requisicao.setAttribute(ATRIBUTO_NONCE, nonce);
        resposta.setHeader("Content-Security-Policy", politica(nonce));
        cadeia.doFilter(requisicao, resposta);
    }

    static String politica(String nonce) {
        return "default-src 'self'; img-src 'self' data:; style-src 'self' 'nonce-" + nonce + "'; "
                + "script-src 'self'; object-src 'none'; base-uri 'self'; frame-ancestors 'none'; form-action 'self'";
    }

    private String novoNonce() {
        byte[] bytes = new byte[18];
        aleatorio.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
