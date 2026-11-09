import { Component, input } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-estado-vazio',
  imports: [LucideAngularModule],
  templateUrl: './estado-vazio.component.html',
  styleUrl: './estado-vazio.component.css'
})
export class EstadoVazioComponent {
  readonly titulo = input.required<string>();
  readonly texto = input<string>('');
  readonly icone = input<string>('inbox');
}
