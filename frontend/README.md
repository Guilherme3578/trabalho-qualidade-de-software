# Front-end — Alocação de Salas (Angular)

Interface web do sistema de alocação de salas, professores e materiais. Este documento serve de **guia de
estudo**: cada seção explica uma etapa da construção, os conceitos de Angular envolvidos e onde eles aparecem no
código.

## Sumário

1. [Como executar](#1-como-executar)
2. [Tecnologias](#2-tecnologias)
3. [Estrutura de pastas](#3-estrutura-de-pastas)
4. [Etapa 1 — Criação do projeto](#4-etapa-1--criação-do-projeto)
5. [Etapa 2 — Models](#5-etapa-2--models)
6. [Etapa 3 — Services](#6-etapa-3--services)
7. [Etapa 4 — Interceptor e guards](#7-etapa-4--interceptor-e-guards)
8. [Etapa 5 — Tema visual](#8-etapa-5--tema-visual)
9. [Etapa 6 — Componentes reutilizáveis](#9-etapa-6--componentes-reutilizáveis)
10. [Etapa 7 — Tela de login](#10-etapa-7--tela-de-login)
11. [Glossário](#glossário)

---

## 1. Como executar

São dois processos ao mesmo tempo: a API (Spring, porta 8080) e o front (Angular, porta 4200).

```bash
# Terminal 1 — API, na raiz do repositório
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Terminal 2 — front-end
cd frontend
npm install        # só na primeira vez
npm start          # abre em http://localhost:4200
```

Usuários de demonstração (senha `Senha123`): `admin@senac.br`, `responsavel@senac.br`,
`coordenador@senac.br` e `ana.souza@senac.br`.

| Comando | O que faz |
|---|---|
| `npm start` | Servidor de desenvolvimento com recarga automática |
| `npm run build` | Gera a versão de produção em `dist/` |
| `npm test` | Roda os testes unitários (Vitest) |

> Se o `npm install` falhar com `Cannot read properties of null (reading 'edgesOut')`, o npm da máquina é antigo
> (9.x). Use `npx npm@11.6.2 install`.

## 2. Tecnologias

| Tecnologia | Versão | Para quê |
|---|---|---|
| Angular | 21 | Framework do front-end |
| TypeScript | 5.9 | JavaScript com tipos |
| RxJS | 7.8 | Programação reativa (as chamadas HTTP retornam `Observable`) |
| lucide-angular | 1.0 | Ícones em SVG |
| Inter (fontsource) | 5.3 | Fonte da interface, servida localmente |
| Vitest | 4 | Testes unitários |

**Por que Angular 21 e não 22?** O Angular 22 exige Node 22.22.3 ou mais novo, e a máquina de desenvolvimento
tem o 22.22.1. O 21 tem os mesmos recursos usados aqui (componentes standalone, signals e a nova sintaxe de
template).

## 3. Estrutura de pastas

```
frontend/
├── proxy.conf.json          # repassa /api para o Spring (porta 8080)
├── angular.json             # configuração do Angular CLI
├── package.json             # dependências e scripts npm
└── src/
    ├── index.html           # página única onde o Angular é montado
    ├── main.ts              # ponto de entrada: inicia o AppComponent
    ├── styles.css           # estilos globais (tema, botões, formulários)
    └── app/
        ├── app.component.*  # componente raiz (só contém o <router-outlet>)
        ├── app.config.ts    # providers globais: rotas, HTTP, ícones, idioma
        ├── app.routes.ts    # mapa de URLs → páginas
        ├── models/          # interfaces TypeScript iguais aos DTOs da API
        ├── services/        # comunicação com a API e estado compartilhado
        ├── interceptors/    # código executado em toda requisição HTTP
        ├── guards/          # regras de acesso às rotas
        ├── components/      # peças reutilizáveis de interface
        └── pages/           # telas, uma pasta por rota
```

Regra de dependência: `pages` e `components` usam `services`, que usam `models`. Nunca o contrário.

### O front não acessa o banco de dados

```
[ Angular / TypeScript ]  --HTTP + JSON-->  [ Spring / Java ]  -->  [ MongoDB ]
        frontend/                            src/main/java            banco
```

O front-end **só conversa com a API** por HTTP. Quem lê e grava no MongoDB é exclusivamente o back-end em Java.
Os arquivos de `models/` descrevem o **JSON que a API envia e recebe**, e não as coleções do banco. Se o banco
mudar, o front não precisa mudar.

Cada componente tem três arquivos:

| Arquivo | Conteúdo |
|---|---|
| `nome.component.ts` | A classe: dados, eventos, chamadas aos services |
| `nome.component.html` | O template: o que aparece na tela |
| `nome.component.css` | Estilos que valem só para esse componente |

---

## 4. Etapa 1 — Criação do projeto

```bash
npx @angular/cli@21.2.29 new alocacao-web --directory frontend --style css --routing --ssr=false
```

| Opção | Significado |
|---|---|
| `--style css` | CSS puro (sem SCSS) |
| `--routing` | Cria o `app.routes.ts` para navegar entre páginas |
| `--ssr=false` | Sem renderização no servidor; o Spring serve a API e o Angular roda no navegador |

### Proxy de desenvolvimento

O navegador abre o front em `localhost:4200`, mas a API está em `localhost:8080`. Como as portas são
diferentes, o navegador bloquearia as chamadas (regra de **CORS**). O `proxy.conf.json` resolve isso: o
servidor do Angular recebe `/api/...` e repassa para o Spring, e para o navegador tudo vem da mesma origem.

```json
{ "/api": { "target": "http://localhost:8080", "secure": false } }
```

### Padrão de nomes

O Angular 21 gera, por padrão, arquivos como `app.ts`. O `angular.json` foi configurado (`schematics`) para
gerar no padrão clássico `nome.component.ts`, `nome.service.ts` e `nome.guard.ts`, que deixa claro o papel de
cada arquivo.

## 5. Etapa 2 — Models

Pasta `src/app/models`. Um **model** é uma `interface` TypeScript que descreve o formato de um objeto. Ele não
gera código em JavaScript, só serve para o compilador avisar quando um campo é usado errado.

```ts
export interface Sala {
  id: string;
  codigo: string;
  tipo: TipoSala;
  capacidade: number;
  ativa: boolean;
}
```

- **Request x Response.** `SalaRequest` é o que **enviamos** (sem `id` e sem `ativa`, que o servidor
  controla). `Sala` é o que **recebemos**.
- **Tipos união** (`type TipoSala = 'SALA_AULA' | 'LABORATORIO' | 'AUDITORIO'`) só aceitam esses valores
  exatos, iguais aos enums do Java.
- **Rótulos** (`TIPOS_SALA`, `STATUS_RESERVA`): `Record<Tipo, string>` traduz o valor técnico para o texto
  exibido. Ex.: `STATUS_RESERVA['EM_USO']` → `"Em uso"`.
- `erro-api.model.ts` descreve o erro padronizado que a API devolve (Problem Details):

```json
{ "status": 400, "codigo": "VALIDACAO", "detail": "Existem campos inválidos...",
  "campos": [{ "campo": "nome", "mensagem": "Informe o nome da sala." }] }
```

## 6. Etapa 3 — Services

Pasta `src/app/services`. Um **service** é uma classe com `@Injectable({ providedIn: 'root' })`: o Angular
cria **uma única instância** para o app inteiro e a entrega a quem pedir (**injeção de dependência**).

```ts
@Injectable({ providedIn: 'root' })
export class SalaService {
  private readonly http = inject(HttpClient);

  listar(filtro: FiltroSalas = {}): Observable<Sala[]> {
    return this.http.get<Sala[]>('/api/salas', { params: parametros(filtro) });
  }
}
```

### Conceitos

- **`inject(HttpClient)`** pede ao Angular o cliente HTTP. Equivale a receber pelo construtor.
- **`Observable`**: a requisição só é enviada quando alguém chama `.subscribe()`. Quem usa:

```ts
this.salaService.listar({ capacidadeMinima: 30 }).subscribe({
  next: salas => this.salas.set(salas),
  error: erro => this.mensagens.erro(erro)
});
```

- **`parametros()`** transforma `{ tipo: 'LABORATORIO', nome: '' }` em `?tipo=LABORATORIO`, ignorando filtros
  vazios.

### Signals: estado compartilhado

`AuthService` e `NotificacaoService` guardam estado em **signals**: valores que avisam a tela quando mudam.

```ts
private readonly usuarioAtual = signal<Usuario | null>(null);
readonly usuario = this.usuarioAtual.asReadonly();
readonly autenticado = computed(() => this.tokenAtual() !== null);
```

| API | Uso |
|---|---|
| `signal(valor)` | Cria; lê com `usuario()`, altera com `.set()` ou `.update()` |
| `computed(() => ...)` | Valor derivado, recalculado sozinho quando as dependências mudam |
| `asReadonly()` | Expõe para leitura; só o service altera |

### Token no `sessionStorage`

Depois do login, o token JWT fica no `sessionStorage`, que é apagado quando a aba é fechada. Isso é mais seguro
que o `localStorage`, que dura para sempre.

### `MensagemService`

Centraliza os avisos na tela. O método `erro(falha)` converte qualquer erro HTTP em uma mensagem legível,
usando o `detail` e a lista de `campos` da API, e trata o caso de o servidor estar fora do ar (`status 0`).

## 7. Etapa 4 — Interceptor e guards

### Interceptor (`interceptors/auth.interceptor.ts`)

Um **interceptor** é uma função que roda **em toda requisição HTTP**, antes de ela sair e depois de a resposta
chegar. Aqui ele faz duas coisas:

1. **Na ida:** coloca `Authorization: Bearer <token>` em toda chamada para `/api/`.
2. **Na volta:** se a API responder **401** (token expirado ou usuário desativado), faz logout e manda para a
   tela de login.

```ts
const requisicao = token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;
```

> As requisições são **imutáveis**: para mudar algo, usa-se `req.clone(...)`.

Ele é registrado em `app.config.ts`: `provideHttpClient(withInterceptors([authInterceptor]))`.

### Guards (`guards/`)

Um **guard** decide se uma rota pode ser aberta. Retorna `true` (pode) ou um `UrlTree` (redireciona).

| Guard | Regra |
|---|---|
| `authGuard` | Só entra quem está logado; senão vai para `/login` |
| `visitanteGuard` | Quem já está logado não vê o login de novo; vai para `/inicio` |
| `perfilGuard` | Só entra quem tem um dos perfis listados na rota; senão volta para `/inicio` |

O `perfilGuard` lê os perfis permitidos do `data` da rota:

```ts
{ path: 'aprovacoes', canActivate: [perfilGuard], data: { perfis: ['RESPONSAVEL'] } }
```

> **Importante:** os guards só melhoram a experiência, escondendo o que o usuário não pode usar. **A segurança de
> verdade está na API**, que confere o perfil em cada requisição e responde 403. Um usuário que burlar o front
> continua bloqueado.

### `app.config.ts`

É onde ficam os **providers globais**:

| Provider | Função |
|---|---|
| `provideRouter(routes, withComponentInputBinding(), withViewTransitions())` | Rotas; parâmetros da URL viram `input()` do componente; animação na troca de página |
| `provideHttpClient(withInterceptors([...]))` | Cliente HTTP com o interceptor |
| `{ provide: LOCALE_ID, useValue: 'pt-BR' }` + `registerLocaleData` | Datas e números no formato brasileiro (`08/11/2026`, `1.234,5`) |
| `LucideAngularModule.pick({...})` | Registra só os ícones usados, para não carregar a biblioteca inteira |

## 8. Etapa 5 — Tema visual

Arquivo `src/styles.css`. Os estilos **globais** valem para o app inteiro. Os de cada componente
(`nome.component.css`) valem só para ele, porque o Angular isola o CSS de cada componente automaticamente.

### Variáveis CSS (design tokens)

Todas as cores, raios e sombras ficam em variáveis no `:root`. Para mudar a cor principal do sistema, basta
alterar um lugar:

```css
:root {
  --fundo: #07080d;
  --primaria: #8b5cf6;
  --degrade: linear-gradient(135deg, #8b5cf6 0%, #6366f1 45%, #22d3ee 100%);
}
.btn-primario { background: var(--degrade); }
```

| Grupo | Variáveis | Uso |
|---|---|---|
| Fundo e superfícies | `--fundo`, `--superficie`, `--borda` | Camadas do tema escuro, do mais escuro ao mais claro |
| Texto | `--texto`, `--texto-suave`, `--texto-fraco` | Títulos, textos comuns e legendas |
| Destaque | `--primaria`, `--secundaria`, `--degrade`, `--brilho` | Botão principal, links, brilho no hover |
| Estados | `--sucesso`, `--aviso`, `--perigo`, `--info` | Selos de status e mensagens |

### Classes reutilizáveis

| Classe | Onde usar |
|---|---|
| `.btn` + `.btn-primario` / `.btn-fantasma` / `.btn-perigo` / `.btn-sucesso` | Botões; `.btn-sm` (menor) e `.btn-icone` (só ícone) |
| `.cartao` / `.cartao-interativo` | Painel com efeito de vidro; o interativo sobe e brilha no hover |
| `.formulario`, `.linha-campos`, `.campo` | Formulários; `.linha-campos` coloca campos lado a lado e quebra linha no celular |
| `.tabela-container` + `table` | Tabelas com rolagem horizontal em telas pequenas |
| `.badge` + `.badge-sucesso` etc. | Selos coloridos; a cor de fundo é derivada do texto com `color-mix()` |
| `.animar` | Entrada suave; com `style="--i: 2"` cada item entra um pouco depois do anterior |
| `.skeleton` | Bloco cinza animado que aparece enquanto os dados carregam |

### Efeitos e animações

- **Fundo "aurora":** três `radial-gradient` sobrepostos com `background-attachment: fixed`.
- **Vidro fosco:** `backdrop-filter: blur(16px)` com fundo semitransparente nos cartões.
- **Hover:** botões sobem 1px (`translateY(-1px)`); o principal desliza o degradê (`background-position`) e ganha
  brilho (`box-shadow`). Cartões interativos sobem 3px com borda violeta.
- **Entrada escalonada:** `animation-delay: calc(var(--i, 0) * 60ms)` faz os cartões aparecerem um após o
  outro.
- **Troca de página:** `withViewTransitions()` no roteador usa a View Transitions API do navegador.
- **Acessibilidade:** `:focus-visible` mostra contorno ciano ao navegar pelo teclado, e
  `prefers-reduced-motion` desliga as animações para quem configurou isso no sistema.

### Fonte

A fonte Inter vem do pacote `@fontsource-variable/inter`, registrado em `angular.json` → `styles`. Ela é servida
pelo próprio app, e não pelo Google Fonts, porque a política de segurança (CSP) do back-end só permite recursos
da mesma origem.

## 9. Etapa 6 — Componentes reutilizáveis

Pasta `src/app/components`. São peças usadas por várias telas. Cada uma fica na sua pasta, com `.ts`, `.html` e
`.css`.

| Componente | Seletor | Para quê |
|---|---|---|
| `layout` | `<app-layout>` | Moldura das telas internas: menu lateral, barra superior e `<router-outlet>` |
| `mensagens` | `<app-mensagens>` | Avisos flutuantes de sucesso, erro e atenção (lê o `MensagemService`) |
| `status-badge` | `<app-status-badge [status]="r.status" />` | Selo colorido do status; pisca em "Em uso" e "Aguardando" |
| `cabecalho-pagina` | `<app-cabecalho-pagina titulo="..." icone="..." />` | Título da tela, com espaço para botões |
| `estado-vazio` | `<app-estado-vazio titulo="..." />` | Mensagem para lista vazia |
| `dialogo` | `<app-dialogo titulo="..." (fechar)="...">` | Janela modal; fecha com Esc, com o X ou clicando fora |

### Entrada e saída de dados: `input()` e `output()`

```ts
export class StatusBadgeComponent {
  readonly status = input.required<StatusReserva>();
  protected readonly rotulo = computed(() => STATUS_RESERVA[this.status()]);
}
```

- **`input()`**: dado que vem **de fora**, do componente pai (`[status]="reserva.status"`). É um signal; lê com
  `status()`. `input.required` obriga o pai a informar.
- **`output()`**: evento que o componente **dispara para o pai**. No `dialogo`, `fechar.emit()` avisa que o
  usuário quer fechar, e o pai decide o que fazer: `(fechar)="dialogoAberto.set(false)"`.

### Projeção de conteúdo: `<ng-content />`

O `dialogo` e o `cabecalho-pagina` não sabem o que vão exibir dentro deles. O `<ng-content />` marca onde entra
o HTML que o pai colocou entre as tags:

```html
<app-dialogo titulo="Cancelar reserva" (fechar)="fechar()">
  <p>Este texto aparece dentro do diálogo.</p>
</app-dialogo>
```

### Sintaxe de template

| Sintaxe | Exemplo | Significado |
|---|---|---|
| `{{ }}` | `{{ m.titulo }}` | Mostra um valor |
| `[prop]` | `[class.ativo]="menuAberto()"` | Liga uma propriedade a um valor |
| `(evento)` | `(click)="sair()"` | Reage a um evento |
| `@if` | `@if (texto()) { <p>...</p> }` | Mostra só se a condição for verdadeira |
| `@for` | `@for (m of mensagens(); track m.id) { ... }` | Repete para cada item; `track` identifica o item para o Angular não recriar à toa |

### Layout e menu por perfil

O `layout` monta o menu a partir de uma lista (`MENU`) em que cada item informa os perfis que podem vê-lo.
Um `computed` filtra os itens pelo perfil do usuário logado. No celular (até 900px), o menu vira uma gaveta que
abre pelo botão ☰ e fecha sozinha ao trocar de página.

O `routerLinkActive="ativo"` aplica a classe `ativo` no link da página atual, que ganha a barra em degradê.

## 10. Etapa 7 — Tela de login

Pasta `src/app/pages/login`. Primeira página do sistema, em `/login`.

### Formulário reativo

O formulário é criado **no TypeScript**, e não no HTML. Isso deixa as validações num lugar só e facilita
testar:

```ts
protected readonly formulario = inject(FormBuilder).nonNullable.group({
  email: ['', [Validators.required, Validators.email]],
  senha: ['', Validators.required]
});
```

No HTML, cada campo é ligado pelo nome:

```html
<form [formGroup]="formulario" (ngSubmit)="entrar()">
  <input formControlName="email" />
  @if (formulario.controls.email.touched && formulario.controls.email.invalid) {
    <small>Informe um e-mail válido.</small>
  }
</form>
```

| Conceito | Significado |
|---|---|
| `nonNullable` | Ao limpar o formulário, os campos voltam para `''`, e não para `null` |
| `Validators.required` / `.email` | Regras prontas do Angular |
| `touched` | O usuário já passou pelo campo; só então mostramos o erro, para não assustar quem acabou de abrir a tela |
| `markAllAsTouched()` | Ao tentar enviar inválido, mostra todos os erros de uma vez |
| `getRawValue()` | Pega os valores tipados: `{ email: string, senha: string }` |

### Fluxo do login

1. O usuário clica em **Entrar**; se o formulário for inválido, os erros aparecem e nada é enviado.
2. `enviando` vira `true`: o botão fica desabilitado e mostra um ícone girando, evitando cliques duplos.
3. `AuthService.login()` chama `POST /api/auth/login`. Em caso de sucesso, guarda token e usuário e navega para
   `/inicio`.
4. Em caso de erro, `MensagemService.converter()` extrai a mensagem da API ("E-mail ou senha inválidos.") e ela
   aparece num alerta que treme.

### Outros detalhes

- **Mostrar senha:** um signal `mostrarSenha` troca o `type` do input entre `password` e `text`.
- **Acesso rápido de demonstração:** os botões preenchem e-mail e senha dos usuários de teste com
  `formulario.setValue(...)`.
- **`visitanteGuard`:** quem já está logado e abre `/login` é levado direto para `/inicio`.
- **Animações:** as "orbes" coloridas do fundo flutuam com `@keyframes flutuar`, e os itens da lista entram em
  sequência com `.animar` + `--i`.
- **Título da aba:** a rota define `title: 'Entrar · AlocaSENAC'`, e o Angular atualiza o `<title>` sozinho.

### Ícones

O `lucide-icon` é usado como `<lucide-icon name="calendar-days" />`. O tamanho vem do CSS global
(`width: 1.15em`), então o ícone acompanha o tamanho do texto ao redor; para um tamanho específico, basta definir
`width` e `height` no CSS do componente.

---

## Glossário

| Termo | Significado |
|---|---|
| **Componente** | Pedaço da tela com classe, template e estilo próprios |
| **Standalone** | Componente que declara as próprias dependências em `imports`, sem precisar de `NgModule` |
| **Template** | O HTML do componente, com sintaxe extra do Angular (`@if`, `@for`, `{{ }}`) |
| **Service** | Classe compartilhada que guarda regras e acessa a API |
| **Injeção de dependência** | O Angular cria e entrega os objetos de que uma classe precisa (`inject(...)`) |
| **Observable** | Fluxo de dados assíncrono do RxJS; só executa quando alguém se inscreve (`subscribe`) |
| **Signal** | Valor reativo; a tela atualiza sozinha quando ele muda |
| **Interceptor** | Função que intercepta todas as requisições HTTP |
| **Guard** | Função que permite ou bloqueia a entrada em uma rota |
| **Proxy** | Servidor intermediário que repassa `/api` para o back-end |
| **JWT** | Token assinado que identifica o usuário logado |
| **CORS** | Regra do navegador que bloqueia chamadas entre origens diferentes |
| **Formulário reativo** | Formulário definido no TypeScript (`FormGroup`), ligado ao HTML com `formControlName` |
