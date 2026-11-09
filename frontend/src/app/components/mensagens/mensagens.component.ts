import { Component, inject } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';
import { MensagemService, TipoMensagem } from '../../services/mensagem.service';

const ICONES: Record<TipoMensagem, string> = {
  sucesso: 'circle-check',
  erro: 'triangle-alert',
  aviso: 'info'
};

@Component({
  selector: 'app-mensagens',
  imports: [LucideAngularModule],
  templateUrl: './mensagens.component.html',
  styleUrl: './mensagens.component.css'
})
export class MensagensComponent {
  protected readonly servico = inject(MensagemService);
  protected readonly icones = ICONES;
}
