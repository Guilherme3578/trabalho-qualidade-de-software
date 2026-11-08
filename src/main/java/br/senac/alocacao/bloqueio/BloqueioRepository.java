package br.senac.alocacao.bloqueio;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BloqueioRepository extends MongoRepository<Bloqueio, String> {

    /** Bloqueios do recurso que se sobrepõem ao intervalo [inicio, fim). */
    List<Bloqueio> findByTipoRecursoAndRecursoIdAndInicioLessThanAndFimGreaterThan(
            TipoRecurso tipoRecurso, String recursoId, LocalDateTime fim, LocalDateTime inicio);
}
