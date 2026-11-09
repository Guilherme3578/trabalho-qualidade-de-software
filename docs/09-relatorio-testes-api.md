# 9. Relatório de testes de API (Postman)

> Gerado automaticamente por `scripts/relatorio_postman.py` a partir da execução da collection `postman/alocacao-salas.postman_collection.json` com o ambiente `postman/local.postman_environment.json`.

## 9.1 Resumo

**Resultado: ✅ APROVADO**

| Item | Valor |
|---|---|
| Data da execução | 08/11/2026 23:28:32 |
| Duração total | 3.7 s |
| Requisições executadas | 59 (0 com falha) |
| Asserções verificadas | 124 (0 com falha) |
| Scripts de teste | 59 (0 com falha) |
| Scripts de pré-requisição | 3 (0 com falha) |
| Tempo de resposta médio | 45 ms (mín. 5 ms, máx. 441 ms) |

## 9.2 Resultado por pasta

| Pasta | Requisições | Asserções aprovadas | Situação |
|---|---|---|---|
| 01 Autenticação [RF01] | 7 | 17/17 | ✅ |
| 02 Cadastros [RF02] | 8 | 15/15 | ✅ |
| 03 Pesquisa [RF03] | 6 | 11/11 | ✅ |
| 04 Reservas e conflitos [RF04][RF05] | 13 | 31/31 | ✅ |
| 05 Aprovação de recursos restritos [RF07][RN06] | 7 | 17/17 | ✅ |
| 06 Manutenção [RF08][RN04] | 3 | 6/6 | ✅ |
| 07 Uso, retirada e devolução [RF09] (executar entre 07h e 21h30) | 8 | 16/16 | ✅ |
| 08 Relatórios, auditoria e notificações [RF10][RF11][RF12] | 7 | 11/11 | ✅ |

## 9.3 Falhas

Nenhuma falha. Todas as asserções passaram.

## 9.4 Requisições mais lentas

| Requisição | Rota | Tempo |
|---|---|---|
| Especificação OpenAPI [RF14] | `GET /v3/api-docs` | 441 ms |
| Login admin@senac.br | `POST /api/auth/login` | 296 ms |
| Criar reserva → 201 APROVADA | `POST /api/reservas` | 115 ms |
| Login com senha errada → 401 genérico | `POST /api/auth/login` | 89 ms |
| ADMIN cria sala | `POST /api/salas` | 87 ms |

## 9.5 Detalhamento de todas as requisições

### 01 Autenticação [RF01]

| # | Requisição | Rota | Status | Tempo | Asserções |
|---|---|---|---|---|---|
| 1 | Login admin@senac.br | `POST /api/auth/login` | 200 OK | 296 ms | ✅ Status 200<br>✅ Retorna token Bearer |
| 2 | Login coordenador@senac.br | `POST /api/auth/login` | 200 OK | 83 ms | ✅ Status 200<br>✅ Retorna token Bearer |
| 3 | Login responsavel@senac.br | `POST /api/auth/login` | 200 OK | 78 ms | ✅ Status 200<br>✅ Retorna token Bearer |
| 4 | Login ana.souza@senac.br | `POST /api/auth/login` | 200 OK | 75 ms | ✅ Status 200<br>✅ Retorna token Bearer |
| 5 | Login com senha errada → 401 genérico | `POST /api/auth/login` | 401 Unauthorized | 89 ms | ✅ Status 401<br>✅ Código de erro CREDENCIAIS_INVALIDAS<br>✅ Resposta não expõe detalhes internos<br>✅ Mensagem genérica |
| 6 | Acesso sem token → 401 | `GET /api/salas` | 401 Unauthorized | 7 ms | ✅ Status 401<br>✅ Código de erro NAO_AUTENTICADO<br>✅ Resposta não expõe detalhes internos |
| 7 | Quem sou eu | `GET /api/auth/eu` | 200 OK | 42 ms | ✅ Status 200<br>✅ Perfil COORDENADOR |

### 02 Cadastros [RF02]

| # | Requisição | Rota | Status | Tempo | Asserções |
|---|---|---|---|---|---|
| 1 | ADMIN cria sala | `POST /api/salas` | 201 Created | 87 ms | ✅ Status 201 |
| 2 | ADMIN cria sala restrita | `POST /api/salas` | 201 Created | 14 ms | ✅ Status 201 |
| 3 | ADMIN cria professor | `POST /api/professores` | 201 Created | 45 ms | ✅ Status 201 |
| 4 | ADMIN cria segundo professor | `POST /api/professores` | 201 Created | 20 ms | ✅ Status 201 |
| 5 | ADMIN cria material | `POST /api/materiais` | 201 Created | 31 ms | ✅ Status 201 |
| 6 | Código de sala duplicado → 409 | `POST /api/salas` | 409 Conflict | 23 ms | ✅ Status 409<br>✅ Código de erro REGISTRO_DUPLICADO<br>✅ Resposta não expõe detalhes internos |
| 7 | Campos inválidos → 400 com mensagens [RNF03] | `POST /api/salas` | 400 Bad Request | 22 ms | ✅ Status 400<br>✅ Código de erro VALIDACAO<br>✅ Resposta não expõe detalhes internos<br>✅ Lista os campos inválidos |
| 8 | PROFESSOR tentando criar sala → 403 [RNF02] | `POST /api/salas` | 403 Forbidden | 16 ms | ✅ Status 403<br>✅ Código de erro ACESSO_NEGADO<br>✅ Resposta não expõe detalhes internos |

### 03 Pesquisa [RF03]

| # | Requisição | Rota | Status | Tempo | Asserções |
|---|---|---|---|---|---|
| 1 | Salas por tipo e capacidade | `GET /api/salas?tipo=SALA_AULA&capacidadeMinima=30` | 200 OK | 44 ms | ✅ Status 200<br>✅ Todas com capacidade >= 30 |
| 2 | Salas por localização | `GET /api/salas?localizacao=bloco%20p` | 200 OK | 21 ms | ✅ Status 200<br>✅ Encontra as salas do Bloco P |
| 3 | Salas disponíveis no horário | `GET /api/salas?inicio=2027-05-02T09:00&fim=2027-05-02T11:00` | 200 OK | 53 ms | ✅ Status 200 |
| 4 | Professores por competência | `GET /api/professores?competencia=qualidade%20de%20software` | 200 OK | 17 ms | ✅ Status 200<br>✅ Encontra pelo menos um |
| 5 | Materiais com quantidade disponível | `GET /api/materiais?inicio=2027-05-02T09:00&fim=2027-05-02T11:00` | 200 OK | 34 ms | ✅ Status 200<br>✅ Informa quantidadeDisponivel |
| 6 | Injeção no filtro é tratada como texto [RNF03] | `GET /api/salas?localizacao=.*` | 200 OK | 9 ms | ✅ Status 200<br>✅ Nenhum resultado indevido |

### 04 Reservas e conflitos [RF04][RF05]

| # | Requisição | Rota | Status | Tempo | Asserções |
|---|---|---|---|---|---|
| 1 | Criar reserva → 201 APROVADA | `POST /api/reservas` | 201 Created | 115 ms | ✅ Status 201<br>✅ Aprovada automaticamente |
| 2 | Mesma sala, horário sobreposto → 409 CONFLITO_SALA | `POST /api/reservas` | 409 Conflict | 45 ms | ✅ Status 409<br>✅ Código de erro CONFLITO_SALA<br>✅ Resposta não expõe detalhes internos<br>✅ Mensagem compreensível |
| 3 | Mesmo professor em outra sala → 409 CONFLITO_PROFESSOR | `POST /api/reservas` | 409 Conflict | 33 ms | ✅ Status 409<br>✅ Código de erro CONFLITO_PROFESSOR<br>✅ Resposta não expõe detalhes internos |
| 4 | Material esgotado no horário → 409 CONFLITO_MATERIAL | `POST /api/reservas` | 409 Conflict | 39 ms | ✅ Status 409<br>✅ Código de erro CONFLITO_MATERIAL<br>✅ Resposta não expõe detalhes internos |
| 5 | Horário adjacente (fim = início) → 201 | `POST /api/reservas` | 201 Created | 46 ms | ✅ Status 201 |
| 6 | Término antes do início → 422 [RN01] | `POST /api/reservas` | 422 Unprocessable Entity (WebDAV) (RFC 4918) | 13 ms | ✅ Status 422<br>✅ Código de erro HORARIO_INVALIDO<br>✅ Resposta não expõe detalhes internos<br>✅ Mensagem RN01 |
| 7 | Mais alunos que a capacidade → 422 [RN09] | `POST /api/reservas` | 422 Unprocessable Entity (WebDAV) (RFC 4918) | 23 ms | ✅ Status 422<br>✅ Código de erro CAPACIDADE_EXCEDIDA<br>✅ Resposta não expõe detalhes internos |
| 8 | Professor reservando para outro professor → 403 | `POST /api/reservas` | 403 Forbidden | 25 ms | ✅ Status 403<br>✅ Código de erro ACESSO_NEGADO<br>✅ Resposta não expõe detalhes internos |
| 9 | Alterar reserva (com versão) | `PUT /api/reservas/6af12fd19533faa700de3a3f` | 200 OK | 48 ms | ✅ Status 200 |
| 10 | Alterar com versão desatualizada → 409 | `PUT /api/reservas/6af12fd19533faa700de3a3f` | 409 Conflict | 15 ms | ✅ Status 409<br>✅ Código de erro VERSAO_DESATUALIZADA<br>✅ Resposta não expõe detalhes internos |
| 11 | Cancelar reserva adjacente | `POST /api/reservas/6af12fd29533faa700de3a46/cancelar` | 200 OK | 42 ms | ✅ Status 200<br>✅ CANCELADA |
| 12 | Apagar reserva não iniciada → 204 | `DELETE /api/reservas/6af12fd29533faa700de3a46` | 204 No Content | 26 ms | ✅ Status 204 |
| 13 | Minhas reservas | `GET /api/reservas?minhas=true` | 200 OK | 19 ms | ✅ Status 200 |

### 05 Aprovação de recursos restritos [RF07][RN06]

| # | Requisição | Rota | Status | Tempo | Asserções |
|---|---|---|---|---|---|
| 1 | Reserva em sala restrita → SOLICITADA | `POST /api/reservas` | 201 Created | 53 ms | ✅ Status 201<br>✅ Aguardando aprovação<br>✅ Informa o motivo |
| 2 | COORDENADOR tentando aprovar → 403 | `POST /api/reservas/6af12fd29533faa700de3a4e/aprovar` | 403 Forbidden | 10 ms | ✅ Status 403<br>✅ Código de erro ACESSO_NEGADO<br>✅ Resposta não expõe detalhes internos |
| 3 | ADMIN tentando aprovar → 403 | `POST /api/reservas/6af12fd29533faa700de3a4e/aprovar` | 403 Forbidden | 9 ms | ✅ Status 403<br>✅ Código de erro ACESSO_NEGADO<br>✅ Resposta não expõe detalhes internos |
| 4 | Pendentes de aprovação (RESPONSAVEL) | `GET /api/reservas?status=SOLICITADA` | 200 OK | 14 ms | ✅ Status 200 |
| 5 | RESPONSAVEL aprova | `POST /api/reservas/6af12fd29533faa700de3a4e/aprovar` | 200 OK | 42 ms | ✅ Status 200<br>✅ APROVADA |
| 6 | Aprovar de novo → 409 TRANSICAO_INVALIDA | `POST /api/reservas/6af12fd29533faa700de3a4e/aprovar` | 409 Conflict | 33 ms | ✅ Status 409<br>✅ Código de erro TRANSICAO_INVALIDA<br>✅ Resposta não expõe detalhes internos |
| 7 | Histórico auditável da reserva [RF10][RN08] | `GET /api/reservas/6af12fd29533faa700de3a4e/historico` | 200 OK | 14 ms | ✅ Status 200<br>✅ Registra criação e aprovação |

### 06 Manutenção [RF08][RN04]

| # | Requisição | Rota | Status | Tempo | Asserções |
|---|---|---|---|---|---|
| 1 | ADMIN bloqueia a sala para manutenção | `POST /api/bloqueios` | 201 Created | 37 ms | ✅ Status 201 |
| 2 | Reservar sala em manutenção → 409 | `POST /api/reservas` | 409 Conflict | 26 ms | ✅ Status 409<br>✅ Código de erro RECURSO_EM_MANUTENCAO<br>✅ Resposta não expõe detalhes internos<br>✅ Informa o motivo |
| 3 | ADMIN remove o bloqueio | `DELETE /api/bloqueios/6af12fd29533faa700de3a53` | 204 No Content | 27 ms | ✅ Status 204 |

### 07 Uso, retirada e devolução [RF09] (executar entre 07h e 21h30)

| # | Requisição | Rota | Status | Tempo | Asserções |
|---|---|---|---|---|---|
| 1 | Reserva começando em 10 min com material | `POST /api/reservas` | 201 Created | 50 ms | ✅ Status 201 |
| 2 | Iniciar uso → EM_USO | `POST /api/reservas/6af12fd39533faa700de3a57/iniciar` | 200 OK | 35 ms | ✅ Status 200<br>✅ EM_USO |
| 3 | Apagar reserva iniciada → 409 [RN07] | `DELETE /api/reservas/6af12fd39533faa700de3a57` | 409 Conflict | 18 ms | ✅ Status 409<br>✅ Código de erro RESERVA_INICIADA<br>✅ Resposta não expõe detalhes internos |
| 4 | Retirada acima do reservado → 422 | `POST /api/reservas/6af12fd39533faa700de3a57/retiradas` | 422 Unprocessable Entity (WebDAV) (RFC 4918) | 29 ms | ✅ Status 422<br>✅ Código de erro MOVIMENTACAO_INVALIDA<br>✅ Resposta não expõe detalhes internos |
| 5 | RESPONSAVEL registra retirada | `POST /api/reservas/6af12fd39533faa700de3a57/retiradas` | 200 OK | 29 ms | ✅ Status 200 |
| 6 | Concluir com material pendente → 409 | `POST /api/reservas/6af12fd39533faa700de3a57/concluir` | 409 Conflict | 21 ms | ✅ Status 409<br>✅ Código de erro MATERIAIS_PENDENTES<br>✅ Resposta não expõe detalhes internos |
| 7 | RESPONSAVEL registra devolução | `POST /api/reservas/6af12fd39533faa700de3a57/devolucoes` | 200 OK | 28 ms | ✅ Status 200 |
| 8 | Concluir → CONCLUIDA | `POST /api/reservas/6af12fd39533faa700de3a57/concluir` | 200 OK | 35 ms | ✅ Status 200<br>✅ CONCLUIDA |

### 08 Relatórios, auditoria e notificações [RF10][RF11][RF12]

| # | Requisição | Rota | Status | Tempo | Asserções |
|---|---|---|---|---|---|
| 1 | Relatório de utilização | `GET /api/relatorios/utilizacao?inicio=2027-05-02&fim=2027-05-02` | 200 OK | 30 ms | ✅ Status 200<br>✅ Tem salas e materiais |
| 2 | Carga horária por professor | `GET /api/relatorios/carga-horaria?inicio=2027-05-02&fim=2027-05-02` | 200 OK | 17 ms | ✅ Status 200 |
| 3 | Conflitos evitados (hoje) | `GET /api/relatorios/conflitos-evitados?inicio=2026-11-09&fim=2026-11-09` | 200 OK | 25 ms | ✅ Status 200<br>✅ Contabilizou os conflitos desta execução |
| 4 | PROFESSOR acessando relatório → 403 | `GET /api/relatorios/utilizacao?inicio=2027-05-02&fim=2027-05-02` | 403 Forbidden | 5 ms | ✅ Status 403<br>✅ Código de erro ACESSO_NEGADO<br>✅ Resposta não expõe detalhes internos |
| 5 | Auditoria (ADMIN) | `GET /api/auditoria?entidade=Reserva` | 200 OK | 16 ms | ✅ Status 200 |
| 6 | Notificações do coordenador | `GET /api/notificacoes` | 200 OK | 18 ms | ✅ Status 200 |
| 7 | Especificação OpenAPI [RF14] | `GET /v3/api-docs` | 200 OK | 441 ms | ✅ Status 200 |

## 9.6 Como reproduzir

1. Suba o MongoDB e o WireMock: `docker compose up -d mongo wiremock`.
2. Inicie a aplicação no perfil `dev`: `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`.
3. Execute a collection, pelo Collection Runner do Postman ou pela linha de comando:

```bash
npx newman@6.2.1 run postman/alocacao-salas.postman_collection.json \
  -e postman/local.postman_environment.json \
  --reporters cli,json --reporter-json-export target/newman.json
python3 scripts/relatorio_postman.py target/newman.json docs/09-relatorio-testes-api.md
```

A pasta 07 cria uma reserva que começa cerca de 10 minutos após a execução, então rode entre 07h e 21h30.
