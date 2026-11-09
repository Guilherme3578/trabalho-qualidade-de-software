export interface Material {
  id: string;
  nome: string;
  categoria: string;
  patrimonio: string | null;
  quantidadeTotal: number;
  quantidadeDisponivel: number | null;
  localizacao: string;
  restrito: boolean;
  ativo: boolean;
}

export interface MaterialRequest {
  nome: string;
  categoria: string;
  patrimonio: string | null;
  quantidadeTotal: number;
  localizacao: string;
  restrito: boolean;
}
