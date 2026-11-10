package br.senac.alocacao.banco;

import br.senac.alocacao.professor.Professor;
import br.senac.alocacao.reserva.ReservaRequest;
import br.senac.alocacao.reserva.ReservaResponse;
import br.senac.alocacao.reserva.ReservaService;
import br.senac.alocacao.sala.Sala;
import br.senac.alocacao.suporte.IntegracaoBase;
import br.senac.alocacao.usuario.Perfil;
import br.senac.alocacao.usuario.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[RNF07] Banco relacional: integridade garantida pelo MySQL")
class IntegridadeRelacionalIT extends IntegracaoBase {
    @Autowired ReservaService servico;

    private ReservaResponse reservaValida() {
        Sala sala = sala("A101", 40, false);
        Professor ana = professor("Ana Souza", "Qualidade de Software");
        Usuario coord = usuario(Perfil.COORDENADOR);
        return servico.criar(new ReservaRequest(sala.getId(), ana.getId(), "Qualidade de Software",
                em("2030-03-11T09:00"), em("2030-03-11T11:00"), 20, null, null, null), coord);
    }

    @Test
    @DisplayName("[RNF07] O esquema é criado pelas migrações versionadas do Flyway")
    void migracoesAplicadas() {
        List<String> versoes = jdbc.queryForList(
                "SELECT version FROM flyway_schema_history WHERE success = 1 ORDER BY installed_rank", String.class);
        assertThat(versoes).startsWith("1");
    }

    @Test
    @DisplayName("[RNF07] Chave estrangeira impede reserva apontando para sala inexistente")
    void chaveEstrangeiraSala() {
        ReservaResponse r = reservaValida();
        assertThatThrownBy(() -> jdbc.update("UPDATE reservas SET sala_id = 'nao-existe' WHERE id = ?", r.id()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("[RNF07] Sala com reservas não pode ser apagada diretamente no banco")
    void salaReferenciadaNaoApaga() {
        ReservaResponse r = reservaValida();
        assertThatThrownBy(() -> jdbc.update("DELETE FROM salas WHERE id = ?", r.salaId()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("[RNF07] CHECK impede período invertido mesmo fora da API")
    void checkPeriodo() {
        ReservaResponse r = reservaValida();
        assertThatThrownBy(() -> jdbc.update("UPDATE reservas SET fim = inicio WHERE id = ?", r.id()))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("ck_reservas_periodo");
    }

    @Test
    @DisplayName("[RNF07] CHECK de bloqueio exige exatamente o recurso do tipo informado")
    void checkBloqueioRecurso() {
        Sala sala = sala("A101", 40, false);
        Usuario admin = usuario(Perfil.ADMIN);
        assertThatThrownBy(() -> jdbc.update("""
                INSERT INTO bloqueios (id, tipo_recurso, professor_id, inicio, fim, motivo, criado_por_id, criado_em)
                VALUES ('b1', 'SALA', NULL, '2030-03-11 08:00', '2030-03-11 09:00', 'x', ?, NOW())""",
                admin.getId())).isInstanceOf(DataAccessException.class)
                .hasMessageContaining("ck_bloqueios_recurso");
        assertThat(jdbc.update("""
                INSERT INTO bloqueios (id, tipo_recurso, sala_id, inicio, fim, motivo, criado_por_id, criado_em)
                VALUES ('b2', 'SALA', ?, '2030-03-11 08:00', '2030-03-11 09:00', 'x', ?, NOW())""",
                sala.getId(), admin.getId())).isEqualTo(1);
    }

    @Test
    @DisplayName("[RNF07] Apagar a reserva remove itens e motivos em cascata")
    void cascataItens() {
        ReservaResponse r = reservaValida();
        jdbc.update("INSERT INTO reserva_motivos_aprovacao (reserva_id, ordem, motivo) VALUES (?, 0, 'x')", r.id());
        jdbc.update("DELETE FROM reservas WHERE id = ?", r.id());
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM reserva_motivos_aprovacao WHERE reserva_id = ?",
                Integer.class, r.id())).isZero();
    }
}
