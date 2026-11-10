package br.senac.alocacao.notificacao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, String> {
    List<Notificacao> findTop100ByDestinatarioIdOrderByCriadoEmDescIdDesc(String destinatarioId);

    List<Notificacao> findByReservaId(String reservaId);
}
