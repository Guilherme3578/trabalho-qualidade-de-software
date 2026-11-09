package br.senac.alocacao.auditoria;

import br.senac.alocacao.config.CorrelationIdFilter;
import br.senac.alocacao.usuario.Usuario;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditoriaService {
    public static final String SISTEMA = "SISTEMA";
    private static final int LIMITE_CONSULTA = 500;

    private final AuditoriaRepository repositorio;
    private final MongoTemplate mongo;
    private final Clock clock;

    public AuditoriaService(AuditoriaRepository repositorio, MongoTemplate mongo, Clock clock) {
        this.repositorio = repositorio;
        this.mongo = mongo;
        this.clock = clock;
    }

    public void registrar(String entidade, String entidadeId, AcaoAuditoria acao, Usuario ator,
                          String estadoAnterior, String estadoNovo, String detalhes) {
        repositorio.save(new EventoAuditoria(null, LocalDateTime.now(clock),
                ator == null ? null : ator.getId(),
                ator == null ? SISTEMA : ator.getNome(),
                acao, entidade, entidadeId, estadoAnterior, estadoNovo, detalhes, CorrelationIdFilter.atual()));
    }

    public List<EventoAuditoria> historico(String entidade, String entidadeId) {
        Query q = Query.query(Criteria.where("entidade").is(entidade).and("entidadeId").is(entidadeId))
                .with(Sort.by(Sort.Direction.ASC, "dataHora", "id"));
        return mongo.find(q, EventoAuditoria.class);
    }

    public List<EventoAuditoria> pesquisar(String entidade, String entidadeId, AcaoAuditoria acao) {
        Criteria c = new Criteria();
        if (entidade != null) {
            c = c.and("entidade").is(entidade);
        }
        if (entidadeId != null) {
            c = c.and("entidadeId").is(entidadeId);
        }
        if (acao != null) {
            c = c.and("acao").is(acao);
        }
        Query q = Query.query(c).with(Sort.by(Sort.Direction.DESC, "dataHora", "id")).limit(LIMITE_CONSULTA);
        return mongo.find(q, EventoAuditoria.class);
    }
}
