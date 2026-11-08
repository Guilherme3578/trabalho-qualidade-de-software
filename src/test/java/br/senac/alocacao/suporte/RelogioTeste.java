package br.senac.alocacao.suporte;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Relógio ajustável usado nos testes de integração: permite "viajar no tempo" para testar
 * regras como iniciar uso, não comparecimento e reservas no passado de forma determinística.
 */
public class RelogioTeste extends Clock {

    public static final LocalDateTime AGORA_PADRAO = LocalDateTime.parse("2030-03-11T08:00");

    private final ZoneId zona = ZoneId.systemDefault();
    private volatile Instant instante = AGORA_PADRAO.atZone(zona).toInstant();

    public void definir(LocalDateTime agora) {
        this.instante = agora.atZone(zona).toInstant();
    }

    @Override
    public ZoneId getZone() {
        return zona;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return Clock.fixed(instante, zone);
    }

    @Override
    public Instant instant() {
        return instante;
    }
}
