package br.senac.alocacao.config;

import br.senac.alocacao.usuario.Usuario;
import br.senac.alocacao.usuario.UsuarioRepository;
import br.senac.alocacao.web.FrontendConfig;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private static final int TAMANHO_MINIMO_SEGREDO = 32;
    private static final String ADMIN = "ADMIN";
    private static final String RESPONSAVEL = "RESPONSAVEL";
    private static final String COORDENADOR = "COORDENADOR";
    private static final String PROFESSOR = "PROFESSOR";

    @Bean
    public SecurityFilterChain filtros(HttpSecurity http, RespostasSeguranca respostas, UsuarioRepository usuarios)
            throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, FrontendConfig.ARQUIVOS).permitAll()
                        .requestMatchers(HttpMethod.GET, FrontendConfig.ROTAS).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/actuator/health", "/error").permitAll()
                        .requestMatchers("/api/usuarios/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/salas/**", "/api/professores/**",
                                "/api/materiais/**", "/api/bloqueios/**").authenticated()
                        .requestMatchers("/api/salas/**", "/api/professores/**", "/api/materiais/**",
                                "/api/bloqueios/**").hasRole(ADMIN)
                        .requestMatchers("/api/auditoria/**").hasAnyRole(ADMIN, RESPONSAVEL)
                        .requestMatchers("/api/relatorios/**").hasAnyRole(COORDENADOR, RESPONSAVEL, ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/reservas/*/aprovar", "/api/reservas/*/rejeitar",
                                "/api/reservas/*/nao-compareceu", "/api/reservas/*/retiradas",
                                "/api/reservas/*/devolucoes").hasRole(RESPONSAVEL)
                        .requestMatchers(HttpMethod.POST, "/api/reservas", "/api/reservas/*/cancelar")
                        .hasAnyRole(PROFESSOR, COORDENADOR)
                        .requestMatchers(HttpMethod.PUT, "/api/reservas/*").hasAnyRole(PROFESSOR, COORDENADOR)
                        .anyRequest().authenticated())
                .oauth2ResourceServer(o -> o
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(conversorPerfis(usuarios)))
                        .authenticationEntryPoint(respostas.naoAutenticado())
                        .accessDeniedHandler(respostas.acessoNegado()))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(respostas.naoAutenticado())
                        .accessDeniedHandler(respostas.acessoNegado()))
                .headers(h -> h
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'self'; img-src 'self' data:; style-src 'self' 'unsafe-inline'; "
                                        + "script-src 'self'; frame-ancestors 'none'; form-action 'self'"))
                        .frameOptions(f -> f.deny()));
        return http.build();
    }

    static Converter<Jwt, AbstractAuthenticationToken> conversorPerfis(UsuarioRepository usuarios) {
        return jwt -> {
            String uid = jwt.getClaimAsString("uid");
            Usuario u = (uid == null ? null : usuarios.findById(uid).filter(Usuario::isAtivo).orElse(null));
            if (u == null) {
                throw new InvalidBearerTokenException("Usuário inexistente ou desativado.");
            }
            return new JwtAuthenticationToken(jwt,
                    List.of(new SimpleGrantedAuthority("ROLE_" + u.getPerfil().name())), u.getEmail());
        };
    }

    @Bean
    public JwtDecoder jwtDecoder(AppProperties props) {
        return NimbusJwtDecoder.withSecretKey(chave(props)).macAlgorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    public JwtEncoder jwtEncoder(AppProperties props) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(chave(props)));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private SecretKey chave(AppProperties props) {
        String segredo = props.jwt().segredo();
        if (segredo == null || segredo.length() < TAMANHO_MINIMO_SEGREDO) {
            throw new IllegalStateException(
                    "Defina app.jwt.segredo (variável JWT_SEGREDO) com pelo menos 32 caracteres.");
        }
        return new SecretKeySpec(segredo.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }
}
