package br.senac.alocacao.reserva;

import br.senac.alocacao.auditoria.AuditoriaService;
import br.senac.alocacao.auditoria.EventoAuditoria;
import br.senac.alocacao.usuario.UsuarioAtual;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas", description = "Criação, alteração, cancelamento, aprovação e uso de reservas")
public class ReservaController {
    private static final String SOLICITANTES = "hasAnyRole('PROFESSOR','COORDENADOR')";
    private static final String RESPONSAVEL = "hasRole('RESPONSAVEL')";

    private final ReservaService servico;
    private final AuditoriaService auditoria;
    private final UsuarioAtual usuarioAtual;

    public ReservaController(ReservaService servico, AuditoriaService auditoria, UsuarioAtual usuarioAtual) {
        this.servico = servico;
        this.auditoria = auditoria;
        this.usuarioAtual = usuarioAtual;
    }

    @GetMapping
    @Operation(summary = "Pesquisa reservas (agenda). minhas=true retorna só as do usuário logado")
    public List<ReservaResponse> pesquisar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            @RequestParam(required = false) String salaId,
            @RequestParam(required = false) String professorId,
            @RequestParam(required = false) StatusReserva status,
            @RequestParam(defaultValue = "false") boolean minhas) {
        String solicitante = minhas ? usuarioAtual.obter().getId() : null;
        return servico.pesquisar(inicio, fim, salaId, professorId, status, solicitante);
    }

    @GetMapping("/{id}")
    public ReservaResponse obter(@PathVariable String id) {
        return servico.obter(id);
    }

    @GetMapping("/{id}/historico")
    @Operation(summary = "Histórico auditável da reserva (todas as mudanças de estado e alterações)")
    public List<EventoAuditoria> historico(@PathVariable String id) {
        return auditoria.historico(ReservaService.ENTIDADE, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(SOLICITANTES)
    @Operation(summary = "Cria reserva. Fica APROVADA automaticamente, ou SOLICITADA se envolver recurso restrito")
    public ReservaResponse criar(@Valid @RequestBody ReservaRequest req) {
        return servico.criar(req, usuarioAtual.obter());
    }

    @PutMapping("/{id}")
    @PreAuthorize(SOLICITANTES)
    @Operation(summary = "Altera reserva própria (informe a 'versao' lida anteriormente)")
    public ReservaResponse alterar(@PathVariable String id, @Valid @RequestBody ReservaRequest req) {
        return servico.alterar(id, req, usuarioAtual.obter());
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize(SOLICITANTES)
    public ReservaResponse cancelar(@PathVariable String id, @Valid @RequestBody(required = false) AcaoRequest req) {
        return servico.cancelar(id, AcaoRequest.texto(req), usuarioAtual.obter());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('PROFESSOR','COORDENADOR','ADMIN')")
    @Operation(summary = "Apaga reserva não iniciada (reservas iniciadas não podem ser apagadas)")
    public void excluir(@PathVariable String id) {
        servico.excluir(id, usuarioAtual.obter());
    }

    @PostMapping("/{id}/aprovar")
    @PreAuthorize(RESPONSAVEL)
    @Operation(summary = "Aprova reserva SOLICITADA (somente RESPONSAVEL)")
    public ReservaResponse aprovar(@PathVariable String id, @Valid @RequestBody(required = false) AcaoRequest req) {
        return servico.aprovar(id, AcaoRequest.texto(req), usuarioAtual.obter());
    }

    @PostMapping("/{id}/rejeitar")
    @PreAuthorize(RESPONSAVEL)
    public ReservaResponse rejeitar(@PathVariable String id, @Valid @RequestBody(required = false) AcaoRequest req) {
        return servico.rejeitar(id, AcaoRequest.texto(req), usuarioAtual.obter());
    }

    @PostMapping("/{id}/iniciar")
    @PreAuthorize("hasAnyRole('PROFESSOR','COORDENADOR','RESPONSAVEL')")
    public ReservaResponse iniciar(@PathVariable String id) {
        return servico.iniciar(id, usuarioAtual.obter());
    }

    @PostMapping("/{id}/concluir")
    @PreAuthorize("hasAnyRole('PROFESSOR','COORDENADOR','RESPONSAVEL')")
    public ReservaResponse concluir(@PathVariable String id) {
        return servico.concluir(id, usuarioAtual.obter());
    }

    @PostMapping("/{id}/nao-compareceu")
    @PreAuthorize(RESPONSAVEL)
    public ReservaResponse naoCompareceu(@PathVariable String id) {
        return servico.registrarNaoComparecimento(id, usuarioAtual.obter());
    }

    @PostMapping("/{id}/retiradas")
    @PreAuthorize(RESPONSAVEL)
    @Operation(summary = "Registra retirada de material (reserva EM_USO)")
    public ReservaResponse retirada(@PathVariable String id, @Valid @RequestBody MovimentacaoRequest req) {
        return servico.registrarRetirada(id, req, usuarioAtual.obter());
    }

    @PostMapping("/{id}/devolucoes")
    @PreAuthorize(RESPONSAVEL)
    @Operation(summary = "Registra devolução de material")
    public ReservaResponse devolucao(@PathVariable String id, @Valid @RequestBody MovimentacaoRequest req) {
        return servico.registrarDevolucao(id, req, usuarioAtual.obter());
    }
}
