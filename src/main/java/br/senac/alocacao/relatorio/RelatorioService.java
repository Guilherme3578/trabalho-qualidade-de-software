package br.senac.alocacao.relatorio;

import br.senac.alocacao.comum.CodigoErro;
import br.senac.alocacao.comum.NegocioException;
import br.senac.alocacao.material.Material;
import br.senac.alocacao.material.MaterialRepository;
import br.senac.alocacao.reserva.ItemReserva;
import br.senac.alocacao.reserva.Reserva;
import br.senac.alocacao.reserva.StatusReserva;
import br.senac.alocacao.reserva.regras.PoliticaHorario;
import br.senac.alocacao.sala.Sala;
import br.senac.alocacao.sala.SalaRepository;
import br.senac.alocacao.relatorio.RelatorioDtos.CargaHorariaProfessor;
import br.senac.alocacao.relatorio.RelatorioDtos.ConflitosPorRecurso;
import br.senac.alocacao.relatorio.RelatorioDtos.RelatorioConflitos;
import br.senac.alocacao.relatorio.RelatorioDtos.RelatorioUtilizacao;
import br.senac.alocacao.relatorio.RelatorioDtos.UtilizacaoRecurso;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RelatorioService {
    private static final long MAX_DIAS = 366;

    private final MongoTemplate mongo;
    private final SalaRepository salas;
    private final MaterialRepository materiais;

    public RelatorioService(MongoTemplate mongo, SalaRepository salas, MaterialRepository materiais) {
        this.mongo = mongo;
        this.salas = salas;
        this.materiais = materiais;
    }

    public RelatorioUtilizacao utilizacao(LocalDate inicio, LocalDate fim) {
        validarPeriodo(inicio, fim);
        List<Reserva> efetivas = reservasEfetivas(inicio, fim);
        long dias = ChronoUnit.DAYS.between(inicio, fim) + 1;
        double horasDisponiveis = dias * Duration.between(PoliticaHorario.ABERTURA, PoliticaHorario.FECHAMENTO)
                .toMinutes() / 60.0;

        Map<String, Sala> mapaSalas = salas.findAll().stream().collect(Collectors.toMap(Sala::getId, Function.identity()));
        List<UtilizacaoRecurso> porSala = mapaSalas.values().stream()
                .sorted(Comparator.comparing(Sala::getCodigo))
                .map(s -> {
                    List<Reserva> rs = efetivas.stream().filter(r -> r.getSalaId().equals(s.getId())).toList();
                    double horas = rs.stream().mapToDouble(RelatorioService::horas).sum();
                    return new UtilizacaoRecurso("SALA", s.getId(), s.getCodigo() + " - " + s.getNome(), rs.size(),
                            arredondar(horas), arredondar(100.0 * horas / horasDisponiveis));
                })
                .toList();

        List<UtilizacaoRecurso> porMaterial = materiais.findAll().stream()
                .sorted(Comparator.comparing(Material::getNome))
                .map(m -> {
                    long qtdReservas = 0;
                    double unidadesHora = 0;
                    for (Reserva r : efetivas) {
                        int q = r.item(m.getId()).map(ItemReserva::quantidade).orElse(0);
                        if (q > 0) {
                            qtdReservas++;
                            unidadesHora += q * horas(r);
                        }
                    }
                    double capacidade = horasDisponiveis * Math.max(1, m.getQuantidadeTotal());
                    return new UtilizacaoRecurso("MATERIAL", m.getId(), m.getNome(), qtdReservas,
                            arredondar(unidadesHora), arredondar(100.0 * unidadesHora / capacidade));
                })
                .toList();

        return new RelatorioUtilizacao(inicio, fim, horasDisponiveis, porSala, porMaterial);
    }

    public List<CargaHorariaProfessor> cargaHoraria(LocalDate inicio, LocalDate fim) {
        validarPeriodo(inicio, fim);
        return reservasEfetivas(inicio, fim).stream()
                .collect(Collectors.groupingBy(Reserva::getProfessorId))
                .values().stream()
                .map(rs -> new CargaHorariaProfessor(rs.get(0).getProfessorId(), rs.get(0).getProfessorNome(),
                        rs.size(), arredondar(rs.stream().mapToDouble(RelatorioService::horas).sum())))
                .sorted(Comparator.comparingDouble(CargaHorariaProfessor::horasAlocadas).reversed())
                .toList();
    }

    public RelatorioConflitos conflitosEvitados(LocalDate inicio, LocalDate fim) {
        validarPeriodo(inicio, fim);
        List<ConflitoEvitado> conflitos = mongo.find(Query.query(Criteria.where("registradoEm")
                .gte(inicio.atStartOfDay()).lt(fim.plusDays(1).atStartOfDay())), ConflitoEvitado.class);

        Map<String, Long> porTipo = conflitos.stream()
                .collect(Collectors.groupingBy(ConflitoEvitado::tipo, TreeMap::new, Collectors.counting()));
        Map<String, String> nomesSalas = salas.findAll().stream()
                .collect(Collectors.toMap(Sala::getId, Sala::getCodigo));
        Map<String, String> nomesMateriais = materiais.findAll().stream()
                .collect(Collectors.toMap(Material::getId, Material::getNome));

        List<ConflitosPorRecurso> porRecurso = conflitos.stream()
                .collect(Collectors.groupingBy(c -> c.recursoTipo() + "|" + c.recursoId(), Collectors.counting()))
                .entrySet().stream()
                .map(e -> {
                    String[] partes = e.getKey().split("\\|", 2);
                    String descricao = nomesSalas.getOrDefault(partes[1],
                            nomesMateriais.getOrDefault(partes[1], partes[1]));
                    return new ConflitosPorRecurso(partes[0], partes[1], descricao, e.getValue());
                })
                .sorted(Comparator.comparingLong(ConflitosPorRecurso::quantidade).reversed())
                .toList();
        return new RelatorioConflitos(inicio, fim, conflitos.size(), porTipo, porRecurso);
    }

    private List<Reserva> reservasEfetivas(LocalDate inicio, LocalDate fim) {
        LocalDateTime de = inicio.atStartOfDay();
        LocalDateTime ate = fim.plusDays(1).atStartOfDay();
        return mongo.find(Query.query(Criteria.where("status").in(StatusReserva.EFETIVOS)
                .and("inicio").gte(de).lt(ate)), Reserva.class);
    }

    private static void validarPeriodo(LocalDate inicio, LocalDate fim) {
        if (fim.isBefore(inicio)) {
            throw NegocioException.regra(CodigoErro.VALIDACAO, "A data final deve ser igual ou posterior à inicial.");
        }
        if (ChronoUnit.DAYS.between(inicio, fim) > MAX_DIAS) {
            throw NegocioException.regra(CodigoErro.VALIDACAO, "O período do relatório deve ser de no máximo 1 ano.");
        }
    }

    private static double horas(Reserva r) {
        return Duration.between(r.getInicio(), r.getFim()).toMinutes() / 60.0;
    }

    private static double arredondar(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
