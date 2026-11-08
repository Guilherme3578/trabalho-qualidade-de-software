package br.senac.alocacao.api;

import br.senac.alocacao.material.Material;
import br.senac.alocacao.professor.Professor;
import br.senac.alocacao.sala.Sala;
import br.senac.alocacao.suporte.IntegracaoBase;
import br.senac.alocacao.suporte.JsonTeste;
import br.senac.alocacao.usuario.Perfil;
import br.senac.alocacao.usuario.Usuario;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@DisplayName("[RF04][RF05] API caixa-preta: reservas")
class ReservaApiIT extends IntegracaoBase {

    Usuario coord;
    Usuario responsavel;
    Sala a101;
    Sala lab;
    Professor ana;
    Material projetor;

    @BeforeEach
    void dados() {
        coord = usuario(Perfil.COORDENADOR);
        responsavel = usuario(Perfil.RESPONSAVEL);
        a101 = sala("A101", 40, false);
        lab = sala("LAB", 25, true);
        ana = professor("Ana", "Qualidade de Software");
        projetor = material("Projetor", 2, false);
    }

    private ExtractableResponse<Response> criar(Usuario u, Sala s, String ini, String fim, int status) {
        return como(u).body(reservaJson(s, ana, "Qualidade de Software", ini, fim, 20,
                        List.of(Map.of("materialId", projetor.getId(), "quantidade", 1))))
                .post("/api/reservas").then().statusCode(status).extract();
    }

    @Test
    @DisplayName("[RF04] Criar reserva devolve 201 com os dados e status APROVADA")
    void criar() {
        como(coord).body(reservaJson(a101, ana, "Qualidade de Software", "09:00", "11:00", 20,
                        List.of(Map.of("materialId", projetor.getId(), "quantidade", 2))))
                .post("/api/reservas").then()
                .statusCode(201)
                .body("status", equalTo("APROVADA"))
                .body("salaCodigo", equalTo("A101"))
                .body("materiais", hasSize(1))
                .body("materiais[0].quantidade", equalTo(2));
    }

    @Test
    @DisplayName("[RF05][RF13] Conflito devolve 409 com mensagem compreensível e dados do conflito")
    void conflito() {
        String id = criar(coord, a101, "09:00", "11:00", 201).path("id");
        criar(coord, a101, "10:00", "12:00", 409).response().then()
                .body("codigo", equalTo("CONFLITO_SALA"))
                .body("detail", equalTo("A sala A101 já está reservada das 09:00 às 11:00 neste dia."))
                .body("detalhes.reservaConflitanteId", equalTo(id));
    }

    @Test
    @DisplayName("[RN01] Término antes do início devolve 422 com mensagem clara")
    void terminoAntesDoInicio() {
        criar(coord, a101, "11:00", "09:00", 422).response().then()
                .body("codigo", equalTo("HORARIO_INVALIDO"))
                .body("detail", equalTo("O término deve ser posterior ao início."));
    }

    @Test
    @DisplayName("[RF04] Alterar e cancelar a própria reserva")
    void alterarECancelar() {
        ExtractableResponse<Response> r = criar(coord, a101, "09:00", "11:00", 201);
        String id = r.path("id");
        Integer versao = r.path("versao");

        String corpo = JsonTeste.json(JsonTeste.mapa("salaId", a101.getId(), "professorId", ana.getId(),
                "disciplina", "Qualidade de Software", "inicio", "2030-03-11T13:00", "fim", "2030-03-11T15:00",
                "quantidadeAlunos", 25, "versao", versao));
        como(coord).body(corpo).put("/api/reservas/" + id).then()
                .statusCode(200).body("inicio", containsString("13:00")).body("quantidadeAlunos", equalTo(25));

        como(coord).body(JsonTeste.json(Map.of("observacao", "Turma dispensada")))
                .post("/api/reservas/" + id + "/cancelar").then()
                .statusCode(200).body("status", equalTo("CANCELADA"));
    }

    @Test
    @DisplayName("[RF07][RN06] Fluxo de aprovação de sala restrita pelo responsável")
    void aprovacao() {
        String id = criar(coord, lab, "09:00", "11:00", 201).path("id");
        como(responsavel).get("/api/reservas?status=SOLICITADA").then().body("id", hasSize(1));
        como(responsavel).post("/api/reservas/" + id + "/aprovar").then()
                .statusCode(200).body("status", equalTo("APROVADA"));
        como(responsavel).post("/api/reservas/" + id + "/aprovar").then()
                .statusCode(409).body("codigo", equalTo("TRANSICAO_INVALIDA"));
    }

    @Test
    @DisplayName("[RF10][RN08] Histórico da reserva lista cada mudança de estado")
    void historico() {
        String id = criar(coord, lab, "09:00", "11:00", 201).path("id");
        como(responsavel).post("/api/reservas/" + id + "/rejeitar").then().statusCode(200);
        como(coord).get("/api/reservas/" + id + "/historico").then()
                .statusCode(200)
                .body("acao", equalTo(List.of("CRIACAO", "TRANSICAO_ESTADO")))
                .body("[1].estadoAnterior", equalTo("SOLICITADA"))
                .body("[1].estadoNovo", equalTo("REJEITADA"));
    }

    @Test
    @DisplayName("[RN07] DELETE de reserva iniciada devolve 409")
    void apagarIniciada() {
        String id = criar(coord, a101, "09:00", "11:00", 201).path("id");
        relogio.definir(em("2030-03-11T09:00"));
        como(coord).post("/api/reservas/" + id + "/iniciar").then().statusCode(200);
        como(coord).delete("/api/reservas/" + id).then()
                .statusCode(409).body("codigo", equalTo("RESERVA_INICIADA"));
    }

    @Test
    @DisplayName("[RF09] Retirada e devolução de material pelo responsável")
    void retiradaDevolucao() {
        String id = criar(coord, a101, "09:00", "11:00", 201).path("id");
        relogio.definir(em("2030-03-11T09:00"));
        como(coord).post("/api/reservas/" + id + "/iniciar").then().statusCode(200);
        String mov = JsonTeste.json(Map.of("materialId", projetor.getId(), "quantidade", 1));
        como(responsavel).body(mov).post("/api/reservas/" + id + "/retiradas").then()
                .statusCode(200).body("movimentacoes", hasSize(1));
        como(responsavel).body(mov).post("/api/reservas/" + id + "/devolucoes").then()
                .statusCode(200).body("movimentacoes[1].tipo", equalTo("DEVOLUCAO"));
        como(coord).post("/api/reservas/" + id + "/concluir").then()
                .statusCode(200).body("status", equalTo("CONCLUIDA"));
    }

    @Test
    @DisplayName("[RF04] minhas=true filtra as reservas do usuário logado")
    void minhas() {
        criar(coord, a101, "09:00", "10:00", 201);
        Usuario outro = usuario(Perfil.COORDENADOR);
        como(outro).get("/api/reservas?minhas=true").then().body("$", hasSize(0));
        como(coord).get("/api/reservas?minhas=true").then().body("$", hasSize(1));
    }
}
