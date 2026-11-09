export interface CampoInvalido {
  campo: string;
  mensagem: string;
}

export interface ErroApi {
  status: number;
  codigo: string;
  mensagem: string;
  campos: CampoInvalido[];
  correlationId: string | null;
}
