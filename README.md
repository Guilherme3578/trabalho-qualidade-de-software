# Alocação de Salas, Professores e Materiais

Sistema para organizar a alocação de **salas, professores e materiais sem conflitos de horário**,
desenvolvido para a disciplina de **Qualidade de Software**. O foco é apresentar evidências objetivas de
**qualidade, segurança, rastreabilidade e desempenho**.

## Stack

| Camada | Tecnologia |
|---|---|
| Back-end / API REST | Java 21, Spring Boot 4 (Web MVC, Security, Validation, Data MongoDB) |
| Banco de dados | MongoDB 9 (replica set, transações multi-documento) |
| Interface web | HTML + CSS + JavaScript (responsiva, servida pelo Spring Boot) |
| Documentação da API | OpenAPI 3 / Swagger UI (springdoc) |
| Testes | JUnit 5, Mockito, AssertJ, Testcontainers, WireMock, REST Assured, Cucumber, Selenium, JMeter |
| Qualidade | JaCoCo, SonarCloud, GitHub Actions |

## Como executar

**Pré-requisitos:** Java 21 e Docker. O Maven não é obrigatório, porque o projeto inclui o wrapper `./mvnw`.

```bash
# 1. Subir MongoDB e o simulador da API de notificações
docker compose up -d mongo wiremock

# 2. Rodar a aplicação com dados de demonstração
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

- Interface web: <http://localhost:8080>
- Documentação da API (Swagger): <http://localhost:8080/swagger-ui.html>
- Requisições recebidas pelo simulador de notificações: <http://localhost:8089/__admin/requests>

Alternativa, tudo em containers: `docker compose --profile app up -d --build`

### Usuários de demonstração (perfil `dev`), senha `Senha123`

| E-mail | Perfil |
|---|---|
| `admin@senac.br` | ADMIN |
| `responsavel@senac.br` | RESPONSAVEL |
| `coordenador@senac.br` | COORDENADOR |
| `ana.souza@senac.br` | PROFESSOR |

### IntelliJ IDEA

*File → Open* → selecione a pasta do projeto (o IntelliJ importa o `pom.xml` automaticamente).
Para rodar: classe `AlocacaoApplication` com *Active profiles* = `dev`.

## Testes

```bash
./mvnw test                  # unitários (não precisa de Docker)
./mvnw verify                # todos: integração, API, concorrência, BDD, E2E + cobertura
./mvnw verify -Psem-e2e      # todos, exceto o navegador (Selenium)
./mvnw verify -Pdesempenho   # JMeter (com a aplicação rodando)
python3 scripts/rastreabilidade.py   # atualiza a matriz de rastreabilidade
```

Postman: importe `postman/alocacao-salas.postman_collection.json` e `postman/local.postman_environment.json`.

## Documentação

| Documento | Conteúdo |
|---|---|
| [01 — Requisitos](docs/01-requisitos.md) | Perfis, RF, RN, RNF com IDs e ciclo de vida da reserva |
| [02 — Arquitetura](docs/02-arquitetura.md) | Componentes, modelo de dados, fluxo de reserva, proteção contra dupla reserva |
| [03 — Plano de testes](docs/03-plano-de-testes.md) | Estratégia, níveis, técnicas, ambiente, critérios |
| [04 — Matriz de rastreabilidade](docs/04-matriz-rastreabilidade.md) | Requisito → testes (gerada automaticamente) |
| [05 — Segurança](docs/05-seguranca.md) | Ameaças × controles × evidências |
| [06 — Desempenho](docs/06-desempenho.md) | Cenário JMeter, metas e resultados |
| [07 — TDD e BDD](docs/07-tdd-bdd.md) | Funcionalidades desenvolvidas com cada técnica |
| [08 — CI e análise estática](docs/08-ci-analise-estatica.md) | GitHub Actions, JaCoCo, SonarCloud |
| [09 — Relatório de testes de API](docs/09-relatorio-testes-api.md) | Resultado da collection Postman (gerado automaticamente) |
| [10 — Relatório de testes de back-end](docs/10-relatorio-testes-backend.md) | JUnit, níveis da pirâmide e cobertura JaCoCo (gerado automaticamente) |
| [11 — Revisão de código](docs/11-revisao-de-codigo.md) | Achados da revisão e pendências de regra de negócio |
| [12 — Relatório técnico e de testes](docs/12-relatorio-tecnico.md) | Verificação consolidada de back-end, API, front-end, integração e desempenho |

## Estrutura

```
src/main/java/br/senac/alocacao/
├── config/        segurança, JWT, MongoDB, OpenAPI, relógio, dados de demonstração
├── comum/         tratamento de erros e utilitários
├── auth/ usuario/ autenticação e usuários
├── sala/ professor/ material/ bloqueio/   cadastros e manutenção
├── reserva/       núcleo: serviço, estados, disponibilidade, concorrência
│   └── regras/    regras puras (horário, sobreposição, pico de materiais, aprovação)
├── auditoria/ notificacao/ relatorio/
src/main/resources/static/   interface web
src/test/java/.../           unitários (*Test) e integração (*IT)
src/test/resources/features/ cenários BDD
src/test/jmeter/             plano de carga
postman/                     collection e environment do Postman
docs/                        documentação de qualidade
```
