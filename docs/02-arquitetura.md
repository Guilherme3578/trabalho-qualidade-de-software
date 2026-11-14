# 2. Arquitetura

## 2.0 Diagrama de contexto

Mostra o sistema como uma caixa só, com quem o usa e com que ele se comunica.

```mermaid
flowchart LR
    PROF([Professor]) -->|solicita, altera e cancela<br/>as próprias reservas| SIS
    COORD([Coordenador]) -->|reserva para professores,<br/>consulta relatórios| SIS
    RESP([Responsável pelos recursos]) -->|aprova ou rejeita reservas,<br/>registra retirada e devolução| SIS
    ADM([Administrador]) -->|cadastra salas, professores,<br/>materiais, usuários e bloqueios| SIS
    SIS[["Sistema de Alocação de Salas,<br/>Professores e Materiais<br/>(Spring Boot + Angular)"]]
    SIS -->|lê e grava reservas,<br/>cadastros e auditoria| DB[(MySQL 8.4)]
    SIS -->|HTTP POST JSON<br/>timeout de 2 s| NOT[API externa de notificação<br/>WireMock nos testes e no perfil dev]
    SIS -.->|aviso de aprovação pendente<br/>ou mudança de status| PROF
    SIS -.-> RESP
```

| Elemento | Tipo | Interação |
|---|---|---|
| Professor, Coordenador, Responsável, Administrador | Atores humanos (perfis do RF01) | Usam a interface Angular ou a API REST com token JWT |
| MySQL 8.4 | Sistema externo (banco) | Única fonte de dados; esquema criado pelo Flyway |
| API de notificação | Sistema externo (HTTP) | Recebe as notificações (RF11). Se falhar ou demorar, a reserva continua válida e a falha fica registrada |

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

## 2.4.1 Login e uso do token JWT (RF01, RNF02)

```mermaid
sequenceDiagram
    participant C as Cliente (Angular)
    participant AC as AuthController
    participant AS as AuthService
    participant L as LimitadorTentativasLogin
    participant DB as MySQL
    participant SEC as Spring Security
    C->>AC: POST /api/auth/login {email, senha}
    AC->>AS: login(req)
    AS->>L: verificar(email)
    alt 5 falhas nos últimos 15 min
        L-->>C: 429 Too Many Requests
    end
    AS->>DB: busca usuário por e-mail
    AS->>AS: BCrypt.matches (hash fictício se o e-mail não existe)
    alt e-mail inexistente, senha errada ou usuário inativo
        AS->>L: registrarFalha(email)
        AS->>DB: auditoria LOGIN_FALHA
        AS-->>C: 401 "E-mail ou senha inválidos."
    else credenciais válidas
        AS->>L: limpar(email)
        AS->>DB: auditoria LOGIN_SUCESSO
        AS-->>C: 200 {token HS256 (uid, perfil, exp = 2 h)}
    end
    Note over C,SEC: Requisições seguintes
    C->>SEC: GET /api/... Authorization: Bearer token
    SEC->>SEC: valida assinatura, expiração e emissor (iss)
    SEC->>DB: carrega o usuário pelo uid (perfil atual)
    alt token inválido, usuário inativo ou removido
        SEC-->>C: 401
    else perfil sem permissão na rota
        SEC-->>C: 403
    else autorizado
        SEC-->>C: resposta do controller
    end
```

O perfil vem do banco a cada requisição, e não do token. Assim, desativar um usuário ou mudar o perfil
vale na hora (defeito [DEF-002](defeitos/DEF-002.md)).

## 2.4.2 Aprovação de reserva com recurso restrito (RF07, RN04, RN05)

```mermaid
sequenceDiagram
    participant R as Responsável
    participant RC as ReservaController
    participant RS as ReservaService
    participant PC as ProtecaoConcorrencia
    participant DB as MySQL
    participant N as NotificacaoService
    R->>RC: POST /api/reservas/{id}/aprovar {observacao}
    RC->>RC: Spring Security exige perfil RESPONSAVEL (senão 403)
    RC->>RS: aprovar(id, observacao, responsável)
    RS->>PC: executar(operação)
    PC->>DB: inicia transação
    RS->>DB: busca a reserva (404 se não existir)
    RS->>DB: SELECT ... FOR UPDATE em sala, professor e materiais
    RS->>DB: há bloqueio de manutenção no período?
    alt recurso em manutenção
        RS-->>R: 409 RECURSO_EM_MANUTENCAO
    else status atual não é SOLICITADA
        RS-->>R: 409 transição inválida (máquina de estados)
    else ok
        RS->>DB: status = APROVADA + auditoria TRANSICAO_ESTADO
        RS->>RS: publica ReservaStatusAlteradoEvent
        DB-->>PC: commit
        DB-->>N: evento após commit
        N->>N: notifica o solicitante (RESERVA_APROVADA)
        RS-->>R: 200 reserva APROVADA
    end
```

A revalidação da manutenção na aprovação corrige o defeito [DEF-003](defeitos/DEF-003.md).

## 2.4.3 Dupla reserva em solicitações simultâneas (RF06, RN03)

```mermaid
sequenceDiagram
    participant A as Usuário A
    participant B as Usuário B
    participant RS as ReservaService
    participant DB as MySQL (READ COMMITTED)
    par mesmo horário, mesma sala
        A->>RS: POST /api/reservas (sala 101, 08h–10h)
    and
        B->>RS: POST /api/reservas (sala 101, 08h–10h)
    end
    RS->>DB: [A] SELECT sala 101 FOR UPDATE
    DB-->>RS: [A] trava obtida
    RS->>DB: [B] SELECT sala 101 FOR UPDATE
    Note over DB: B fica esperando a trava de A
    RS->>DB: [A] consulta sobreposição → nenhuma
    RS->>DB: [A] INSERT reserva + COMMIT (libera a trava)
    RS-->>A: 201 Created
    DB-->>RS: [B] trava obtida
    RS->>DB: [B] consulta sobreposição → enxerga a reserva de A
    RS->>DB: [B] ROLLBACK
    RS->>DB: [B] grava em conflitos_evitados (fora da transação desfeita, RF12)
    RS-->>B: 409 CONFLITO_SALA
    Note over RS,DB: Em deadlock ou espera esgotada, ProtecaoConcorrencia repete a transação (até 15 vezes)
```

Evidência: `ConcorrenciaReservaIT` (20 pedidos simultâneos → 1 aceito; 10 requisições HTTP → um 201 e
nove 409; estoque de 3 projetores → exatamente 3 aceitos).

## 2.4.4 Notificação para a API externa (RF11)

```mermaid
sequenceDiagram
    participant RS as ReservaService
    participant DB as MySQL
    participant N as NotificacaoService
    participant G as HttpNotificacaoGateway
    participant EXT as API externa
    RS->>DB: commit da reserva (status mudou)
    DB-->>N: ReservaStatusAlteradoEvent (AFTER_COMMIT, nova transação)
    alt novo status = SOLICITADA
        N->>DB: busca os usuários RESPONSAVEL ativos
    else outro status
        N->>DB: busca o solicitante
    end
    loop para cada destinatário
        N->>G: enviar(notificação)
        alt notificação desabilitada (padrão)
            G-->>N: SIMULADA (só registra em log)
        else habilitada
            G->>EXT: POST {destinatario, tipo, mensagem, reservaId}
            alt HTTP 2xx
                EXT-->>G: ok
                G-->>N: ENVIADA
            else erro HTTP, rede ou timeout (2 s)
                G-->>N: exceção → FALHA (com o motivo)
            end
        end
        N->>DB: grava a notificação com o status
    end
```

A notificação roda **depois** do commit e numa transação própria: uma falha externa nunca desfaz a
reserva. Evidência: `NotificacaoWireMockIT` (contrato JSON, erro 500 e resposta acima do timeout).

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
