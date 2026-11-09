package br.senac.alocacao.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Alocação de Salas, Professores e Materiais")
                        .version("1.0.0")
                        .description("""
                                Organiza reservas de salas, professores e materiais sem conflitos de horário.
                                Autentique-se em POST /api/auth/login e use o botão "Authorize" com o token recebido.
                                Erros seguem o formato Problem Details (RFC 9457) com o campo "codigo".
                                """))
                .components(new Components().addSecuritySchemes("bearer",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearer"));
    }
}
