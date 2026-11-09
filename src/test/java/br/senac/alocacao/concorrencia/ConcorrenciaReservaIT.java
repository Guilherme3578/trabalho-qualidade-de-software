package br.senac.alocacao.concorrencia;

import br.senac.alocacao.comum.CodigoErro;
import br.senac.alocacao.comum.NegocioException;
import br.senac.alocacao.material.Material;
import br.senac.alocacao.professor.Professor;
import br.senac.alocacao.reserva.Reserva;
import br.senac.alocacao.reserva.ReservaRequest;
import br.senac.alocacao.reserva.ReservaService;
import br.senac.alocacao.sala.Sala;
import br.senac.alocacao.suporte.IntegracaoBase;
import br.senac.alocacao.usuario.Perfil;
import br.senac.alocacao.usuario.Usuario;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[RF06][RN03] Concorrência: solicitações simultâneas")
class ConcorrenciaReservaIT extends IntegracaoBase {
    private static final int THREADS = 20;

    @Autowired ReservaService servico;

    private Map<String, Integer> disparar(List<Callable<Object>> tarefas) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(tarefas.size());
        CountDownLatch largada = new CountDownLatch(1);
        Map<String, Integer> resultados = new ConcurrentHashMap<>();
        List<Future<?>> futuros = new ArrayList<>();
        for (Callable<Object> t : tarefas) {
            futuros.add(pool.submit(() -> {
                largada.await();
                String chave;
                try {
                    t.call();
                    chave = "ACEITA";
                } catch (NegocioException e) {
                    chave = e.getCodigo().name();
                }
                resultados.merge(chave, 1, Integer::sum);
                return null;
            }));
        }
        largada.countDown();
        for (Future<?> f : futuros) {
            f.get(60, TimeUnit.SECONDS);
        }
        pool.shutdown();
        return resultados;
    }

    @RepeatedTest(value = 3, name = "[RF06][RN03] rodada {currentRepetition}/{totalRepetitions}")
    @DisplayName("[RF06][RN03] 20 pedidos simultâneos para a mesma sala → 1 aceito, 19 recusados")
    void mesmaSala() throws Exception {
        Usuario coord = usuario(Perfil.COORDENADOR);
        Sala sala = sala("A101", 50, false);
        List<Callable<Object>> tarefas = new ArrayList<>();
        for (int i = 0; i < THREADS; i++) {
            Professor p = professor("Professor " + i, "Disciplina");
            tarefas.add(() -> servico.criar(new ReservaRequest(sala.getId(), p.getId(), "Disciplina",
                    em("2030-03-11T09:00"), em("2030-03-11T11:00"), 10, null, null, null), coord));
        }

        Map<String, Integer> r = disparar(tarefas);

        assertThat(r).containsEntry("ACEITA", 1);
        assertThat(r.getOrDefault(CodigoErro.CONFLITO_SALA.name(), 0)).isEqualTo(THREADS - 1);
        assertThat(mongo.findAll(Reserva.class)).hasSize(1);
    }

    @Test
    @DisplayName("[RF06][RN03] Mesmo professor em 20 salas diferentes ao mesmo tempo → 1 aceito")
    void mesmoProfessor() throws Exception {
        Usuario coord = usuario(Perfil.COORDENADOR);
        Professor ana = professor("Ana", "Disciplina");
        List<Callable<Object>> tarefas = new ArrayList<>();
        for (int i = 0; i < THREADS; i++) {
            Sala s = sala("S" + i, 50, false);
            tarefas.add(() -> servico.criar(new ReservaRequest(s.getId(), ana.getId(), "Disciplina",
                    em("2030-03-11T09:00"), em("2030-03-11T11:00"), 10, null, null, null), coord));
        }

        Map<String, Integer> r = disparar(tarefas);

        assertThat(r).containsEntry("ACEITA", 1);
        assertThat(r.getOrDefault(CodigoErro.CONFLITO_PROFESSOR.name(), 0)).isEqualTo(THREADS - 1);
    }

    @Test
    @DisplayName("[RF06][RN03] Estoque de 3 projetores e 20 pedidos simultâneos de 1 → exatamente 3 aceitos")
    void estoqueDeMaterial() throws Exception {
        Usuario coord = usuario(Perfil.COORDENADOR);
        Material projetor = material("Projetor", 3, false);
        List<Callable<Object>> tarefas = new ArrayList<>();
        for (int i = 0; i < THREADS; i++) {
            Sala s = sala("S" + i, 50, false);
            Professor p = professor("Prof " + i, "Disciplina");
            tarefas.add(() -> servico.criar(new ReservaRequest(s.getId(), p.getId(), "Disciplina",
                    em("2030-03-11T09:00"), em("2030-03-11T11:00"), 10, null,
                    List.of(new ReservaRequest.ItemRequest(projetor.getId(), 1)), null), coord));
        }

        Map<String, Integer> r = disparar(tarefas);

        assertThat(r).containsEntry("ACEITA", 3);
        assertThat(r.getOrDefault(CodigoErro.CONFLITO_MATERIAL.name(), 0)).isEqualTo(THREADS - 3);
    }

    @Test
    @DisplayName("[RF06][RN03] Via HTTP: 10 requisições simultâneas à API → um 201 e nove 409")
    void viaApi() throws Exception {
        Usuario coord = usuario(Perfil.COORDENADOR);
        String token = token(coord);
        Sala sala = sala("A101", 50, false);
        Professor ana = professor("Ana", "Disciplina");
        String corpo = reservaJson(sala, ana, "Disciplina", "09:00", "11:00", 10, List.of());

        int n = 10;
        ExecutorService pool = Executors.newFixedThreadPool(n);
        CountDownLatch largada = new CountDownLatch(1);
        AtomicInteger criados = new AtomicInteger();
        AtomicInteger conflitos = new AtomicInteger();
        List<Future<?>> futuros = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            futuros.add(pool.submit(() -> {
                largada.await();
                int status = given().port(porta).contentType(ContentType.JSON).auth().oauth2(token)
                        .body(corpo).post("/api/reservas").statusCode();
                (status == 201 ? criados : status == 409 ? conflitos : new AtomicInteger()).incrementAndGet();
                return null;
            }));
        }
        largada.countDown();
        for (Future<?> f : futuros) {
            f.get(60, TimeUnit.SECONDS);
        }
        pool.shutdown();

        assertThat(criados).hasValue(1);
        assertThat(conflitos).hasValue(n - 1);
    }
}
