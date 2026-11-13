# 2. Arquitetura

> 📝 **Rascunho para revisão.**

## 2.1 Visão geral

Aplicação única **Java 21 + Spring Boot 4**, que expõe uma API REST e serve a interface web em **Angular 21**. O Maven compila o Angular
(`frontend/`) durante o build e copia o resultado para `static/` dentro do `.jar`, então API e
interface são entregues juntas, na mesma porta.
Persistência em **MySQL 8.4** (banco relacional), acessado com **Spring Data JPA / Hibernate 7**. O
esquema é versionado com **Flyway** (`src/main/resources/db/migration`) e o Hibernate só valida o
mapeamento (`ddl-auto=validate`), nunca altera tabelas sozinho.

```mermaid
flowchart LR
    U[Navegador<br/>Angular 21] -->|HTTPS + JWT| API
    subgraph Spring Boot
      API[Controllers REST<br/>+ Spring Security] --> S[Serviços<br/>regras de negócio]
      S --> R[(Repositórios<br/>Spring Data JPA)]
      S -.evento após commit.-> N[NotificacaoService]
    end
    R --> M[(MySQL 8.4<br/>esquema Flyway)]
    N -->|HTTP POST JSON| EXT[API externa de<br/>notificação]
```

## 2.2 Organização do código (pacote por funcionalidade)

| Pacote | Responsabilidade |
|---|---|
| `web` | Encaminha as rotas do Angular (`/reservas`, `/login`...) para o `index.html`, permitindo recarregar a página em qualquer tela |
| `config` | Segurança (JWT, perfis, cabeçalhos), OpenAPI, relógio, correlationId, dados de demonstração |
| `comum` | Tratamento centralizado de erros (`ApiExceptionHandler`), códigos de erro, utilitários de filtro |
| `auth`, `usuario` | Login, emissão do token, limite de tentativas, gestão de usuários |
| `sala`, `professor`, `material` | Cadastros e pesquisas |
| `bloqueio` | Períodos de manutenção / indisponibilidade |
| `reserva` | Núcleo: `ReservaService`, máquina de estados, disponibilidade, concorrência |
| `reserva.regras` | Regras puras, sem banco (facilmente testáveis): `Intervalo`, `PoliticaHorario`, `CalculadoraOcupacao`, `AvaliadorAprovacao` |
| `auditoria` | Histórico imutável de mudanças |
| `notificacao` | Notificações e integração HTTP com a API externa |
| `relatorio` | Utilização, carga horária e conflitos evitados |

**Decisão:** as regras de negócio mais críticas foram isoladas em classes puras (`reserva.regras`), sem
dependência de Spring ou banco. Isso permite testá-las com centenas de combinações em milissegundos
(testes parametrizados) e deixa os testes de integração focados na interação com o banco.

## 2.3 Modelo de dados (relacional)

O esquema completo está em `src/main/resources/db/migration/V1__esquema_inicial.sql`. As tabelas
seguem a 3ª forma normal: a reserva guarda só as chaves de sala, professor e solicitante, e os nomes
são obtidos por junção (não há cópias que possam ficar desatualizadas).

```mermaid
erDiagram
    PROFESSORES ||--o{ PROFESSOR_COMPETENCIAS : "possui"
    PROFESSORES |o--o{ USUARIOS : "vinculado a"
    SALAS ||--o{ RESERVAS : "recebe"
    PROFESSORES ||--o{ RESERVAS : "ministra"
    USUARIOS ||--o{ RESERVAS : "solicita"
    RESERVAS ||--o{ RESERVA_ITENS : "contém"
    MATERIAIS ||--o{ RESERVA_ITENS : "é reservado em"
    RESERVAS ||--o{ RESERVA_MOTIVOS_APROVACAO : "exige aprovação por"
    RESERVAS ||--o{ RESERVA_MOVIMENTACOES : "registra"
    MATERIAIS ||--o{ RESERVA_MOVIMENTACOES : "é movimentado em"
    USUARIOS ||--o{ RESERVA_MOVIMENTACOES : "registra"
    SALAS |o--o{ BLOQUEIOS : "fica indisponível"
    PROFESSORES |o--o{ BLOQUEIOS : "fica indisponível"
    MATERIAIS |o--o{ BLOQUEIOS : "fica indisponível"
    USUARIOS ||--o{ BLOQUEIOS : "cria"
    USUARIOS ||--o{ NOTIFICACOES : "recebe"
    RESERVAS |o--o{ NOTIFICACOES : "origina"
    USUARIOS ||--o{ CONFLITOS_EVITADOS : "tentou"

    SALAS {
        varchar id PK
        varchar codigo UK
        varchar tipo "CHECK SALA_AULA, LABORATORIO, AUDITORIO"
        int capacidade "CHECK 1..1000"
        boolean restrita
        boolean ativa
    }
    PROFESSORES {
        varchar id PK
        varchar email UK
        varchar departamento
        boolean ativo
    }
    MATERIAIS {
        varchar id PK
        varchar patrimonio UK "opcional"
        int quantidade_total "CHECK 0..10000"
        boolean restrito
        boolean ativo
    }
    USUARIOS {
        varchar id PK
        varchar email UK
        varchar senha_hash "BCrypt"
        varchar perfil "CHECK"
        varchar professor_id FK
    }
    RESERVAS {
        varchar id PK
        varchar sala_id FK
        varchar professor_id FK
        varchar solicitante_id FK
        datetime inicio
        datetime fim "CHECK fim > inicio"
        varchar status "CHECK"
        bigint versao "bloqueio otimista"
    }
    RESERVA_ITENS {
        varchar reserva_id PK, FK
        varchar material_id PK, FK
        int quantidade "CHECK 1..500"
    }
    RESERVA_MOVIMENTACOES {
        varchar id PK
        varchar reserva_id FK
        varchar material_id FK
        varchar tipo "RETIRADA ou DEVOLUCAO"
        varchar registrado_por_id FK
    }
    BLOQUEIOS {
        varchar id PK
        varchar tipo_recurso
        varchar sala_id FK
        varchar professor_id FK
        varchar material_id FK
        datetime inicio
        datetime fim
    }
    AUDITORIA {
        bigint id PK
        datetime data_hora
        varchar acao
        varchar entidade
        varchar entidade_id
    }
```

| Tabela | Conteúdo | Restrições e índices relevantes |
|---|---|---|
| `usuarios` | nome, e-mail, hash BCrypt, perfil, vínculo com professor | e-mail único; FK `professor_id`; CHECK do perfil |
| `salas` | código, tipo, capacidade, localização, restrita, ativa | código único; CHECK de tipo e capacidade |
| `professores` / `professor_competencias` | dados do docente; uma linha por competência | e-mail único; PK (professor, competência) |
| `materiais` | nome, categoria, patrimônio, quantidade total, restrito | patrimônio único (vários nulos permitidos) |
| `reservas` | sala, professor, solicitante, período, status, `versao` | FKs; CHECK `fim > inicio`; índices (sala_id, status, inicio) e (professor_id, status, inicio) |
| `reserva_itens` | materiais e quantidades da reserva | PK (reserva, material); FK com `ON DELETE CASCADE` |
| `reserva_motivos_aprovacao` | motivos que exigem aprovação, em ordem | PK (reserva, ordem) |
| `reserva_movimentacoes` | retiradas e devoluções | FKs para reserva, material e usuário |
| `bloqueios` | recurso, período e motivo | uma FK por tipo de recurso + CHECK que exige só a coluna do tipo informado |
| `auditoria` | data/hora, autor, ação, entidade, estado anterior/novo, correlationId | `BIGINT AUTO_INCREMENT` (ordem estável); **sem FK de propósito**, para o histórico sobreviver ao registro |
| `notificacoes` | destinatário, mensagem, status (SIMULADA/ENVIADA/FALHA) | FK do destinatário; FK da reserva com `ON DELETE SET NULL` |
| `conflitos_evitados` | tipo de conflito, recurso, período, solicitante | índice em `registrado_em` |

**Identificadores.** As entidades usam UUID versão 7 em texto (`VARCHAR(36)`). Ele é gerado pela
aplicação, não expõe a quantidade de registros (como faria um número sequencial) e é ordenável por
data de criação. Assim, o contrato da API, os testes Postman e o front-end continuaram os mesmos.

**Datas.** `DATETIME(6)` sem fuso, como `LocalDateTime` no Java: os horários de aula são sempre locais.

## 2.4 Fluxo de criação de reserva

```mermaid
sequenceDiagram
    participant C as Cliente
    participant RS as ReservaService
    participant PC as ProtecaoConcorrencia
    participant DB as MySQL (transação READ COMMITTED)
    participant N as NotificacaoService
    C->>RS: POST /api/reservas
    RS->>RS: valida perfil e horário (RN01, RN10)
    RS->>PC: executar(operação)
    loop até 15 tentativas
        PC->>DB: inicia transação
        RS->>DB: SELECT ... FOR UPDATE em sala, professor e materiais (ordem fixa)
        RS->>DB: lê reservas e bloqueios sobrepostos (RN02, RN04)
        RS->>DB: grava reserva + auditoria (RN08)
        alt deadlock ou espera de trava esgotada
            DB-->>PC: erro transitório → repete
        else sucesso
            DB-->>PC: commit
        end
    end
    DB-->>N: evento após commit
    N->>N: grava notificação e chama API externa
    RS-->>C: 201 Created (APROVADA ou SOLICITADA)
```

## 2.5 Proteção contra dupla reserva (RF06 / RN03)

O MySQL não tem restrição de exclusão de intervalos (como o `EXCLUDE` do PostgreSQL). A proteção usa
**travas pessimistas de linha** do InnoDB:

1. A transação executa `SELECT ... FOR UPDATE` nas linhas da sala, do professor e dos materiais
   envolvidos, **sempre na mesma ordem** (sala → professor → materiais por id), o que evita deadlocks.
2. Uma segunda transação que queira o mesmo recurso **espera** a primeira terminar.
3. Com isolamento **READ COMMITTED**, quando ela continua, a consulta de sobreposição já enxerga a
   reserva confirmada pela primeira e a solicitação é recusada com `409`.
4. Se mesmo assim ocorrer deadlock ou o tempo de espera esgotar, o `ProtecaoConcorrencia` repete a
   transação (até 15 vezes, com espera aleatória).

O mesmo mecanismo protege a aprovação de reservas e a criação de bloqueios de manutenção.

Evidência: `ConcorrenciaReservaIT` dispara 20 solicitações simultâneas e verifica que **exatamente uma**
é aceita (também para o mesmo professor e para o estoque de materiais).

Alterações simultâneas da mesma reserva são protegidas por **bloqueio otimista** (`@Version`).

## 2.6 Decisões e alternativas consideradas

| Decisão | Alternativa | Motivo da escolha |
|---|---|---|
| MySQL relacional com FKs e CHECK | MongoDB (versão anterior do projeto) | Pedido do professor; a integridade passa a ser garantida pelo banco, mesmo fora da API |
| `SELECT ... FOR UPDATE` em ordem fixa | Trava distribuída (Redis), fila única, coluna de versão | Sem infraestrutura extra; garantia dada pelo próprio banco |
| Flyway + `ddl-auto=validate` | `ddl-auto=update` | Esquema revisável e versionado no Git; nada muda no banco sem migração |
| JWT stateless | Sessão com cookie | API consumida por front-end e ferramentas (JMeter); dispensa CSRF |
| Notificação após o commit | Notificar dentro da transação | Nunca notificar algo que foi desfeito; falha externa não desfaz a reserva |
| Relógio injetado (`Clock`) | `LocalDateTime.now()` direto | Regras de tempo testáveis de forma determinística |
| Angular compilado pelo Maven e servido pelo Spring | Servir o Angular à parte (`ng serve` em :4200) | Uma única aplicação para rodar; não depende do Node instalado na máquina; mesma origem, então não precisa de CORS |
