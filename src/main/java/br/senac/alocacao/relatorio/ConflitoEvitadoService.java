package br.senac.alocacao.relatorio;

import br.senac.alocacao.comum.CodigoErro;
import br.senac.alocacao.comum.NegocioException;
import br.senac.alocacao.reserva.regras.Intervalo;
import br.senac.alocacao.usuario.Usuario;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

/**
 * Registra tentativas recusadas. É chamado FORA da transação da reserva (que já foi desfeita),
 * por isso o registro persiste mesmo com a reserva rejeitada.
 */
@Service
public class ConflitoEvitadoService {

    static final Set<CodigoErro> CODIGOS = EnumSet.of(CodigoErro.CONFLITO_SALA, CodigoErro.CONFLITO_PROFESSOR,
            CodigoErro.CONFLITO_MATERIAL, CodigoErro.RECURSO_EM_MANUTENCAO);

    private final MongoTemplate mongo;
    private final Clock clock;

    public ConflitoEvitadoService(MongoTemplate mongo, Clock clock) {
        this.mongo = mongo;
        this.clock = clock;
    }

    public void registrarSeConflito(NegocioException e, Usuario solicitante, Intervalo intervalo) {
        if (!CODIGOS.contains(e.getCodigo())) {
            return;
        }
        mongo.insert(new ConflitoEvitado(null, e.getCodigo().name(),
                String.valueOf(e.getDetalhes().get("recursoTipo")),
                String.valueOf(e.getDetalhes().get("recursoId")),
                intervalo.inicio(), intervalo.fim(), solicitante.getId(), LocalDateTime.now(clock)));
    }
}
