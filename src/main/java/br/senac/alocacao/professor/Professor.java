package br.senac.alocacao.professor;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import org.hibernate.annotations.SortNatural;
import org.hibernate.annotations.UuidGenerator;

import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Entity
@Table(name = "professores")
public class Professor {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private String id;

    private String nome;

    private String email;

    private String departamento;

    @ElementCollection
    @CollectionTable(name = "professor_competencias", joinColumns = @JoinColumn(name = "professor_id"))
    @Column(name = "competencia")
    @SortNatural
    private SortedSet<String> competencias = new TreeSet<>();

    private boolean ativo = true;

    protected Professor() {
    }

    public Professor(String nome, String email, String departamento, Set<String> competencias) {
        atualizar(nome, email, departamento, competencias);
    }

    public final void atualizar(String nome, String email, String departamento, Set<String> competencias) {
        this.nome = nome;
        this.email = email.toLowerCase(Locale.ROOT);
        this.departamento = departamento;
        this.competencias.clear();
        if (competencias != null) {
            this.competencias.addAll(competencias.stream().map(String::trim)
                    .collect(Collectors.toCollection(TreeSet::new)));
        }
    }

    public boolean possuiCompetencia(String disciplina) {
        if (disciplina == null) {
            return false;
        }
        String alvo = disciplina.trim();
        return competencias.stream().anyMatch(c -> c.equalsIgnoreCase(alvo));
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

    public String getDepartamento() {
        return departamento;
    }

    public Set<String> getCompetencias() {
        return competencias;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
