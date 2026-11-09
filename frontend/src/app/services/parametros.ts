import { HttpParams } from '@angular/common/http';

export function parametros(filtro: object): HttpParams {
  let params = new HttpParams();
  for (const [chave, valor] of Object.entries(filtro)) {
    if (valor !== undefined && valor !== null && valor !== '') {
      params = params.set(chave, String(valor));
    }
  }
  return params;
}
