package br.senac.alocacao.bloqueio;

import br.senac.alocacao.auditoria.AcaoAuditoria;
import br.senac.alocacao.auditoria.AuditoriaService;
import br.senac.alocacao.comum.NegocioException;
import br.senac.alocacao.material.MaterialRepository;
import br.senac.alocacao.professor.ProfessorRepository;
import br.senac.alocacao.reserva.DisponibilidadeService;
import br.senac.alocacao.reserva.ProtecaoConcorrencia;
import br.senac.alocacao.reserva.Reserva;
import br.senac.alocacao.reserva.regras.Intervalo;
import br.senac.alocacao.sala.SalaRepository;
import br.senac.alocacao.usuario.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BloqueioService {
    private static final String ENTIDADE = "Bloqueio";

    private final BloqueioRepository bloqueios;
    private final SalaRepository salas;
    private final MaterialRepository materiais;
    private final ProfessorRepository professores;
    private final DisponibilidadeService disponibilidade;
    private final AuditoriaService auditoria;
    private final ProtecaoConcorrencia protecao;
    private final Clock clock;

    @SuppressWarnings("java:S107")
    public BloqueioService(BloqueioRepository bloqueios, SalaRepository salas, MaterialRepository materiais,
                           ProfessorRepository professores, DisponibilidadeService disponibilidade,
                           AuditoriaService auditoria, ProtecaoConcorrencia protecao,
                           Clock clock) {
        this.bloqueios = bloqueios;
        this.salas = salas;
        this.materiais = materiais;
        this.professores = professores;
        this.disponibilidade = disponibilidade;
        this.auditoria = auditoria;
        this.protecao = protecao;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<BloqueioResponse> pesquisar(TipoRecurso tipo, String recursoId) {
        return bloqueios.pesquisar(tipo, recursoId).stream()
                .map(b -> BloqueioResponse.de(b, List.of()))
                .toList();
    }

    public BloqueioResponse criar(BloqueioRequest req, Usuario ator) {
        Intervalo intervalo = new Intervalo(req.inicio(), req.fim());
        return protecao.executar(() -> registrar(req, intervalo, ator));
    }

    private BloqueioResponse registrar(BloqueioRequest req, Intervalo intervalo, Usuario ator) {
        travarRecurso(req.tipoRecurso(), req.recursoId());

        Bloqueio b = bloqueios.save(new Bloqueio(req.tipoRecurso(), req.recursoId(), req.inicio(), req.fim(),
                req.motivo(), ator.getId(), LocalDateTime.now(clock)));
        auditoria.registrar(ENTIDADE, b.getId(), AcaoAuditoria.CRIACAO, ator, null, null,
                req.tipoRecurso() + " " + req.recursoId() + " de " + req.inicio() + " a " + req.fim() + ": " + req.motivo());

        List<String> afetadas = disponibilidade.reservasSobrepostas(req.tipoRecurso(), req.recursoId(), intervalo, null)
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

    private void travarRecurso(TipoRecurso tipo, String id) {
        boolean existe = switch (tipo) {
            case SALA -> salas.travar(id).isPresent();
            case MATERIAL -> materiais.travar(id).isPresent();
            case PROFESSOR -> professores.travar(id).isPresent();
        };
        if (!existe) {
            throw NegocioException.naoEncontrado(tipo.name().charAt(0) + tipo.name().substring(1).toLowerCase(), id);
        }
    }
}
