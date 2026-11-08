package br.senac.alocacao.professor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Document("professores")
public class Professor {

    @Id
    private String id;

    private String nome;

    @Indexed(unique = true)
    private String email;

    private String departamento;

    /** Competências (disciplinas/áreas) que o docente está habilitado a lecionar. */
    @Indexed
    private Set<String> competencias = new TreeSet<>();

    private boolean ativo = true;

    /**
     * Contador incrementado a cada reserva que envolve este professor, dentro da transação.
     * Serve para forçar conflito de escrita entre transações simultâneas (ver ReservaService).
     */
    private long versaoAgenda;

    protected Professor() {
    }

    public Professor(String nome, String email, String departamento, Set<String> competencias) {
        atualizar(nome, email, departamento, competencias);
    }

    public final void atualizar(String nome, String email, String departamento, Set<String> competencias) {
        this.nome = nome;
        this.email = email.toLowerCase(Locale.ROOT);
        this.departamento = departamento;
        this.competencias = competencias == null ? new TreeSet<>()
                : competencias.stream().map(String::trim).collect(Collectors.toCollection(TreeSet::new));
    }

    /** Comparação sem diferenciar maiúsculas/minúsculas e espaços nas pontas. */
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

    public long getVersaoAgenda() {
        return versaoAgenda;
    }
}
