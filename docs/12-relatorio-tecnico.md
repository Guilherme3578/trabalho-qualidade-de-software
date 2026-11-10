# 12. Relatório técnico e de testes

> Consolida a verificação completa feita em 08/11/2026 na branch `feat/frontend-angular`: back-end, API,
> front-end, integração entre eles e desempenho. Os números detalhados estão nos relatórios gerados
> automaticamente ([09 — API](09-relatorio-testes-api.md), [10 — back-end](10-relatorio-testes-backend.md))
> e em [06 — desempenho](06-desempenho.md).
>
> **Atualização de 10/11/2026 (branch `feat/migracao-mysql`):** o banco passou de MongoDB para **MySQL 8.4**,
> após feedback do professor. Back-end, collection Postman e carga JMeter foram executados de novo sobre o
> MySQL, e os números abaixo são os novos. O front-end não foi alterado (a API manteve o mesmo contrato),
> então os resultados dele são os de 08/11.

## 12.1 Resultado geral

**✅ APROVADO: todas as suítes passaram, sem falhas.**

| Suíte | Ferramenta | Executado | Resultado |
|---|---|---|---|
| Unitários do back-end | JUnit 5 (Surefire) | 136 testes | ✅ 0 falhas |
| API caixa-preta, integração, integridade do banco, concorrência, BDD e E2E | JUnit 5, Testcontainers/MySQL real, WireMock, Cucumber, Selenium (Failsafe) | 126 testes | ✅ 0 falhas |
| Cobertura do back-end | JaCoCo | 86,4% de linhas, 73,2% de desvios | ✅ meta 80% / 70% |
| API pela collection | Postman (Newman 6.2.1) | 59 requisições, 124 asserções | ✅ 0 falhas |
| Unitários do front-end | Vitest (Angular) | 40 testes em 7 arquivos | ✅ 0 falhas |
| Build de produção do front-end | Angular CLI | 1 build | ✅ sem erros nem avisos |
| Integração front-end → API | Smoke test pelo proxy do Angular | 4 perfis × 12 rotas | ✅ respostas conforme o perfil |
| Carga | JMeter 5 | 2.004 requisições, 20 usuários | ✅ 0% de erro, p95 = 52 ms |

## 12.2 Visão técnica do sistema

| Camada | Tecnologia | Observação |
|---|---|---|
| Back-end | Java 21, Spring Boot 4.0.9, Spring Security (JWT HS256), springdoc | API REST em `/api/*`, erros em Problem Details (RFC 9457) |
| Banco | MySQL 8.4 (JPA/Hibernate 7, esquema versionado com Flyway) | FKs, chaves únicas e CHECK; travas `SELECT ... FOR UPDATE` contra dupla reserva (RF06) |
| Front-end | Angular 21 (standalone, signals, lazy loading), TypeScript 5.9, lucide-angular | Pasta `frontend/`, 13 páginas e 6 componentes reutilizáveis |
| Notificações | Cliente HTTP para API externa | Simulada com WireMock nos testes (RF11) |
| CI | GitHub Actions | Jobs de back-end (build, testes, JaCoCo, SonarCloud), front-end e JMeter em todo PR |

### Cobertura funcional do front-end

Cada endpoint do back-end tem um método de serviço no front, e cada método é usado por alguma tela
(verificado por varredura dos serviços e das páginas). Só `GET /api/auth/eu` e `GET /{id}` de
salas, professores e materiais ficam sem uso, porque as telas recebem esses dados por outras rotas.

| Requisito | Tela no front-end |
|---|---|
| RF01 Autenticação por perfil | Login, `authGuard`, `perfilGuard`, interceptor com logout no 401 |
| RF02/RF03 Cadastros e pesquisa | Salas, Professores, Materiais, Usuários; Disponibilidade |
| RF04/RF05 Reservas e conflitos | Nova reserva, Alterar, Lista e Detalhe de reserva |
| RF07 Aprovação | Aprovações (RESPONSAVEL) e ações no detalhe |
| RF08 Manutenção | Manutenções (bloqueios) |
| RF09 Retirada e devolução | Detalhe da reserva (RESPONSAVEL, reserva em uso) |
| RF10 Auditoria | Auditoria e histórico no detalhe da reserva |
| RF11 Notificações | Notificações, com marcação de lida |
| RF12 Relatórios | Relatórios de utilização, carga horária e conflitos evitados |
| RF13 Interface responsiva e mensagens claras | Layout responsivo; `MensagemService` traduz o Problem Details |

## 12.3 Integração front-end ↔ back-end

O front em `localhost:4200` encaminha `/api` para `localhost:8080` (`frontend/proxy.conf.json`).
Com o token de cada perfil, as rotas usadas pelas telas responderam assim:

| Perfil | Cadastros, reservas, bloqueios, notificações | Relatórios | Auditoria | Usuários |
|---|---|---|---|---|
| ADMIN | 200 | 200 | 200 | 200 |
| RESPONSAVEL | 200 | 200 | 200 | 403 |
| COORDENADOR | 200 | 200 | 403 | 403 |
| PROFESSOR | 200 | 403 | 403 | 403 |

As respostas 403 batem com o `SecurityConfig` e com os perfis exigidos pelo `perfilGuard` em
`frontend/src/app/app.routes.ts`, ou seja, o front esconde exatamente o que a API recusa.

## 12.4 Desempenho

| Requisição | Amostras | Média | p95 | p99 | Erros |
|---|---|---|---|---|---|
| Pesquisar salas disponíveis | 500 | 16 ms | 26 ms | 36 ms | 0% |
| Agenda do dia | 500 | 6 ms | 9 ms | 13 ms | 0% |
| Reserva conflitante (espera 409) | 500 | 14 ms | 22 ms | 29 ms | 0% |
| Relatório de utilização | 500 | 8 ms | 13 ms | 19 ms | 0% |

Nenhuma das 500 tentativas conflitantes virou reserva, o que confirma a RN03 também sob carga.

## 12.5 Defeitos encontrados nesta verificação

| # | Defeito | Impacto | Situação |
|---|---|---|---|
| D1 | O perfil Maven `desempenho` define `skipTests=true` para pular o JUnit, e o `jmeter-maven-plugin` também obedece a essa propriedade. O plano JMeter **nunca era executado**, nem no CI (log: `Performance tests are skipped`), e o check ficava verde mesmo assim. | Alto: a evidência de desempenho (RNF01) era falsa | ✅ Corrigido no `pom.xml` com `skipTests=false` no plugin |
| D2 | Com o plugin executando, o goal `jmeter` procurava a execução de configuração com id padrão `configuration`, mas a do projeto se chama `configuracao`. | Bloqueava a execução após a correção da D1 | ✅ Corrigido com `selectedConfiguration` |

Observações que não são defeitos do código:

- **Pasta 07 da collection Postman** (uso, retirada e devolução): ela cria uma reserva que começa em
  10 minutos, e a RN10 aceita reservas só entre 07h e 23h. Fora desse horário a pasta falha por
  definição. Esta execução foi feita às 23h30, então a API e o Newman rodaram com o mesmo fuso
  (`TZ=Asia/Tokyo`, 11h30 locais). Como o `Clock` da aplicação e o script do Postman usam o fuso da
  máquina, isso simula o horário comercial sem alterar código.
- **Não sobrescrever o `.jar` em uso.** Rodar `./mvnw verify` enquanto a aplicação roda a partir de
  `target/*.jar` substitui o arquivo aberto. As classes ainda não carregadas (por exemplo, o tratador
  de 403) deixam de carregar, e as requisições negadas ficam sem resposta. Para testes manuais, rode
  a aplicação a partir de uma cópia do `.jar` ou com `./mvnw spring-boot:run`.

## 12.6 Pendências conhecidas

| Pendência | Onde está |
|---|---|
| O front antigo (`src/main/resources/static/`) ainda é o servido em `:8080`; o Angular roda à parte em `:4200` e não entra no `.jar` nem no Docker | Integração a fazer |
| O teste E2E com Selenium (`FluxoReservaE2EIT`) cobre o front antigo, não o Angular | Adaptar após a integração |
| O front-end não mede cobertura (falta `@vitest/coverage-v8`) | Front-end |
| README, `02-arquitetura.md` e o RF13 em `01-requisitos.md` ainda descrevem o front sem framework | Documentação |
| Regras de negócio P1–P5 aguardando decisão | [11 — revisão de código](11-revisao-de-codigo.md) |

## 12.7 Como reproduzir

```bash
# MySQL local com o usuário alocacao e os bancos alocacao e alocacao_teste (ver README)
MYSQL_URL_TESTE='jdbc:mysql://127.0.0.1:3306/alocacao_teste' ./mvnw verify
python3 scripts/relatorio_backend.py .

cp target/alocacao-salas-*.jar /tmp/app.jar
TZ=Asia/Tokyo java -jar /tmp/app.jar --spring.profiles.active=dev --server.port=8081 &
TZ=Asia/Tokyo npx newman@6.2.1 run postman/alocacao-salas.postman_collection.json \
  -e postman/local.postman_environment.json --env-var baseUrl=http://localhost:8081 \
  -r cli,json --reporter-json-export newman.json
python3 scripts/relatorio_postman.py newman.json docs/09-relatorio-testes-api.md

TZ=Asia/Tokyo ./mvnw verify -Pdesempenho -Djmeter.port=8081

cd frontend && npm run test:ci && npm run build
```

`TZ=Asia/Tokyo` só é necessário quando a execução acontece fora do horário de 07h às 21h30.
