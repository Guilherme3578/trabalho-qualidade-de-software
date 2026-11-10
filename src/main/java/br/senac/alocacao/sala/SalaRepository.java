package br.senac.alocacao.sala;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SalaRepository extends JpaRepository<Sala, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Sala s where s.id = :id")
    Optional<Sala> travar(@Param("id") String id);

    @Query("""
            select s from Sala s
            where (:somenteAtivas = false or s.ativa = true)
              and (:tipo is null or s.tipo = :tipo)
              and (:capacidadeMinima is null or s.capacidade >= :capacidadeMinima)
              and (:localizacao is null or lower(s.localizacao) like lower(:localizacao) escape '\\')
            order by s.codigo""")
    List<Sala> pesquisar(@Param("somenteAtivas") boolean somenteAtivas, @Param("tipo") TipoSala tipo,
                         @Param("capacidadeMinima") Integer capacidadeMinima,
                         @Param("localizacao") String localizacao);
}
