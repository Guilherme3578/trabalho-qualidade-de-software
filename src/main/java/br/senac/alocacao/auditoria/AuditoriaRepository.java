package br.senac.alocacao.auditoria;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuditoriaRepository extends JpaRepository<EventoAuditoria, Long> {
    List<EventoAuditoria> findByEntidadeAndEntidadeIdOrderByDataHoraAscIdAsc(String entidade, String entidadeId);

    @Query("""
            select e from EventoAuditoria e
            where (:entidade is null or e.entidade = :entidade)
              and (:entidadeId is null or e.entidadeId = :entidadeId)
              and (:acao is null or e.acao = :acao)
            order by e.dataHora desc, e.id desc""")
    List<EventoAuditoria> pesquisar(@Param("entidade") String entidade, @Param("entidadeId") String entidadeId,
                                    @Param("acao") AcaoAuditoria acao, Pageable pagina);
}
