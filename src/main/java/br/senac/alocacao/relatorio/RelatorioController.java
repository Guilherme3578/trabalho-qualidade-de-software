package br.senac.alocacao.relatorio;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/relatorios")
@PreAuthorize("hasAnyRole('COORDENADOR','RESPONSAVEL','ADMIN')")
@Tag(name = "Relatórios", description = "Utilização por recurso, carga horária e conflitos evitados")
public class RelatorioController {

    private final RelatorioService servico;

    public RelatorioController(RelatorioService servico) {
        this.servico = servico;
    }

    @GetMapping("/utilizacao")
    @Operation(summary = "Horas reservadas e taxa de ocupação de cada sala e material no período")
    public RelatorioDtos.RelatorioUtilizacao utilizacao(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return servico.utilizacao(inicio, fim);
    }

    @GetMapping("/carga-horaria")
    @Operation(summary = "Carga horária alocada por professor no período")
    public List<RelatorioDtos.CargaHorariaProfessor> cargaHoraria(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return servico.cargaHoraria(inicio, fim);
    }

    @GetMapping("/conflitos-evitados")
    @Operation(summary = "Tentativas de reserva recusadas por conflito ou manutenção")
    public RelatorioDtos.RelatorioConflitos conflitosEvitados(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return servico.conflitosEvitados(inicio, fim);
    }
}
