package br.senac.alocacao.usuario;

import br.senac.alocacao.comum.CodigoErro;
import br.senac.alocacao.comum.NegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class UsuarioAtual {
    private final UsuarioRepository usuarios;

    public UsuarioAtual(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    public Usuario obter() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            throw new NegocioException(HttpStatus.UNAUTHORIZED, CodigoErro.NAO_AUTENTICADO, "Usuário não autenticado.");
        }
        return usuarios.findById(jwt.getClaimAsString("uid"))
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new NegocioException(HttpStatus.UNAUTHORIZED, CodigoErro.NAO_AUTENTICADO,
                        "Usuário inexistente ou desativado. Faça login novamente."));
    }
}
