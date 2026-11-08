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

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Requisitos não funcionais de segurança verificados de fora (caixa-preta):
 * autorização, validação de entrada e tratamento seguro de erros.
 */
@DisplayName("[RNF02][RNF03][RNF04] API caixa-preta: segurança")
class SegurancaApiIT extends IntegracaoBase {

    // ---------------------------------------------------------------- autenticação/autorização

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
    @DisplayName("[RNF02] Token forjado com outra chave e perfil ADMIN é rejeitado")
    void tokenForjado() {
        // header {"alg":"HS256"} . payload {"sub":"x","perfil":"ADMIN","uid":"1"} . assinatura inválida
        String forjado = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ4IiwicGVyZmlsIjoiQURNSU4iLCJ1aWQiOiIxIn0."
                + "c2lnbmF0dXJhLWZhbHNh";
        given().auth().oauth2(forjado).get("/api/usuarios").then().statusCode(401);
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

    // ---------------------------------------------------------------- validação de entrada

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
    @ValueSource(strings = {".*", "{\"$ne\":null}", "$where", "A|B", "(a+)+$"})
    @DisplayName("[RNF03] Injeção NoSQL / regex em filtros não retorna dados indevidos")
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

    // ---------------------------------------------------------------- tratamento seguro de erros

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
                .header("Content-Security-Policy", containsString("default-src 'self'"));
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
