package br.senac.alocacao.api;

import br.senac.alocacao.suporte.IntegracaoBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("[RF14] Documentação pública da API")
class DocumentacaoApiIT extends IntegracaoBase {
    @Test
    @DisplayName("[RF14] Especificação OpenAPI publicada com os principais fluxos e o esquema de segurança")
    void openApi() {
        given().get("/v3/api-docs").then()
                .statusCode(200)
                .body("info.title", containsString("Alocação"))
                .body("paths.'/api/reservas'.post", notNullValue())
                .body("paths.'/api/reservas/{id}/aprovar'.post", notNullValue())
                .body("paths.'/api/salas'.get.parameters.name", org.hamcrest.Matchers.hasItems("tipo",
                        "capacidadeMinima", "localizacao", "inicio", "fim"))
                .body("components.securitySchemes.bearer.scheme", equalTo("bearer"));
    }

    @Test
    @DisplayName("[RF14] Swagger UI acessível sem autenticação")
    void swaggerUi() {
        given().redirects().follow(true).get("/swagger-ui.html").then()
                .statusCode(200)
                .body(containsString("swagger"));
    }
}
