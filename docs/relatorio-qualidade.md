# Relatório final de qualidade

| Item | Valor |
|---|---|
| Sistema | Alocação de Salas, Professores e Materiais |
| Disciplina | Qualidade de Software, SENAC, 4º semestre |
| Data | 13/11/2026 |
| Base avaliada | `main` em `0d4a652` (PRs #10, #11 e #12 mergeados), base do release `v1.0.0-rc2`. A primeira versão deste relatório avaliou `bbb946e` para a `v1.0.0-rc1` |
| Documentos consolidados | [rtm](rtm.md), [defeitos](defeitos/README.md), [desempenho](performance/resultado-01.md), [09](09-relatorio-testes-api.md), [10](10-relatorio-testes-backend.md), [12](12-relatorio-tecnico.md), [decisão de qualidade](decisao-qualidade.md), [checklist de entrega](entrega/checklist.md) |

## 1. Conclusão

**O sistema atende aos requisitos funcionais e não funcionais especificados, com evidência objetiva e
reproduzível. A versão está apta para avaliação acadêmica e homologação, não para produção.**

- **Produto:** 281 testes automatizados no back-end e 200 no front passam, a cobertura do back-end está acima das metas, nenhuma
  dupla reserva foi aceita nos testes de concorrência e de carga, e todos os 31 requisitos rastreados
  têm pelo menos um teste. Os 14 defeitos registrados estão fechados; todos
  tiveram a correção registrada no Git, e os de código seguiram a ordem teste → correção.
- **Processo:** o CI roda em todo PR e a `main` está protegida. O trabalho foi feito por **uma única
  pessoa**, então não existe revisão por pares: esse risco foi aceito e compensado em parte pelo CI
  obrigatório, pelo Quality Gate do SonarCloud e pela auto-revisão registrada em
  [11-revisao-de-codigo.md](11-revisao-de-codigo.md). A análise completa do SonarCloud na `main` (`0d4a652`)
  passou no Quality Gate.

## 2. Estratégia de testes

| Nível | Ferramentas | O que verifica |
|---|---|---|
| Unitário | JUnit 5 (parametrizados), Mockito, AssertJ | Regras puras de `reserva.regras`, máquina de estados, limitador de login, repetição em deadlock |
| Integração | Testcontainers (MySQL 8.4), WireMock | Serviços com banco real, integridade relacional (FKs, CHECK), notificação externa com erro e timeout |
| Concorrência | `ExecutorService` + `CountDownLatch` | Dupla reserva com 20 pedidos simultâneos (sala, professor, estoque, via HTTP) |
| API caixa-preta | REST Assured; Postman/Newman | Contrato HTTP, códigos de status, autorização por perfil, ataques comuns |
| BDD | Cucumber | Retirada e devolução de materiais (RF09) escritos em Gherkin |
| E2E | Selenium (Chrome headless) | Fluxos no Angular servido pelo Spring, incluindo CSP e layout em 390 px |
| Front-end | Vitest | Serviços, guards, interceptor e componentes do Angular |
| Desempenho | JMeter | 20 usuários × 25 iterações com metas de erro, tempo máximo e p95 |

Detalhes em [03 — plano de testes](03-plano-de-testes.md) e [07 — TDD e BDD](07-tdd-bdd.md).

## 3. Resultados

| Suíte | Execução | Resultado | Fonte |
|---|---|---|---|
| Back-end (unitário, integração, API, concorrência, BDD, E2E) | CI da `main`, 14/11/2026, run 43556658613 (`0d4a652`) | **281 testes (137 unitários + 144 de integração), 0 falhas** | `target/*-reports`, [rtm](rtm.md) |
| Back-end num clone limpo | Avaliador externo, 13/11/2026 (antes do PR #4) | 262 testes, 0 falhas | [checklist](entrega/checklist.md) §1, passo 6 |
| CI da `main` | GitHub Actions, run 43556658613 (`0d4a652`) | ✅ verde | `gh run list --branch main` |
| API pela collection Postman | Newman 6.2.1, 09/11/2026 | 59 requisições, 124 asserções, 0 falhas | [09](09-relatorio-testes-api.md) |
| Front-end | Vitest, 14/11/2026, `main` em `0d4a652` | 200 testes em 17 arquivos, 0 falhas | `npm run test:ci` |
| Carga | JMeter, 09/11/2026 | 2.004 requisições, 0% de erro, p95 52 ms | [performance/resultado-01](performance/resultado-01.md) |

### Cobertura (JaCoCo, back-end)

| Métrica | Medido (14/11, `main` em `0d4a652`) | Meta | Situação |
|---|---|---|---|
| Linhas | 89,7% | ≥ 80% | ✅ |
| Desvios (branches) | 75,3% | ≥ 70% | ✅ |

O build falha abaixo das metas (`jacoco:check`).

### Cobertura (Vitest + V8, front-end)

| Métrica | Medido (14/11, `main` em `0d4a652`) | Meta | Situação |
|---|---|---|---|
| Linhas | 84,3% | ≥ 80% | ✅ |
| Desvios (branches) | 90,1% | ≥ 80% | ✅ |
| Funções | 85,9% | ≥ 80% | ✅ |
| Instruções | 85,9% | ≥ 80% | ✅ |

Medida em `src/app/**/*.ts`, sem specs, rotas, `app.config.ts` e `models/` (PR #11). `npm run test:ci`
falha abaixo de 80%, e o CI publica o relatório no artifact `cobertura-frontend`.

### Análise estática (SonarCloud)

| Item | Situação |
|---|---|
| Configuração | Organização `guilherme3578`, projeto `Guilherme3578_trabalho-qualidade-de-software`, secret `SONAR_TOKEN` cadastrado em 13/11 (PR #8) |
| Quality Gate no PR #8 | ✅ **Aprovado**: 0 bugs, 0 vulnerabilidades, 0 code smells, 0 security hotspots, 0% de duplicação no código novo, cobertura 84,1% |
| Análise completa da `main` | ✅ **Aprovada** em `0d4a652` (análise `9b2c1ce3`, 14/11): 0 bugs, 0 vulnerabilidades, 0 code smells, 0 security hotspots, 0% de duplicação, cobertura 86,9%. Os 12 apontamentos da primeira análise completa foram tratados no PR #10 |

### Desempenho

Todos os critérios de [06](06-desempenho.md) §6.3 atingidos: 0% de erro (meta < 1%), maior tempo 475 ms
(meta ≤ 1000 ms), p95 de 52 ms (meta ≤ 500 ms) e 500 de 500 tentativas conflitantes recusadas com `409`.
A medição é de uma máquina de desenvolvimento com 20 usuários; não indica o limite de saturação.

### Segurança

15 ameaças mapeadas com controle e teste ([05 — segurança](05-seguranca.md)), com base no OWASP Top 10 e
no OWASP API Security Top 10: autenticação JWT, autorização por perfil em dois níveis, BOLA, força bruta
(429), enumeração de usuários, injeção de SQL, atribuição em massa, vazamento em erros e cabeçalhos de
segurança. Quatro defeitos de segurança foram encontrados e corrigidos ([DEF-002](defeitos/DEF-002.md),
[DEF-005](defeitos/DEF-005.md), [DEF-013](defeitos/DEF-013.md) e [DEF-014](defeitos/DEF-014.md)). A CSP
não tem mais `'unsafe-inline'`: os estilos do Angular usam um nonce novo a cada requisição. Não houve teste dinâmico (DAST) nem teste de invasão.

## 4. Rastreabilidade

A [matriz](rtm.md) é gerada a partir dos próprios testes, que citam os requisitos no nome
(ex.: `[RF05][RN02]`). Resultado: **31 de 31 requisitos (RF, RN e RNF) com teste**. Rodar
`scripts/rastreabilidade.py` de novo não altera o arquivo, ou seja, a matriz está em dia com o código.

## 5. Defeitos

| Severidade | Encontrados | Fechados | Corrigidos | Abertos |
|---|---|---|---|---|
| Crítica | 0 | 0 | 0 | 0 |
| Alta | 6 | 6 | 0 | 0 |
| Média | 6 | 6 | 0 | 0 |
| Baixa | 2 | 2 | 0 | 0 |
| **Total** | **14** | **14** | **0** | **0** |

- Os 5 defeitos de código (DEF-001 a DEF-005) seguiram a ordem `test:` → `fix:`: primeiro um commit com
  o teste que falha, depois a correção.
- Quatro defeitos estavam na infraestrutura de teste, de build ou de CI (DEF-006, 007, 008 e 009). O mais
  grave foi o DEF-006: o teste de carga **nunca rodava**, e o CI ficava verde mesmo assim. Por isso a
  evidência de desempenho só vale a partir do PR #2.
- O DEF-008 foi encontrado na apresentação em sala, fora dos testes automatizados. Depois dele entrou um
  teste E2E que impede a regressão.
- DEF-010 a DEF-014 eram as pendências P1–P5 da revisão de código. Depois da decisão das regras
  ([11](11-revisao-de-codigo.md) §11.4), cada uma ganhou um teste que falhava e a correção, também na
  ordem `test:` → `fix:`. Entraram na `main` pelo PR #12 e estão fechados.

Registro completo: [docs/defeitos](defeitos/README.md).

## 6. Processo

| Prática | Situação | Evidência |
|---|---|---|
| CI em todo PR | ✅ | Workflows `CI` e `Desempenho (JMeter)` em todo PR; o CI também roda a cada push na `main` |
| Proteção da `main` | ✅ | Exige PR e o check `Build, testes e cobertura` verde. A exigência de aprovação foi retirada porque o projeto tem um único autor, e o GitHub não deixa o autor aprovar o próprio PR |
| Revisão por pares | ❌ (risco aceito) | Projeto individual: todos os PRs foram abertos e mergeados pelo único autor, e houve commits direto na `main` antes da proteção. Não há revisão independente para registrar |
| TDD / BDD | ✅ | Pares `test:` → `fix:` no histórico; cenários Cucumber em `src/test/resources/features/` |
| Commits rastreáveis | ✅ | Conventional Commits em português, com o porquê no corpo |
| Release versionado | ✅ | Tag `v1.0.0-rc1` publicada em `89f4292`; `v1.0.0-rc2` no commit de merge do PR de release. Nenhuma tag publicada é movida |

## 7. Situação dos critérios de qualidade

Atualização dos critérios C1–C13 da [decisão de qualidade](decisao-qualidade.md), que foi escrita com a
`main` em `c7992e9`.

| # | Critério | Antes | Agora | O que mudou |
|---|---|---|---|---|
| C1 | Suíte automatizada | ✅ | ✅ | 281 testes no back-end e 200 no front |
| C2 | Cobertura | ⚠️ | ✅ | Back-end 89,7% / 75,3%; front 84,3% / 90,1%, com meta de 80% no CI (PR #11) |
| C3 | Contrato da API | ✅ | ✅ | — |
| C4 | Concorrência | ✅ | ✅ | — |
| C5 | Desempenho | ⚠️ | ⚠️ | Continua só em ambiente local |
| C6 | CI em todo PR | ⚠️ | ✅ | JMeter verde desde o PR #4 |
| C7 | SonarCloud | ❌ | ✅ | Quality Gate aprovado na análise completa da `main` (`0d4a652`), depois do PR #10 |
| C8 | Revisão por pares | ❌ | ❌ | Projeto individual: não há outro revisor. Risco aceito (seção 8) |
| C9 | Rastreabilidade | ⚠️ | ✅ | Matriz em `docs/rtm.md` |
| C10 | README reproduzível | ⚠️ | ✅ | Pré-requisitos detalhados |
| C11 | Front-end atual | ❌ | ✅ | `main` serve o Angular (PR #4) |
| C12 | Segurança | ⚠️ | ⚠️ | Sem SAST completo nem DAST |
| C13 | Registro de defeitos | ❌ | ✅ | `docs/defeitos/` |

**Resumo:** 10 ✅, 2 ⚠️ e 1 ❌ (antes eram 4 ✅, 5 ⚠️ e 4 ❌).

## 8. Riscos e pendências

| Pendência | Impacto | Próximo passo |
|---|---|---|
| Sem revisão por pares (C8) | Médio: defeitos de lógica e de design podem passar sem uma segunda opinião | **Aceito**, porque o projeto tem um único autor. Controles compensatórios: CI obrigatório na `main`, Quality Gate do SonarCloud, ordem `test:` → `fix:` e auto-revisão em [11-revisao-de-codigo.md](11-revisao-de-codigo.md). Não equivalem a uma revisão independente |
| Carga só em máquina local (C5) | Desconhecido em produção | Repetir em ambiente parecido com produção e com mais usuários |

## 9. Recomendação

Liberar a versão `v1.0.0-rc2` **para avaliação e homologação**, como já registrado na
[decisão de qualidade](decisao-qualidade.md). Para produção, falta pelo menos: teste de carga em ambiente representativo e uma revisão independente do código,
que num projeto individual só pode vir de fora (por exemplo, do professor).
