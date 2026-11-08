package br.senac.alocacao.reserva;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Ciclo de vida da reserva.
 *
 * <pre>
 *   SOLICITADA ──► APROVADA ──► EM_USO ──► CONCLUIDA
 *       │  ▲          │  │
 *       │  └──────────┘  ├──► NAO_COMPARECEU
 *       ├──► REJEITADA   └──► CANCELADA
 *       └──► CANCELADA
 * </pre>
 *
 * APROVADA → SOLICITADA ocorre quando uma alteração passa a exigir nova aprovação.
 * Estados finais: CONCLUIDA, REJEITADA, CANCELADA, NAO_COMPARECEU.
 */
public enum StatusReserva {
    SOLICITADA,
    APROVADA,
    EM_USO,
    CONCLUIDA,
    REJEITADA,
    CANCELADA,
    NAO_COMPARECEU;

    private static final Map<StatusReserva, Set<StatusReserva>> TRANSICOES = Map.of(
            SOLICITADA, EnumSet.of(APROVADA, REJEITADA, CANCELADA),
            APROVADA, EnumSet.of(EM_USO, CANCELADA, NAO_COMPARECEU, SOLICITADA),
            EM_USO, EnumSet.of(CONCLUIDA),
            CONCLUIDA, EnumSet.noneOf(StatusReserva.class),
            REJEITADA, EnumSet.noneOf(StatusReserva.class),
            CANCELADA, EnumSet.noneOf(StatusReserva.class),
            NAO_COMPARECEU, EnumSet.noneOf(StatusReserva.class));

    /** Estados que ocupam a agenda do recurso (participam da detecção de conflitos). */
    public static final Set<StatusReserva> OCUPAM_AGENDA = EnumSet.of(SOLICITADA, APROVADA, EM_USO);

    /** Estados que representam uso efetivo (para relatórios de utilização). */
    public static final Set<StatusReserva> EFETIVOS = EnumSet.of(APROVADA, EM_USO, CONCLUIDA);

    public boolean podeTransicionarPara(StatusReserva destino) {
        return TRANSICOES.get(this).contains(destino);
    }

    public boolean ocupaAgenda() {
        return OCUPAM_AGENDA.contains(this);
    }

    /** Reserva iniciada: o uso começou (ou deveria ter começado). Não pode ser apagada. */
    public boolean iniciada() {
        return this == EM_USO || this == CONCLUIDA || this == NAO_COMPARECEU;
    }

    public boolean podeSerAlterada() {
        return this == SOLICITADA || this == APROVADA;
    }

    public boolean finalizado() {
        return TRANSICOES.get(this).isEmpty();
    }
}
