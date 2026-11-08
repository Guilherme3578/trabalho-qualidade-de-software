package br.senac.alocacao.sala;

import br.senac.alocacao.auditoria.AcaoAuditoria;
import br.senac.alocacao.auditoria.AuditoriaService;
import br.senac.alocacao.comum.Filtros;
import br.senac.alocacao.comum.NegocioException;
import br.senac.alocacao.reserva.DisponibilidadeService;
import br.senac.alocacao.reserva.regras.Intervalo;
import br.senac.alocacao.usuario.Usuario;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SalaService {

    private static final String ENTIDADE = "Sala";

    private final SalaRepository salas;
    private final MongoTemplate mongo;
    private final DisponibilidadeService disponibilidade;
    private final AuditoriaService auditoria;

    public SalaService(SalaRepository salas, MongoTemplate mongo, DisponibilidadeService disponibilidade,
                       AuditoriaService auditoria) {
        this.salas = salas;
        this.mongo = mongo;
        this.disponibilidade = disponibilidade;
        this.auditoria = auditoria;
    }

    /** Pesquisa por tipo, capacidade mínima, localização e, opcionalmente, disponibilidade no intervalo (RF03). */
    public List<SalaResponse> pesquisar(TipoSala tipo, Integer capacidadeMinima, String localizacao,
                                        LocalDateTime inicio, LocalDateTime fim, boolean incluirInativas) {
        Optional<Intervalo> intervalo = Filtros.intervaloOpcional(inicio, fim);
        Criteria c = new Criteria();
        if (!incluirInativas || intervalo.isPresent()) {
            c = c.and("ativa").is(true);
        }
        if (tipo != null) {
            c = c.and("tipo").is(tipo);
        }
        if (capacidadeMinima != null) {
            c = c.and("capacidade").gte(capacidadeMinima);
        }
        if (Filtros.preenchido(localizacao)) {
            c = c.and("localizacao").regex(Filtros.contem(localizacao));
        }
        return mongo.find(Query.query(c).with(Sort.by("codigo")), Sala.class).stream()
                .filter(s -> intervalo.map(i -> disponibilidade.salaLivre(s, i)).orElse(true))
                .map(SalaResponse::de)
                .toList();
    }

    public SalaResponse obter(String id) {
        return SalaResponse.de(buscar(id));
    }

    @Transactional
    public SalaResponse criar(SalaRequest req, Usuario ator) {
        Sala sala = salas.save(new Sala(req.codigo(), req.nome(), req.tipo(), req.capacidade(),
                req.localizacao(), req.restrita()));
        auditoria.registrar(ENTIDADE, sala.getId(), AcaoAuditoria.CRIACAO, ator, null, null, descrever(sala));
        return SalaResponse.de(sala);
    }

    @Transactional
    public SalaResponse atualizar(String id, SalaRequest req, Usuario ator) {
        Sala sala = buscar(id);
        String antes = descrever(sala);
        sala.atualizar(req.codigo(), req.nome(), req.tipo(), req.capacidade(), req.localizacao(), req.restrita());
        sala = salas.save(sala);
        auditoria.registrar(ENTIDADE, id, AcaoAuditoria.ALTERACAO, ator, antes, descrever(sala), null);
        return SalaResponse.de(sala);
    }

    @Transactional
    public SalaResponse definirAtiva(String id, boolean ativa, Usuario ator) {
        Sala sala = buscar(id);
        sala.setAtiva(ativa);
        salas.save(sala);
        auditoria.registrar(ENTIDADE, id, ativa ? AcaoAuditoria.ATIVACAO : AcaoAuditoria.DESATIVACAO, ator,
                null, null, null);
        return SalaResponse.de(sala);
    }

    private Sala buscar(String id) {
        return salas.findById(id).orElseThrow(() -> NegocioException.naoEncontrado("Sala", id));
    }

    private static String descrever(Sala s) {
        return s.getCodigo() + " | " + s.getNome() + " | " + s.getTipo() + " | cap=" + s.getCapacidade()
                + " | " + s.getLocalizacao() + " | restrita=" + s.isRestrita();
    }
}
