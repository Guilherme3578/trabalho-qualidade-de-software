package br.senac.alocacao.usuario;

/**
 * Perfis de acesso.
 * PROFESSOR e COORDENADOR são "Solicitantes": consultam disponibilidade e gerenciam suas reservas.
 * RESPONSAVEL aprova recursos restritos, valida docentes e acompanha retirada/devolução de materiais.
 * ADMIN gerencia cadastros, usuários, bloqueios e períodos de manutenção.
 */
public enum Perfil {
    PROFESSOR,
    COORDENADOR,
    RESPONSAVEL,
    ADMIN;

    public boolean solicitante() {
        return this == PROFESSOR || this == COORDENADOR;
    }
}
