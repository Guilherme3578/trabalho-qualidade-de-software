import { Component, OnInit, computed, inject, input, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { LucideAngularModule } from 'lucide-angular';
import { CabecalhoPaginaComponent } from '../../components/cabecalho-pagina/cabecalho-pagina.component';
import { EstadoVazioComponent } from '../../components/estado-vazio/estado-vazio.component';
import { Material } from '../../models/material.model';
import { Professor } from '../../models/professor.model';
import { Reserva } from '../../models/reserva.model';
import { Sala, TIPOS_SALA } from '../../models/sala.model';
import { AuthService } from '../../services/auth.service';
import { dataIso, horas } from '../../services/datas';
import { MaterialService } from '../../services/material.service';
import { MensagemService } from '../../services/mensagem.service';
import { ProfessorService } from '../../services/professor.service';
import { ReservaService } from '../../services/reserva.service';
import { SalaService } from '../../services/sala.service';

const ABERTURA = '07:00';
const FECHAMENTO = '23:00';

@Component({
  selector: 'app-reserva-form',
  imports: [ReactiveFormsModule, RouterLink, LucideAngularModule, CabecalhoPaginaComponent, EstadoVazioComponent],
  templateUrl: './reserva-form.component.html',
  styleUrl: './reserva-form.component.css'
})
export class ReservaFormComponent implements OnInit {
  readonly id = input<string>();

  private readonly auth = inject(AuthService);
  private readonly reservaService = inject(ReservaService);
  private readonly salaService = inject(SalaService);
  private readonly professorService = inject(ProfessorService);
  private readonly materialService = inject(MaterialService);
  private readonly mensagens = inject(MensagemService);
  private readonly router = inject(Router);

  protected readonly tiposSala = TIPOS_SALA;
  protected readonly ehProfessor = this.auth.possuiPerfil('PROFESSOR');
  protected readonly hoje = dataIso(new Date());

  protected readonly original = signal<Reserva | null>(null);
  protected readonly salas = signal<Sala[]>([]);
  protected readonly professores = signal<Professor[]>([]);
  protected readonly materiais = signal<Material[]>([]);
  protected readonly quantidades = signal<Record<string, number>>({});
  protected readonly buscando = signal(false);
  protected readonly salvando = signal(false);
  protected readonly horarioConsultado = signal(false);

  protected readonly formulario = inject(FormBuilder).nonNullable.group({
    data: ['', Validators.required],
    inicio: ['08:00', Validators.required],
    fim: ['10:00', Validators.required],
    salaId: ['', Validators.required],
    professorId: ['', Validators.required],
    disciplina: ['', [Validators.required, Validators.maxLength(120)]],
    quantidadeAlunos: [20, [Validators.required, Validators.min(1)]],
    observacao: ['']
  });

  private readonly valores = signal(this.formulario.getRawValue());

  protected readonly erroHorario = computed(() => {
    const { data, inicio, fim } = this.valores();
    if (!data || !inicio || !fim) {
      return 'Informe a data e o horário.';
    }
    if (inicio < ABERTURA || fim > FECHAMENTO) {
      return 'O horário de funcionamento é das 07:00 às 23:00.';
    }
    const duracao = horas(`${data}T${inicio}`, `${data}T${fim}`);
    if (duracao <= 0) {
      return 'O término deve ser depois do início.';
    }
    if (duracao < 0.5 || duracao > 6) {
      return 'A reserva deve durar entre 30 minutos e 6 horas.';
    }
    if (new Date(`${data}T${inicio}`) < new Date()) {
      return 'Escolha um horário que ainda não passou.';
    }
    return null;
  });

  protected readonly salaEscolhida = computed(() => this.salas().find(s => s.id === this.valores().salaId) ?? null);
  protected readonly professorEscolhido = computed(
    () => this.professores().find(p => p.id === this.valores().professorId) ?? null
  );
  protected readonly duracao = computed(() => {
    const { data, inicio, fim } = this.valores();
    return data && inicio && fim ? horas(`${data}T${inicio}`, `${data}T${fim}`) : 0;
  });
  protected readonly itensEscolhidos = computed(() =>
    this.materiais()
      .filter(m => (this.quantidades()[m.id] ?? 0) > 0)
      .map(m => ({ material: m, quantidade: this.quantidades()[m.id] }))
  );

  protected readonly avisosAprovacao = computed(() => {
    const avisos: string[] = [];
    const sala = this.salaEscolhida();
    const professor = this.professorEscolhido();
    const disciplina = this.valores().disciplina.trim().toLowerCase();
    if (sala?.restrita) {
      avisos.push(`A sala ${sala.codigo} é restrita.`);
    }
    for (const item of this.itensEscolhidos()) {
      if (item.material.restrito) {
        avisos.push(`${item.material.nome} é um material restrito.`);
      }
    }
    if (professor && disciplina && !professor.competencias.some(c => c.toLowerCase() === disciplina)) {
      avisos.push(`${professor.nome} não tem "${this.valores().disciplina.trim()}" entre as competências cadastradas.`);
    }
    return avisos;
  });

  ngOnInit(): void {
    this.formulario.valueChanges.subscribe(() => this.valores.set(this.formulario.getRawValue()));
    const amanha = new Date();
    amanha.setDate(amanha.getDate() + 1);
    this.formulario.patchValue({ data: dataIso(amanha) });

    const id = this.id();
    if (id) {
      this.reservaService.obter(id).subscribe({
        next: reserva => this.preencher(reserva),
        error: falha => this.mensagens.erro(falha)
      });
    } else if (this.ehProfessor) {
      this.formulario.patchValue({ professorId: this.auth.usuario()?.professorId ?? '' });
    }
  }

  protected consultar(): void {
    if (this.erroHorario()) {
      return;
    }
    const { data, inicio, fim } = this.formulario.getRawValue();
    const intervalo = { inicio: `${data}T${inicio}`, fim: `${data}T${fim}` };
    this.buscando.set(true);
    forkJoin({
      salas: this.salaService.listar(intervalo),
      professores: this.professorService.listar(intervalo),
      materiais: this.materialService.listar({ ...intervalo, quantidadeMinima: 0 })
    }).subscribe({
      next: dados => {
        const original = this.original();
        const mesmoHorario = original !== null && this.mesmoIntervaloDoOriginal(intervalo.inicio, intervalo.fim);
        this.salas.set(dados.salas);
        this.professores.set(dados.professores);
        this.materiais.set(
          dados.materiais.map(m => {
            const reservado = mesmoHorario ? original!.materiais.find(i => i.materialId === m.id)?.quantidade ?? 0 : 0;
            return { ...m, quantidadeDisponivel: (m.quantidadeDisponivel ?? 0) + reservado };
          })
        );
        if (original) {
          this.manterRecursosDoOriginal(original, dados.salas, dados.professores);
        }
        this.limparEscolhasIndisponiveis();
        this.horarioConsultado.set(true);
        this.buscando.set(false);
      },
      error: falha => {
        this.buscando.set(false);
        this.mensagens.erro(falha);
      }
    });
  }

  protected quantidade(material: Material): number {
    return this.quantidades()[material.id] ?? 0;
  }

  protected escolherSala(sala: Sala): void {
    this.formulario.patchValue({ salaId: sala.id });
  }

  protected alterarQuantidade(material: Material, delta: number): void {
    const atual = this.quantidade(material);
    const maximo = material.quantidadeDisponivel ?? 0;
    const nova = Math.min(Math.max(atual + delta, 0), maximo);
    this.quantidades.update(q => ({ ...q, [material.id]: nova }));
  }

  protected salvar(): void {
    if (this.formulario.invalid || this.erroHorario()) {
      this.formulario.markAllAsTouched();
      this.mensagens.aviso('Revise os campos destacados antes de confirmar.');
      return;
    }
    const v = this.formulario.getRawValue();
    const dados = {
      salaId: v.salaId,
      professorId: v.professorId,
      disciplina: v.disciplina.trim(),
      inicio: `${v.data}T${v.inicio}`,
      fim: `${v.data}T${v.fim}`,
      quantidadeAlunos: v.quantidadeAlunos,
      observacao: v.observacao.trim() || null,
      materiais: this.itensEscolhidos().map(i => ({ materialId: i.material.id, quantidade: i.quantidade })),
      versao: this.original()?.versao
    };
    this.salvando.set(true);
    const original = this.original();
    const requisicao = original ? this.reservaService.alterar(original.id, dados) : this.reservaService.criar(dados);
    requisicao.subscribe({
      next: reserva => {
        this.mensagens.sucesso(
          reserva.status === 'APROVADA'
            ? 'Reserva confirmada e aprovada automaticamente.'
            : 'Reserva registrada. Ela aguarda a aprovação de um responsável.'
        );
        this.router.navigate(['/reservas', reserva.id]);
      },
      error: falha => {
        this.salvando.set(false);
        this.mensagens.erro(falha);
      }
    });
  }

  private preencher(reserva: Reserva): void {
    this.original.set(reserva);
    this.formulario.patchValue({
      data: reserva.inicio.substring(0, 10),
      inicio: reserva.inicio.substring(11, 16),
      fim: reserva.fim.substring(11, 16),
      salaId: reserva.salaId,
      professorId: reserva.professorId,
      disciplina: reserva.disciplina,
      quantidadeAlunos: reserva.quantidadeAlunos,
      observacao: reserva.observacao ?? ''
    });
    this.quantidades.set(Object.fromEntries(reserva.materiais.map(i => [i.materialId, i.quantidade])));
    this.consultar();
  }

  private mesmoIntervaloDoOriginal(inicio: string, fim: string): boolean {
    const original = this.original();
    if (!original) {
      return false;
    }
    return inicio < original.fim.substring(0, 16) && fim > original.inicio.substring(0, 16);
  }

  private manterRecursosDoOriginal(original: Reserva, salas: Sala[], professores: Professor[]): void {
    const { data, inicio, fim } = this.formulario.getRawValue();
    if (!this.mesmoIntervaloDoOriginal(`${data}T${inicio}`, `${data}T${fim}`)) {
      return;
    }
    if (!salas.some(s => s.id === original.salaId)) {
      this.salaService.listar({ incluirInativas: true }).subscribe(todas => {
        const sala = todas.find(s => s.id === original.salaId);
        if (sala) {
          this.salas.update(lista => [sala, ...lista]);
        }
      });
    }
    if (!professores.some(p => p.id === original.professorId)) {
      this.professorService.listar().subscribe(todos => {
        const professor = todos.find(p => p.id === original.professorId);
        if (professor) {
          this.professores.update(lista => [professor, ...lista]);
        }
      });
    }
  }

  private limparEscolhasIndisponiveis(): void {
    const original = this.original();
    const { salaId, professorId } = this.formulario.getRawValue();
    if (salaId && !this.salas().some(s => s.id === salaId) && salaId !== original?.salaId) {
      this.formulario.patchValue({ salaId: '' });
    }
    if (!this.ehProfessor && professorId && !this.professores().some(p => p.id === professorId)
      && professorId !== original?.professorId) {
      this.formulario.patchValue({ professorId: '' });
    }
    this.quantidades.update(q => {
      const ajustado: Record<string, number> = {};
      for (const m of this.materiais()) {
        if (q[m.id]) {
          ajustado[m.id] = Math.min(q[m.id], m.quantidadeDisponivel ?? 0);
        }
      }
      return ajustado;
    });
  }
}
