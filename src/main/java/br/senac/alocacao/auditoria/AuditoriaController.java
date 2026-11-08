package br.senac.alocacao.auditoria;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
@Tag(name = "Auditoria", description = "Histórico auditável de mudanças (somente leitura)")
public class AuditoriaController {

    private final AuditoriaService auditoria;

    public AuditoriaController(AuditoriaService auditoria) {
        this.auditoria = auditoria;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSAVEL')")
    @Operation(summary = "Pesquisa eventos de auditoria (mais recentes primeiro, máx. 500)")
    public List<EventoAuditoria> pesquisar(@RequestParam(required = false) String entidade,
                                           @RequestParam(required = false) String entidadeId,
                                           @RequestParam(required = false) AcaoAuditoria acao) {
        return auditoria.pesquisar(entidade, entidadeId, acao);
    }
}
