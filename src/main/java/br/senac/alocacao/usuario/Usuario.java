package br.senac.alocacao.usuario;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Locale;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private String id;

    private String nome;

    private String email;

    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private Perfil perfil;

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
