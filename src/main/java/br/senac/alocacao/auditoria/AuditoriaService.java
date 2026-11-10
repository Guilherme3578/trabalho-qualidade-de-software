package br.senac.alocacao.auditoria;

import br.senac.alocacao.config.CorrelationIdFilter;
import br.senac.alocacao.usuario.Usuario;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditoriaService {
    public static final String SISTEMA = "SISTEMA";
    private static final int LIMITE_CONSULTA = 500;

    private final AuditoriaRepository repositorio;
    private final Clock clock;

    public AuditoriaService(AuditoriaRepository repositorio, Clock clock) {
        this.repositorio = repositorio;
        this.clock = clock;
    }

    public void registrar(String entidade, String entidadeId, AcaoAuditoria acao, Usuario ator,
                          String estadoAnterior, String estadoNovo, String detalhes) {
        repositorio.save(new EventoAuditoria(LocalDateTime.now(clock),
                ator == null ? null : ator.getId(),
                ator == null ? SISTEMA : ator.getNome(),
                acao, entidade, entidadeId, estadoAnterior, estadoNovo, detalhes, CorrelationIdFilter.atual()));
    }

    @Transactional(readOnly = true)
    public List<EventoAuditoria> historico(String entidade, String entidadeId) {
        return repositorio.findByEntidadeAndEntidadeIdOrderByDataHoraAscIdAsc(entidade, entidadeId);
    }

    @Transactional(readOnly = true)
    public List<EventoAuditoria> pesquisar(String entidade, String entidadeId, AcaoAuditoria acao) {
        return repositorio.pesquisar(entidade, entidadeId, acao, PageRequest.of(0, LIMITE_CONSULTA));
    }
}
