export type TipoRecurso = 'SALA' | 'PROFESSOR' | 'MATERIAL';

export interface Bloqueio {
  id: string;
  tipoRecurso: TipoRecurso;
  recursoId: string;
  inicio: string;
  fim: string;
  motivo: string;
  reservasAfetadas: string[];
}

export interface BloqueioRequest {
  tipoRecurso: TipoRecurso;
  recursoId: string;
  inicio: string;
  fim: string;
  motivo: string;
}

export const TIPOS_RECURSO: Record<TipoRecurso, string> = {
  SALA: 'Sala',
  PROFESSOR: 'Professor',
  MATERIAL: 'Material'
};
