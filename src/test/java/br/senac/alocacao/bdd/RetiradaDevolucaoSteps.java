package br.senac.alocacao.bdd;

import br.senac.alocacao.material.Material;
import br.senac.alocacao.professor.Professor;
import br.senac.alocacao.sala.Sala;
import br.senac.alocacao.suporte.IntegracaoBase;
import br.senac.alocacao.suporte.JsonTeste;
import br.senac.alocacao.usuario.Perfil;
import br.senac.alocacao.usuario.Usuario;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Passos (glue) dos cenários BDD. Todos os passos interagem com o sistema apenas via API HTTP.
 */
@CucumberContextConfiguration
public class RetiradaDevolucaoSteps extends IntegracaoBase {

    private final Map<String, Material> materiaisPorNome = new HashMap<>();
    private Usuario solicitante;
    private Usuario responsavel;
    private String reservaId;
    private Response resposta;

    @Before
    public void limpar() {
        prepararAmbiente();
        materiaisPorNome.clear();
        solicitante = usuario(Perfil.COORDENADOR);
        responsavel = usuario(Perfil.RESPONSAVEL);
    }

    @Dado("que existe o material {string} com {int} unidades")
    public void existeMaterial(String nome, int quantidade) {
        materiaisPorNome.put(nome, material(nome, quantidade, false));
    }

    @E("existe uma reserva em uso com {int} unidades de {string}")
    public void reservaEmUso(int quantidade, String nome) {
        Sala sala = sala("A101", 40, false);
        Professor prof = professor("Ana", "Qualidade");
        reservaId = como(solicitante)
                .body(reservaJson(sala, prof, "Qualidade", "09:00", "11:00", 20,
                        List.of(Map.of("materialId", materiaisPorNome.get(nome).getId(), "quantidade", quantidade))))
                .post("/api/reservas").then().statusCode(201).extract().path("id");
        relogio.definir(em("2030-03-11T09:00"));
        como(solicitante).post("/api/reservas/" + reservaId + "/iniciar").then().statusCode(200);
    }

    @Quando("o responsável registra a retirada de {int} unidades de {string}")
    public void registraRetirada(int qtd, String nome) {
        resposta = movimentar(responsavel, "retiradas", qtd, nome);
    }

    @Quando("o responsável registra a devolução de {int} unidades de {string}")
    public void registraDevolucao(int qtd, String nome) {
        resposta = movimentar(responsavel, "devolucoes", qtd, nome);
    }

    @Dado("que o responsável já retirou {int} unidades de {string}")
    public void jaRetirou(int qtd, String nome) {
        movimentar(responsavel, "retiradas", qtd, nome).then().statusCode(200);
    }

    @Dado("que o responsável já devolveu {int} unidades de {string}")
    public void jaDevolveu(int qtd, String nome) {
        movimentar(responsavel, "devolucoes", qtd, nome).then().statusCode(200);
    }

    @Quando("o solicitante conclui a reserva")
    public void conclui() {
        resposta = como(solicitante).post("/api/reservas/" + reservaId + "/concluir");
    }

    @Quando("um usuário com perfil {string} registra a retirada de {int} unidades de {string}")
    public void perfilRegistra(String perfil, int qtd, String nome) {
        Perfil p = Perfil.valueOf(perfil);
        Usuario u = p == Perfil.PROFESSOR ? usuario(p, professor("Outro", "X")) : usuario(p);
        resposta = movimentar(u, "retiradas", qtd, nome);
    }

    @Entao("a operação é aceita")
    public void aceita() {
        assertThat(resposta.statusCode()).isEqualTo(200);
    }

    @Entao("a operação é recusada com o código {string}")
    public void recusada(String codigo) {
        assertThat(resposta.statusCode()).isBetween(400, 499);
        assertThat(resposta.jsonPath().getString("codigo")).isEqualTo(codigo);
    }

    @E("a mensagem contém {string}")
    public void mensagemContem(String trecho) {
        assertThat(resposta.jsonPath().getString("detail")).contains(trecho);
    }

    @E("constam {int} unidades retiradas e {int} devolvidas de {string}")
    public void constam(int retiradas, int devolvidas, String nome) {
        List<Map<String, Object>> movs = como(responsavel).get("/api/reservas/" + reservaId)
                .jsonPath().getList("movimentacoes");
        String id = materiaisPorNome.get(nome).getId();
        assertThat(soma(movs, id, "RETIRADA")).isEqualTo(retiradas);
        assertThat(soma(movs, id, "DEVOLUCAO")).isEqualTo(devolvidas);
    }

    @Entao("a reserva fica com status {string}")
    public void status(String status) {
        assertThat(resposta.statusCode()).isEqualTo(200);
        assertThat(resposta.jsonPath().getString("status")).isEqualTo(status);
    }

    @E("o histórico da reserva registra {string} e {string}")
    public void historico(String acao1, String acao2) {
        List<String> acoes = como(responsavel).get("/api/reservas/" + reservaId + "/historico")
                .jsonPath().getList("acao");
        assertThat(acoes).contains(acao1, acao2);
    }

    @Entao("a resposta HTTP é {int}")
    public void respostaHttp(int status) {
        assertThat(resposta.statusCode()).isEqualTo(status);
    }

    private Response movimentar(Usuario u, String tipo, int qtd, String nome) {
        return como(u).body(JsonTeste.json(Map.of("materialId", materiaisPorNome.get(nome).getId(), "quantidade", qtd)))
                .post("/api/reservas/" + reservaId + "/" + tipo);
    }

    private static int soma(List<Map<String, Object>> movs, String materialId, String tipo) {
        return movs.stream()
                .filter(m -> materialId.equals(m.get("materialId")) && tipo.equals(m.get("tipo")))
                .mapToInt(m -> ((Number) m.get("quantidade")).intValue())
                .sum();
    }
}
