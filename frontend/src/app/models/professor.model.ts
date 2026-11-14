export interface Professor {
  id: string;
  nome: string;
  email: string;
  departamento: string;
  competencias: string[];
  ativo: boolean;
  reservasAfetadas?: string[];
}

export interface ProfessorRequest {
  nome: string;
  email: string;
  departamento: string;
  competencias: string[];
}
