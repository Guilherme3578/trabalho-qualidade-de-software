package br.senac.alocacao.usuario;

/** Representação pública do usuário: nunca inclui o hash da senha. */
public record UsuarioResponse(String id, String nome, String email, Perfil perfil, String professorId,
                              boolean ativo) {

    public static UsuarioResponse de(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getPerfil(), u.getProfessorId(),
                u.isAtivo());
    }
}
