package br.senac.alocacao.reserva;

import br.senac.alocacao.comum.CodigoErro;
import br.senac.alocacao.comum.NegocioException;
import br.senac.alocacao.professor.Professor;
import br.senac.alocacao.reserva.regras.Intervalo;
import br.senac.alocacao.sala.Sala;
import br.senac.alocacao.usuario.Usuario;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Document("reservas")
@CompoundIndex(name = "reserva_sala_periodo", def = "{'salaId': 1, 'status': 1, 'inicio': 1}")
@CompoundIndex(name = "reserva_professor_periodo", def = "{'professorId': 1, 'status': 1, 'inicio': 1}")
@CompoundIndex(name = "reserva_material_periodo", def = "{'itens.materialId': 1, 'status': 1, 'inicio': 1}")
public class Reserva {
    @Id
    private String id;

    private String salaId;
    private String salaCodigo;
    private String professorId;
    private String professorNome;
    private String solicitanteId;
    private String solicitanteNome;

    private String disciplina;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private int quantidadeAlunos;

    private StatusReserva status = StatusReserva.SOLICITADA;

    private List<String> motivosAprovacao = new ArrayList<>();

    private String observacao;

    @Version
    private Long versao;

    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    private List<ItemReserva> itens = new ArrayList<>();
    private List<MovimentacaoMaterial> movimentacoes = new ArrayList<>();

    protected Reserva() {
    }

    public Reserva(Usuario solicitante, LocalDateTime agora) {
        this.solicitanteId = solicitante.getId();
        this.solicitanteNome = solicitante.getNome();
        this.criadoEm = agora;
        this.atualizadoEm = agora;
    }

    public void definirAgenda(Sala sala, Professor professor, String disciplina, Intervalo intervalo,
                              int quantidadeAlunos, String observacao, List<ItemReserva> itens) {
        this.salaId = sala.getId();
        this.salaCodigo = sala.getCodigo();
        this.professorId = professor.getId();
        this.professorNome = professor.getNome();
        this.disciplina = disciplina.trim();
        this.inicio = intervalo.inicio();
        this.fim = intervalo.fim();
        this.quantidadeAlunos = quantidadeAlunos;
        this.observacao = observacao;
        this.itens = new ArrayList<>(itens);
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
        return "sala=" + salaCodigo + "; professor=" + professorNome + "; disciplina=" + disciplina
                + "; inicio=" + inicio + "; fim=" + fim + "; alunos=" + quantidadeAlunos + "; materiais=" + mats;
    }

    public void setMotivosAprovacao(List<String> motivosAprovacao) {
        this.motivosAprovacao = new ArrayList<>(motivosAprovacao);
    }

    public String getId() {
        return id;
    }

    public String getSalaId() {
        return salaId;
    }

    public String getSalaCodigo() {
        return salaCodigo;
    }

    public String getProfessorId() {
        return professorId;
    }

    public String getProfessorNome() {
        return professorNome;
    }

    public String getSolicitanteId() {
        return solicitanteId;
    }

    public String getSolicitanteNome() {
        return solicitanteNome;
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
