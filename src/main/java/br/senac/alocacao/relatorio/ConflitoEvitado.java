package br.senac.alocacao.relatorio;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("conflitos_evitados")
public record ConflitoEvitado(
        @Id String id,
        String tipo,
        String recursoTipo,
        String recursoId,
        LocalDateTime inicio,
        LocalDateTime fim,
        String solicitanteId,
        @Indexed LocalDateTime registradoEm) {
}
