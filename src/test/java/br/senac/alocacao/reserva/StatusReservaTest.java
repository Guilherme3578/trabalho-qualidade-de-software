package br.senac.alocacao.reserva;

import br.senac.alocacao.comum.CodigoErro;
import br.senac.alocacao.comum.NegocioException;
import br.senac.alocacao.usuario.Perfil;
import br.senac.alocacao.usuario.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Stream;

import static br.senac.alocacao.reserva.StatusReserva.APROVADA;
import static br.senac.alocacao.reserva.StatusReserva.CANCELADA;
import static br.senac.alocacao.reserva.StatusReserva.CONCLUIDA;
import static br.senac.alocacao.reserva.StatusReserva.EM_USO;
import static br.senac.alocacao.reserva.StatusReserva.NAO_COMPARECEU;
import static br.senac.alocacao.reserva.StatusReserva.REJEITADA;
import static br.senac.alocacao.reserva.StatusReserva.SOLICITADA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[RN05] Máquina de estados da reserva")
class StatusReservaTest {

    /** Tabela completa das transições permitidas. Qualquer par fora dela deve ser recusado. */
    private static final Set<String> PERMITIDAS = Set.of(
            "SOLICITADA>APROVADA", "SOLICITADA>REJEITADA", "SOLICITADA>CANCELADA",
            "APROVADA>EM_USO", "APROVADA>CANCELADA", "APROVADA>NAO_COMPARECEU", "APROVADA>SOLICITADA",
            "EM_USO>CONCLUIDA");

    static Stream<Arguments> todosOsPares() {
        return EnumSet.allOf(StatusReserva.class).stream()
                .flatMap(de -> EnumSet.allOf(StatusReserva.class).stream().map(para -> Arguments.of(de, para)));
    }

    @ParameterizedTest(name = "[RN05] {0} → {1}")
    @DisplayName("[RN05] Todas as 49 combinações de transição seguem a tabela")
    @MethodSource("todosOsPares")
    void matrizDeTransicoes(StatusReserva de, StatusReserva para) {
        boolean esperado = PERMITIDAS.contains(de + ">" + para);
        assertThat(de.podeTransicionarPara(para)).isEqualTo(esperado);
    }

    @Test
    @DisplayName("[RN05] Fluxo principal SOLICITADA → APROVADA → EM_USO → CONCLUIDA")
    void fluxoPrincipal() {
        Reserva r = new Reserva(usuario(), LocalDateTime.now());
        LocalDateTime t = LocalDateTime.now();
        r.transicionar(APROVADA, t);
        r.transicionar(EM_USO, t);
        r.transicionar(CONCLUIDA, t);
        assertThat(r.getStatus()).isEqualTo(CONCLUIDA);
    }

    @ParameterizedTest(name = "[RN05] {0} → {1} é recusada")
    @CsvSource({"SOLICITADA,EM_USO", "SOLICITADA,CONCLUIDA", "CONCLUIDA,CANCELADA", "CANCELADA,APROVADA",
            "REJEITADA,APROVADA", "EM_USO,CANCELADA", "NAO_COMPARECEU,EM_USO"})
    @DisplayName("[RN05] Transições inválidas lançam TRANSICAO_INVALIDA")
    void transicaoInvalidaNaEntidade(StatusReserva de, StatusReserva para) {
        Reserva r = new Reserva(usuario(), LocalDateTime.now());
        levarAte(r, de);
        assertThatThrownBy(() -> r.transicionar(para, LocalDateTime.now()))
                .isInstanceOf(NegocioException.class)
                .extracting("codigo").isEqualTo(CodigoErro.TRANSICAO_INVALIDA);
    }

    @ParameterizedTest(name = "[RN07] {0} iniciada={1}")
    @DisplayName("[RN07] Identificação de reservas iniciadas (não podem ser apagadas)")
    @CsvSource({"SOLICITADA,false", "APROVADA,false", "REJEITADA,false", "CANCELADA,false",
            "EM_USO,true", "CONCLUIDA,true", "NAO_COMPARECEU,true"})
    void iniciadas(StatusReserva status, boolean iniciada) {
        assertThat(status.iniciada()).isEqualTo(iniciada);
    }

    @ParameterizedTest(name = "[RN02] {0} ocupa agenda")
    @EnumSource(value = StatusReserva.class, names = {"SOLICITADA", "APROVADA", "EM_USO"})
    @DisplayName("[RN02] Estados que bloqueiam a agenda do recurso")
    void ocupamAgenda(StatusReserva s) {
        assertThat(s.ocupaAgenda()).isTrue();
    }

    @ParameterizedTest(name = "[RN02] {0} libera agenda")
    @EnumSource(value = StatusReserva.class, names = {"CONCLUIDA", "REJEITADA", "CANCELADA", "NAO_COMPARECEU"})
    @DisplayName("[RN02] Estados finais liberam a agenda")
    void liberamAgenda(StatusReserva s) {
        assertThat(s.ocupaAgenda()).isFalse();
        assertThat(s.finalizado()).isTrue();
    }

    private static void levarAte(Reserva r, StatusReserva alvo) {
        LocalDateTime t = LocalDateTime.now();
        switch (alvo) {
            case SOLICITADA -> { }
            case APROVADA -> r.transicionar(APROVADA, t);
            case REJEITADA -> r.transicionar(REJEITADA, t);
            case CANCELADA -> r.transicionar(CANCELADA, t);
            case EM_USO -> { r.transicionar(APROVADA, t); r.transicionar(EM_USO, t); }
            case CONCLUIDA -> { r.transicionar(APROVADA, t); r.transicionar(EM_USO, t); r.transicionar(CONCLUIDA, t); }
            case NAO_COMPARECEU -> { r.transicionar(APROVADA, t); r.transicionar(NAO_COMPARECEU, t); }
        }
    }

    private static Usuario usuario() {
        return new Usuario("Teste", "t@t.com", "hash", Perfil.COORDENADOR, null, LocalDateTime.now());
    }
}
