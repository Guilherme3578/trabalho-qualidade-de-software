package br.senac.alocacao.e2e;

import br.senac.alocacao.professor.Professor;
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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste end-to-end: navegador real (Chrome headless) → interface web → API → MongoDB.
 * Executar sem E2E: ./mvnw verify -Psem-e2e
 */
@Tag("e2e")
@DisplayName("[RF13] End-to-end: fluxo de reserva pela interface web")
class FluxoReservaE2EIT extends IntegracaoBase {

    private WebDriver navegador;
    private WebDriverWait espera;
    private Usuario professorUsuario;

    @BeforeEach
    void abrirNavegador() {
        ChromeOptions opcoes = new ChromeOptions();
        opcoes.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=1280,900");
        navegador = new ChromeDriver(opcoes);
        espera = new WebDriverWait(navegador, Duration.ofSeconds(10));

        Professor ana = professor("Ana Souza", "Qualidade de Software");
        professorUsuario = usuario(Perfil.PROFESSOR, ana);
        sala("A101", 40, false);
    }

    @AfterEach
    void fecharNavegador() {
        if (navegador != null) {
            navegador.quit();
        }
    }

    private void entrar() {
        navegador.get("http://localhost:" + porta + "/");
        navegador.findElement(By.id("login-email")).sendKeys(professorUsuario.getEmail());
        navegador.findElement(By.id("login-senha")).sendKeys(SENHA);
        navegador.findElement(By.id("login-entrar")).click();
        espera.until(ExpectedConditions.visibilityOfElementLocated(By.id("abas")));
    }

    private void preencherReserva() {
        navegador.findElement(By.cssSelector("[data-aba='nova']")).click();
        espera.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#r-sala option")));
        navegador.findElement(By.id("r-disciplina")).sendKeys("Qualidade de Software");
        // Campos de data/hora preenchidos via script: o formato digitado varia com o idioma do navegador.
        JavascriptExecutor js = (JavascriptExecutor) navegador;
        js.executeScript("document.getElementById('r-data').value='2030-03-11';"
                + "document.getElementById('r-inicio').value='09:00';"
                + "document.getElementById('r-fim').value='11:00';");
    }

    private WebElement mensagem(String tipo) {
        return espera.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#mensagem." + tipo)));
    }

    @Test
    @DisplayName("[RF13][RF04] Professor entra, cria reserva e vê confirmação")
    void criaReserva() {
        entrar();
        preencherReserva();
        navegador.findElement(By.id("r-salvar")).click();
        assertThat(mensagem("sucesso").getText()).contains("Reserva confirmada");
    }

    @Test
    @DisplayName("[RF13][RF05] Segunda reserva no mesmo horário mostra mensagem de conflito compreensível")
    void mostraConflito() {
        entrar();
        preencherReserva();
        navegador.findElement(By.id("r-salvar")).click();
        mensagem("sucesso");
        navegador.findElement(By.id("r-salvar")).click();
        assertThat(mensagem("erro").getText())
                .contains("A sala A101 já está reservada das 09:00 às 11:00")
                .contains("Código para suporte");
    }

    @Test
    @DisplayName("[RF13] Credenciais inválidas exibem mensagem de erro")
    void loginInvalido() {
        navegador.get("http://localhost:" + porta + "/");
        navegador.findElement(By.id("login-email")).sendKeys(professorUsuario.getEmail());
        navegador.findElement(By.id("login-senha")).sendKeys("errada123");
        navegador.findElement(By.id("login-entrar")).click();
        assertThat(mensagem("erro").getText()).contains("E-mail ou senha inválidos");
    }

    @Test
    @DisplayName("[RF13] Interface responsiva: sem rolagem horizontal em tela de celular (390px)")
    void responsivo() {
        navegador.manage().window().setSize(new Dimension(390, 844));
        entrar();
        navegador.findElement(By.cssSelector("[data-aba='nova']")).click();
        espera.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#r-sala option")));
        Long larguraConteudo = (Long) ((JavascriptExecutor) navegador)
                .executeScript("return document.documentElement.scrollWidth;");
        Long larguraJanela = (Long) ((JavascriptExecutor) navegador).executeScript("return window.innerWidth;");
        assertThat(larguraConteudo).isLessThanOrEqualTo(larguraJanela);
    }
}
