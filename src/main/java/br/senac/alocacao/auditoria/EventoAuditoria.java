package br.senac.alocacao.auditoria;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Registro imutável do histórico de mudanças (RF10 / RN08).
 * Não há endpoint de alteração ou exclusão: a API só permite consultar.
 */
@Document("auditoria")
@CompoundIndex(name = "auditoria_entidade", def = "{'entidade': 1, 'entidadeId': 1, 'dataHora': -1}")
public record EventoAuditoria(
        @Id String id,
        LocalDateTime dataHora,
        String usuarioId,
        String usuarioNome,
        AcaoAuditoria acao,
        String entidade,
        String entidadeId,
        String estadoAnterior,
        String estadoNovo,
        String detalhes,
        String correlationId) {
}
