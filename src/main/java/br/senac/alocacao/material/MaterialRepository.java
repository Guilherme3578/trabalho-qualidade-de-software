package br.senac.alocacao.material;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MaterialRepository extends JpaRepository<Material, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Material m where m.id = :id")
    Optional<Material> travar(@Param("id") String id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Material m where m.id in :ids order by m.id")
    List<Material> travarTodos(@Param("ids") Collection<String> ids);

    @Query("""
            select m from Material m
            where m.ativo = true
              and (:categoria is null or lower(m.categoria) = lower(:categoria))
              and (:localizacao is null or lower(m.localizacao) like lower(:localizacao) escape '\\')
              and (:nome is null or lower(m.nome) like lower(:nome) escape '\\')
            order by m.nome""")
    List<Material> pesquisar(@Param("categoria") String categoria, @Param("localizacao") String localizacao,
                             @Param("nome") String nome);
}
