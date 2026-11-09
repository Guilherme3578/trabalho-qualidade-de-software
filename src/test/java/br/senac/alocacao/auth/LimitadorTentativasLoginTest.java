package br.senac.alocacao.auth;

import br.senac.alocacao.comum.CodigoErro;
import br.senac.alocacao.comum.NegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[RNF02] Proteção contra força bruta no login")
class LimitadorTentativasLoginTest {
    private final AtomicReference<Instant> agora = new AtomicReference<>(Instant.parse("2030-01-01T10:00:00Z"));
    private final LimitadorTentativasLogin limitador =
            new LimitadorTentativasLogin(3, Duration.ofMinutes(15), agora::get);

    @Test
    @DisplayName("[RNF02] Bloqueia após o número máximo de falhas")
    void bloqueia() {
        for (int i = 0; i < 3; i++) {
            limitador.verificar("a@x.com");
            limitador.registrarFalha("a@x.com");
        }
        assertThatThrownBy(() -> limitador.verificar("A@X.com "))
                .isInstanceOf(NegocioException.class)
                .extracting("codigo").isEqualTo(CodigoErro.MUITAS_TENTATIVAS);
    }

    @Test
    @DisplayName("[RNF02] Tentativas expiradas são descartadas para a memória não crescer sem limite")
    void descartaTentativasExpiradas() {
        for (int i = 0; i < 2000; i++) {
            limitador.registrarFalha("atacante" + i + "@x.com");
        }
        agora.set(agora.get().plus(Duration.ofMinutes(16)));
        limitador.registrarFalha("novo@x.com");

        assertThat(limitador.emailsMonitorados()).isEqualTo(1);
    }

    @Test
    @DisplayName("[RNF02] Libera após a janela expirar")
    void liberaAposJanela() {
        for (int i = 0; i < 3; i++) {
            limitador.registrarFalha("a@x.com");
        }
        agora.set(agora.get().plus(Duration.ofMinutes(16)));
        assertThatCode(() -> limitador.verificar("a@x.com")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("[RNF02] Login bem-sucedido zera o contador")
    void sucessoZera() {
        limitador.registrarFalha("a@x.com");
        limitador.registrarFalha("a@x.com");
        limitador.limpar("a@x.com");
        limitador.registrarFalha("a@x.com");
        assertThatCode(() -> limitador.verificar("a@x.com")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("[RNF02] Contadores são independentes por e-mail")
    void independentes() {
        for (int i = 0; i < 3; i++) {
            limitador.registrarFalha("a@x.com");
        }
        assertThatCode(() -> limitador.verificar("b@x.com")).doesNotThrowAnyException();
    }
}
