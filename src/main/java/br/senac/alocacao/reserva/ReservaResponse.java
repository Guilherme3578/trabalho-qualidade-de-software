package br.senac.alocacao.reserva;

import java.time.LocalDateTime;
import java.util.List;

public record ReservaResponse(
        String id,
        String salaId,
        String salaCodigo,
        String professorId,
        String professorNome,
        String solicitanteId,
        String solicitanteNome,
        String disciplina,
        LocalDateTime inicio,
        LocalDateTime fim,
        int quantidadeAlunos,
        StatusReserva status,
        List<String> motivosAprovacao,
        String observacao,
        Long versao,
        List<Item> materiais,
        List<Movimentacao> movimentacoes,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm) {
    public static ReservaResponse de(Reserva r) {
        return new ReservaResponse(r.getId(), r.getSalaId(), r.getSalaCodigo(), r.getProfessorId(),
                r.getProfessorNome(), r.getSolicitanteId(), r.getSolicitanteNome(), r.getDisciplina(),
                r.getInicio(), r.getFim(), r.getQuantidadeAlunos(), r.getStatus(), List.copyOf(r.getMotivosAprovacao()),
                r.getObservacao(), r.getVersao(),
                r.getItens().stream().map(i -> new Item(i.materialId(), i.materialNome(), i.quantidade())).toList(),
                r.getMovimentacoes().stream().map(m -> new Movimentacao(m.materialId(), m.tipo(), m.quantidade(),
                        m.registradoPorId(), m.registradoPorNome(), m.registradoEm(), m.observacao())).toList(),
                r.getCriadoEm(), r.getAtualizadoEm());
    }

    public record Item(String materialId, String materialNome, int quantidade) {
    }

    public record Movimentacao(String materialId, TipoMovimentacao tipo, int quantidade, String registradoPorId,
                               String registradoPorNome, LocalDateTime registradoEm, String observacao) {
    }
}
