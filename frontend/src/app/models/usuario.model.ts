export type Perfil = 'PROFESSOR' | 'COORDENADOR' | 'RESPONSAVEL' | 'ADMIN';

export interface Usuario {
  id: string;
  nome: string;
  email: string;
  perfil: Perfil;
  professorId: string | null;
  ativo: boolean;
}

export interface UsuarioRequest {
  nome: string;
  email: string;
  senha: string;
  perfil: Perfil;
  professorId: string | null;
}

export interface LoginRequest {
  email: string;
  senha: string;
}

export interface TokenResponse {
  token: string;
  tipo: string;
  expiraEm: string;
  usuario: Usuario;
}

export const PERFIS: Record<Perfil, string> = {
  PROFESSOR: 'Professor',
  COORDENADOR: 'Coordenador',
  RESPONSAVEL: 'Responsável',
  ADMIN: 'Administrador'
};
