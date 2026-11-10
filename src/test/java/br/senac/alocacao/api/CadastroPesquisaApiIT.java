package br.senac.alocacao.api;

import br.senac.alocacao.professor.Professor;
import br.senac.alocacao.sala.Sala;
import br.senac.alocacao.sala.TipoSala;
import br.senac.alocacao.suporte.IntegracaoBase;
import br.senac.alocacao.suporte.JsonTeste;
import br.senac.alocacao.usuario.Perfil;
import br.senac.alocacao.usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@DisplayName("[RF02][RF03] API caixa-preta: cadastros e pesquisa")
class CadastroPesquisaApiIT extends IntegracaoBase {
    Usuario admin;
    Usuario coord;

    @BeforeEach
    void dados() {
        admin = usuario(Perfil.ADMIN);
        coord = usuario(Perfil.COORDENADOR);
    }

    @Test
    @DisplayName("[RF02] ADMIN cadastra sala, professor e material; demais perfis consultam")
    void cadastros() {
        String salaId = como(admin).body(JsonTeste.json(JsonTeste.mapa("codigo", "B201", "nome", "Sala B201",
                        "tipo", "LABORATORIO", "capacidade", 30, "localizacao", "Bloco B", "restrita", false)))
                .post("/api/salas").then().statusCode(201).extract().path("id");
        como(admin).body(JsonTeste.json(JsonTeste.mapa("nome", "Carla", "email", "carla@x.com",
                        "departamento", "Design", "competencias", List.of("UX", "Design de Interfaces"))))
                .post("/api/professores").then().statusCode(201).body("competencias", hasSize(2));
        como(admin).body(JsonTeste.json(JsonTeste.mapa("nome", "Notebook", "categoria", "Informática",
                        "quantidadeTotal", 15, "restrito", false)))
                .post("/api/materiais").then().statusCode(201);

        como(coord).get("/api/salas/" + salaId).then().statusCode(200).body("codigo", equalTo("B201"));
    }

    @Test
    @DisplayName("[RF02] Campos booleanos opcionais omitidos assumem false em vez de erro de formato")
    void booleanosOpcionaisOmitidos() {
        como(admin).body(JsonTeste.json(JsonTeste.mapa("codigo", "C301", "nome", "Sala C301",
                        "tipo", "SALA_AULA", "capacidade", 25, "localizacao", "Bloco C")))
                .post("/api/salas").then().statusCode(201).body("restrita", equalTo(false));
        como(admin).body(JsonTeste.json(JsonTeste.mapa("nome", "Projetor", "categoria", "Audiovisual",
                        "quantidadeTotal", 3)))
                .post("/api/materiais").then().statusCode(201).body("restrito", equalTo(false));
    }

    @Test
    @DisplayName("[RF02] Código de sala duplicado devolve 409 compreensível")
    void duplicado() {
        sala("A101", 40, false);
        como(admin).body(JsonTeste.json(JsonTeste.mapa("codigo", "A101", "nome", "Outra", "tipo", "SALA_AULA",
                        "capacidade", 10, "localizacao", "X", "restrita", false)))
                .post("/api/salas").then().statusCode(409).body("codigo", equalTo("REGISTRO_DUPLICADO"));
    }

    @Test
    @DisplayName("[RF03] Pesquisa de salas por tipo, capacidade e localização")
    void pesquisaSalas() {
        salas.save(new Sala("A1", "A1", TipoSala.SALA_AULA, 40, "Bloco A", false));
        salas.save(new Sala("A2", "A2", TipoSala.SALA_AULA, 20, "Bloco A", false));
        salas.save(new Sala("L1", "L1", TipoSala.LABORATORIO, 30, "Bloco B", false));

        como(coord).get("/api/salas?tipo=SALA_AULA").then().body("codigo", containsInAnyOrder("A1", "A2"));
        como(coord).get("/api/salas?capacidadeMinima=30").then().body("codigo", containsInAnyOrder("A1", "L1"));
        como(coord).get("/api/salas?localizacao=bloco b").then().body("codigo", equalTo(List.of("L1")));
    }

    @Test
    @DisplayName("[RNF03] Curingas e SQL na pesquisa são tratados como texto literal")
    void pesquisaComCuringasLiterais() {
        salas.save(new Sala("A1", "A1", TipoSala.SALA_AULA, 40, "Bloco A", false));
        salas.save(new Sala("P1", "P1", TipoSala.SALA_AULA, 40, "Prédio 100% acessível", false));

        como(coord).get("/api/salas?localizacao=%").then().statusCode(200).body("codigo", equalTo(List.of("P1")));
        como(coord).get("/api/salas?localizacao=_").then().statusCode(200).body("", hasSize(0));
        como(coord).get("/api/salas?localizacao=' OR '1'='1").then().statusCode(200).body("", hasSize(0));
        como(coord).get("/api/salas").then().statusCode(200).body("", hasSize(2));
    }

    @Test
    @DisplayName("[RF03] Pesquisa por disponibilidade exclui salas ocupadas e em manutenção")
    void pesquisaDisponibilidade() {
        Sala ocupada = sala("OCUP", 40, false);
        Sala manutencao = sala("MANUT", 40, false);
        sala("LIVRE", 40, false);
        Professor ana = professor("Ana", "Q");
        como(coord).body(reservaJson(ocupada, ana, "Q", "09:00", "11:00", 10, List.of()))
                .post("/api/reservas").then().statusCode(201);
        como(admin).body(JsonTeste.json(JsonTeste.mapa("tipoRecurso", "SALA", "recursoId", manutencao.getId(),
                        "inicio", "2030-03-11T00:00", "fim", "2030-03-11T23:59", "motivo", "Reforma")))
                .post("/api/bloqueios").then().statusCode(201);

        como(coord).get("/api/salas?inicio=2030-03-11T10:00&fim=2030-03-11T12:00").then()
                .body("codigo", equalTo(List.of("LIVRE")));
    }

    @Test
    @DisplayName("[RF03] Pesquisa de professores por competência e disponibilidade")
    void pesquisaProfessores() {
        professores.save(new Professor("Ana", "ana@x.com", "Comp", Set.of("Banco de Dados")));
        Professor bruno = professores.save(new Professor("Bruno", "bruno@x.com", "Comp", Set.of("Banco de Dados", "Redes")));
        professores.save(new Professor("Carla", "carla@x.com", "Design", Set.of("UX")));

        como(coord).get("/api/professores?competencia=banco de dados").then()
                .body("nome", containsInAnyOrder("Ana", "Bruno"));

        como(coord).body(reservaJson(sala("A1", 40, false), bruno, "Banco de Dados", "09:00", "11:00", 10, List.of()))
                .post("/api/reservas").then().statusCode(201);
        como(coord).get("/api/professores?competencia=Banco de Dados&inicio=2030-03-11T10:00&fim=2030-03-11T11:00")
                .then().body("nome", equalTo(List.of("Ana")));
    }

    @Test
    @DisplayName("[RF03] Pesquisa de materiais informa a quantidade disponível no período")
    void pesquisaMateriais() {
        var notebook = material("Notebook", 10, false);
        como(coord).body(reservaJson(sala("A1", 40, false), professor("Ana", "Q"), "Q", "09:00", "11:00", 10,
                        List.of(Map.of("materialId", notebook.getId(), "quantidade", 4))))
                .post("/api/reservas").then().statusCode(201);
        como(coord).get("/api/materiais?inicio=2030-03-11T10:00&fim=2030-03-11T12:00").then()
                .body("[0].quantidadeDisponivel", equalTo(6));
        como(coord).get("/api/materiais?inicio=2030-03-11T10:00&fim=2030-03-11T12:00&quantidadeMinima=7").then()
                .body("$", hasSize(0));
    }

    @Test
    @DisplayName("[RF10] Alteração de cadastro fica registrada na auditoria com antes/depois")
    void auditoriaCadastro() {
        Sala s = sala("A1", 40, false);
        como(admin).body(JsonTeste.json(JsonTeste.mapa("codigo", "A1", "nome", "Sala A1", "tipo", "SALA_AULA",
                "capacidade", 50, "localizacao", "Bloco A", "restrita", false))).put("/api/salas/" + s.getId())
                .then().statusCode(200);
        como(admin).get("/api/auditoria?entidade=Sala&entidadeId=" + s.getId()).then()
                .statusCode(200)
                .body("[0].acao", equalTo("ALTERACAO"))
                .body("[0].estadoNovo", org.hamcrest.Matchers.containsString("cap=50"));
    }
}
