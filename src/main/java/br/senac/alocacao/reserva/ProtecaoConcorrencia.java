package br.senac.alocacao.reserva;

import br.senac.alocacao.comum.CodigoErro;
import br.senac.alocacao.comum.NegocioException;
import com.mongodb.MongoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

@Component
public class ProtecaoConcorrencia {
    private static final Logger log = LoggerFactory.getLogger(ProtecaoConcorrencia.class);
    static final int MAX_TENTATIVAS = 15;
    private static final int CODIGO_WRITE_CONFLICT = 112;

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
                    log.warn("Desistindo após {} tentativas por conflito de escrita", tentativa);
                    throw new NegocioException(HttpStatus.CONFLICT, CodigoErro.CONCORRENCIA,
                            "Há muitas solicitações simultâneas para este recurso. Tente novamente em instantes.");
                }
                log.debug("Conflito de escrita na tentativa {}; repetindo", tentativa);
                pausar(tentativa);
            }
        }
    }

    static boolean transitorio(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof MongoException me
                    && (me.hasErrorLabel(MongoException.TRANSIENT_TRANSACTION_ERROR_LABEL)
                    || me.getCode() == CODIGO_WRITE_CONFLICT)) {
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
