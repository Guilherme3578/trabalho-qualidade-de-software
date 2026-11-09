package br.senac.alocacao.auth;

import br.senac.alocacao.usuario.UsuarioAtual;
import br.senac.alocacao.usuario.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação")
public class AuthController {
    private final AuthService servico;
    private final UsuarioAtual usuarioAtual;

    public AuthController(AuthService servico, UsuarioAtual usuarioAtual) {
        this.servico = servico;
        this.usuarioAtual = usuarioAtual;
    }

    @PostMapping("/login")
    @SecurityRequirements
    @Operation(summary = "Autentica e devolve um token JWT (Bearer)")
    public TokenResponse login(@Valid @RequestBody LoginRequest req) {
        return servico.login(req);
    }

    @GetMapping("/eu")
    @Operation(summary = "Dados do usuário autenticado")
    public UsuarioResponse eu() {
        return UsuarioResponse.de(usuarioAtual.obter());
    }
}
