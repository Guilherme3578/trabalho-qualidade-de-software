package br.senac.alocacao.relatorio;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ConflitoEvitadoRepository extends JpaRepository<ConflitoEvitado, Long> {
    List<ConflitoEvitado> findByRegistradoEmGreaterThanEqualAndRegistradoEmLessThan(LocalDateTime de,
                                                                                   LocalDateTime ate);
}
