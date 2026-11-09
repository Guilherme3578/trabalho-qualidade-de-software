package br.senac.alocacao.material;

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
@RequestMapping("/api/materiais")
@Tag(name = "Materiais", description = "Cadastro e pesquisa de materiais e equipamentos")
public class MaterialController {
    private final MaterialService servico;
    private final UsuarioAtual usuarioAtual;

    public MaterialController(MaterialService servico, UsuarioAtual usuarioAtual) {
        this.servico = servico;
        this.usuarioAtual = usuarioAtual;
    }

    @GetMapping
    @Operation(summary = "Pesquisa materiais; com inicio+fim informa a quantidade disponível no período")
    public List<MaterialResponse> pesquisar(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String localizacao,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            @RequestParam(required = false) Integer quantidadeMinima) {
        return servico.pesquisar(categoria, localizacao, nome, inicio, fim, quantidadeMinima);
    }

    @GetMapping("/{id}")
    public MaterialResponse obter(@PathVariable String id) {
        return servico.obter(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public MaterialResponse criar(@Valid @RequestBody MaterialRequest req) {
        return servico.criar(req, usuarioAtual.obter());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public MaterialResponse atualizar(@PathVariable String id, @Valid @RequestBody MaterialRequest req) {
        return servico.atualizar(id, req, usuarioAtual.obter());
    }

    @PatchMapping("/{id}/ativo")
    @PreAuthorize("hasRole('ADMIN')")
    public MaterialResponse definirAtivo(@PathVariable String id, @RequestParam boolean valor) {
        return servico.definirAtivo(id, valor, usuarioAtual.obter());
    }
}
