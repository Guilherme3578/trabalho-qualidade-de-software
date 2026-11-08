package br.senac.alocacao.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Atribui um identificador de correlação a cada requisição.
 * Ele aparece nos logs, nas respostas de erro e nos registros de auditoria,
 * permitindo rastrear uma operação de ponta a ponta.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Correlation-Id";
    public static final String MDC_CHAVE = "correlationId";
    private static final Pattern FORMATO_SEGURO = Pattern.compile("^[A-Za-z0-9-]{8,64}$");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String recebido = request.getHeader(HEADER);
        // Só reaproveita o valor do cliente se tiver formato seguro (evita injeção em logs).
        String id = recebido != null && FORMATO_SEGURO.matcher(recebido).matches()
                ? recebido
                : UUID.randomUUID().toString();
        MDC.put(MDC_CHAVE, id);
        response.setHeader(HEADER, id);
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_CHAVE);
        }
    }

    public static String atual() {
        return MDC.get(MDC_CHAVE);
    }
}
