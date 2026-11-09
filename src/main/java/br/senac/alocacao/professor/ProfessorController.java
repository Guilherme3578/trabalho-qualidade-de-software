package br.senac.alocacao.professor;

import br.senac.alocacao.usuario.UsuarioAtual;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/api/professores")
@Tag(name = "Professores", description = "Cadastro e pesquisa de professores e competências")
public class ProfessorController {
    private final ProfessorService servico;
    private final UsuarioAtual usuarioAtual;

    public ProfessorController(ProfessorService servico, UsuarioAtual usuarioAtual) {
        this.servico = servico;
        this.usuarioAtual = usuarioAtual;
    }

    @GetMapping
    @Operation(summary = "Pesquisa professores por competência, departamento, nome e disponibilidade")
    public List<ProfessorResponse> pesquisar(
            @RequestParam(required = false) String competencia,
            @RequestParam(required = false) String departamento,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return servico.pesquisar(competencia, departamento, nome, inicio, fim);
    }

    @GetMapping("/{id}")
    public ProfessorResponse obter(@PathVariable String id) {
        return servico.obter(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ProfessorResponse criar(@Valid @RequestBody ProfessorRequest req) {
        return servico.criar(req, usuarioAtual.obter());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProfessorResponse atualizar(@PathVariable String id, @Valid @RequestBody ProfessorRequest req) {
        return servico.atualizar(id, req, usuarioAtual.obter());
    }

    @PatchMapping("/{id}/ativo")
    @PreAuthorize("hasRole('ADMIN')")
    public ProfessorResponse definirAtivo(@PathVariable String id, @RequestParam boolean valor) {
        return servico.definirAtivo(id, valor, usuarioAtual.obter());
    }
}
