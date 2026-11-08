package br.senac.alocacao.integracao;

import br.senac.alocacao.auditoria.AcaoAuditoria;
import br.senac.alocacao.auditoria.AuditoriaService;
import br.senac.alocacao.auditoria.EventoAuditoria;
import br.senac.alocacao.bloqueio.BloqueioRequest;
import br.senac.alocacao.bloqueio.BloqueioService;
import br.senac.alocacao.bloqueio.TipoRecurso;
import br.senac.alocacao.comum.CodigoErro;
import br.senac.alocacao.comum.NegocioException;
import br.senac.alocacao.material.Material;
import br.senac.alocacao.professor.Professor;
import br.senac.alocacao.relatorio.ConflitoEvitado;
import br.senac.alocacao.reserva.MovimentacaoRequest;
import br.senac.alocacao.reserva.ReservaRequest;
import br.senac.alocacao.reserva.ReservaResponse;
import br.senac.alocacao.reserva.ReservaService;
import br.senac.alocacao.reserva.StatusReserva;
import br.senac.alocacao.sala.Sala;
import br.senac.alocacao.suporte.IntegracaoBase;
import br.senac.alocacao.usuario.Perfil;
import br.senac.alocacao.usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Integração: regras de reserva com MongoDB real")
class ReservaServiceIT extends IntegracaoBase {

    @Autowired ReservaService servico;
    @Autowired BloqueioService bloqueios;
    @Autowired AuditoriaService auditoria;

    Usuario coordenador;
    Usuario responsavel;
    Usuario admin;
    Sala a101;
    Sala labRestrito;
    Professor ana;
    Professor bruno;
    Material projetor;

    @BeforeEach
    void dados() {
        coordenador = usuario(Perfil.COORDENADOR);
        responsavel = usuario(Perfil.RESPONSAVEL);
        admin = usuario(Perfil.ADMIN);
        a101 = sala("A101", 40, false);
        labRestrito = sala("LAB-R", 20, true);
        ana = professor("Ana Souza", "Qualidade de Software");
        bruno = professor("Bruno Lima", "Banco de Dados");
        projetor = material("Projetor", 2, false);
    }

    private ReservaRequest req(Sala s, Professor p, String disc, String ini, String fim, List<ReservaRequest.ItemRequest> mats) {
        return new ReservaRequest(s.getId(), p.getId(), disc, em("2030-03-11T" + ini), em("2030-03-11T" + fim), 20,
                null, mats, null);
    }

    private ReservaResponse criar(Sala s, Professor p, String disc, String ini, String fim) {
        return servico.criar(req(s, p, disc, ini, fim, List.of()), coordenador);
    }

    // ---------------------------------------------------------------- sobreposição

    @Test
    @DisplayName("[RF05][RN02] Mesma sala em horário sobreposto é recusada com CONFLITO_SALA")
    void conflitoSala() {
        criar(a101, ana, "Qualidade de Software", "09:00", "11:00");
        assertThatThrownBy(() -> criar(a101, bruno, "Banco de Dados", "10:00", "12:00"))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("A101 já está reservada das 09:00 às 11:00")
                .extracting("codigo").isEqualTo(CodigoErro.CONFLITO_SALA);
    }

    @Test
    @DisplayName("[RF05][RN02] Mesmo professor em salas diferentes no mesmo horário é recusado")
    void conflitoAgendaProfessor() {
        Sala a102 = sala("A102", 40, false);
        criar(a101, ana, "Qualidade de Software", "09:00", "11:00");
        assertThatThrownBy(() -> criar(a102, ana, "Qualidade de Software", "10:30", "12:00"))
                .isInstanceOf(NegocioException.class)
                .extracting("codigo").isEqualTo(CodigoErro.CONFLITO_PROFESSOR);
    }

    @Test
    @DisplayName("[RN02] Reservas adjacentes (fim = início) são permitidas")
    void adjacentes() {
        criar(a101, ana, "Qualidade de Software", "09:00", "11:00");
        ReservaResponse r = criar(a101, ana, "Qualidade de Software", "11:00", "13:00");
        assertThat(r.status()).isEqualTo(StatusReserva.APROVADA);
    }

    @Test
    @DisplayName("[RF05][RN02] Material: soma no pico não pode passar do estoque")
    void conflitoMaterial() {
        Sala a102 = sala("A102", 40, false);
        servico.criar(req(a101, ana, "Qualidade de Software", "09:00", "11:00",
                List.of(new ReservaRequest.ItemRequest(projetor.getId(), 2))), coordenador);
        assertThatThrownBy(() -> servico.criar(req(a102, bruno, "Banco de Dados", "10:00", "12:00",
                List.of(new ReservaRequest.ItemRequest(projetor.getId(), 1))), coordenador))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("disponível 0")
                .extracting("codigo").isEqualTo(CodigoErro.CONFLITO_MATERIAL);
    }

    @Test
    @DisplayName("[RN02] Reserva cancelada libera o horário")
    void canceladaLibera() {
        ReservaResponse r = criar(a101, ana, "Qualidade de Software", "09:00", "11:00");
        servico.cancelar(r.id(), "turma dispensada", coordenador);
        assertThat(criar(a101, bruno, "Banco de Dados", "09:00", "11:00").status()).isEqualTo(StatusReserva.APROVADA);
    }

    @Test
    @DisplayName("[RN09] Quantidade de alunos acima da capacidade é recusada")
    void capacidade() {
        ReservaRequest r = new ReservaRequest(labRestrito.getId(), ana.getId(), "Qualidade de Software",
                em("2030-03-11T09:00"), em("2030-03-11T10:00"), 21, null, null, null);
        assertThatThrownBy(() -> servico.criar(r, coordenador))
                .extracting("codigo").isEqualTo(CodigoErro.CAPACIDADE_EXCEDIDA);
    }

    // ---------------------------------------------------------------- manutenção

    @Test
    @DisplayName("[RF08][RN04] Sala em manutenção não pode ser reservada")
    void salaEmManutencao() {
        bloqueios.criar(new BloqueioRequest(TipoRecurso.SALA, a101.getId(), em("2030-03-11T00:00"),
                em("2030-03-12T00:00"), "Troca do ar-condicionado"), admin);
        assertThatThrownBy(() -> criar(a101, ana, "Qualidade de Software", "09:00", "11:00"))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("Troca do ar-condicionado")
                .extracting("codigo").isEqualTo(CodigoErro.RECURSO_EM_MANUTENCAO);
    }

    @Test
    @DisplayName("[RF08][RN04] Material em manutenção não pode ser reservado")
    void materialEmManutencao() {
        bloqueios.criar(new BloqueioRequest(TipoRecurso.MATERIAL, projetor.getId(), em("2030-03-11T08:00"),
                em("2030-03-11T12:00"), "Lâmpada queimada"), admin);
        assertThatThrownBy(() -> servico.criar(req(a101, ana, "Qualidade de Software", "09:00", "10:00",
                List.of(new ReservaRequest.ItemRequest(projetor.getId(), 1))), coordenador))
                .extracting("codigo").isEqualTo(CodigoErro.RECURSO_EM_MANUTENCAO);
    }

    @Test
    @DisplayName("[RF08] Bloqueio informa reservas já existentes afetadas")
    void bloqueioInformaAfetadas() {
        ReservaResponse r = criar(a101, ana, "Qualidade de Software", "09:00", "11:00");
        var resp = bloqueios.criar(new BloqueioRequest(TipoRecurso.SALA, a101.getId(), em("2030-03-11T10:00"),
                em("2030-03-11T18:00"), "Pintura"), admin);
        assertThat(resp.reservasAfetadas()).containsExactly(r.id());
    }

    // ---------------------------------------------------------------- aprovação e estados

    @Test
    @DisplayName("[RF07] Recurso comum e docente habilitado: aprovação automática")
    void aprovacaoAutomatica() {
        assertThat(criar(a101, ana, "Qualidade de Software", "09:00", "11:00").status())
                .isEqualTo(StatusReserva.APROVADA);
    }

    @Test
    @DisplayName("[RF07][RN06] Sala restrita fica SOLICITADA até o responsável aprovar")
    void restritaPrecisaAprovacao() {
        ReservaResponse r = criar(labRestrito, ana, "Qualidade de Software", "09:00", "11:00");
        assertThat(r.status()).isEqualTo(StatusReserva.SOLICITADA);
        assertThat(r.motivosAprovacao()).containsExactly("Sala restrita: LAB-R");

        ReservaResponse aprovada = servico.aprovar(r.id(), "ok", responsavel);
        assertThat(aprovada.status()).isEqualTo(StatusReserva.APROVADA);
    }

    @Test
    @DisplayName("[RF07] Professor sem competência fica SOLICITADA (validação docente)")
    void docenteSemCompetencia() {
        ReservaResponse r = criar(a101, ana, "Banco de Dados", "09:00", "11:00");
        assertThat(r.status()).isEqualTo(StatusReserva.SOLICITADA);
        assertThat(r.motivosAprovacao()).anyMatch(m -> m.contains("sem competência"));
    }

    @Test
    @DisplayName("[RN05] Ciclo completo SOLICITADA → APROVADA → EM_USO → CONCLUIDA com auditoria de cada passo")
    void cicloCompletoAuditado() {
        ReservaResponse r = criar(labRestrito, ana, "Qualidade de Software", "09:00", "11:00");
        servico.aprovar(r.id(), null, responsavel);
        relogio.definir(em("2030-03-11T08:45"));
        servico.iniciar(r.id(), coordenador);
        servico.concluir(r.id(), coordenador);

        List<EventoAuditoria> transicoes = auditoria.historico("Reserva", r.id()).stream()
                .filter(e -> e.acao() == AcaoAuditoria.TRANSICAO_ESTADO).toList();
        assertThat(transicoes).extracting(EventoAuditoria::estadoNovo)
                .containsExactly("APROVADA", "EM_USO", "CONCLUIDA");
        assertThat(transicoes).extracting(EventoAuditoria::usuarioNome)
                .containsExactly(responsavel.getNome(), coordenador.getNome(), coordenador.getNome());
    }

    @Test
    @DisplayName("[RN08] Toda mudança de estado gera exatamente um evento de auditoria")
    void auditoriaPorTransicao() {
        ReservaResponse r = criar(a101, ana, "Qualidade de Software", "09:00", "11:00");
        servico.cancelar(r.id(), "motivo", coordenador);
        List<EventoAuditoria> h = auditoria.historico("Reserva", r.id());
        assertThat(h).extracting(EventoAuditoria::acao).containsExactly(
                AcaoAuditoria.CRIACAO, AcaoAuditoria.TRANSICAO_ESTADO, AcaoAuditoria.TRANSICAO_ESTADO);
        assertThat(h.get(1).usuarioNome()).isEqualTo(AuditoriaService.SISTEMA);
        assertThat(h.get(2).estadoAnterior()).isEqualTo("APROVADA");
        assertThat(h.get(2).estadoNovo()).isEqualTo("CANCELADA");
        assertThat(h.get(2).detalhes()).isEqualTo("motivo");
    }

    @Test
    @DisplayName("[RN08] Operação recusada não deixa auditoria parcial (mesma transação)")
    void semAuditoriaParcial() {
        criar(a101, ana, "Qualidade de Software", "09:00", "11:00");
        long antes = mongo.count(new org.springframework.data.mongodb.core.query.Query(), EventoAuditoria.class);
        assertThatThrownBy(() -> criar(a101, bruno, "Banco de Dados", "09:00", "11:00"));
        long depois = mongo.count(new org.springframework.data.mongodb.core.query.Query(), EventoAuditoria.class);
        assertThat(depois).isEqualTo(antes);
    }

    @Test
    @DisplayName("[RN07] Reserva iniciada não pode ser apagada")
    void iniciadaNaoApaga() {
        ReservaResponse r = criar(a101, ana, "Qualidade de Software", "09:00", "11:00");
        relogio.definir(em("2030-03-11T09:05"));
        servico.iniciar(r.id(), coordenador);
        assertThatThrownBy(() -> servico.excluir(r.id(), admin))
                .isInstanceOf(NegocioException.class)
                .extracting("codigo").isEqualTo(CodigoErro.RESERVA_INICIADA);
    }

    @Test
    @DisplayName("[RN07] Reserva não iniciada pode ser apagada e a exclusão é auditada")
    void naoIniciadaApaga() {
        ReservaResponse r = criar(a101, ana, "Qualidade de Software", "09:00", "11:00");
        servico.excluir(r.id(), coordenador);
        assertThat(servico.pesquisar(null, null, null, null, null, null)).isEmpty();
        assertThat(auditoria.historico("Reserva", r.id())).extracting(EventoAuditoria::acao)
                .contains(AcaoAuditoria.EXCLUSAO);
    }

    @Test
    @DisplayName("[RN05] Não comparecimento só após a tolerância de 15 minutos")
    void naoCompareceu() {
        ReservaResponse r = criar(a101, ana, "Qualidade de Software", "09:00", "11:00");
        relogio.definir(em("2030-03-11T09:10"));
        assertThatThrownBy(() -> servico.registrarNaoComparecimento(r.id(), responsavel))
                .extracting("codigo").isEqualTo(CodigoErro.HORARIO_INVALIDO);
        relogio.definir(em("2030-03-11T09:20"));
        assertThat(servico.registrarNaoComparecimento(r.id(), responsavel).status())
                .isEqualTo(StatusReserva.NAO_COMPARECEU);
    }

    // ---------------------------------------------------------------- alteração

    @Test
    @DisplayName("[RF04] Alteração revalida conflitos ignorando a própria reserva")
    void alteracao() {
        ReservaResponse r = criar(a101, ana, "Qualidade de Software", "09:00", "11:00");
        ReservaRequest nova = new ReservaRequest(a101.getId(), ana.getId(), "Qualidade de Software",
                em("2030-03-11T10:00"), em("2030-03-11T12:00"), 20, null, null, r.versao());
        ReservaResponse alterada = servico.alterar(r.id(), nova, coordenador);
        assertThat(alterada.inicio()).isEqualTo(em("2030-03-11T10:00"));
        assertThat(alterada.status()).isEqualTo(StatusReserva.APROVADA);
    }

    @Test
    @DisplayName("[RF04] Alteração com versão desatualizada é recusada")
    void alteracaoVersaoAntiga() {
        ReservaResponse r = criar(a101, ana, "Qualidade de Software", "09:00", "11:00");
        ReservaRequest nova = new ReservaRequest(a101.getId(), ana.getId(), "Qualidade de Software",
                em("2030-03-11T10:00"), em("2030-03-11T12:00"), 20, null, null, r.versao() - 1);
        assertThatThrownBy(() -> servico.alterar(r.id(), nova, coordenador))
                .extracting("codigo").isEqualTo(CodigoErro.VERSAO_DESATUALIZADA);
    }

    @Test
    @DisplayName("[RF07] Alterar para sala restrita devolve a reserva para SOLICITADA")
    void alteracaoExigeNovaAprovacao() {
        ReservaResponse r = criar(a101, ana, "Qualidade de Software", "09:00", "11:00");
        ReservaRequest nova = new ReservaRequest(labRestrito.getId(), ana.getId(), "Qualidade de Software",
                em("2030-03-11T09:00"), em("2030-03-11T11:00"), 20, null, null, r.versao());
        assertThat(servico.alterar(r.id(), nova, coordenador).status()).isEqualTo(StatusReserva.SOLICITADA);
    }

    // ---------------------------------------------------------------- materiais e relatório

    @Test
    @DisplayName("[RF09] Retirada acima do reservado e conclusão com pendência são recusadas")
    void movimentacao() {
        ReservaResponse r = servico.criar(req(a101, ana, "Qualidade de Software", "09:00", "11:00",
                List.of(new ReservaRequest.ItemRequest(projetor.getId(), 2))), coordenador);
        relogio.definir(em("2030-03-11T09:00"));
        servico.iniciar(r.id(), coordenador);

        assertThatThrownBy(() -> servico.registrarRetirada(r.id(), new MovimentacaoRequest(projetor.getId(), 3, null),
                responsavel)).extracting("codigo").isEqualTo(CodigoErro.MOVIMENTACAO_INVALIDA);

        servico.registrarRetirada(r.id(), new MovimentacaoRequest(projetor.getId(), 2, null), responsavel);
        assertThatThrownBy(() -> servico.concluir(r.id(), coordenador))
                .extracting("codigo").isEqualTo(CodigoErro.MATERIAIS_PENDENTES);

        servico.registrarDevolucao(r.id(), new MovimentacaoRequest(projetor.getId(), 2, "ok"), responsavel);
        assertThat(servico.concluir(r.id(), coordenador).status()).isEqualTo(StatusReserva.CONCLUIDA);
    }

    @Test
    @DisplayName("[RF12] Conflitos recusados são registrados para o relatório")
    void conflitoRegistrado() {
        criar(a101, ana, "Qualidade de Software", "09:00", "11:00");
        assertThatThrownBy(() -> criar(a101, bruno, "Banco de Dados", "10:00", "11:00"));
        List<ConflitoEvitado> conflitos = mongo.findAll(ConflitoEvitado.class);
        assertThat(conflitos).hasSize(1);
        assertThat(conflitos.get(0).tipo()).isEqualTo("CONFLITO_SALA");
        assertThat(conflitos.get(0).recursoId()).isEqualTo(a101.getId());
    }

    @Test
    @DisplayName("[RN10] Reserva no passado é recusada")
    void passado() {
        relogio.definir(LocalDateTime.parse("2030-03-11T12:00"));
        assertThatThrownBy(() -> criar(a101, ana, "Qualidade de Software", "09:00", "11:00"))
                .extracting("codigo").isEqualTo(CodigoErro.HORARIO_INVALIDO);
    }
}
