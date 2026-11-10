package br.senac.alocacao.reserva;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, String> {
    @Query("""
            select r from Reserva r
            where r.sala.id = :salaId and r.status in :status and r.inicio < :fim and r.fim > :inicio
              and (:ignorarId is null or r.id <> :ignorarId)
            order by r.inicio""")
    List<Reserva> sobrepostasPorSala(@Param("salaId") String salaId, @Param("status") Collection<StatusReserva> status,
                                     @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim,
                                     @Param("ignorarId") String ignorarId);

    @Query("""
            select r from Reserva r
            where r.professor.id = :professorId and r.status in :status and r.inicio < :fim and r.fim > :inicio
              and (:ignorarId is null or r.id <> :ignorarId)
            order by r.inicio""")
    List<Reserva> sobrepostasPorProfessor(@Param("professorId") String professorId,
                                          @Param("status") Collection<StatusReserva> status,
                                          @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim,
                                          @Param("ignorarId") String ignorarId);

    @Query("""
            select distinct r from Reserva r join r.itens i
            where i.material.id = :materialId and r.status in :status and r.inicio < :fim and r.fim > :inicio
              and (:ignorarId is null or r.id <> :ignorarId)
            order by r.inicio""")
    List<Reserva> sobrepostasPorMaterial(@Param("materialId") String materialId,
                                         @Param("status") Collection<StatusReserva> status,
                                         @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim,
                                         @Param("ignorarId") String ignorarId);

    @Query("""
            select r from Reserva r
            where (:inicio is null or r.fim > :inicio)
              and (:fim is null or r.inicio < :fim)
              and (:salaId is null or r.sala.id = :salaId)
              and (:professorId is null or r.professor.id = :professorId)
              and (:status is null or r.status = :status)
              and (:solicitanteId is null or r.solicitante.id = :solicitanteId)
            order by r.inicio, r.id""")
    List<Reserva> pesquisar(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim,
                            @Param("salaId") String salaId, @Param("professorId") String professorId,
                            @Param("status") StatusReserva status, @Param("solicitanteId") String solicitanteId,
                            Pageable pagina);

    @Query("""
            select r from Reserva r
            where r.status in :status and r.inicio >= :de and r.inicio < :ate
            order by r.inicio""")
    List<Reserva> efetivasNoPeriodo(@Param("status") Collection<StatusReserva> status,
                                    @Param("de") LocalDateTime de, @Param("ate") LocalDateTime ate);
}
