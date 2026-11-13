package br.senac.alocacao.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FrontendConfig implements WebMvcConfigurer {
    public static final String[] ROTAS = {
            "/login", "/inicio", "/disponibilidade", "/reservas", "/reservas/**", "/aprovacoes",
            "/notificacoes", "/relatorios", "/auditoria", "/salas", "/professores", "/materiais",
            "/manutencoes", "/usuarios"
    };

    public static final String[] ARQUIVOS = {
            "/", "/index.html", "/*.js", "/*.css", "/media/**", "/favicon.ico"
    };

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        for (String rota : ROTAS) {
            registry.addViewController(rota).setViewName("forward:/index.html");
        }
    }
}
