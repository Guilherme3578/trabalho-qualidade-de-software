package br.senac.alocacao.comum;

/**
 * Códigos estáveis de erro devolvidos pela API. O cliente (interface web, testes)
 * se baseia no código, nunca no texto da mensagem.
 */
public enum CodigoErro {
    VALIDACAO,
    REQUISICAO_INVALIDA,
    NAO_ENCONTRADO,
    NAO_AUTENTICADO,
    ACESSO_NEGADO,
    CREDENCIAIS_INVALIDAS,
    MUITAS_TENTATIVAS,
    HORARIO_INVALIDO,
    CAPACIDADE_EXCEDIDA,
    RECURSO_INATIVO,
    RECURSO_EM_MANUTENCAO,
    CONFLITO_SALA,
    CONFLITO_PROFESSOR,
    CONFLITO_MATERIAL,
    TRANSICAO_INVALIDA,
    RESERVA_INICIADA,
    MATERIAIS_PENDENTES,
    MOVIMENTACAO_INVALIDA,
    VERSAO_DESATUALIZADA,
    CONCORRENCIA,
    REGISTRO_DUPLICADO,
    ERRO_INTERNO
}
