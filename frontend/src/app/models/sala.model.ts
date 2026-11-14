export type TipoSala = 'SALA_AULA' | 'LABORATORIO' | 'AUDITORIO';

export interface Sala {
  id: string;
  codigo: string;
  nome: string;
  tipo: TipoSala;
  capacidade: number;
  localizacao: string;
  restrita: boolean;
  ativa: boolean;
  reservasAfetadas?: string[];
}

export interface SalaRequest {
  codigo: string;
  nome: string;
  tipo: TipoSala;
  capacidade: number;
  localizacao: string;
  restrita: boolean;
}

export const TIPOS_SALA: Record<TipoSala, string> = {
  SALA_AULA: 'Sala de aula',
  LABORATORIO: 'Laboratório',
  AUDITORIO: 'Auditório'
};
