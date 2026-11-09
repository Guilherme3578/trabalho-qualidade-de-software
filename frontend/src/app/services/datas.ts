function doisDigitos(valor: number): string {
  return String(valor).padStart(2, '0');
}

export function dataIso(data: Date): string {
  return `${data.getFullYear()}-${doisDigitos(data.getMonth() + 1)}-${doisDigitos(data.getDate())}`;
}

export function dataHoraIso(data: Date): string {
  return `${dataIso(data)}T${doisDigitos(data.getHours())}:${doisDigitos(data.getMinutes())}`;
}

export function inicioDoDia(data: Date = new Date()): string {
  return `${dataIso(data)}T00:00`;
}

export function fimDoDia(data: Date = new Date()): string {
  return `${dataIso(data)}T23:59`;
}

export function somarDias(data: Date, dias: number): Date {
  const copia = new Date(data);
  copia.setDate(copia.getDate() + dias);
  return copia;
}

export function horas(inicio: string, fim: string): number {
  return (new Date(fim).getTime() - new Date(inicio).getTime()) / 3_600_000;
}
