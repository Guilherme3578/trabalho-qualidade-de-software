package br.senac.alocacao.comum;

import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class NegocioException extends RuntimeException {
    private final HttpStatus status;
    private final CodigoErro codigo;
    private final transient Map<String, Object> detalhes;

    public NegocioException(HttpStatus status, CodigoErro codigo, String mensagem) {
        this(status, codigo, mensagem, Map.of());
    }

    public NegocioException(HttpStatus status, CodigoErro codigo, String mensagem, Map<String, Object> detalhes) {
        super(mensagem);
        this.status = status;
        this.codigo = codigo;
        this.detalhes = Collections.unmodifiableMap(new LinkedHashMap<>(detalhes));
    }

    public static NegocioException naoEncontrado(String entidade, Object id) {
        return new NegocioException(HttpStatus.NOT_FOUND, CodigoErro.NAO_ENCONTRADO,
                entidade + " não encontrado(a) (id " + id + ").");
    }

    public static NegocioException conflito(CodigoErro codigo, String mensagem, Map<String, Object> detalhes) {
        return new NegocioException(HttpStatus.CONFLICT, codigo, mensagem, detalhes);
    }

    public static NegocioException regra(CodigoErro codigo, String mensagem) {
        return new NegocioException(HttpStatus.UNPROCESSABLE_CONTENT, codigo, mensagem);
    }

    public static NegocioException acessoNegado(String mensagem) {
        return new NegocioException(HttpStatus.FORBIDDEN, CodigoErro.ACESSO_NEGADO, mensagem);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public CodigoErro getCodigo() {
        return codigo;
    }

    public Map<String, Object> getDetalhes() {
        return detalhes;
    }
}
