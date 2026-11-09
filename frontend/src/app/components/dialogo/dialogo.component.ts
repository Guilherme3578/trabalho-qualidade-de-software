import { Component, HostListener, input, output } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-dialogo',
  imports: [LucideAngularModule],
  templateUrl: './dialogo.component.html',
  styleUrl: './dialogo.component.css'
})
export class DialogoComponent {
  readonly titulo = input.required<string>();
  readonly largura = input<string>('520px');
  readonly fechar = output<void>();

  @HostListener('document:keydown.escape')
  aoPressionarEsc(): void {
    this.fechar.emit();
  }
}
