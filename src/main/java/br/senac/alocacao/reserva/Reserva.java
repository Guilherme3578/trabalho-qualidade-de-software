package br.senac.alocacao.reserva;

import br.senac.alocacao.comum.CodigoErro;
import br.senac.alocacao.comum.NegocioException;
import br.senac.alocacao.professor.Professor;
import br.senac.alocacao.reserva.regras.Intervalo;
import br.senac.alocacao.sala.Sala;
import br.senac.alocacao.usuario.Usuario;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
@Table(name = "reservas")
public class Reserva {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sala_id")
    private Sala sala;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professor_id")
    private Professor professor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitante_id")
    private Usuario solicitante;

    private String disciplina;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private int quantidadeAlunos;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private StatusReserva status = StatusReserva.SOLICITADA;

    @ElementCollection
    @CollectionTable(name = "reserva_motivos_aprovacao", joinColumns = @JoinColumn(name = "reserva_id"))
    @OrderColumn(name = "ordem")
    @Column(name = "motivo")
    private List<String> motivosAprovacao = new ArrayList<>();

    private String observacao;

    @Version
    private Long versao;

    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    @ElementCollection
    @CollectionTable(name = "reserva_itens", joinColumns = @JoinColumn(name = "reserva_id"))
    private List<ItemReserva> itens = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "reserva_id", nullable = false, updatable = false)
    @OrderBy("registradoEm ASC, id ASC")
    private List<MovimentacaoMaterial> movimentacoes = new ArrayList<>();

    protected Reserva() {
    }

    public Reserva(Usuario solicitante, LocalDateTime agora) {
        this.solicitante = solicitante;
        this.criadoEm = agora;
        this.atualizadoEm = agora;
    }

    public void definirAgenda(Sala sala, Professor professor, String disciplina, Intervalo intervalo,
                              int quantidadeAlunos, String observacao, List<ItemReserva> itens) {
        this.sala = sala;
        this.professor = professor;
        this.disciplina = disciplina.trim();
        this.inicio = intervalo.inicio();
        this.fim = intervalo.fim();
        this.quantidadeAlunos = quantidadeAlunos;
        this.observacao = observacao;
        this.itens.clear();
        this.itens.addAll(itens);
    }

    public void transicionar(StatusReserva destino, LocalDateTime agora) {
        if (!status.podeTransicionarPara(destino)) {
            throw new NegocioException(HttpStatus.CONFLICT, CodigoErro.TRANSICAO_INVALIDA,
                    "Não é possível mudar a reserva de " + status + " para " + destino + ".");
        }
        this.status = destino;
        this.atualizadoEm = agora;
    }

    public Intervalo intervalo() {
        return new Intervalo(inicio, fim);
    }

    public Optional<ItemReserva> item(String materialId) {
        return itens.stream().filter(i -> i.materialId().equals(materialId)).findFirst();
    }

    public int totalMovimentado(String materialId, TipoMovimentacao tipo) {
        return movimentacoes.stream()
                .filter(m -> m.materialId().equals(materialId) && m.tipo() == tipo)
                .mapToInt(MovimentacaoMaterial::quantidade)
                .sum();
    }

    public boolean possuiMateriaisPendentes() {
        return itens.stream().anyMatch(i -> totalMovimentado(i.materialId(), TipoMovimentacao.RETIRADA)
                > totalMovimentado(i.materialId(), TipoMovimentacao.DEVOLUCAO));
    }

    public void registrarMovimentacao(MovimentacaoMaterial movimentacao) {
        movimentacoes.add(movimentacao);
        atualizadoEm = movimentacao.registradoEm();
    }

    public String resumo() {
        String mats = itens.stream()
                .map(i -> i.materialNome() + " x" + i.quantidade())
                .collect(Collectors.joining(", ", "[", "]"));
        return "sala=" + getSalaCodigo() + "; professor=" + getProfessorNome() + "; disciplina=" + disciplina
                + "; inicio=" + inicio + "; fim=" + fim + "; alunos=" + quantidadeAlunos + "; materiais=" + mats;
    }

    public void setMotivosAprovacao(List<String> motivosAprovacao) {
        this.motivosAprovacao.clear();
        this.motivosAprovacao.addAll(motivosAprovacao);
    }

    public String getId() {
        return id;
    }

    public Sala getSala() {
        return sala;
    }

    public Professor getProfessor() {
        return professor;
    }

    public String getSalaId() {
        return sala.getId();
    }

    public String getSalaCodigo() {
        return sala.getCodigo();
    }

    public String getProfessorId() {
        return professor.getId();
    }

    public String getProfessorNome() {
        return professor.getNome();
    }

    public String getSolicitanteId() {
        return solicitante.getId();
    }

    public String getSolicitanteNome() {
        return solicitante.getNome();
    }

    public String getDisciplina() {
        return disciplina;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public LocalDateTime getFim() {
        return fim;
    }

    public int getQuantidadeAlunos() {
        return quantidadeAlunos;
    }

    public StatusReserva getStatus() {
        return status;
    }

    public List<String> getMotivosAprovacao() {
        return motivosAprovacao;
    }

    public String getObservacao() {
        return observacao;
    }

    public Long getVersao() {
        return versao;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public List<ItemReserva> getItens() {
        return itens;
    }

    public List<MovimentacaoMaterial> getMovimentacoes() {
        return movimentacoes;
    }
}
