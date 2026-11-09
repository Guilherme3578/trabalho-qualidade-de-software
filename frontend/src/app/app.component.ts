import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MensagensComponent } from './components/mensagens/mensagens.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MensagensComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {}
