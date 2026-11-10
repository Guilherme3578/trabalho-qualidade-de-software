package br.senac.alocacao.professor;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Professor p where p.id = :id")
    Optional<Professor> travar(@Param("id") String id);

    @Query("""
            select p from Professor p
            where p.ativo = true
              and (:competencia is null or exists (
                    select 1 from Professor p2 join p2.competencias c
                    where p2.id = p.id and lower(c) = lower(:competencia)))
              and (:departamento is null or lower(p.departamento) like lower(:departamento) escape '\\')
              and (:nome is null or lower(p.nome) like lower(:nome) escape '\\')
            order by p.nome""")
    List<Professor> pesquisar(@Param("competencia") String competencia, @Param("departamento") String departamento,
                              @Param("nome") String nome);
}
