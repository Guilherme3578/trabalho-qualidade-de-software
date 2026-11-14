package br.senac.alocacao.api;

import br.senac.alocacao.material.Material;
import br.senac.alocacao.professor.Professor;
import br.senac.alocacao.sala.Sala;
import br.senac.alocacao.suporte.IntegracaoBase;
import br.senac.alocacao.suporte.JsonTeste;
import br.senac.alocacao.usuario.Perfil;
import br.senac.alocacao.usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;

@DisplayName("[RF02] API caixa-preta: alterações de cadastro que afetam reservas futuras")
class ReservasAfetadasApiIT extends IntegracaoBase {
    Usuario admin;
    Usuario coord;
    Sala a101;
    Professor ana;

    @BeforeEach
    void dados() {
        admin = usuario(Perfil.ADMIN);
        coord = usuario(Perfil.COORDENADOR);
        a101 = sala("A101", 40, false);
        ana = professor("Ana Souza", "Qualidade de Software");
    }

    private String reservar(Sala s, String inicio, String fim, int alunos, List<Map<String, Object>> materiais) {
        return como(coord).body(reservaJson(s, ana, "Qualidade de Software", inicio, fim, alunos, materiais))
                .post("/api/reservas").then().statusCode(201).extract().path("id");
    }

    private String salaJson(int capacidade) {
        return JsonTeste.json(JsonTeste.mapa("codigo", "A101", "nome", "Sala A101", "tipo", "SALA_AULA",
                "capacidade", capacidade, "localizacao", "Bloco A", "restrita", false));
    }

    @Test
    @DisplayName("[RF02][RN09] Reduzir a capacidade abaixo dos alunos de uma reserva futura é recusado com a lista de reservas")
    void capacidadeAbaixoDeReservaFutura() {
        String reserva = reservar(a101, "09:00", "11:00", 35, List.of());

        como(admin).body(salaJson(20)).put("/api/salas/" + a101.getId()).then()
                .statusCode(409)
                .body("codigo", equalTo("RESERVAS_AFETADAS"))
                .body("detail", containsString("35 alunos"))
                .body("detalhes.reservas", hasItem(reserva));
        como(admin).get("/api/salas/" + a101.getId()).then().body("capacidade", equalTo(40));
    }

    @Test
    @DisplayName("[RF02][RN09] Reduzir a capacidade sem ficar abaixo de nenhuma reserva futura é aceito")
    void capacidadeSuficiente() {
        reservar(a101, "09:00", "11:00", 20, List.of());
        como(admin).body(salaJson(25)).put("/api/salas/" + a101.getId()).then()
                .statusCode(200).body("capacidade", equalTo(25));
    }

    @Test
    @DisplayName("[RF02][RN09] Reservas já encerradas ou canceladas não impedem a redução da capacidade")
    void reservasEncerradasOuCanceladasNaoContam() {
        reservar(a101, "09:00", "10:00", 35, List.of());
        String cancelada = reservar(a101, "13:00", "14:00", 35, List.of());
        como(coord).body("{}").post("/api/reservas/" + cancelada + "/cancelar").then().statusCode(200);
        relogio.definir(em("2030-03-11T10:30"));

        como(admin).body(salaJson(10)).put("/api/salas/" + a101.getId()).then()
                .statusCode(200).body("capacidade", equalTo(10));
    }

    private String materialJson(int quantidade) {
        return JsonTeste.json(JsonTeste.mapa("nome", "Projetor", "categoria", "Audiovisual",
                "quantidadeTotal", quantidade, "restrito", false));
    }

    private static List<Map<String, Object>> itens(Material m, int quantidade) {
        return List.of(Map.of("materialId", m.getId(), "quantidade", quantidade));
    }

    @Test
    @DisplayName("[RF09][RN02] Reduzir o estoque abaixo do pico já reservado é recusado com a lista de reservas")
    void estoqueAbaixoDoReservado() {
        Material projetor = material("Projetor", 3, false);
        Sala a102 = sala("A102", 40, false);
        Professor bruno = professor("Bruno Lima", "Banco de Dados");
        String primeira = reservar(a101, "09:00", "11:00", 20, itens(projetor, 2));
        String segunda = como(coord).body(reservaJson(a102, bruno, "Banco de Dados", "10:00", "12:00", 20,
                itens(projetor, 1))).post("/api/reservas").then().statusCode(201).extract().path("id");

        como(admin).body(materialJson(2)).put("/api/materiais/" + projetor.getId()).then()
                .statusCode(409)
                .body("codigo", equalTo("RESERVAS_AFETADAS"))
                .body("detail", containsString("3 unidades"))
                .body("detalhes.reservas", hasItems(primeira, segunda));
        como(admin).get("/api/materiais/" + projetor.getId()).then().body("quantidadeTotal", equalTo(3));
    }

    @Test
    @DisplayName("[RF09][RN02] Reduzir o estoque até o pico de uso, sem ficar abaixo dele, é aceito")
    void estoqueIgualAoPico() {
        Material projetor = material("Projetor", 3, false);
        reservar(a101, "09:00", "10:00", 20, itens(projetor, 2));
        reservar(a101, "10:00", "11:00", 20, itens(projetor, 2));

        como(admin).body(materialJson(2)).put("/api/materiais/" + projetor.getId()).then()
                .statusCode(200).body("quantidadeTotal", equalTo(2));
    }
}
