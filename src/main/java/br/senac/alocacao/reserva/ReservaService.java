package br.senac.alocacao.reserva;

import br.senac.alocacao.auditoria.AcaoAuditoria;
import br.senac.alocacao.auditoria.AuditoriaService;
import br.senac.alocacao.comum.CodigoErro;
import br.senac.alocacao.comum.NegocioException;
import br.senac.alocacao.material.Material;
import br.senac.alocacao.material.MaterialRepository;
import br.senac.alocacao.professor.Professor;
import br.senac.alocacao.professor.ProfessorRepository;
import br.senac.alocacao.relatorio.ConflitoEvitadoService;
import br.senac.alocacao.reserva.regras.AvaliadorAprovacao;
import br.senac.alocacao.reserva.regras.Intervalo;
import br.senac.alocacao.reserva.regras.PoliticaHorario;
import br.senac.alocacao.sala.Sala;
import br.senac.alocacao.sala.SalaRepository;
import br.senac.alocacao.usuario.Perfil;
import br.senac.alocacao.usuario.Usuario;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@Service
public class ReservaService {
    static final String ENTIDADE = "Reserva";
    private static final Duration ANTECEDENCIA_INICIO = Duration.ofMinutes(30);
    private static final Duration TOLERANCIA_NAO_COMPARECIMENTO = Duration.ofMinutes(15);

    private final ReservaRepository reservas;
    private final SalaRepository salas;
    private final ProfessorRepository professores;
    private final MaterialRepository materiais;
    private final DisponibilidadeService disponibilidade;
    private final PoliticaHorario politicaHorario;
    private final ProtecaoConcorrencia protecao;
    private final AuditoriaService auditoria;
    private final ConflitoEvitadoService conflitosEvitados;
    private final ApplicationEventPublisher eventos;
    private final MongoTemplate mongo;
    private final Clock clock;

    @SuppressWarnings("java:S107")
    public ReservaService(ReservaRepository reservas, SalaRepository salas, ProfessorRepository professores,
                          MaterialRepository materiais, DisponibilidadeService disponibilidade,
                          PoliticaHorario politicaHorario, ProtecaoConcorrencia protecao,
                          AuditoriaService auditoria, ConflitoEvitadoService conflitosEvitados,
                          ApplicationEventPublisher eventos, MongoTemplate mongo, Clock clock) {
        this.reservas = reservas;
        this.salas = salas;
        this.professores = professores;
        this.materiais = materiais;
        this.disponibilidade = disponibilidade;
        this.politicaHorario = politicaHorario;
        this.protecao = protecao;
        this.auditoria = auditoria;
        this.conflitosEvitados = conflitosEvitados;
        this.eventos = eventos;
        this.mongo = mongo;
        this.clock = clock;
    }

    public ReservaResponse criar(ReservaRequest req, Usuario ator) {
        autorizarProfessor(ator, req.professorId());
        Intervalo intervalo = politicaHorario.validar(req.inicio(), req.fim());

        return comRegistroDeConflito(ator, intervalo, () -> {
            Agenda agenda = carregarEValidar(req, intervalo, null);

            Reserva reserva = new Reserva(ator, agora());
            reserva.definirAgenda(agenda.sala, agenda.professor, req.disciplina(), intervalo,
                    req.quantidadeAlunos(), req.observacao(), agenda.itens);
            reserva = reservas.save(reserva);
            auditoria.registrar(ENTIDADE, reserva.getId(), AcaoAuditoria.CRIACAO, ator,
                    null, StatusReserva.SOLICITADA.name(), reserva.resumo());

            aplicarPoliticaDeAprovacao(reserva, agenda, ator);
            return ReservaResponse.de(reservas.save(reserva));
        });
    }

    public ReservaResponse alterar(String id, ReservaRequest req, Usuario ator) {
        Intervalo intervalo = politicaHorario.validar(req.inicio(), req.fim());

        return comRegistroDeConflito(ator, intervalo, () -> {
            Reserva reserva = buscar(id);
            garantirDono(reserva, ator);
            autorizarProfessor(ator, req.professorId());
            if (!reserva.getStatus().podeSerAlterada()) {
                throw NegocioException.conflito(CodigoErro.TRANSICAO_INVALIDA,
                        "Somente reservas SOLICITADAS ou APROVADAS podem ser alteradas (atual: "
                                + reserva.getStatus() + ").", Map.of());
            }
            if (req.versao() == null || !Objects.equals(req.versao(), reserva.getVersao())) {
                throw NegocioException.conflito(CodigoErro.VERSAO_DESATUALIZADA,
                        "Esta reserva foi modificada desde que você a abriu. Recarregue e tente novamente.",
                        Map.of("versaoAtual", String.valueOf(reserva.getVersao())));
            }

            Agenda agenda = carregarEValidar(req, intervalo, reserva.getId());
            String antes = reserva.resumo();
            reserva.definirAgenda(agenda.sala, agenda.professor, req.disciplina(), intervalo,
                    req.quantidadeAlunos(), req.observacao(), agenda.itens);
            auditoria.registrar(ENTIDADE, reserva.getId(), AcaoAuditoria.ALTERACAO, ator,
                    antes, reserva.resumo(), null);

            aplicarPoliticaDeAprovacao(reserva, agenda, ator);
            return ReservaResponse.de(reservas.save(reserva));
        });
    }

    private ReservaResponse comRegistroDeConflito(Usuario ator, Intervalo intervalo,
                                                  Supplier<ReservaResponse> operacao) {
        try {
            return protecao.executar(operacao);
        } catch (NegocioException e) {
            conflitosEvitados.registrarSeConflito(e, ator, intervalo);
            throw e;
        }
    }

    private record Agenda(Sala sala, Professor professor, List<Material> materiais, List<ItemReserva> itens) {
    }

    private Agenda carregarEValidar(ReservaRequest req, Intervalo intervalo, String ignorarId) {
        Sala sala = salas.findById(req.salaId()).orElseThrow(() -> NegocioException.naoEncontrado("Sala", req.salaId()));
        Professor professor = professores.findById(req.professorId())
                .orElseThrow(() -> NegocioException.naoEncontrado("Professor", req.professorId()));

        if (req.quantidadeAlunos() > sala.getCapacidade()) {
            throw NegocioException.regra(CodigoErro.CAPACIDADE_EXCEDIDA,
                    "A sala " + sala.getCodigo() + " comporta " + sala.getCapacidade() + " pessoas, mas foram informados "
                            + req.quantidadeAlunos() + " alunos.");
        }

        var ids = new HashSet<String>();
        List<Material> listaMateriais = new ArrayList<>();
        List<ItemReserva> itens = new ArrayList<>();
        for (ReservaRequest.ItemRequest item : req.materiaisOuVazio()) {
            if (!ids.add(item.materialId())) {
                throw NegocioException.regra(CodigoErro.VALIDACAO, "O mesmo material foi informado mais de uma vez.");
            }
            Material m = materiais.findById(item.materialId())
                    .orElseThrow(() -> NegocioException.naoEncontrado("Material", item.materialId()));
            listaMateriais.add(m);
            itens.add(new ItemReserva(m.getId(), m.getNome(), item.quantidade()));
        }

        disponibilidade.garantirSala(sala, intervalo, ignorarId);
        disponibilidade.garantirProfessor(professor, intervalo, ignorarId);
        for (int i = 0; i < listaMateriais.size(); i++) {
            disponibilidade.garantirMaterial(listaMateriais.get(i), itens.get(i).quantidade(), intervalo, ignorarId);
        }

        tocarAgenda(Sala.class, sala.getId());
        tocarAgenda(Professor.class, professor.getId());
        listaMateriais.stream().map(Material::getId).sorted(Comparator.naturalOrder())
                .forEach(mid -> tocarAgenda(Material.class, mid));

        return new Agenda(sala, professor, listaMateriais, itens);
    }

    private void tocarAgenda(Class<?> tipo, String id) {
        mongo.updateFirst(Query.query(Criteria.where("id").is(id)), new Update().inc("versaoAgenda", 1), tipo);
    }

    private void aplicarPoliticaDeAprovacao(Reserva reserva, Agenda agenda, Usuario ator) {
        List<String> motivos = AvaliadorAprovacao.motivos(agenda.sala, agenda.materiais, agenda.professor,
                reserva.getDisciplina());
        reserva.setMotivosAprovacao(motivos);

        if (motivos.isEmpty() && reserva.getStatus() == StatusReserva.SOLICITADA) {
            mudarStatus(reserva, StatusReserva.APROVADA, null,
                    "Aprovação automática: nenhum recurso restrito e docente habilitado.");
        } else if (!motivos.isEmpty() && reserva.getStatus() == StatusReserva.APROVADA) {
            mudarStatus(reserva, StatusReserva.SOLICITADA, ator,
                    "Alteração exige nova aprovação: " + String.join("; ", motivos));
        } else if (!motivos.isEmpty()) {
            eventos.publishEvent(evento(reserva, null, StatusReserva.SOLICITADA, String.join("; ", motivos)));
        }
    }

    @Transactional
    public ReservaResponse cancelar(String id, String motivo, Usuario ator) {
        Reserva r = buscar(id);
        garantirDono(r, ator);
        mudarStatus(r, StatusReserva.CANCELADA, ator, motivo);
        return ReservaResponse.de(reservas.save(r));
    }

    @Transactional
    public ReservaResponse aprovar(String id, String observacao, Usuario responsavel) {
        Reserva r = buscar(id);
        mudarStatus(r, StatusReserva.APROVADA, responsavel, observacao);
        return ReservaResponse.de(reservas.save(r));
    }

    @Transactional
    public ReservaResponse rejeitar(String id, String observacao, Usuario responsavel) {
        Reserva r = buscar(id);
        mudarStatus(r, StatusReserva.REJEITADA, responsavel, observacao);
        return ReservaResponse.de(reservas.save(r));
    }

    @Transactional
    public ReservaResponse iniciar(String id, Usuario ator) {
        Reserva r = buscar(id);
        garantirDonoOuResponsavel(r, ator);
        LocalDateTime agora = agora();
        if (agora.isBefore(r.getInicio().minus(ANTECEDENCIA_INICIO)) || !agora.isBefore(r.getFim())) {
            throw NegocioException.regra(CodigoErro.HORARIO_INVALIDO,
                    "O uso só pode ser iniciado a partir de 30 minutos antes do início e antes do término da reserva.");
        }
        mudarStatus(r, StatusReserva.EM_USO, ator, null);
        return ReservaResponse.de(reservas.save(r));
    }

    @Transactional
    public ReservaResponse concluir(String id, Usuario ator) {
        Reserva r = buscar(id);
        garantirDonoOuResponsavel(r, ator);
        if (r.possuiMateriaisPendentes()) {
            throw NegocioException.conflito(CodigoErro.MATERIAIS_PENDENTES,
                    "Há materiais retirados que ainda não foram devolvidos. Registre a devolução antes de concluir.",
                    Map.of());
        }
        mudarStatus(r, StatusReserva.CONCLUIDA, ator, null);
        return ReservaResponse.de(reservas.save(r));
    }

    @Transactional
    public ReservaResponse registrarNaoComparecimento(String id, Usuario responsavel) {
        Reserva r = buscar(id);
        if (agora().isBefore(r.getInicio().plus(TOLERANCIA_NAO_COMPARECIMENTO))) {
            throw NegocioException.regra(CodigoErro.HORARIO_INVALIDO,
                    "O não comparecimento só pode ser registrado 15 minutos após o início da reserva.");
        }
        mudarStatus(r, StatusReserva.NAO_COMPARECEU, responsavel, null);
        return ReservaResponse.de(reservas.save(r));
    }

    @Transactional
    public void excluir(String id, Usuario ator) {
        Reserva r = buscar(id);
        if (ator.getPerfil() != Perfil.ADMIN) {
            garantirDono(r, ator);
        }
        if (r.getStatus().iniciada()) {
            throw NegocioException.conflito(CodigoErro.RESERVA_INICIADA,
                    "Reservas iniciadas não podem ser apagadas (status " + r.getStatus() + ").", Map.of());
        }
        auditoria.registrar(ENTIDADE, r.getId(), AcaoAuditoria.EXCLUSAO, ator, r.getStatus().name(), null, r.resumo());
        reservas.delete(r);
    }

    private void mudarStatus(Reserva r, StatusReserva destino, Usuario ator, String motivo) {
        StatusReserva anterior = r.getStatus();
        r.transicionar(destino, agora());
        auditoria.registrar(ENTIDADE, r.getId(), AcaoAuditoria.TRANSICAO_ESTADO, ator,
                anterior.name(), destino.name(), motivo);
        eventos.publishEvent(evento(r, anterior, destino, motivo));
    }

    private static ReservaStatusAlteradoEvent evento(Reserva r, StatusReserva anterior, StatusReserva novo,
                                                     String motivo) {
        return new ReservaStatusAlteradoEvent(r.getId(), r.getSolicitanteId(), r.getDisciplina(), r.getSalaCodigo(),
                r.getInicio(), anterior, novo, motivo);
    }

    @Transactional
    public ReservaResponse registrarRetirada(String id, MovimentacaoRequest req, Usuario responsavel) {
        Reserva r = buscar(id);
        ItemReserva item = itemParaMovimentar(r, req.materialId());
        int retirado = r.totalMovimentado(req.materialId(), TipoMovimentacao.RETIRADA);
        if (retirado + req.quantidade() > item.quantidade()) {
            throw NegocioException.regra(CodigoErro.MOVIMENTACAO_INVALIDA,
                    "Retirada acima do reservado: reservado " + item.quantidade() + ", já retirado " + retirado + ".");
        }
        return movimentar(r, item, TipoMovimentacao.RETIRADA, req, responsavel, AcaoAuditoria.RETIRADA_MATERIAL);
    }

    @Transactional
    public ReservaResponse registrarDevolucao(String id, MovimentacaoRequest req, Usuario responsavel) {
        Reserva r = buscar(id);
        ItemReserva item = itemParaMovimentar(r, req.materialId());
        int retirado = r.totalMovimentado(req.materialId(), TipoMovimentacao.RETIRADA);
        int devolvido = r.totalMovimentado(req.materialId(), TipoMovimentacao.DEVOLUCAO);
        if (devolvido + req.quantidade() > retirado) {
            throw NegocioException.regra(CodigoErro.MOVIMENTACAO_INVALIDA,
                    "Devolução acima do retirado: retirado " + retirado + ", já devolvido " + devolvido + ".");
        }
        return movimentar(r, item, TipoMovimentacao.DEVOLUCAO, req, responsavel, AcaoAuditoria.DEVOLUCAO_MATERIAL);
    }

    private ItemReserva itemParaMovimentar(Reserva r, String materialId) {
        if (r.getStatus() != StatusReserva.EM_USO) {
            throw NegocioException.conflito(CodigoErro.MOVIMENTACAO_INVALIDA,
                    "Retiradas e devoluções só podem ser registradas com a reserva EM_USO (atual: " + r.getStatus() + ").",
                    Map.of());
        }
        return r.item(materialId).orElseThrow(() -> NegocioException.regra(CodigoErro.MOVIMENTACAO_INVALIDA,
                "Este material não faz parte da reserva."));
    }

    private ReservaResponse movimentar(Reserva r, ItemReserva item, TipoMovimentacao tipo, MovimentacaoRequest req,
                                       Usuario responsavel, AcaoAuditoria acao) {
        r.registrarMovimentacao(new MovimentacaoMaterial(item.materialId(), tipo, req.quantidade(),
                responsavel.getId(), responsavel.getNome(), agora(), req.observacao()));
        auditoria.registrar(ENTIDADE, r.getId(), acao, responsavel, null, null,
                item.materialNome() + " x" + req.quantidade());
        return ReservaResponse.de(reservas.save(r));
    }

    public ReservaResponse obter(String id) {
        return ReservaResponse.de(buscar(id));
    }

    public List<ReservaResponse> pesquisar(LocalDateTime inicio, LocalDateTime fim, String salaId, String professorId,
                                           StatusReserva status, String solicitanteId) {
        Criteria c = new Criteria();
        if (inicio != null) {
            c = c.and("fim").gt(inicio);
        }
        if (fim != null) {
            c = c.and("inicio").lt(fim);
        }
        if (salaId != null) {
            c = c.and("salaId").is(salaId);
        }
        if (professorId != null) {
            c = c.and("professorId").is(professorId);
        }
        if (status != null) {
            c = c.and("status").is(status);
        }
        if (solicitanteId != null) {
            c = c.and("solicitanteId").is(solicitanteId);
        }
        return mongo.find(Query.query(c).with(Sort.by("inicio")).limit(1000), Reserva.class)
                .stream().map(ReservaResponse::de).toList();
    }

    private static void autorizarProfessor(Usuario ator, String professorId) {
        if (ator.getPerfil() == Perfil.PROFESSOR && !Objects.equals(ator.getProfessorId(), professorId)) {
            throw NegocioException.acessoNegado("Professores só podem criar reservas para a própria agenda.");
        }
    }

    private static void garantirDono(Reserva r, Usuario ator) {
        if (!r.getSolicitanteId().equals(ator.getId())) {
            throw NegocioException.acessoNegado("Você só pode modificar as suas próprias reservas.");
        }
    }

    private static void garantirDonoOuResponsavel(Reserva r, Usuario ator) {
        if (ator.getPerfil() != Perfil.RESPONSAVEL) {
            garantirDono(r, ator);
        }
    }

    private Reserva buscar(String id) {
        return reservas.findById(id).orElseThrow(() -> NegocioException.naoEncontrado("Reserva", id));
    }

    private LocalDateTime agora() {
        return LocalDateTime.now(clock);
    }
}
