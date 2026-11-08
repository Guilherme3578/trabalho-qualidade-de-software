package br.senac.alocacao.reserva;

import br.senac.alocacao.comum.CodigoErro;
import br.senac.alocacao.comum.NegocioException;
import com.mongodb.MongoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("[RF06] Repetição de transações em conflito de escrita")
class ProtecaoConcorrenciaTest {

    private final TransactionTemplate tx = mock(TransactionTemplate.class);
    private final ProtecaoConcorrencia protecao = new ProtecaoConcorrencia(tx);

    @BeforeEach
    @SuppressWarnings("unchecked")
    void executaCallbackDireto() {
        when(tx.execute(any())).thenAnswer(inv -> ((TransactionCallback<Object>) inv.getArgument(0)).doInTransaction(null));
    }

    private static MongoException writeConflict() {
        return new MongoException(112, "WriteConflict");
    }

    @Test
    @DisplayName("[RF06] WriteConflict é repetido até a operação conseguir")
    void repeteAteConseguir() {
        AtomicInteger chamadas = new AtomicInteger();
        String r = protecao.executar(() -> {
            if (chamadas.incrementAndGet() < 3) {
                throw new DataAccessResourceFailureException("envolto", writeConflict());
            }
            return "ok";
        });
        assertThat(r).isEqualTo("ok");
        assertThat(chamadas).hasValue(3);
    }

    @Test
    @DisplayName("[RF06] Erro com rótulo TransientTransactionError é considerado transitório")
    void rotuloTransitorio() {
        MongoException e = new MongoException("abortada");
        e.addLabel(MongoException.TRANSIENT_TRANSACTION_ERROR_LABEL);
        assertThat(ProtecaoConcorrencia.transitorio(new RuntimeException(e))).isTrue();
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
            throw writeConflict();
        })).isInstanceOf(NegocioException.class)
                .extracting("codigo").isEqualTo(CodigoErro.CONCORRENCIA);
    }
}
