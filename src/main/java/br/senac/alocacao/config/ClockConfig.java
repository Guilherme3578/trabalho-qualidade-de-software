package br.senac.alocacao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * O relógio é injetado (em vez de LocalDateTime.now() espalhado pelo código)
 * para que as regras dependentes de tempo possam ser testadas de forma determinística.
 */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
