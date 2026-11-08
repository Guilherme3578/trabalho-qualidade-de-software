package br.senac.alocacao.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
        @NotBlank(message = "Informe o nome.")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres.") String nome,
        @NotBlank(message = "Informe o e-mail.")
        @Email(message = "Informe um e-mail válido.")
        @Size(max = 160, message = "O e-mail deve ter no máximo 160 caracteres.") String email,
        @NotBlank(message = "Informe a senha.")
        @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "A senha deve conter letras e números.")
        String senha,
        @NotNull(message = "Informe o perfil (PROFESSOR, COORDENADOR, RESPONSAVEL ou ADMIN).") Perfil perfil,
        String professorId) {

    @Override
    public String toString() {
        // Nunca expor a senha em logs.
        return "UsuarioRequest[nome=" + nome + ", email=" + email + ", perfil=" + perfil + "]";
    }
}
