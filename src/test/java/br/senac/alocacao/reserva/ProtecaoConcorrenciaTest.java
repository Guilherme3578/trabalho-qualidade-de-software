package br.senac.alocacao.reserva;

import br.senac.alocacao.comum.CodigoErro;
import br.senac.alocacao.comum.NegocioException;
import jakarta.persistence.PessimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLTransactionRollbackException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("[RF06] Repetição de transações em deadlock ou espera de trava")
class ProtecaoConcorrenciaTest {
    private final TransactionTemplate tx = mock(TransactionTemplate.class);
    private final ProtecaoConcorrencia protecao = new ProtecaoConcorrencia(tx);

    @BeforeEach
    @SuppressWarnings("unchecked")
    void executaCallbackDireto() {
        when(tx.execute(any())).thenAnswer(inv -> ((TransactionCallback<Object>) inv.getArgument(0)).doInTransaction(null));
    }

    private static RuntimeException deadlock() {
        return new CannotAcquireLockException("Deadlock found when trying to get lock",
                new SQLTransactionRollbackException("Deadlock found when trying to get lock", "40001", 1213));
    }

    @Test
    @DisplayName("[RF06] Deadlock do MySQL é repetido até a operação conseguir")
    void repeteAteConseguir() {
        AtomicInteger chamadas = new AtomicInteger();
        String r = protecao.executar(() -> {
            if (chamadas.incrementAndGet() < 3) {
                throw deadlock();
            }
            return "ok";
        });
        assertThat(r).isEqualTo("ok");
        assertThat(chamadas).hasValue(3);
    }

    @Test
    @DisplayName("[RF06] Falhas de trava são transitórias; violação de integridade não é")
    void classificacaoTransitoria() {
        assertThat(ProtecaoConcorrencia.transitorio(new RuntimeException(
                new SQLTransactionRollbackException("Lock wait timeout exceeded", "40001", 1205)))).isTrue();
        assertThat(ProtecaoConcorrencia.transitorio(new PessimisticLockException("trava")))
                .isTrue();
        assertThat(ProtecaoConcorrencia.transitorio(new DataIntegrityViolationException("FK"))).isFalse();
        assertThat(ProtecaoConcorrencia.transitorio(new RuntimeException("outro"))).isFalse();
    }

    @Test
    @DisplayName("[RF06] Erros de negócio não são repetidos")
    void naoRepeteNegocio() {
        AtomicInteger chamadas = new AtomicInteger();
        assertThatThrownBy(() -> protecao.executar(() -> {
            chamadas.incrementAndGet();
            throw NegocioException.conflito(CodigoErro.CONFLITO_SALA, "ocupada", java.util.Map.of());
        })).isInstanceOf(NegocioException.class);
        assertThat(chamadas).hasValue(1);
    }

    @Test
    @DisplayName("[RF06] Após o limite de tentativas, devolve erro CONCORRENCIA compreensível")
    void desiste() {
        assertThatThrownBy(() -> protecao.executar(() -> {
            throw deadlock();
        })).isInstanceOf(NegocioException.class)
                .extracting("codigo").isEqualTo(CodigoErro.CONCORRENCIA);
    }
}
