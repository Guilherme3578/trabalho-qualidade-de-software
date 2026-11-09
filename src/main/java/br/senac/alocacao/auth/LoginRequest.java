package br.senac.alocacao.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Informe o e-mail.") @Size(max = 160, message = "E-mail muito longo.") String email,
        @NotBlank(message = "Informe a senha.") @Size(max = 72, message = "Senha muito longa.") String senha) {
    @Override
    public String toString() {
        return "LoginRequest[email=" + email + "]";
    }
}
