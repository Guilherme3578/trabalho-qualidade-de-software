package br.senac.alocacao.comum;

import br.senac.alocacao.config.CorrelationIdFilter;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);
    private static final int MYSQL_CHAVE_DUPLICADA = 1062;

    @ExceptionHandler(NegocioException.class)
    public ProblemDetail negocio(NegocioException ex) {
        ProblemDetail p = problema(ex.getStatus(), ex.getCodigo(), ex.getMessage());
        if (!ex.getDetalhes().isEmpty()) {
            p.setProperty("detalhes", ex.getDetalhes());
        }
        return p;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail validacao(MethodArgumentNotValidException ex) {
        List<Map<String, String>> campos = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> Map.of("campo", e.getField(), "mensagem", String.valueOf(e.getDefaultMessage())))
                .toList();
        ProblemDetail p = problema(HttpStatus.BAD_REQUEST, CodigoErro.VALIDACAO,
                "Existem campos inválidos. Corrija-os e tente novamente.");
        p.setProperty("campos", campos);
        return p;
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ProblemDetail validacaoParametros(HandlerMethodValidationException ex) {
        List<Map<String, String>> campos = ex.getParameterValidationResults().stream()
                .flatMap(r -> r.getResolvableErrors().stream().map(e -> Map.of(
                        "campo", String.valueOf(r.getMethodParameter().getParameterName()),
                        "mensagem", String.valueOf(e.getDefaultMessage()))))
                .toList();
        ProblemDetail p = problema(HttpStatus.BAD_REQUEST, CodigoErro.VALIDACAO,
                "Existem parâmetros inválidos. Corrija-os e tente novamente.");
        p.setProperty("campos", campos);
        return p;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail violacao(ConstraintViolationException ex) {
        List<Map<String, String>> campos = ex.getConstraintViolations().stream()
                .map(v -> Map.of("campo", v.getPropertyPath().toString(), "mensagem", v.getMessage()))
                .toList();
        ProblemDetail p = problema(HttpStatus.BAD_REQUEST, CodigoErro.VALIDACAO, "Existem dados inválidos.");
        p.setProperty("campos", campos);
        return p;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail corpoInvalido(HttpMessageNotReadableException ex) {
        return problema(HttpStatus.BAD_REQUEST, CodigoErro.REQUISICAO_INVALIDA,
                "O corpo da requisição está malformado ou contém valores em formato inválido.");
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    public ProblemDetail parametroInvalido(Exception ex) {
        String nome = ex instanceof MethodArgumentTypeMismatchException m ? m.getName()
                : ((MissingServletRequestParameterException) ex).getParameterName();
        return problema(HttpStatus.BAD_REQUEST, CodigoErro.REQUISICAO_INVALIDA,
                "O parâmetro '" + nome + "' está ausente ou em formato inválido.");
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ProblemDetail versao(OptimisticLockingFailureException ex) {
        return problema(HttpStatus.CONFLICT, CodigoErro.VERSAO_DESATUALIZADA,
                "O registro foi alterado por outra pessoa. Recarregue os dados e tente novamente.");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ProblemDetail duplicado(DuplicateKeyException ex) {
        log.warn("Chave duplicada: {}", ex.getMostSpecificCause().getMessage());
        return problema(HttpStatus.CONFLICT, CodigoErro.REGISTRO_DUPLICADO,
                "Já existe um registro com estes dados (e-mail, código ou patrimônio repetido).");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail integridade(DataIntegrityViolationException ex) {
        if (chaveDuplicada(ex)) {
            return duplicado(new DuplicateKeyException(ex.getMessage(), ex));
        }
        log.warn("Violação de integridade: {}", ex.getMostSpecificCause().getMessage());
        return problema(HttpStatus.CONFLICT, CodigoErro.INTEGRIDADE_VIOLADA,
                "A operação viola a integridade dos dados (registro relacionado inexistente ou em uso).");
    }

    static boolean chaveDuplicada(Throwable ex) {
        for (Throwable t = ex; t != null; t = t.getCause()) {
            if (t instanceof DuplicateKeyException
                    || t instanceof SQLException sql && sql.getErrorCode() == MYSQL_CHAVE_DUPLICADA) {
                return true;
            }
            if (t.getCause() == t) {
                break;
            }
        }
        return false;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail acessoNegado(AccessDeniedException ex) {
        return problema(HttpStatus.FORBIDDEN, CodigoErro.ACESSO_NEGADO,
                "Seu perfil não tem permissão para executar esta operação.");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail rotaInexistente(NoResourceFoundException ex) {
        return problema(HttpStatus.NOT_FOUND, CodigoErro.NAO_ENCONTRADO, "Recurso não encontrado.");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail metodo(HttpRequestMethodNotSupportedException ex) {
        return problema(HttpStatus.METHOD_NOT_ALLOWED, CodigoErro.REQUISICAO_INVALIDA,
                "Método HTTP não suportado para este endereço.");
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail inesperado(Exception ex) {
        log.error("Erro inesperado", ex);
        return problema(HttpStatus.INTERNAL_SERVER_ERROR, CodigoErro.ERRO_INTERNO,
                "Ocorreu um erro inesperado. Informe o código de correlação ao suporte.");
    }

    public static ProblemDetail problema(HttpStatus status, CodigoErro codigo, String mensagem) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(status, mensagem);
        p.setTitle(status.getReasonPhrase());
        p.setProperty("codigo", codigo.name());
        p.setProperty("correlationId", CorrelationIdFilter.atual());
        p.setProperty("timestamp", OffsetDateTime.now().toString());
        return p;
    }
}
