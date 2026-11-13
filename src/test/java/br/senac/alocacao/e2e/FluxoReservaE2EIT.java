package br.senac.alocacao.e2e;

import br.senac.alocacao.professor.Professor;
import br.senac.alocacao.sala.Sala;
import br.senac.alocacao.suporte.IntegracaoBase;
import br.senac.alocacao.usuario.Perfil;
import br.senac.alocacao.usuario.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("e2e")
@DisplayName("[RF13] End-to-end: fluxo de reserva pela interface web")
class FluxoReservaE2EIT extends IntegracaoBase {
    private WebDriver navegador;
    private WebDriverWait espera;
    private Usuario professorUsuario;
    private Professor professorAna;
    private Sala salaA101;

    @BeforeEach
    void abrirNavegador() {
        ChromeOptions opcoes = new ChromeOptions();
        opcoes.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=1280,900");
        navegador = new ChromeDriver(opcoes);
        espera = new WebDriverWait(navegador, Duration.ofSeconds(10));

        professorAna = professor("Ana Souza", "Qualidade de Software");
        professorUsuario = usuario(Perfil.PROFESSOR, professorAna);
        salaA101 = sala("A101", 40, false);
    }

    @AfterEach
    void fecharNavegador() {
        if (navegador != null) {
            navegador.quit();
        }
    }

    private void entrar() {
        navegador.get(url("/login"));
        espera.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")))
                .sendKeys(professorUsuario.getEmail());
        navegador.findElement(By.id("senha")).sendKeys(SENHA);
        navegador.findElement(By.cssSelector("button[type='submit']")).click();
        espera.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("header.topo")));
    }

    private String url(String caminho) {
        return "http://localhost:" + porta + caminho;
    }

    private void preencher(String campo, String valor) {
        WebElement elemento = navegador.findElement(By.cssSelector("[formcontrolname='" + campo + "']"));
        ((JavascriptExecutor) navegador).executeScript(
                "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input'));",
                elemento, valor);
    }

    private void consultarHorario() {
        navegador.get(url("/reservas/nova"));
        espera.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[formcontrolname='data']")));
        preencher("data", "2030-03-11");
        preencher("inicio", "09:00");
        preencher("fim", "11:00");
        navegador.findElement(By.xpath("//button[contains(., 'Ver o que está livre')]")).click();
    }

    private void preencherReserva() {
        consultarHorario();
        espera.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class, 'sala')][.//strong[text()='A101']]"))).click();
        preencher("disciplina", "Qualidade de Software");
    }

    private WebElement mensagem(String tipo) {
        return espera.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".mensagem." + tipo)));
    }

    @Test
    @DisplayName("[RF13][RF04] Professor entra, cria reserva e vê confirmação")
    void criaReserva() {
        entrar();
        preencherReserva();
        navegador.findElement(By.cssSelector("button.confirmar")).click();
        assertThat(mensagem("sucesso").getText()).contains("Reserva confirmada");
        espera.until(ExpectedConditions.urlMatches("/reservas/[^/]+$"));
    }

    @Test
    @DisplayName("[RF13][RF05] Sala reservada por outra pessoa durante o preenchimento mostra mensagem de conflito compreensível")
    void mostraConflito() {
        entrar();
        preencherReserva();
        como(professorUsuario).body(reservaJson(salaA101, professorAna, "Outra turma", "09:00", "11:00", 10, List.of()))
                .post("/api/reservas").then().statusCode(201);
        navegador.findElement(By.cssSelector("button.confirmar")).click();
        assertThat(mensagem("erro").getText()).contains("A sala A101 já está reservada das 09:00 às 11:00");
    }

    @Test
    @DisplayName("[RF13][RF03] Sala já reservada não aparece entre as livres do horário")
    void escondeSalaOcupada() {
        como(professorUsuario).body(reservaJson(salaA101, professorAna, "Outra turma", "09:00", "11:00", 10, List.of()))
                .post("/api/reservas").then().statusCode(201);
        entrar();
        consultarHorario();
        espera.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(., 'Nenhuma sala livre')]")));
        assertThat(navegador.findElements(By.cssSelector("button.sala"))).isEmpty();
    }

    @Test
    @DisplayName("[RF13] Credenciais inválidas exibem mensagem de erro")
    void loginInvalido() {
        navegador.get(url("/login"));
        espera.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")))
                .sendKeys(professorUsuario.getEmail());
        navegador.findElement(By.id("senha")).sendKeys("errada123");
        navegador.findElement(By.cssSelector("button[type='submit']")).click();
        assertThat(espera.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[role='alert']")))
                .getText()).contains("E-mail ou senha inválidos");
    }

    @Test
    @DisplayName("[RF13] Recarregar uma rota interna do Angular abre a mesma tela, sem erro 404")
    void recarregaRotaInterna() {
        entrar();
        navegador.get(url("/reservas"));
        espera.until(ExpectedConditions.titleContains("Reservas"));
        navegador.navigate().refresh();
        espera.until(ExpectedConditions.titleContains("Reservas"));
        assertThat(navegador.findElements(By.cssSelector("header.topo"))).isNotEmpty();
    }

    @Test
    @DisplayName("[RF13] Estilos do Angular carregam com a política de segurança de conteúdo ativa")
    void estilosCarregados() {
        navegador.get(url("/login"));
        espera.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        Object fonte = ((JavascriptExecutor) navegador)
                .executeScript("return getComputedStyle(document.body).fontFamily;");
        assertThat(String.valueOf(fonte)).contains("Inter");
    }

    @Test
    @DisplayName("[RF13] Interface responsiva: sem rolagem horizontal em tela de celular (390px)")
    void responsivo() {
        navegador.manage().window().setSize(new Dimension(390, 844));
        entrar();
        navegador.get(url("/reservas/nova"));
        espera.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[formcontrolname='data']")));
        Long larguraConteudo = (Long) ((JavascriptExecutor) navegador)
                .executeScript("return document.documentElement.scrollWidth;");
        Long larguraJanela = (Long) ((JavascriptExecutor) navegador).executeScript("return window.innerWidth;");
        assertThat(larguraConteudo).isLessThanOrEqualTo(larguraJanela);
    }
}
