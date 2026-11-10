package br.senac.alocacao.suporte;

import br.senac.alocacao.material.Material;
import br.senac.alocacao.material.MaterialRepository;
import br.senac.alocacao.professor.Professor;
import br.senac.alocacao.professor.ProfessorRepository;
import br.senac.alocacao.sala.Sala;
import br.senac.alocacao.sala.SalaRepository;
import br.senac.alocacao.sala.TipoSala;
import br.senac.alocacao.usuario.Perfil;
import br.senac.alocacao.usuario.Usuario;
import br.senac.alocacao.usuario.UsuarioRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(ContainersTeste.class)
public abstract class IntegracaoBase {
    public static final String SENHA = "Senha123";
    protected static final WireMockServer WIREMOCK = new WireMockServer(options().dynamicPort());

    static {
        WIREMOCK.start();
    }

    @DynamicPropertySource
    static void propriedades(DynamicPropertyRegistry registro) {
        registro.add("app.notificacao.url", () -> WIREMOCK.baseUrl() + "/notificacoes");
        ContainersTeste.Conexao banco = ContainersTeste.conexao();
        registro.add("spring.datasource.url", banco::url);
        registro.add("spring.datasource.username", banco::usuario);
        registro.add("spring.datasource.password", banco::senha);
    }

    @LocalServerPort
    protected int porta;

    @Autowired protected JdbcTemplate jdbc;
    @Autowired protected RelogioTeste relogio;
    @Autowired protected PasswordEncoder encoder;
    @Autowired protected UsuarioRepository usuarios;
    @Autowired protected SalaRepository salas;
    @Autowired protected ProfessorRepository professores;
    @Autowired protected MaterialRepository materiais;

    @BeforeEach
    protected void prepararAmbiente() {
        LimpezaBanco.limpar(jdbc);
        relogio.definir(RelogioTeste.AGORA_PADRAO);
        WIREMOCK.resetAll();
        WIREMOCK.stubFor(post(urlEqualTo("/notificacoes")).willReturn(aResponse().withStatus(202)));
        RestAssured.port = porta;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    protected Usuario usuario(Perfil perfil) {
        return usuario(perfil, null);
    }

    protected Usuario usuario(Perfil perfil, Professor professor) {
        String email = perfil.name().toLowerCase() + "-" + UUID.randomUUID().toString().substring(0, 8) + "@teste.br";
        return usuarios.save(new Usuario("Usuário " + perfil, email, encoder.encode(SENHA), perfil,
                professor == null ? null : professor.getId(), LocalDateTime.now()));
    }

    protected Sala sala(String codigo, int capacidade, boolean restrita) {
        return salas.save(new Sala(codigo, "Sala " + codigo, TipoSala.SALA_AULA, capacidade, "Bloco A", restrita));
    }

    protected Professor professor(String nome, String... competencias) {
        return professores.save(new Professor(nome, nome.toLowerCase().replace(' ', '.') + "@teste.br", "Computação",
                Set.of(competencias)));
    }

    protected Material material(String nome, int quantidade, boolean restrito) {
        return materiais.save(new Material(nome, "Geral", null, quantidade, "Almoxarifado", restrito));
    }

    protected String token(Usuario u) {
        return given().contentType(ContentType.JSON)
                .body(JsonTeste.json(Map.of("email", u.getEmail(), "senha", SENHA)))
                .post("/api/auth/login")
                .then().statusCode(200)
                .extract().path("token");
    }

    protected RequestSpecification como(Usuario u) {
        return given().contentType(ContentType.JSON).auth().oauth2(token(u));
    }

    protected static LocalDateTime em(String dataHora) {
        return LocalDateTime.parse(dataHora);
    }

    protected static String reservaJson(Sala sala, Professor prof, String disciplina, String inicio, String fim,
                                        int alunos, List<Map<String, Object>> materiais) {
        return JsonTeste.json(JsonTeste.mapa(
                "salaId", sala.getId(), "professorId", prof.getId(), "disciplina", disciplina,
                "inicio", "2030-03-11T" + inicio, "fim", "2030-03-11T" + fim,
                "quantidadeAlunos", alunos, "materiais", materiais));
    }
}
