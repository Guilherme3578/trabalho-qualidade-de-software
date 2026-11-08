package br.senac.alocacao.bloqueio;

import br.senac.alocacao.usuario.UsuarioAtual;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bloqueios")
@Tag(name = "Bloqueios", description = "Períodos de manutenção e indisponibilidade de recursos")
public class BloqueioController {

    private final BloqueioService servico;
    private final UsuarioAtual usuarioAtual;

    public BloqueioController(BloqueioService servico, UsuarioAtual usuarioAtual) {
        this.servico = servico;
        this.usuarioAtual = usuarioAtual;
    }

    @GetMapping
    public List<BloqueioResponse> pesquisar(@RequestParam(required = false) TipoRecurso tipoRecurso,
                                            @RequestParam(required = false) String recursoId) {
        return servico.pesquisar(tipoRecurso, recursoId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Bloqueia um recurso; a resposta lista reservas já existentes afetadas")
    public BloqueioResponse criar(@Valid @RequestBody BloqueioRequest req) {
        return servico.criar(req, usuarioAtual.obter());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void remover(@PathVariable String id) {
        servico.remover(id, usuarioAtual.obter());
    }
}
