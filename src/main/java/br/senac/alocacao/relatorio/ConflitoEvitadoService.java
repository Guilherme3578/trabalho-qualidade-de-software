package br.senac.alocacao.relatorio;

import br.senac.alocacao.comum.CodigoErro;
import br.senac.alocacao.comum.NegocioException;
import br.senac.alocacao.reserva.regras.Intervalo;
import br.senac.alocacao.usuario.Usuario;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

@Service
public class ConflitoEvitadoService {
    static final Set<CodigoErro> CODIGOS = EnumSet.of(CodigoErro.CONFLITO_SALA, CodigoErro.CONFLITO_PROFESSOR,
            CodigoErro.CONFLITO_MATERIAL, CodigoErro.RECURSO_EM_MANUTENCAO);

    private final ConflitoEvitadoRepository repositorio;
    private final Clock clock;

    public ConflitoEvitadoService(ConflitoEvitadoRepository repositorio, Clock clock) {
        this.repositorio = repositorio;
        this.clock = clock;
    }

    public void registrarSeConflito(NegocioException e, Usuario solicitante, Intervalo intervalo) {
        if (!CODIGOS.contains(e.getCodigo())) {
            return;
        }
        repositorio.save(new ConflitoEvitado(e.getCodigo().name(),
                String.valueOf(e.getDetalhes().get("recursoTipo")),
                String.valueOf(e.getDetalhes().get("recursoId")),
                intervalo.inicio(), intervalo.fim(), solicitante.getId(), LocalDateTime.now(clock)));
    }
}
