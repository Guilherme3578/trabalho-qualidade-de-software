package br.senac.alocacao.auth;

import br.senac.alocacao.usuario.UsuarioResponse;

import java.time.Instant;

public record TokenResponse(String token, String tipo, Instant expiraEm, UsuarioResponse usuario) {
}
