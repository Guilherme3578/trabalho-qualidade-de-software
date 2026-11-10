package br.senac.alocacao.bloqueio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BloqueioRepository extends JpaRepository<Bloqueio, String> {
    @Query("""
            select b from Bloqueio b
            where b.tipoRecurso = :tipo
              and (b.salaId = :recursoId or b.professorId = :recursoId or b.materialId = :recursoId)
              and b.inicio < :fim and b.fim > :inicio
            order by b.inicio""")
    List<Bloqueio> sobrepostos(@Param("tipo") TipoRecurso tipo, @Param("recursoId") String recursoId,
                               @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("""
            select b from Bloqueio b
            where (:tipo is null or b.tipoRecurso = :tipo)
              and (:recursoId is null or b.salaId = :recursoId or b.professorId = :recursoId
                   or b.materialId = :recursoId)
            order by b.inicio desc""")
    List<Bloqueio> pesquisar(@Param("tipo") TipoRecurso tipo, @Param("recursoId") String recursoId);
}
