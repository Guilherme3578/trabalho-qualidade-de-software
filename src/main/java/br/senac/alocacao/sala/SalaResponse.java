package br.senac.alocacao.sala;

public record SalaResponse(String id, String codigo, String nome, TipoSala tipo, int capacidade,
                           String localizacao, boolean restrita, boolean ativa) {
    public static SalaResponse de(Sala s) {
        return new SalaResponse(s.getId(), s.getCodigo(), s.getNome(), s.getTipo(), s.getCapacidade(),
                s.getLocalizacao(), s.isRestrita(), s.isAtiva());
    }
}
