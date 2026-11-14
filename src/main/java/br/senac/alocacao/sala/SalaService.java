package br.senac.alocacao.sala;

import br.senac.alocacao.auditoria.AcaoAuditoria;
import br.senac.alocacao.auditoria.AuditoriaService;
import br.senac.alocacao.bloqueio.TipoRecurso;
import br.senac.alocacao.comum.CodigoErro;
import br.senac.alocacao.comum.Filtros;
import br.senac.alocacao.comum.NegocioException;
import br.senac.alocacao.reserva.DisponibilidadeService;
import br.senac.alocacao.reserva.Reserva;
import br.senac.alocacao.reserva.regras.Intervalo;
import br.senac.alocacao.usuario.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SalaService {
    private static final String ENTIDADE = "Sala";
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final SalaRepository salas;
    private final DisponibilidadeService disponibilidade;
    private final AuditoriaService auditoria;
    private final Clock clock;

    public SalaService(SalaRepository salas, DisponibilidadeService disponibilidade,
                       AuditoriaService auditoria, Clock clock) {
        this.salas = salas;
        this.disponibilidade = disponibilidade;
        this.auditoria = auditoria;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<SalaResponse> pesquisar(TipoSala tipo, Integer capacidadeMinima, String localizacao,
                                        LocalDateTime inicio, LocalDateTime fim, boolean incluirInativas) {
        Optional<Intervalo> intervalo = Filtros.intervaloOpcional(inicio, fim);
        boolean somenteAtivas = !incluirInativas || intervalo.isPresent();
        return salas.pesquisar(somenteAtivas, tipo, capacidadeMinima, Filtros.contem(localizacao)).stream()
                .filter(s -> intervalo.map(i -> disponibilidade.salaLivre(s, i)).orElse(true))
                .map(SalaResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
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
        Sala sala = salas.travar(id).orElseThrow(() -> NegocioException.naoEncontrado("Sala", id));
        garantirCapacidadeParaReservasFuturas(sala, req.capacidade());
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

    private void garantirCapacidadeParaReservasFuturas(Sala sala, int novaCapacidade) {
        if (novaCapacidade >= sala.getCapacidade()) {
            return;
        }
        List<Reserva> afetadas = disponibilidade.reservasFuturas(TipoRecurso.SALA, sala.getId(), LocalDateTime.now(clock))
                .stream().filter(r -> r.getQuantidadeAlunos() > novaCapacidade).toList();
        if (afetadas.isEmpty()) {
            return;
        }
        String lista = afetadas.stream()
                .map(r -> r.getInicio().format(FORMATO) + " com " + r.getQuantidadeAlunos() + " alunos")
                .collect(Collectors.joining("; "));
        throw NegocioException.conflito(CodigoErro.RESERVAS_AFETADAS,
                "A capacidade da sala " + sala.getCodigo() + " não pode ser reduzida para " + novaCapacidade
                        + ": há " + afetadas.size() + " reserva(s) futura(s) com mais alunos (" + lista
                        + "). Remaneje ou cancele essas reservas antes.",
                Map.of("reservas", afetadas.stream().map(Reserva::getId).toList()));
    }

    private Sala buscar(String id) {
        return salas.findById(id).orElseThrow(() -> NegocioException.naoEncontrado("Sala", id));
    }

    private static String descrever(Sala s) {
        return s.getCodigo() + " | " + s.getNome() + " | " + s.getTipo() + " | cap=" + s.getCapacidade()
                + " | " + s.getLocalizacao() + " | restrita=" + s.isRestrita();
    }
}
