export interface Notificacao {
  id: string;
  destinatarioId: string;
  destinatarioEmail: string;
  tipo: string;
  mensagem: string;
  reservaId: string | null;
  status: 'SIMULADA' | 'ENVIADA' | 'FALHA';
  erro: string | null;
  lida: boolean;
  criadoEm: string;
}
