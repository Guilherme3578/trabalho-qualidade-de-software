package br.senac.alocacao.api;

import br.senac.alocacao.suporte.IntegracaoBase;
import br.senac.alocacao.suporte.JsonTeste;
import br.senac.alocacao.usuario.Perfil;
import br.senac.alocacao.usuario.Usuario;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

@DisplayName("[RF01] API caixa-preta: autenticação")
class AutenticacaoApiIT extends IntegracaoBase {

    private io.restassured.response.ValidatableResponse login(String email, String senha) {
        return given().contentType(ContentType.JSON)
                .body(JsonTeste.json(Map.of("email", email, "senha", senha)))
                .post("/api/auth/login").then();
    }

    @Test
    @DisplayName("[RF01] Credenciais válidas devolvem token Bearer e perfil, sem o hash da senha")
    void loginValido() {
        Usuario u = usuario(Perfil.RESPONSAVEL);
        login(u.getEmail(), SENHA)
                .statusCode(200)
                .body("token", not(emptyOrNullString()))
                .body("tipo", equalTo("Bearer"))
                .body("usuario.perfil", equalTo("RESPONSAVEL"))
                .body("usuario.senhaHash", nullValue());
    }

    @Test
    @DisplayName("[RF01][RNF04] Senha errada e e-mail inexistente devolvem a mesma mensagem genérica")
    void mensagemGenerica() {
        Usuario u = usuario(Perfil.PROFESSOR);
        login(u.getEmail(), "SenhaErrada1").statusCode(401)
                .body("codigo", equalTo("CREDENCIAIS_INVALIDAS"))
                .body("detail", equalTo("E-mail ou senha inválidos."));
        login("naoexiste@teste.br", "SenhaErrada1").statusCode(401)
                .body("detail", equalTo("E-mail ou senha inválidos."));
    }

    @Test
    @DisplayName("[RF01] Usuário desativado não consegue entrar")
    void inativo() {
        Usuario u = usuario(Perfil.PROFESSOR);
        u.setAtivo(false);
        usuarios.save(u);
        login(u.getEmail(), SENHA).statusCode(401);
    }

    @Test
    @DisplayName("[RNF02] Após 5 falhas o login é bloqueado temporariamente (429)")
    void forcaBruta() {
        Usuario u = usuario(Perfil.ADMIN);
        for (int i = 0; i < 5; i++) {
            login(u.getEmail(), "Errada123").statusCode(401);
        }
        login(u.getEmail(), SENHA).statusCode(429).body("codigo", equalTo("MUITAS_TENTATIVAS"));
    }

    @Test
    @DisplayName("[RF01] /api/auth/eu devolve o usuário do token")
    void eu() {
        Usuario u = usuario(Perfil.COORDENADOR);
        como(u).get("/api/auth/eu").then().statusCode(200).body("email", equalTo(u.getEmail()));
    }
}
