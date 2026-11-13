# Checklist de entrega — avaliação externa

> Avaliação feita em 13/11/2026 sobre um clone limpo de
> `https://github.com/Guilherme3578/trabalho-qualidade-de-software`, branch `main` (commit `c7992e9`).
> Foi seguido apenas o README, com cache Maven novo (sem reaproveitar `~/.m2` nem o banco da máquina
> de desenvolvimento). Nada foi corrigido nesta etapa.

> **Atualização de 13/11/2026, depois da avaliação:**
> - Bloqueador 3 resolvido: o PR #4 entrou na `main` (`fd027c8`) com o CI todo verde, incluindo o JMeter.
>   A `main` agora serve o front Angular. Isso foi conferido num clone limpo, tanto pelo jar quanto pela
>   imagem Docker, com o mesmo bundle `main-BAKKS5VW.js` do build local.
> - Bloqueador 8 resolvido: o README agora lista Docker, Compose v2, Chrome, internet e as portas.
> - **Continuam abertos:** 1 (SonarCloud), 2 (revisão por pares; o PR #4 também foi mergeado sem
>   aprovação de outro integrante), 4 (diagramas), 5 (registro de defeitos), 6 (relatório final),
>   7 (`docs/rtm.md`) e 9 (rascunhos).

## 1. Execução seguindo o README

| # | Passo do README | Resultado | Observação |
|---|---|---|---|
| 1 | `git clone` | ✅ | Sem problemas. |
| 2 | Pré-requisitos "Java 21 e MySQL 8" | ⚠️ incompleto | O README não diz que `./mvnw verify` **exige Docker** (Testcontainers), nem que precisa do **Docker Compose v2** (Opção B) ou do **Google Chrome** (E2E com Selenium). |
| 3 | `./mvnw -B verify` sem Docker acessível | ❌ | 126 testes de integração com erro: `Could not find a valid Docker environment`. O README não avisa disso. |
| 4 | Opção A: SQL de criação do usuário e dos bancos | ✅ | O script funcionou como está escrito (testado num MySQL 8.4 próprio do avaliador na porta 3307, porque a 3306 estava ocupada). |
| 5 | `MYSQL_URL_TESTE=... ./mvnw -B verify` (sem Docker) | ✅ | 136 testes unitários e 126 de integração aprovados, cobertura atingida, 1 min 53 s. |
| 6 | `./mvnw -B verify` com Docker acessível | ✅ | 262 testes aprovados, `All coverage checks have been met`, JaCoCo: **86,4% de linhas / 73,2% de desvios**, 2 min 57 s. |
| 7 | Opção B: `docker compose up -d mysql wiremock` | ❌ | `docker: unknown command: docker compose`. O plugin Compose v2 não está instalado e não aparece nos pré-requisitos. O avaliador reproduziu os serviços com `docker run` (mesmas imagens, variáveis e portas). |
| 8 | `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev` | ✅ | `/actuator/health` UP, `/swagger-ui.html` 200, `/v3/api-docs` com 32 caminhos, login `admin@senac.br / Senha123` devolve JWT. |
| 9 | Interface web em `http://localhost:8080` | ⚠️ | A `main` serve o **front antigo em HTML/JS** (`static/index.html`). O Angular de `frontend/` não é compilado nem servido, e o README não explica como rodá-lo. A correção está no PR #4, ainda aberto. |
| 10 | `./mvnw verify -Pdesempenho` (com a aplicação rodando) | ✅ | 2.004 requisições, 0% de erro, média 38 ms, p95 64 ms (meta ≤ 500 ms). |
| 11 | `python3 scripts/rastreabilidade.py` | ✅ | Regenerar a matriz não alterou nada: ela está em dia com os testes. |
| 12 | `docker compose --profile app up -d --build` | não testado | Depende do Compose v2 (passo 7). |

## 2. Entregáveis da especificação

Legenda: ✅ sim · ⚠️ parcial · ❌ não

| Item | Existe | Atualizado | Caminho |
|---|---|---|---|
| Repositório com revisão por pares | ⚠️ repositório sim; revisão **não** | ❌ | GitHub: 4 PRs (#1–#4), todos abertos **e** mergeados por `Guilherme3578`, com 0 aprovações (o único review, no PR #3, é um comentário do próprio autor). A `main` não tem proteção de branch, e 18 commits entraram direto na `main`, sem PR. |
| Aplicação executável | ✅ | ⚠️ | `./mvnw spring-boot:run` funciona, mas a `main` serve o front antigo (o Angular depende do PR #4). |
| README | ✅ | ⚠️ | `README.md`: faltam pré-requisitos (Docker obrigatório para `verify`, Compose v2, Chrome, Node para o Angular), e a stack ainda descreve a interface como "HTML + CSS + JavaScript". |
| Documentação da API | ✅ | ✅ | Swagger em `/swagger-ui.html` e `/v3/api-docs` (springdoc), mais `postman/alocacao-salas.postman_collection.json`. |
| `docs/rtm.md` | ❌ (no caminho pedido) | ✅ (conteúdo) | O arquivo não existe. A matriz está em `docs/04-matriz-rastreabilidade.md`, gerada por `scripts/rastreabilidade.py`, e está em dia. |
| Diagrama de contexto | ❌ | — | Não existe diagrama de contexto (sistema × atores × sistemas externos). |
| Diagrama de componentes | ⚠️ | ⚠️ | `docs/02-arquitetura.md` §2.1 (flowchart Mermaid). Ainda mostra o navegador como "HTML/CSS/JS". Há também o modelo de dados (erDiagram, §2.3). |
| Diagramas de sequências críticas | ⚠️ (só 1) | ✅ | `docs/02-arquitetura.md` §2.4: só a criação de reserva. Faltam outras sequências críticas, como login/JWT, aprovação, dupla reserva concorrente e notificação externa. |
| Plano de testes | ✅ | ⚠️ | `docs/03-plano-de-testes.md`, ainda marcado "📝 Rascunho para revisão". |
| Relatório de testes | ✅ | ✅ | `docs/10-relatorio-testes-backend.md` (262/262, 86,4%/73,2%, bate com a execução do avaliador); `docs/09-relatorio-testes-api.md` (Postman, 59 requisições / 124 asserções, de 09/11); `docs/12-relatorio-tecnico.md`. |
| Pipeline de CI | ✅ | ⚠️ | `.github/workflows/` (CI + Desempenho). O CI passa na `main` e no PR #4. O **último run do JMeter no PR #4 falhou**. A correção (`1cbdc13`) existe só na máquina local, sem push. |
| Relatório do SonarCloud | ❌ | ❌ | Nunca rodou: o passo "Análise SonarCloud" aparece como `skipped` em todos os runs (falta o secret `SONAR_TOKEN`), e o `pom.xml` ainda usa `SUA_ORGANIZACAO`. Em `docs/08-ci-analise-estatica.md` §8.4, as evidências continuam sem marcar. |
| Relatório do JaCoCo | ⚠️ | ✅ | É gerado em `target/site/jacoco/` e publicado como artefato `relatorios-de-teste` no CI, com o resumo em `docs/10-relatorio-testes-backend.md` §10.5. Não há print nem cópia versionada (§8.4 sem marcar). Não há cobertura do front Angular. |
| Plano do teste de carga | ✅ | ✅ | `src/test/jmeter/carga-reservas.jmx`, cenário em `docs/06-desempenho.md` §6.2–6.3. |
| Resultado do teste de carga | ✅ | ⚠️ | `docs/06-desempenho.md` §6.4 (MySQL, 10/11). O cabeçalho diz "preenchida com a execução de 08/11", o que contradiz a seção, e o documento segue marcado como rascunho. |
| Registro de defeitos | ⚠️ | ⚠️ | Não há documento próprio. Os defeitos estão espalhados em `docs/11-revisao-de-codigo.md` §11.2, `docs/12-relatorio-tecnico.md` §12.5 e nas mensagens de commit, sem ID único, severidade ou status. |
| Relatório final de qualidade | ⚠️ | ⚠️ | O mais próximo é `docs/12-relatorio-tecnico.md` (técnico e de testes). Falta consolidar SonarCloud/Quality Gate, revisão por pares, defeitos e conclusão final. |

### Outras observações

- Oito documentos ainda estão marcados "📝 Rascunho para revisão": 01, 02, 03, 05, 06, 07, 08 e 11.
- O branch local `fix/servir-angular-pelo-spring` **divergiu** do remoto (5 à frente, 4 atrás: os mesmos commits com hashes novos, mais `1cbdc13`). Publicar vai exigir push forçado no PR #4.
- `docs/05-seguranca.md` tem só 27 linhas.
- Em `docs/07-tdd-bdd.md` §7.3, a evidência de TDD depende da ordem `test:` → `fix:`/`feat:` no Git. Isso não foi conferido commit a commit nesta etapa.

## 3. Bloqueadores, em ordem de prioridade

1. **SonarCloud nunca executado.** Não há relatório nem Quality Gate. É preciso criar a organização e o projeto, ajustar `sonar.organization`/`sonar.projectKey` no `pom.xml` e cadastrar o secret `SONAR_TOKEN`.
2. **Sem revisão por pares.** Os PRs foram mergeados pelo próprio autor, sem aprovação, a `main` não tem proteção e há commits direto na `main`. Precisa de revisão de outro integrante (há um segundo autor no histórico, `K1byl_`) e de proteção de branch exigindo review e CI.
3. **A `main` serve o front antigo.** O PR #4 está aberto, o último run do JMeter nele falhou, e a correção local ainda não foi enviada (o branch divergiu e vai exigir push forçado).
4. **Faltam diagramas:** nenhum diagrama de contexto e só uma sequência crítica.
5. **Falta o registro de defeitos** como documento próprio, com ID, severidade, status e vínculo com o commit de teste e de correção.
6. **Falta o relatório final de qualidade** consolidado (o `12-relatorio-tecnico.md` não cobre Sonar, revisão nem conclusão).
7. **`docs/rtm.md` não existe** no caminho pedido. O conteúdo está em `docs/04-matriz-rastreabilidade.md`.
8. **O README não leva sozinho a um `verify` verde:** faltam Docker (obrigatório), Compose v2, Chrome e Node/Angular nos pré-requisitos, e a stack está desatualizada.
9. **Documentos ainda marcados como rascunho** (8 arquivos) e evidências de §8.4 sem marcar (prints do CI, SonarCloud e JaCoCo).
