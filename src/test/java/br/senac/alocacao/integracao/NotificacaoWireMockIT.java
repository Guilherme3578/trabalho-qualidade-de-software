package br.senac.alocacao.integracao;

import br.senac.alocacao.notificacao.Notificacao;
import br.senac.alocacao.notificacao.NotificacaoRepository;
import br.senac.alocacao.notificacao.StatusNotificacao;
import br.senac.alocacao.professor.Professor;
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

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[RF11] Integração com API externa de notificação (WireMock)")
class NotificacaoWireMockIT extends IntegracaoBase {
    @Autowired ReservaService servico;
    @Autowired NotificacaoRepository notificacoes;

    Usuario coord;
    Usuario responsavel;
    Sala restrita;
    Professor ana;

    @BeforeEach
    void dados() {
        coord = usuario(Perfil.COORDENADOR);
        responsavel = usuario(Perfil.RESPONSAVEL);
        restrita = sala("LAB-R", 30, true);
        ana = professor("Ana", "Qualidade");
    }

    private ReservaResponse criarRestrita() {
        return servico.criar(new ReservaRequest(restrita.getId(), ana.getId(), "Qualidade",
                em("2030-03-11T09:00"), em("2030-03-11T11:00"), 10, null, null, null), coord);
    }

    @Test
    @DisplayName("[RF11] Reserva pendente notifica o responsável com o contrato JSON esperado")
    void notificaResponsavel() {
        ReservaResponse r = criarRestrita();

        WIREMOCK.verify(1, postRequestedFor(urlEqualTo("/notificacoes"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(matchingJsonPath("$.destinatario", equalTo(responsavel.getEmail())))
                .withRequestBody(matchingJsonPath("$.tipo", equalTo("APROVACAO_PENDENTE")))
                .withRequestBody(matchingJsonPath("$.reservaId", equalTo(r.id())))
                .withRequestBody(matchingJsonPath("$.mensagem", containing("LAB-R"))));
        assertThat(notificacoes.findByReservaId(r.id()))
                .extracting(Notificacao::getStatus).containsExactly(StatusNotificacao.ENVIADA);
    }

    @Test
    @DisplayName("[RF11] Aprovação notifica o solicitante")
    void notificaSolicitante() {
        ReservaResponse r = criarRestrita();
        WIREMOCK.resetRequests();
        servico.aprovar(r.id(), "Liberado", responsavel);

        WIREMOCK.verify(1, postRequestedFor(urlEqualTo("/notificacoes"))
                .withRequestBody(matchingJsonPath("$.destinatario", equalTo(coord.getEmail())))
                .withRequestBody(matchingJsonPath("$.tipo", equalTo("RESERVA_APROVADA"))));
    }

    @Test
    @DisplayName("[RF11] API externa com erro 500: reserva é mantida e a falha fica registrada")
    void falhaExterna() {
        WIREMOCK.stubFor(post(urlEqualTo("/notificacoes")).willReturn(aResponse().withStatus(500)));
        ReservaResponse r = criarRestrita();

        assertThat(servico.obter(r.id()).status()).isEqualTo(StatusReserva.SOLICITADA);
        List<Notificacao> ns = notificacoes.findByReservaId(r.id());
        assertThat(ns).extracting(Notificacao::getStatus).containsExactly(StatusNotificacao.FALHA);
        assertThat(ns.get(0).getErro()).contains("500");
    }

    @Test
    @DisplayName("[RF11] API externa lenta (acima do timeout): reserva é mantida e a falha registrada")
    void timeoutExterno() {
        WIREMOCK.stubFor(post(urlEqualTo("/notificacoes"))
                .willReturn(aResponse().withStatus(200).withFixedDelay(3000)));
        long inicio = System.currentTimeMillis();
        ReservaResponse r = criarRestrita();
        long duracao = System.currentTimeMillis() - inicio;

        assertThat(duracao).as("timeout configurado de 1s deve limitar a espera").isLessThan(2900);
        assertThat(notificacoes.findByReservaId(r.id()))
                .extracting(Notificacao::getStatus).containsExactly(StatusNotificacao.FALHA);
    }

    @Test
    @DisplayName("[RF11] Operação recusada não gera notificação (evento só após commit)")
    void semNotificacaoEmFalha() {
        criarRestrita();
        WIREMOCK.resetRequests();
        try {
            criarRestrita();
        } catch (RuntimeException esperado) {
        }
        WIREMOCK.verify(0, postRequestedFor(urlEqualTo("/notificacoes")));
    }
}
