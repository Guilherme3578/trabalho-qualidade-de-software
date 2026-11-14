package br.senac.alocacao.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

@Controller
public class PaginaFrontendController {
    private static final String RAIZ = "<app-root";

    private final String pagina;

    public PaginaFrontendController() {
        this.pagina = carregar();
    }

    @GetMapping({"/", "/index.html"})
    public ResponseEntity<String> pagina(HttpServletRequest requisicao) {
        if (pagina == null) {
            return ResponseEntity.notFound().build();
        }
        String nonce = (String) requisicao.getAttribute(PoliticaConteudoFilter.ATRIBUTO_NONCE);
        return ResponseEntity.ok()
                .contentType(new MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8))
                .cacheControl(CacheControl.noStore())
                .body(pagina.replace(RAIZ, RAIZ + " ngCspNonce=\"" + nonce + "\""));
    }

    private static String carregar() {
        ClassPathResource recurso = new ClassPathResource("static/index.html");
        if (!recurso.exists()) {
            return null;
        }
        try (InputStream entrada = recurso.getInputStream()) {
            return new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
