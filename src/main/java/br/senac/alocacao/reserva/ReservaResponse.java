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
        List<ItemReserva> materiais,
        List<MovimentacaoMaterial> movimentacoes,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm) {

    public static ReservaResponse de(Reserva r) {
        return new ReservaResponse(r.getId(), r.getSalaId(), r.getSalaCodigo(), r.getProfessorId(),
                r.getProfessorNome(), r.getSolicitanteId(), r.getSolicitanteNome(), r.getDisciplina(),
                r.getInicio(), r.getFim(), r.getQuantidadeAlunos(), r.getStatus(), r.getMotivosAprovacao(),
                r.getObservacao(), r.getVersao(), r.getItens(), r.getMovimentacoes(), r.getCriadoEm(),
                r.getAtualizadoEm());
    }
}
