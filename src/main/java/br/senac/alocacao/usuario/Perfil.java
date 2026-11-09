package br.senac.alocacao.usuario;

public enum Perfil {
    PROFESSOR,
    COORDENADOR,
    RESPONSAVEL,
    ADMIN;

    public boolean solicitante() {
        return this == PROFESSOR || this == COORDENADOR;
    }
}
