package br.senac.alocacao.bloqueio;

import br.senac.alocacao.auditoria.AcaoAuditoria;
import br.senac.alocacao.auditoria.AuditoriaService;
import br.senac.alocacao.comum.NegocioException;
import br.senac.alocacao.material.MaterialRepository;
import br.senac.alocacao.professor.ProfessorRepository;
import br.senac.alocacao.reserva.DisponibilidadeService;
import br.senac.alocacao.reserva.Reserva;
import br.senac.alocacao.reserva.regras.Intervalo;
import br.senac.alocacao.sala.SalaRepository;
import br.senac.alocacao.usuario.Usuario;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/** Bloqueios de recursos por manutenção/afastamento (RF08). Gerenciados pelo ADMIN. */
@Service
public class BloqueioService {

    private static final String ENTIDADE = "Bloqueio";

    private final BloqueioRepository bloqueios;
    private final SalaRepository salas;
    private final MaterialRepository materiais;
    private final ProfessorRepository professores;
    private final DisponibilidadeService disponibilidade;
    private final AuditoriaService auditoria;
    private final MongoTemplate mongo;
    private final Clock clock;

    @SuppressWarnings("java:S107")
    public BloqueioService(BloqueioRepository bloqueios, SalaRepository salas, MaterialRepository materiais,
                           ProfessorRepository professores, DisponibilidadeService disponibilidade,
                           AuditoriaService auditoria, MongoTemplate mongo, Clock clock) {
        this.bloqueios = bloqueios;
        this.salas = salas;
        this.materiais = materiais;
        this.professores = professores;
        this.disponibilidade = disponibilidade;
        this.auditoria = auditoria;
        this.mongo = mongo;
        this.clock = clock;
    }

    public List<BloqueioResponse> pesquisar(TipoRecurso tipo, String recursoId) {
        Criteria c = new Criteria();
        if (tipo != null) {
            c = c.and("tipoRecurso").is(tipo);
        }
        if (recursoId != null) {
            c = c.and("recursoId").is(recursoId);
        }
        return mongo.find(Query.query(c).with(Sort.by(Sort.Direction.DESC, "inicio")), Bloqueio.class).stream()
                .map(b -> BloqueioResponse.de(b, List.of()))
                .toList();
    }

    @Transactional
    public BloqueioResponse criar(BloqueioRequest req, Usuario ator) {
        Intervalo intervalo = new Intervalo(req.inicio(), req.fim());
        garantirRecursoExiste(req.tipoRecurso(), req.recursoId());

        Bloqueio b = bloqueios.save(new Bloqueio(req.tipoRecurso(), req.recursoId(), req.inicio(), req.fim(),
                req.motivo(), ator.getId(), LocalDateTime.now(clock)));
        auditoria.registrar(ENTIDADE, b.getId(), AcaoAuditoria.CRIACAO, ator, null, null,
                req.tipoRecurso() + " " + req.recursoId() + " de " + req.inicio() + " a " + req.fim() + ": " + req.motivo());

        String campo = switch (req.tipoRecurso()) {
            case SALA -> "salaId";
            case PROFESSOR -> "professorId";
            case MATERIAL -> "itens.materialId";
        };
        List<String> afetadas = disponibilidade.reservasSobrepostas(campo, req.recursoId(), intervalo, null)
                .stream().map(Reserva::getId).toList();
        return BloqueioResponse.de(b, afetadas);
    }

    @Transactional
    public void remover(String id, Usuario ator) {
        Bloqueio b = bloqueios.findById(id).orElseThrow(() -> NegocioException.naoEncontrado("Bloqueio", id));
        auditoria.registrar(ENTIDADE, id, AcaoAuditoria.EXCLUSAO, ator, null, null,
                b.getTipoRecurso() + " " + b.getRecursoId() + ": " + b.getMotivo());
        bloqueios.delete(b);
    }

    private void garantirRecursoExiste(TipoRecurso tipo, String id) {
        boolean existe = switch (tipo) {
            case SALA -> salas.existsById(id);
            case MATERIAL -> materiais.existsById(id);
            case PROFESSOR -> professores.existsById(id);
        };
        if (!existe) {
            throw NegocioException.naoEncontrado(tipo.name().charAt(0) + tipo.name().substring(1).toLowerCase(), id);
        }
    }
}
