export interface Professor {
  id: string;
  nome: string;
  email: string;
  departamento: string;
  competencias: string[];
  ativo: boolean;
}

export interface ProfessorRequest {
  nome: string;
  email: string;
  departamento: string;
  competencias: string[];
}
