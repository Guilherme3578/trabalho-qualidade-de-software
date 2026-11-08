package br.senac.alocacao.auditoria;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditoriaRepository extends MongoRepository<EventoAuditoria, String> {
}
