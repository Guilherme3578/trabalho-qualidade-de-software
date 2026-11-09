import { parametros } from './parametros';

describe('parametros', () => {
  it('ignora filtros vazios, nulos e indefinidos', () => {
    const params = parametros({ tipo: 'LABORATORIO', nome: '', capacidade: null, local: undefined, minhas: false });
    expect(params.keys()).toEqual(['tipo', 'minhas']);
    expect(params.get('tipo')).toBe('LABORATORIO');
    expect(params.get('minhas')).toBe('false');
  });

  it('converte números em texto', () => {
    expect(parametros({ capacidadeMinima: 30 }).get('capacidadeMinima')).toBe('30');
  });
});
