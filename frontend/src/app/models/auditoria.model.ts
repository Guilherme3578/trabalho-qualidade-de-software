export type AcaoAuditoria =
  | 'CRIACAO'
  | 'ALTERACAO'
  | 'TRANSICAO_ESTADO'
  | 'EXCLUSAO'
  | 'ATIVACAO'
  | 'DESATIVACAO'
  | 'RETIRADA_MATERIAL'
  | 'DEVOLUCAO_MATERIAL'
  | 'LOGIN_SUCESSO'
  | 'LOGIN_FALHA';

export interface EventoAuditoria {
  id: string;
  dataHora: string;
  usuarioId: string | null;
  usuarioNome: string | null;
  acao: AcaoAuditoria;
  entidade: string;
  entidadeId: string | null;
  estadoAnterior: string | null;
  estadoNovo: string | null;
  detalhes: string | null;
  correlationId: string | null;
}

export const ACOES_AUDITORIA: Record<AcaoAuditoria, string> = {
  CRIACAO: 'Criação',
  ALTERACAO: 'Alteração',
  TRANSICAO_ESTADO: 'Mudança de status',
  EXCLUSAO: 'Exclusão',
  ATIVACAO: 'Ativação',
  DESATIVACAO: 'Desativação',
  RETIRADA_MATERIAL: 'Retirada de material',
  DEVOLUCAO_MATERIAL: 'Devolução de material',
  LOGIN_SUCESSO: 'Login',
  LOGIN_FALHA: 'Falha de login'
};
