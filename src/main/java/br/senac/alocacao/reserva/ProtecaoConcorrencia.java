package br.senac.alocacao.reserva;

import br.senac.alocacao.comum.CodigoErro;
import br.senac.alocacao.comum.NegocioException;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PessimisticLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLTransientException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

@Component
public class ProtecaoConcorrencia {
    private static final Logger log = LoggerFactory.getLogger(ProtecaoConcorrencia.class);
    static final int MAX_TENTATIVAS = 15;

    private final TransactionTemplate transacao;

    public ProtecaoConcorrencia(TransactionTemplate transacao) {
        this.transacao = transacao;
    }

    public <T> T executar(Supplier<T> operacao) {
        for (int tentativa = 1; ; tentativa++) {
            try {
                return transacao.execute(status -> operacao.get());
            } catch (RuntimeException e) {
                if (!transitorio(e)) {
                    throw e;
                }
                if (tentativa >= MAX_TENTATIVAS) {
                    log.warn("Desistindo após {} tentativas por disputa de travas no banco", tentativa);
                    throw new NegocioException(HttpStatus.CONFLICT, CodigoErro.CONCORRENCIA,
                            "Há muitas solicitações simultâneas para este recurso. Tente novamente em instantes.");
                }
                log.debug("Deadlock ou espera de trava esgotada na tentativa {}; repetindo", tentativa);
                pausar(tentativa);
            }
        }
    }

    static boolean transitorio(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof PessimisticLockingFailureException || t instanceof SQLTransientException
                    || t instanceof PessimisticLockException
                    || t instanceof LockTimeoutException) {
                return true;
            }
            if (t.getCause() == t) {
                break;
            }
        }
        return false;
    }

    private static void pausar(int tentativa) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextLong(5, 20L * Math.min(tentativa, 5)));
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Operação interrompida", ie);
        }
    }
}
