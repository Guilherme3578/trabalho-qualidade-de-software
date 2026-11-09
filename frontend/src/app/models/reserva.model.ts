export type StatusReserva =
  | 'SOLICITADA'
  | 'APROVADA'
  | 'EM_USO'
  | 'CONCLUIDA'
  | 'REJEITADA'
  | 'CANCELADA'
  | 'NAO_COMPARECEU';

export interface ItemReserva {
  materialId: string;
  materialNome: string;
  quantidade: number;
}

export interface Movimentacao {
  materialId: string;
  tipo: 'RETIRADA' | 'DEVOLUCAO';
  quantidade: number;
  registradoPorId: string;
  registradoPorNome: string;
  registradoEm: string;
  observacao: string | null;
}

export interface Reserva {
  id: string;
  salaId: string;
  salaCodigo: string;
  professorId: string;
  professorNome: string;
  solicitanteId: string;
  solicitanteNome: string;
  disciplina: string;
  inicio: string;
  fim: string;
  quantidadeAlunos: number;
  status: StatusReserva;
  motivosAprovacao: string[];
  observacao: string | null;
  versao: number;
  materiais: ItemReserva[];
  movimentacoes: Movimentacao[];
  criadoEm: string;
  atualizadoEm: string;
}

export interface ItemRequest {
  materialId: string;
  quantidade: number;
}

export interface ReservaRequest {
  salaId: string;
  professorId: string;
  disciplina: string;
  inicio: string;
  fim: string;
  quantidadeAlunos: number;
  observacao: string | null;
  materiais: ItemRequest[];
  versao?: number;
}

export interface MovimentacaoRequest {
  materialId: string;
  quantidade: number;
  observacao: string | null;
}

export interface FiltroReservas {
  inicio?: string;
  fim?: string;
  salaId?: string;
  professorId?: string;
  status?: StatusReserva;
  minhas?: boolean;
}

export const STATUS_RESERVA: Record<StatusReserva, string> = {
  SOLICITADA: 'Aguardando aprovação',
  APROVADA: 'Aprovada',
  EM_USO: 'Em uso',
  CONCLUIDA: 'Concluída',
  REJEITADA: 'Rejeitada',
  CANCELADA: 'Cancelada',
  NAO_COMPARECEU: 'Não compareceu'
};
