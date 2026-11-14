# Alocação de Salas, Professores e Materiais

Sistema para organizar a alocação de **salas, professores e materiais sem conflitos de horário**,
desenvolvido para a disciplina de **Qualidade de Software**. O foco é apresentar evidências objetivas de
**qualidade, segurança, rastreabilidade e desempenho**.

## Stack

| Camada | Tecnologia |
|---|---|
| Back-end / API REST | Java 21, Spring Boot 4 (Web MVC, Security, Validation, Data JPA / Hibernate) |
| Banco de dados | MySQL 8.4 (relacional, com FKs e CHECK), esquema versionado com Flyway |
| Interface web | Angular 21 + TypeScript (pasta `frontend/`), compilada pelo Maven e servida pelo Spring Boot |
| Documentação da API | OpenAPI 3 / Swagger UI (springdoc) |
| Testes | JUnit 5, Mockito, AssertJ, Testcontainers, WireMock, REST Assured, Cucumber, Selenium, JMeter |
| Qualidade | JaCoCo, SonarCloud, GitHub Actions |

## Como executar

**Pré-requisitos:** Java 21 e MySQL 8 (instalado na máquina ou via Docker). O Maven não é obrigatório,
porque o projeto inclui o wrapper `./mvnw`.

| Para | Precisa de |
|---|---|
| Rodar a aplicação (`spring-boot:run`) | Java 21 + MySQL (Opção A ou B abaixo) |
| Opção B e `docker compose --profile app` | Docker com o plugin **Compose v2** (comando `docker compose`, com espaço) |
| `./mvnw verify` sem variáveis | **Docker em execução**, acessível pelo usuário (os testes de integração sobem o MySQL com Testcontainers). Sem Docker, use `MYSQL_URL_TESTE` (seção Testes) |
| Testes E2E (Selenium), incluídos no `verify` | Google Chrome instalado. Sem Chrome, use `./mvnw verify -Psem-e2e` |
| Primeira execução | Internet (dependências do Maven, Node e pacotes npm) |
| Portas livres | 8080 (aplicação), 3306 (MySQL) e 8089 (simulador de notificações) |

**Node.js e Angular CLI não são necessários.** Durante o build, o Maven baixa uma cópia própria do
Node 22.22.1 e do npm 11.6.2 em `target/node`, compila o Angular e coloca o resultado dentro do `.jar`.
A versão de Node instalada no computador é ignorada (o Angular 21 exige Node 20.19+ ou 22.12+, e uma
versão mais antiga, como a 22.11, impede o `npm start`). Só a primeira execução precisa de internet
para esse download.

### Banco de dados (MySQL)

A aplicação conecta, por padrão, em `jdbc:mysql://localhost:3306/alocacao` com o usuário `alocacao` /
senha `alocacao`. As tabelas são criadas automaticamente pelo **Flyway** na primeira execução.

**Opção A: MySQL instalado na máquina** (Workbench, extensão do VS Code etc.). Crie uma única vez o
usuário e os bancos de desenvolvimento e de teste:

```sql
CREATE DATABASE IF NOT EXISTS alocacao;
CREATE DATABASE IF NOT EXISTS alocacao_teste;
CREATE USER IF NOT EXISTS 'alocacao'@'localhost' IDENTIFIED BY 'alocacao';
GRANT ALL PRIVILEGES ON alocacao.* TO 'alocacao'@'localhost';
GRANT ALL PRIVILEGES ON alocacao_teste.* TO 'alocacao'@'localhost';
```

No Linux, com o `root` autenticando pelo sistema: `sudo mysql < arquivo.sql`.

**Opção B: Docker.** `docker compose up -d mysql wiremock` sobe o MySQL já com esse usuário. Se a porta
3306 estiver ocupada por um MySQL local, use `MYSQL_PORTA=3307 docker compose up -d mysql` e
`DB_URL=jdbc:mysql://localhost:3307/alocacao`.

Outra conexão pode ser configurada pelas variáveis `DB_URL`, `DB_USUARIO` e `DB_SENHA`.

### Aplicação

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

- Interface web (Angular): <http://localhost:8080>
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
Para rodar, use a configuração **AlocacaoApplication (dev)**, que já vem no projeto (pasta `.run/`).
Antes de iniciar, ela executa `mvn process-resources`, que compila o Angular. Sem essa etapa, o build
do próprio IntelliJ não gera o front-end e a página inicial fica em branco.

### Desenvolvendo o front-end

Para editar telas com recarga automática, rode a API (`./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`)
e, em outro terminal, `cd frontend && npm install && npm start` (exige Node 22.12+ instalado). Acesse
<http://localhost:4200>; as chamadas a `/api` são repassadas para `:8080`. Para compilar só o back-end,
sem o front, use `-Dfrontend.pular=true`.

## Testes

```bash
./mvnw test                  # unitários (não precisa de banco)
./mvnw verify                # todos: integração, API, concorrência, BDD, E2E + cobertura (exige Docker: MySQL via Testcontainers)
MYSQL_URL_TESTE='jdbc:mysql://localhost:3306/alocacao_teste' ./mvnw verify   # sem Docker, usando o MySQL local
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
| [RTM — Matriz de rastreabilidade](docs/rtm.md) | Requisito → testes (gerada automaticamente) |
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
├── config/        segurança, JWT, OpenAPI, relógio, dados de demonstração
├── comum/         tratamento de erros e utilitários
├── auth/ usuario/ autenticação e usuários
├── sala/ professor/ material/ bloqueio/   cadastros e manutenção
├── reserva/       núcleo: serviço, estados, disponibilidade, concorrência
│   └── regras/    regras puras (horário, sobreposição, pico de materiais, aprovação)
├── auditoria/ notificacao/ relatorio/
frontend/                    interface web em Angular (compilada para o .jar pelo Maven)
src/test/java/.../           unitários (*Test) e integração (*IT)
src/test/resources/features/ cenários BDD
src/test/jmeter/             plano de carga
postman/                     collection e environment do Postman
docs/                        documentação de qualidade
```
