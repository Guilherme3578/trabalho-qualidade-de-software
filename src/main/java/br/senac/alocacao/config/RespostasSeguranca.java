package br.senac.alocacao.config;

import br.senac.alocacao.comum.CodigoErro;
import br.senac.alocacao.comum.Json;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Respostas 401/403 geradas pela camada de segurança (antes de chegar aos controllers),
 * no mesmo formato Problem Details usado pelo restante da API.
 */
@Component
public class RespostasSeguranca {

    public AuthenticationEntryPoint naoAutenticado() {
        return (req, res, ex) -> escrever(res, HttpStatus.UNAUTHORIZED, CodigoErro.NAO_AUTENTICADO,
                "Autenticação necessária. Faça login e envie o token no cabeçalho Authorization.");
    }

    public AccessDeniedHandler acessoNegado() {
        return (req, res, ex) -> escrever(res, HttpStatus.FORBIDDEN, CodigoErro.ACESSO_NEGADO,
                "Seu perfil não tem permissão para executar esta operação.");
    }

    private void escrever(HttpServletResponse res, HttpStatus status, CodigoErro codigo, String mensagem)
            throws IOException {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("type", "about:blank");
        corpo.put("title", status.getReasonPhrase());
        corpo.put("status", status.value());
        corpo.put("detail", mensagem);
        corpo.put("codigo", codigo.name());
        corpo.put("correlationId", CorrelationIdFilter.atual());
        corpo.put("timestamp", OffsetDateTime.now().toString());
        res.setStatus(status.value());
        res.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        res.setCharacterEncoding(StandardCharsets.UTF_8.name());
        res.getWriter().write(Json.objeto(corpo));
    }
}
