import { dataHoraIso, dataIso, fimDoDia, horas, inicioDoDia, somarDias } from './datas';

describe('datas', () => {
  const data = new Date(2026, 10, 9, 8, 5);

  it('formata a data no padrão da API, sem fuso', () => {
    expect(dataIso(data)).toBe('2026-11-09');
    expect(dataHoraIso(data)).toBe('2026-11-09T08:05');
  });

  it('monta o início e o fim do dia', () => {
    expect(inicioDoDia(data)).toBe('2026-11-09T00:00');
    expect(fimDoDia(data)).toBe('2026-11-09T23:59');
  });

  it('soma dias atravessando o mês sem alterar a data original', () => {
    const resultado = somarDias(new Date(2026, 10, 30), 2);
    expect(dataIso(resultado)).toBe('2026-12-02');
    expect(dataIso(data)).toBe('2026-11-09');
  });

  it.each([
    ['2026-11-09T08:00', '2026-11-09T10:00', 2],
    ['2026-11-09T08:00', '2026-11-09T08:30', 0.5],
    ['2026-11-09T19:15', '2026-11-09T22:00', 2.75]
  ])('calcula a duração de %s até %s', (inicio, fim, esperado) => {
    expect(horas(inicio, fim)).toBe(esperado);
  });
});
