package br.senac.alocacao.sala;

import br.senac.alocacao.usuario.UsuarioAtual;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
@RequestMapping("/api/salas")
@Tag(name = "Salas", description = "Cadastro e pesquisa de salas")
public class SalaController {

    private final SalaService servico;
    private final UsuarioAtual usuarioAtual;

    public SalaController(SalaService servico, UsuarioAtual usuarioAtual) {
        this.servico = servico;
        this.usuarioAtual = usuarioAtual;
    }

    @GetMapping
    @Operation(summary = "Pesquisa salas por tipo, capacidade, localização e disponibilidade (inicio+fim)")
    public List<SalaResponse> pesquisar(
            @RequestParam(required = false) TipoSala tipo,
            @RequestParam(required = false) @Min(value = 1, message = "A capacidade mínima deve ser positiva.")
            Integer capacidadeMinima,
            @RequestParam(required = false) String localizacao,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            @RequestParam(defaultValue = "false") boolean incluirInativas) {
        return servico.pesquisar(tipo, capacidadeMinima, localizacao, inicio, fim, incluirInativas);
    }

    @GetMapping("/{id}")
    public SalaResponse obter(@PathVariable String id) {
        return servico.obter(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public SalaResponse criar(@Valid @RequestBody SalaRequest req) {
        return servico.criar(req, usuarioAtual.obter());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public SalaResponse atualizar(@PathVariable String id, @Valid @RequestBody SalaRequest req) {
        return servico.atualizar(id, req, usuarioAtual.obter());
    }

    @PatchMapping("/{id}/ativa")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Ativa/desativa a sala (registros não são apagados para preservar o histórico)")
    public SalaResponse definirAtiva(@PathVariable String id, @RequestParam boolean valor) {
        return servico.definirAtiva(id, valor, usuarioAtual.obter());
    }
}
