import { Component, input } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-cabecalho-pagina',
  imports: [LucideAngularModule],
  templateUrl: './cabecalho-pagina.component.html',
  styleUrl: './cabecalho-pagina.component.css'
})
export class CabecalhoPaginaComponent {
  readonly titulo = input.required<string>();
  readonly subtitulo = input<string>('');
  readonly icone = input<string>('');
}
