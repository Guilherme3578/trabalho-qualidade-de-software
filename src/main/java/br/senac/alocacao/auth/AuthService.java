package br.senac.alocacao.auth;

import br.senac.alocacao.auditoria.AcaoAuditoria;
import br.senac.alocacao.auditoria.AuditoriaService;
import br.senac.alocacao.comum.CodigoErro;
import br.senac.alocacao.comum.NegocioException;
import br.senac.alocacao.config.AppProperties;
import br.senac.alocacao.usuario.Usuario;
import br.senac.alocacao.usuario.UsuarioRepository;
import br.senac.alocacao.usuario.UsuarioResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    private final String hashFicticio;

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final JwtEncoder jwtEncoder;
    private final LimitadorTentativasLogin limitador;
    private final AuditoriaService auditoria;
    private final AppProperties props;

    public AuthService(UsuarioRepository usuarios, PasswordEncoder encoder, JwtEncoder jwtEncoder,
                       LimitadorTentativasLogin limitador, AuditoriaService auditoria, AppProperties props) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.jwtEncoder = jwtEncoder;
        this.limitador = limitador;
        this.auditoria = auditoria;
        this.props = props;
        this.hashFicticio = encoder.encode(UUID.randomUUID().toString());
    }

    public TokenResponse login(LoginRequest req) {
        String email = req.email().trim().toLowerCase(Locale.ROOT);
        limitador.verificar(email);

        Optional<Usuario> encontrado = usuarios.findByEmail(email);
        boolean senhaOk = encoder.matches(req.senha(), encontrado.map(Usuario::getSenhaHash).orElse(hashFicticio));

        if (encontrado.isEmpty() || !senhaOk || !encontrado.get().isAtivo()) {
            limitador.registrarFalha(email);
            auditoria.registrar("Usuario", encontrado.map(Usuario::getId).orElse(null), AcaoAuditoria.LOGIN_FALHA,
                    null, null, null, email);
            throw new NegocioException(HttpStatus.UNAUTHORIZED, CodigoErro.CREDENCIAIS_INVALIDAS,
                    "E-mail ou senha inválidos.");
        }

        Usuario u = encontrado.get();
        limitador.limpar(email);
        auditoria.registrar("Usuario", u.getId(), AcaoAuditoria.LOGIN_SUCESSO, u, null, null, null);

        Instant agora = Instant.now();
        Instant expira = agora.plus(props.jwt().expiracao());
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("alocacao-salas")
                .subject(u.getEmail())
                .issuedAt(agora)
                .expiresAt(expira)
                .claim("uid", u.getId())
                .claim("nome", u.getNome())
                .claim("perfil", u.getPerfil().name())
                .build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
        return new TokenResponse(token, "Bearer", expira, UsuarioResponse.de(u));
    }
}
