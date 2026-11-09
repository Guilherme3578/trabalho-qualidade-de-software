package br.senac.alocacao.api;

import br.senac.alocacao.professor.Professor;
import br.senac.alocacao.sala.Sala;
import br.senac.alocacao.suporte.IntegracaoBase;
import br.senac.alocacao.usuario.Perfil;
import br.senac.alocacao.usuario.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@DisplayName("[RF12] API caixa-preta: relatórios")
class RelatorioApiIT extends IntegracaoBase {
    @Test
    @DisplayName("[RF12] Utilização, carga horária e conflitos evitados refletem as reservas")
    void relatorios() {
        Usuario coord = usuario(Perfil.COORDENADOR);
        Sala a101 = sala("A101", 40, false);
        Professor ana = professor("Ana", "Q");
        Professor bruno = professor("Bruno", "Q");

        como(coord).body(reservaJson(a101, ana, "Q", "08:00", "12:00", 10, List.of()))
                .post("/api/reservas").then().statusCode(201);
        como(coord).body(reservaJson(a101, bruno, "Q", "13:00", "15:00", 10, List.of()))
                .post("/api/reservas").then().statusCode(201);
        como(coord).body(reservaJson(a101, bruno, "Q", "09:00", "10:00", 10, List.of()))
                .post("/api/reservas").then().statusCode(409);

        String periodo = "?inicio=2030-03-11&fim=2030-03-11";
        como(coord).get("/api/relatorios/utilizacao" + periodo).then().statusCode(200)
                .body("salas[0].reservas", equalTo(2))
                .body("salas[0].horasReservadas", equalTo(6.0f))
                .body("salas[0].taxaOcupacaoPercentual", equalTo(37.5f));

        como(coord).get("/api/relatorios/carga-horaria" + periodo).then().statusCode(200)
                .body("professorNome", equalTo(List.of("Ana", "Bruno")))
                .body("horasAlocadas", equalTo(List.of(4.0f, 2.0f)));

        como(coord).get("/api/relatorios/conflitos-evitados" + periodo.replace("2030-03-11", "2030-03-11")).then()
                .statusCode(200);
    }

    @Test
    @DisplayName("[RF12] Conflitos evitados são contabilizados por tipo")
    void conflitosEvitados() {
        Usuario coord = usuario(Perfil.COORDENADOR);
        Sala a101 = sala("A101", 40, false);
        Professor ana = professor("Ana", "Q");
        como(coord).body(reservaJson(a101, ana, "Q", "09:00", "11:00", 10, List.of())).post("/api/reservas");
        como(coord).body(reservaJson(a101, professor("Bruno", "Q"), "Q", "09:00", "11:00", 10, List.of()))
                .post("/api/reservas").then().statusCode(409);
        como(coord).body(reservaJson(sala("A102", 40, false), ana, "Q", "10:00", "11:00", 10, List.of()))
                .post("/api/reservas").then().statusCode(409);

        como(coord).get("/api/relatorios/conflitos-evitados?inicio=2030-03-11&fim=2030-03-11").then()
                .body("total", equalTo(2))
                .body("porTipo.CONFLITO_SALA", equalTo(1))
                .body("porTipo.CONFLITO_PROFESSOR", equalTo(1))
                .body("porRecurso", hasSize(2));
    }

    @Test
    @DisplayName("[RF12] Período inválido devolve mensagem compreensível")
    void periodoInvalido() {
        como(usuario(Perfil.ADMIN)).get("/api/relatorios/utilizacao?inicio=2030-03-10&fim=2030-03-01").then()
                .statusCode(422).body("detail", equalTo("A data final deve ser igual ou posterior à inicial."));
    }
}
