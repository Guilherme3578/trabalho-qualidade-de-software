package br.senac.alocacao.professor;

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
public class ProfessorService {
    private static final String ENTIDADE = "Professor";

    private final ProfessorRepository professores;
    private final MongoTemplate mongo;
    private final DisponibilidadeService disponibilidade;
    private final AuditoriaService auditoria;

    public ProfessorService(ProfessorRepository professores, MongoTemplate mongo,
                            DisponibilidadeService disponibilidade, AuditoriaService auditoria) {
        this.professores = professores;
        this.mongo = mongo;
        this.disponibilidade = disponibilidade;
        this.auditoria = auditoria;
    }

    public List<ProfessorResponse> pesquisar(String competencia, String departamento, String nome,
                                             LocalDateTime inicio, LocalDateTime fim) {
        Optional<Intervalo> intervalo = Filtros.intervaloOpcional(inicio, fim);
        Criteria c = Criteria.where("ativo").is(true);
        if (Filtros.preenchido(competencia)) {
            c = c.and("competencias").regex(Filtros.igual(competencia));
        }
        if (Filtros.preenchido(departamento)) {
            c = c.and("departamento").regex(Filtros.contem(departamento));
        }
        if (Filtros.preenchido(nome)) {
            c = c.and("nome").regex(Filtros.contem(nome));
        }
        return mongo.find(Query.query(c).with(Sort.by("nome")), Professor.class).stream()
                .filter(p -> intervalo.map(i -> disponibilidade.professorLivre(p, i)).orElse(true))
                .map(ProfessorResponse::de)
                .toList();
    }

    public ProfessorResponse obter(String id) {
        return ProfessorResponse.de(buscar(id));
    }

    @Transactional
    public ProfessorResponse criar(ProfessorRequest req, Usuario ator) {
        Professor p = professores.save(new Professor(req.nome(), req.email(), req.departamento(), req.competencias()));
        auditoria.registrar(ENTIDADE, p.getId(), AcaoAuditoria.CRIACAO, ator, null, null, descrever(p));
        return ProfessorResponse.de(p);
    }

    @Transactional
    public ProfessorResponse atualizar(String id, ProfessorRequest req, Usuario ator) {
        Professor p = buscar(id);
        String antes = descrever(p);
        p.atualizar(req.nome(), req.email(), req.departamento(), req.competencias());
        p = professores.save(p);
        auditoria.registrar(ENTIDADE, id, AcaoAuditoria.ALTERACAO, ator, antes, descrever(p), null);
        return ProfessorResponse.de(p);
    }

    @Transactional
    public ProfessorResponse definirAtivo(String id, boolean ativo, Usuario ator) {
        Professor p = buscar(id);
        p.setAtivo(ativo);
        professores.save(p);
        auditoria.registrar(ENTIDADE, id, ativo ? AcaoAuditoria.ATIVACAO : AcaoAuditoria.DESATIVACAO, ator,
                null, null, null);
        return ProfessorResponse.de(p);
    }

    private Professor buscar(String id) {
        return professores.findById(id).orElseThrow(() -> NegocioException.naoEncontrado("Professor", id));
    }

    private static String descrever(Professor p) {
        return p.getNome() + " | " + p.getEmail() + " | " + p.getDepartamento() + " | " + p.getCompetencias();
    }
}
