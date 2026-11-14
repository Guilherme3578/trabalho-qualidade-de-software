package br.senac.alocacao.api;

import br.senac.alocacao.professor.Professor;
import br.senac.alocacao.sala.Sala;
import br.senac.alocacao.suporte.IntegracaoBase;
import br.senac.alocacao.suporte.JsonTeste;
import br.senac.alocacao.usuario.Perfil;
import br.senac.alocacao.usuario.Usuario;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("[RNF02][RNF03][RNF04] API caixa-preta: segurança")
class SegurancaApiIT extends IntegracaoBase {
    @ParameterizedTest(name = "[RNF02] {0} {1} sem token → 401")
    @CsvSource({"GET,/api/salas", "GET,/api/reservas", "POST,/api/reservas", "GET,/api/relatorios/utilizacao",
            "GET,/api/auditoria", "GET,/api/usuarios"})
    @DisplayName("[RNF02] Rotas protegidas exigem autenticação")
    void semToken(String metodo, String rota) {
        given().contentType(ContentType.JSON).body("{}").request(metodo, rota).then()
                .statusCode(401)
                .contentType(containsString("application/problem+json"))
                .body("codigo", equalTo("NAO_AUTENTICADO"));
    }

    @Test
    @DisplayName("[RNF02] Token adulterado é rejeitado")
    void tokenAdulterado() {
        String token = token(usuario(Perfil.PROFESSOR));
        String adulterado = token.substring(0, token.length() - 4) + "AAAA";
        given().auth().oauth2(adulterado).get("/api/salas").then().statusCode(401);
    }

    @Test
    @DisplayName("[RNF02] Usuário desativado perde o acesso imediatamente, mesmo com token ainda válido")
    void usuarioDesativadoPerdeAcesso() {
        Usuario u = usuario(Perfil.ADMIN);
        String token = token(u);
        given().auth().oauth2(token).get("/api/salas").then().statusCode(200);

        u.setAtivo(false);
        usuarios.save(u);

        given().auth().oauth2(token).get("/api/salas").then().statusCode(401)
                .body("codigo", equalTo("NAO_AUTENTICADO"));
        given().auth().oauth2(token).get("/api/usuarios").then().statusCode(401);
    }

    @Test
    @DisplayName("[RNF02] Mudança de perfil vale imediatamente: o perfil antigo do token não é aceito")
    void perfilAtualizadoValeImediatamente() {
        Usuario u = usuario(Perfil.ADMIN);
        String token = token(u);
        jdbc.update("UPDATE usuarios SET perfil = ? WHERE id = ?", Perfil.PROFESSOR.name(), u.getId());

        given().auth().oauth2(token).get("/api/usuarios").then().statusCode(403);
    }

    @Test
    @DisplayName("[RNF02] Token forjado com outra chave e perfil ADMIN é rejeitado")
    void tokenForjado() {
        String forjado = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ4IiwicGVyZmlsIjoiQURNSU4iLCJ1aWQiOiIxIn0."
                + "c2lnbmF0dXJhLWZhbHNh";
        given().auth().oauth2(forjado).get("/api/usuarios").then().statusCode(401);
    }

    @Autowired JwtEncoder jwtEncoder;

    private String tokenComEmissor(Usuario u, String emissor) {
        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .subject(u.getEmail())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                .claim("uid", u.getId())
                .claim("perfil", u.getPerfil().name());
        if (emissor != null) {
            claims.issuer(emissor);
        }
        return jwtEncoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(),
                claims.build())).getTokenValue();
    }

    @Test
    @DisplayName("[RNF02] Token assinado com a chave certa, mas emitido por outro sistema ou sem emissor, é recusado")
    void emissorDoToken() {
        Usuario u = usuario(Perfil.ADMIN);
        given().auth().oauth2(tokenComEmissor(u, "alocacao-salas")).get("/api/salas").then().statusCode(200);
        given().auth().oauth2(tokenComEmissor(u, "outro-sistema")).get("/api/salas").then().statusCode(401);
        given().auth().oauth2(tokenComEmissor(u, null)).get("/api/salas").then().statusCode(401);
    }

    @ParameterizedTest(name = "[RNF02] {0} não pode {1} {2} → 403")
    @CsvSource({
            "PROFESSOR,POST,/api/salas",
            "COORDENADOR,POST,/api/materiais",
            "RESPONSAVEL,POST,/api/professores",
            "PROFESSOR,GET,/api/usuarios",
            "PROFESSOR,GET,/api/auditoria",
            "PROFESSOR,GET,/api/relatorios/utilizacao?inicio=2030-03-01&fim=2030-03-31",
            "ADMIN,POST,/api/reservas",
            "RESPONSAVEL,POST,/api/reservas",
            "COORDENADOR,POST,/api/reservas/qualquer/aprovar",
            "ADMIN,POST,/api/reservas/qualquer/aprovar",
            "PROFESSOR,POST,/api/reservas/qualquer/retiradas"
    })
    @DisplayName("[RNF02][RN06] Autorização por perfil")
    void autorizacaoPorPerfil(Perfil perfil, String metodo, String rota) {
        Professor p = perfil == Perfil.PROFESSOR ? professor("Prof", "X") : null;
        como(usuario(perfil, p)).body("{}").request(metodo, rota).then()
                .statusCode(403)
                .body("codigo", equalTo("ACESSO_NEGADO"));
    }

    @Test
    @DisplayName("[RNF02] Professor não pode reservar na agenda de outro professor")
    void professorSoReservaParaSi() {
        Professor ana = professor("Ana", "Qualidade");
        Professor outro = professor("Outro", "Qualidade");
        Sala s = sala("A1", 30, false);
        como(usuario(Perfil.PROFESSOR, ana))
                .body(reservaJson(s, outro, "Qualidade", "09:00", "10:00", 10, List.of()))
                .post("/api/reservas").then().statusCode(403);
    }

    @Test
    @DisplayName("[RNF02] Usuário não pode cancelar reserva de outro usuário")
    void cancelarReservaAlheia() {
        Professor ana = professor("Ana", "Qualidade");
        Sala s = sala("A1", 30, false);
        String id = como(usuario(Perfil.COORDENADOR))
                .body(reservaJson(s, ana, "Qualidade", "09:00", "10:00", 10, List.of()))
                .post("/api/reservas").then().statusCode(201).extract().path("id");
        como(usuario(Perfil.COORDENADOR)).post("/api/reservas/" + id + "/cancelar").then().statusCode(403);
    }

    @Test
    @DisplayName("[RNF03][RF13] Campos inválidos devolvem 400 com a lista de mensagens em português")
    void validacaoCampos() {
        como(usuario(Perfil.ADMIN))
                .body(JsonTeste.json(JsonTeste.mapa("codigo", "A 1 !", "nome", "", "tipo", null,
                        "capacidade", 0, "localizacao", "")))
                .post("/api/salas").then()
                .statusCode(400)
                .body("codigo", equalTo("VALIDACAO"))
                .body("campos", hasSize(5))
                .body("campos.mensagem", hasItem("A capacidade deve ser de pelo menos 1 pessoa."))
                .body("campos.mensagem", hasItem("O código deve ter até 20 letras, números ou hífen."));
    }

    @ParameterizedTest(name = "[RNF03] corpo malformado: {0}")
    @ValueSource(strings = {"{", "não é json", "{\"capacidade\": \"muitos\"}", "[]"})
    @DisplayName("[RNF03] JSON malformado devolve 400 sem detalhes internos")
    void jsonMalformado(String corpo) {
        como(usuario(Perfil.ADMIN)).body(corpo).post("/api/salas").then()
                .statusCode(400)
                .body("codigo", equalTo("REQUISICAO_INVALIDA"))
                .body(not(containsString("Exception")))
                .body(not(containsString("jackson")));
    }

    @Test
    @DisplayName("[RNF03] Parâmetro de data em formato inválido devolve 400 legível")
    void parametroInvalido() {
        como(usuario(Perfil.PROFESSOR, professor("P", "X")))
                .get("/api/salas?inicio=ontem&fim=amanha").then()
                .statusCode(400)
                .body("detail", containsString("'inicio'"));
    }

    @ParameterizedTest(name = "[RNF03] busca com \"{0}\" é tratada como texto")
    @ValueSource(strings = {"%", "_", "' OR '1'='1", "Bloco A' -- ", "'; DROP TABLE salas; --",
            "\\", ".*", "{\"$ne\":null}"})
    @DisplayName("[RNF03] Injeção de SQL e curingas em filtros não retornam dados indevidos")
    void injecaoNoFiltro(String malicioso) {
        sala("A101", 40, false);
        como(usuario(Perfil.COORDENADOR)).queryParam("localizacao", malicioso).get("/api/salas").then()
                .statusCode(200)
                .body("$", hasSize(0));
    }

    @Test
    @DisplayName("[RNF03] Campos extras no corpo (ex.: status) são ignorados — sem atribuição em massa")
    void atribuicaoEmMassa() {
        Professor ana = professor("Ana", "Qualidade");
        Sala restrita = sala("LAB", 30, true);
        String corpo = reservaJson(restrita, ana, "Qualidade", "09:00", "10:00", 10, List.of())
                .replaceFirst("\\{", "{\"status\":\"APROVADA\",\"id\":\"abc\",");
        como(usuario(Perfil.COORDENADOR)).body(corpo).post("/api/reservas").then()
                .statusCode(201)
                .body("status", equalTo("SOLICITADA"))
                .body("id", not(equalTo("abc")));
    }

    @Test
    @DisplayName("[RNF04] Erros incluem correlationId e não expõem stack trace")
    void erroSeguro() {
        como(usuario(Perfil.COORDENADOR)).get("/api/reservas/id-inexistente").then()
                .statusCode(404)
                .header("X-Correlation-Id", notNullValue())
                .body("correlationId", notNullValue())
                .body("codigo", equalTo("NAO_ENCONTRADO"))
                .body(not(containsString("at br.senac")))
                .body(not(containsString("trace")));
    }

    @Test
    @DisplayName("[RNF04] Rota inexistente devolve 404 no formato padrão")
    void rotaInexistente() {
        como(usuario(Perfil.ADMIN)).get("/api/nao-existe").then().statusCode(404).body("codigo", equalTo("NAO_ENCONTRADO"));
    }

    @Test
    @DisplayName("[RNF02] Cabeçalhos de segurança presentes nas respostas")
    void cabecalhos() {
        given().get("/index.html").then()
                .statusCode(200)
                .header("X-Content-Type-Options", "nosniff")
                .header("X-Frame-Options", "DENY")
                .header("Content-Security-Policy", containsString("default-src 'self'"))
                .header("Content-Security-Policy", not(containsString("unsafe-inline")));
    }

    @Test
    @DisplayName("[RNF02] A página do front leva um nonce de CSP novo a cada requisição, igual ao do cabeçalho")
    void nonceDaPolitica() {
        java.util.function.Function<String, String[]> nonces = rota -> {
            var resposta = given().get(rota).then().statusCode(200).extract();
            java.util.regex.Matcher noCabecalho = java.util.regex.Pattern.compile("'nonce-([A-Za-z0-9+/=_-]+)'")
                    .matcher(resposta.header("Content-Security-Policy"));
            java.util.regex.Matcher naPagina = java.util.regex.Pattern.compile("ngCspNonce=\"([^\"]+)\"")
                    .matcher(resposta.asString());
            return new String[]{noCabecalho.find() ? noCabecalho.group(1) : null,
                    naPagina.find() ? naPagina.group(1) : null};
        };
        String[] primeira = nonces.apply("/");
        String[] segunda = nonces.apply("/reservas/nova");

        org.assertj.core.api.Assertions.assertThat(primeira[0]).isNotBlank().isEqualTo(primeira[1]);
        org.assertj.core.api.Assertions.assertThat(segunda[0]).isNotBlank().isEqualTo(segunda[1]);
        org.assertj.core.api.Assertions.assertThat(primeira[0]).isNotEqualTo(segunda[0]);
    }

    @Test
    @DisplayName("[RF13] Rotas internas do Angular devolvem a página da aplicação sem exigir token")
    void rotasDoFront() {
        given().get("/reservas/nova").then()
                .statusCode(200)
                .contentType(containsString("text/html"))
                .body(containsString("<app-root"));
    }

    @Test
    @DisplayName("[RNF02] Liberar as rotas do front não abre a API: sem token continua 401")
    void rotasDoFrontNaoAbremApi() {
        given().get("/api/reservas").then().statusCode(401);
    }

    @Test
    @DisplayName("[RNF02] Lista de usuários (ADMIN) nunca devolve o hash da senha")
    void semHash() {
        Usuario admin = usuario(Perfil.ADMIN);
        como(admin).get("/api/usuarios").then().statusCode(200)
                .body(not(containsString("senhaHash")))
                .body(not(containsString("$2a$")));
    }
}
