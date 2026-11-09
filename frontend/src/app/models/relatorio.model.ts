export interface UtilizacaoRecurso {
  recursoTipo: string;
  recursoId: string;
  descricao: string;
  reservas: number;
  horasReservadas: number;
  taxaOcupacaoPercentual: number;
}

export interface RelatorioUtilizacao {
  inicio: string;
  fim: string;
  horasDisponiveisPorRecurso: number;
  salas: UtilizacaoRecurso[];
  materiais: UtilizacaoRecurso[];
}

export interface CargaHoraria {
  professorId: string;
  professorNome: string;
  reservas: number;
  horasAlocadas: number;
}

export interface ConflitosPorRecurso {
  recursoTipo: string;
  recursoId: string;
  descricao: string;
  quantidade: number;
}

export interface RelatorioConflitos {
  inicio: string;
  fim: string;
  total: number;
  porTipo: Record<string, number>;
  porRecurso: ConflitosPorRecurso[];
}
