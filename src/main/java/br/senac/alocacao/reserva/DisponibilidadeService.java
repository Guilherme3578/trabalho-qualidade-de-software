package br.senac.alocacao.reserva;

import br.senac.alocacao.bloqueio.Bloqueio;
import br.senac.alocacao.bloqueio.BloqueioRepository;
import br.senac.alocacao.bloqueio.TipoRecurso;
import br.senac.alocacao.comum.CodigoErro;
import br.senac.alocacao.comum.NegocioException;
import br.senac.alocacao.material.Material;
import br.senac.alocacao.professor.Professor;
import br.senac.alocacao.reserva.regras.CalculadoraOcupacao;
import br.senac.alocacao.reserva.regras.Intervalo;
import br.senac.alocacao.sala.Sala;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DisponibilidadeService {
    private final ReservaRepository reservas;
    private final BloqueioRepository bloqueios;

    public DisponibilidadeService(ReservaRepository reservas, BloqueioRepository bloqueios) {
        this.reservas = reservas;
        this.bloqueios = bloqueios;
    }

    public List<Reserva> reservasSobrepostas(TipoRecurso tipo, String recursoId, Intervalo intervalo,
                                             String ignorarId) {
        return switch (tipo) {
            case SALA -> reservas.sobrepostasPorSala(recursoId, StatusReserva.OCUPAM_AGENDA, intervalo.inicio(),
                    intervalo.fim(), ignorarId);
            case PROFESSOR -> reservas.sobrepostasPorProfessor(recursoId, StatusReserva.OCUPAM_AGENDA,
                    intervalo.inicio(), intervalo.fim(), ignorarId);
            case MATERIAL -> reservas.sobrepostasPorMaterial(recursoId, StatusReserva.OCUPAM_AGENDA,
                    intervalo.inicio(), intervalo.fim(), ignorarId);
        };
    }

    public List<Reserva> reservasFuturas(TipoRecurso tipo, String recursoId, LocalDateTime agora) {
        return switch (tipo) {
            case SALA -> reservas.futurasPorSala(recursoId, StatusReserva.OCUPAM_AGENDA, agora);
            case PROFESSOR -> reservas.futurasPorProfessor(recursoId, StatusReserva.OCUPAM_AGENDA, agora);
            case MATERIAL -> reservas.futurasPorMaterial(recursoId, StatusReserva.OCUPAM_AGENDA, agora);
        };
    }

    public List<Bloqueio> bloqueiosSobrepostos(TipoRecurso tipo, String recursoId, Intervalo intervalo) {
        return bloqueios.sobrepostos(tipo, recursoId, intervalo.inicio(), intervalo.fim());
    }

    public boolean salaLivre(Sala sala, Intervalo intervalo) {
        return sala.isAtiva()
                && bloqueiosSobrepostos(TipoRecurso.SALA, sala.getId(), intervalo).isEmpty()
                && reservasSobrepostas(TipoRecurso.SALA, sala.getId(), intervalo, null).isEmpty();
    }

    public boolean professorLivre(Professor professor, Intervalo intervalo) {
        return professor.isAtivo()
                && bloqueiosSobrepostos(TipoRecurso.PROFESSOR, professor.getId(), intervalo).isEmpty()
                && reservasSobrepostas(TipoRecurso.PROFESSOR, professor.getId(), intervalo, null).isEmpty();
    }

    public int quantidadeDisponivel(Material material, Intervalo intervalo, String ignorarReservaId) {
        if (!material.isAtivo() || !bloqueiosSobrepostos(TipoRecurso.MATERIAL, material.getId(), intervalo).isEmpty()) {
            return 0;
        }
        List<CalculadoraOcupacao.Uso> usos = reservasSobrepostas(TipoRecurso.MATERIAL, material.getId(), intervalo,
                ignorarReservaId).stream()
                .map(r -> new CalculadoraOcupacao.Uso(r.getInicio(), r.getFim(),
                        r.item(material.getId()).map(ItemReserva::quantidade).orElse(0)))
                .toList();
        return Math.max(0, material.getQuantidadeTotal() - CalculadoraOcupacao.picoDeUso(usos, intervalo));
    }

    public void garantirSala(Sala sala, Intervalo intervalo, String ignorarId) {
        if (!sala.isAtiva()) {
            throw NegocioException.regra(CodigoErro.RECURSO_INATIVO,
                    "A sala " + sala.getCodigo() + " está desativada e não pode ser reservada.");
        }
        garantirSemBloqueio(TipoRecurso.SALA, sala.getId(), "A sala " + sala.getCodigo(), intervalo);
        reservasSobrepostas(TipoRecurso.SALA, sala.getId(), intervalo, ignorarId).stream().findFirst().ifPresent(r -> {
            throw NegocioException.conflito(CodigoErro.CONFLITO_SALA,
                    "A sala " + sala.getCodigo() + " já está reservada das " + hora(r.getInicio()) + " às "
                            + hora(r.getFim()) + " neste dia.",
                    detalhes(TipoRecurso.SALA, sala.getId(), r));
        });
    }

    public void garantirProfessor(Professor professor, Intervalo intervalo, String ignorarId) {
        if (!professor.isAtivo()) {
            throw NegocioException.regra(CodigoErro.RECURSO_INATIVO,
                    "O professor " + professor.getNome() + " está inativo.");
        }
        garantirSemBloqueio(TipoRecurso.PROFESSOR, professor.getId(), "O professor " + professor.getNome(), intervalo);
        reservasSobrepostas(TipoRecurso.PROFESSOR, professor.getId(), intervalo, ignorarId).stream().findFirst()
                .ifPresent(r -> {
                    throw NegocioException.conflito(CodigoErro.CONFLITO_PROFESSOR,
                            "O professor " + professor.getNome() + " já tem aula na sala " + r.getSalaCodigo()
                                    + " das " + hora(r.getInicio()) + " às " + hora(r.getFim()) + ".",
                            detalhes(TipoRecurso.PROFESSOR, professor.getId(), r));
                });
    }

    public void garantirMaterial(Material material, int quantidade, Intervalo intervalo, String ignorarId) {
        if (!material.isAtivo()) {
            throw NegocioException.regra(CodigoErro.RECURSO_INATIVO,
                    "O material " + material.getNome() + " está desativado.");
        }
        garantirSemBloqueio(TipoRecurso.MATERIAL, material.getId(), "O material " + material.getNome(), intervalo);
        int disponivel = quantidadeDisponivel(material, intervalo, ignorarId);
        if (quantidade > disponivel) {
            Map<String, Object> det = new LinkedHashMap<>();
            det.put("recursoTipo", TipoRecurso.MATERIAL.name());
            det.put("recursoId", material.getId());
            det.put("solicitado", quantidade);
            det.put("disponivel", disponivel);
            throw NegocioException.conflito(CodigoErro.CONFLITO_MATERIAL,
                    "Quantidade indisponível de " + material.getNome() + " neste horário: solicitado "
                            + quantidade + ", disponível " + disponivel + ".", det);
        }
    }

    public void garantirSemManutencao(Reserva r) {
        Intervalo intervalo = new Intervalo(r.getInicio(), r.getFim());
        garantirSemBloqueio(TipoRecurso.SALA, r.getSalaId(), "A sala " + r.getSalaCodigo(), intervalo);
        garantirSemBloqueio(TipoRecurso.PROFESSOR, r.getProfessorId(), "O professor " + r.getProfessorNome(),
                intervalo);
        for (ItemReserva item : r.getItens()) {
            garantirSemBloqueio(TipoRecurso.MATERIAL, item.materialId(), "O material " + item.materialNome(),
                    intervalo);
        }
    }

    private void garantirSemBloqueio(TipoRecurso tipo, String id, String descricao, Intervalo intervalo) {
        bloqueiosSobrepostos(tipo, id, intervalo).stream().findFirst().ifPresent(b -> {
            Map<String, Object> det = new LinkedHashMap<>();
            det.put("recursoTipo", tipo.name());
            det.put("recursoId", id);
            det.put("bloqueioId", b.getId());
            det.put("motivo", b.getMotivo());
            det.put("bloqueioInicio", b.getInicio().toString());
            det.put("bloqueioFim", b.getFim().toString());
            throw NegocioException.conflito(CodigoErro.RECURSO_EM_MANUTENCAO,
                    descricao + " está indisponível neste período (" + b.getMotivo() + ").", det);
        });
    }

    private static Map<String, Object> detalhes(TipoRecurso tipo, String recursoId, Reserva conflitante) {
        Map<String, Object> det = new LinkedHashMap<>();
        det.put("recursoTipo", tipo.name());
        det.put("recursoId", recursoId);
        det.put("reservaConflitanteId", conflitante.getId());
        det.put("conflitoInicio", conflitante.getInicio().toString());
        det.put("conflitoFim", conflitante.getFim().toString());
        return det;
    }

    private static String hora(java.time.LocalDateTime t) {
        return t.toLocalTime().toString();
    }
}
