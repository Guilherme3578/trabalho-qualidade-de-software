package br.senac.alocacao.notificacao;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificacaoRepository extends MongoRepository<Notificacao, String> {

    List<Notificacao> findTop100ByDestinatarioIdOrderByCriadoEmDesc(String destinatarioId);

    List<Notificacao> findByReservaId(String reservaId);
}
