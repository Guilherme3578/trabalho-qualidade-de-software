import { TestBed } from '@angular/core/testing';
import { StatusReserva } from '../../models/reserva.model';
import { StatusBadgeComponent } from './status-badge.component';

describe('StatusBadgeComponent', () => {
  const renderizar = (status: StatusReserva) => {
    const fixture = TestBed.createComponent(StatusBadgeComponent);
    fixture.componentRef.setInput('status', status);
    fixture.detectChanges();
    return fixture.nativeElement.querySelector('.badge') as HTMLElement;
  };

  it.each<[StatusReserva, string, string]>([
    ['SOLICITADA', 'Aguardando aprovação', 'badge-aviso'],
    ['APROVADA', 'Aprovada', 'badge-info'],
    ['EM_USO', 'Em uso', 'badge-primario'],
    ['CONCLUIDA', 'Concluída', 'badge-sucesso'],
    ['REJEITADA', 'Rejeitada', 'badge-perigo'],
    ['CANCELADA', 'Cancelada', 'badge-neutro'],
    ['NAO_COMPARECEU', 'Não compareceu', 'badge-perigo']
  ])('mostra %s como "%s" com a cor %s', (status, texto, classe) => {
    const badge = renderizar(status);
    expect(badge.textContent?.trim()).toBe(texto);
    expect(badge.classList).toContain(classe);
    expect(badge.classList).toContain('badge');
  });

  it('pisca apenas nos status que pedem atenção', () => {
    expect(renderizar('EM_USO').querySelector('.pulsando')).not.toBeNull();
    expect(renderizar('CONCLUIDA').querySelector('.pulsando')).toBeNull();
  });
});
