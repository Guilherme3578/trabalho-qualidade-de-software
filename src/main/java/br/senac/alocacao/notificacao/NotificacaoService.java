package br.senac.alocacao.notificacao;

import br.senac.alocacao.reserva.ReservaStatusAlteradoEvent;
import br.senac.alocacao.reserva.StatusReserva;
import br.senac.alocacao.usuario.Perfil;
import br.senac.alocacao.usuario.Usuario;
import br.senac.alocacao.usuario.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Gera notificações quando uma reserva muda de estado (RF11).
 * Executa APÓS o commit: se a reserva for desfeita, ninguém é notificado; se a API externa
 * falhar, a reserva já está salva e a falha fica registrada na notificação.
 */
@Service
public class NotificacaoService {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final NotificacaoRepository repositorio;
    private final UsuarioRepository usuarios;
    private final NotificacaoGateway gateway;
    private final Clock clock;

    public NotificacaoService(NotificacaoRepository repositorio, UsuarioRepository usuarios,
                              NotificacaoGateway gateway, Clock clock) {
        this.repositorio = repositorio;
        this.usuarios = usuarios;
        this.gateway = gateway;
        this.clock = clock;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void aoMudarStatus(ReservaStatusAlteradoEvent e) {
        String quando = e.inicio().format(FORMATO);
        if (e.novo() == StatusReserva.SOLICITADA) {
            String msg = "Nova reserva aguardando aprovação: " + e.disciplina() + " na sala " + e.salaCodigo()
                    + " em " + quando + ". Motivo: " + e.motivo();
            usuarios.findByPerfilAndAtivoTrue(Perfil.RESPONSAVEL)
                    .forEach(r -> enviar(r, "APROVACAO_PENDENTE", msg, e.reservaId()));
            return;
        }
        String msg = "Sua reserva de " + e.disciplina() + " (sala " + e.salaCodigo() + ", " + quando + ") mudou para "
                + e.novo() + (e.motivo() == null ? "." : ". Observação: " + e.motivo());
        usuarios.findById(e.solicitanteId()).ifPresent(u -> enviar(u, "RESERVA_" + e.novo(), msg, e.reservaId()));
    }

    private void enviar(Usuario destinatario, String tipo, String mensagem, String reservaId) {
        Notificacao n = new Notificacao(destinatario.getId(), destinatario.getEmail(), tipo, mensagem, reservaId,
                LocalDateTime.now(clock));
        try {
            n.registrarResultado(gateway.enviar(n), null);
        } catch (RuntimeException ex) {
            n.registrarResultado(StatusNotificacao.FALHA, ex.getMessage());
        }
        repositorio.save(n);
    }

    public List<Notificacao> minhas(String usuarioId) {
        return repositorio.findTop100ByDestinatarioIdOrderByCriadoEmDesc(usuarioId);
    }

    public Notificacao marcarLida(String id, String usuarioId) {
        Notificacao n = repositorio.findById(id)
                .filter(x -> x.getDestinatarioId().equals(usuarioId))
                .orElseThrow(() -> br.senac.alocacao.comum.NegocioException.naoEncontrado("Notificação", id));
        n.marcarLida();
        return repositorio.save(n);
    }
}
