package br.senac.alocacao.usuario;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Locale;

@Document("usuarios")
public class Usuario {

    @Id
    private String id;

    private String nome;

    @Indexed(unique = true)
    private String email;

    /** Apenas o hash BCrypt é armazenado; a senha em texto nunca é persistida nem devolvida. */
    private String senhaHash;

    private Perfil perfil;

    /** Vínculo com o cadastro docente (obrigatório para o perfil PROFESSOR). */
    private String professorId;

    private boolean ativo = true;

    private LocalDateTime criadoEm;

    protected Usuario() {
    }

    public Usuario(String nome, String email, String senhaHash, Perfil perfil, String professorId,
                   LocalDateTime criadoEm) {
        this.nome = nome;
        this.email = email.toLowerCase(Locale.ROOT);
        this.senhaHash = senhaHash;
        this.perfil = perfil;
        this.professorId = professorId;
        this.criadoEm = criadoEm;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public String getProfessorId() {
        return professorId;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public void setProfessorId(String professorId) {
        this.professorId = professorId;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
