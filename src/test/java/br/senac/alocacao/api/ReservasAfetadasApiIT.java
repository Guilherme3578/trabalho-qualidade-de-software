package br.senac.alocacao.api;

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
}
