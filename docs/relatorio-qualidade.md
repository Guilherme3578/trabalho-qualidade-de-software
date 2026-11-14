# Relatório final de qualidade

| Item | Valor |
|---|---|
| Sistema | Alocação de Salas, Professores e Materiais |
| Disciplina | Qualidade de Software, SENAC, 4º semestre |
| Data | 13/11/2026 |
| Base avaliada | `main` em `bbb946e` (merge do PR #6), mais os PRs abertos #7 (release `v1.0.0-rc1`) e #8 (SonarCloud) |
| Documentos consolidados | [rtm](rtm.md), [defeitos](defeitos/README.md), [desempenho](performance/resultado-01.md), [09](09-relatorio-testes-api.md), [10](10-relatorio-testes-backend.md), [12](12-relatorio-tecnico.md), [decisão de qualidade](decisao-qualidade.md), [checklist de entrega](entrega/checklist.md) |

## 1. Conclusão

**O sistema atende aos requisitos funcionais e não funcionais especificados, com evidência objetiva e
reproduzível. A versão está apta para avaliação acadêmica e homologação, não para produção.**

- **Produto:** 267 testes automatizados passam, a cobertura do back-end está acima das metas, nenhuma
  dupla reserva foi aceita nos testes de concorrência e de carga, e todos os 31 requisitos rastreados
  têm pelo menos um teste. Os 9 defeitos encontrados estão fechados, e os 6 de severidade alta tiveram
  teste e correção registrados no Git.
- **Processo:** o CI roda em todo PR e a `main` está protegida. Ainda faltam a **revisão por pares**
  (nenhum PR teve aprovação de outro integrante) e a **análise completa do SonarCloud na `main`**, que só
  acontece depois do merge do PR #8.

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
| Back-end (unitário, integração, API, concorrência, BDD, E2E) | Local, 12/11/2026, após as mudanças do PR #4 | **267 testes, 0 falhas** | `target/*-reports`, [rtm](rtm.md) |
| Back-end num clone limpo | Avaliador externo, 13/11/2026 (antes do PR #4) | 262 testes, 0 falhas | [checklist](entrega/checklist.md) §1, passo 6 |
| CI da `main` | GitHub Actions, run 43366617175 (`bbb946e`) | ✅ verde | `gh run list --branch main` |
| API pela collection Postman | Newman 6.2.1, 09/11/2026 | 59 requisições, 124 asserções, 0 falhas | [09](09-relatorio-testes-api.md) |
| Front-end | Vitest | 40 testes em 7 arquivos, 0 falhas | [12](12-relatorio-tecnico.md) §12.1 |
| Carga | JMeter, 09/11/2026 | 2.004 requisições, 0% de erro, p95 52 ms | [performance/resultado-01](performance/resultado-01.md) |

### Cobertura (JaCoCo, back-end)

| Métrica | Medido (12/11) | Meta | Situação |
|---|---|---|---|
| Linhas | 86,7% | ≥ 80% | ✅ |
| Desvios (branches) | 73,4% | ≥ 70% | ✅ |

O build falha abaixo das metas (`jacoco:check`). O front Angular **não tem cobertura medida**.

### Análise estática (SonarCloud)

| Item | Situação |
|---|---|
| Configuração | Organização `guilherme3578`, projeto `Guilherme3578_trabalho-qualidade-de-software`, secret `SONAR_TOKEN` cadastrado em 13/11 (PR #8) |
| Quality Gate no PR #8 | ✅ **Aprovado**: 0 bugs, 0 vulnerabilidades, 0 code smells, 0 security hotspots, 0% de duplicação no código novo, cobertura 84,1% |
| Análise completa da `main` | ⏳ Pendente: o PR #8 analisa só o código alterado (406 linhas). O resultado do projeto inteiro aparece depois do merge |

### Desempenho

Todos os critérios de [06](06-desempenho.md) §6.3 atingidos: 0% de erro (meta < 1%), maior tempo 475 ms
(meta ≤ 1000 ms), p95 de 52 ms (meta ≤ 500 ms) e 500 de 500 tentativas conflitantes recusadas com `409`.
A medição é de uma máquina de desenvolvimento com 20 usuários; não indica o limite de saturação.

### Segurança

15 ameaças mapeadas com controle e teste ([05 — segurança](05-seguranca.md)), com base no OWASP Top 10 e
no OWASP API Security Top 10: autenticação JWT, autorização por perfil em dois níveis, BOLA, força bruta
(429), enumeração de usuários, injeção de SQL, atribuição em massa, vazamento em erros e cabeçalhos de
segurança. Dois defeitos de segurança foram encontrados e corrigidos ([DEF-002](defeitos/DEF-002.md) e
[DEF-005](defeitos/DEF-005.md)). Não houve teste dinâmico (DAST) nem teste de invasão.

## 4. Rastreabilidade

A [matriz](rtm.md) é gerada a partir dos próprios testes, que citam os requisitos no nome
(ex.: `[RF05][RN02]`). Resultado: **31 de 31 requisitos (RF, RN e RNF) com teste**. Rodar
`scripts/rastreabilidade.py` de novo não altera o arquivo, ou seja, a matriz está em dia com o código.

## 5. Defeitos

| Severidade | Encontrados | Fechados | Abertos |
|---|---|---|---|
| Crítica | 0 | 0 | 0 |
| Alta | 6 | 6 | 0 |
| Média | 6 | 3 | 3 |
| Baixa | 2 | 0 | 2 |
| **Total** | **14** | **9** | **5** |

- Os 5 defeitos de código (DEF-001 a DEF-005) seguiram a ordem `test:` → `fix:`: primeiro um commit com
  o teste que falha, depois a correção.
- Quatro defeitos estavam na infraestrutura de teste, de build ou de CI (DEF-006, 007, 008 e 009). O mais
  grave foi o DEF-006: o teste de carga **nunca rodava**, e o CI ficava verde mesmo assim. Por isso a
  evidência de desempenho só vale a partir do PR #2.
- O DEF-008 foi encontrado na apresentação em sala, fora dos testes automatizados. Depois dele entrou um
  teste E2E que impede a regressão.
- Os 5 abertos (DEF-010 a DEF-014) dependem de decisão de regra de negócio e estão descritos em
  [11 — revisão de código](11-revisao-de-codigo.md) §11.4.

Registro completo: [docs/defeitos](defeitos/README.md).

## 6. Processo

| Prática | Situação | Evidência |
|---|---|---|
| CI em todo PR | ✅ | Workflows `CI` e `Desempenho (JMeter)` em todo PR; o CI também roda a cada push na `main` |
| Proteção da `main` | ✅ | Exige 1 aprovação e o check `Build, testes e cobertura` |
| Revisão por pares | ❌ | PRs #1–#6 mergeados pelo autor, sem aprovação de outro integrante; houve commits direto na `main` antes da proteção |
| TDD / BDD | ✅ | Pares `test:` → `fix:` no histórico; cenários Cucumber em `src/test/resources/features/` |
| Commits rastreáveis | ✅ | Conventional Commits em português, com o porquê no corpo |
| Release versionado | ⏳ | `CHANGELOG.md` e tag `v1.0.0-rc1` preparados no PR #7 |

## 7. Situação dos critérios de qualidade

Atualização dos critérios C1–C13 da [decisão de qualidade](decisao-qualidade.md), que foi escrita com a
`main` em `c7992e9`.

| # | Critério | Antes | Agora | O que mudou |
|---|---|---|---|---|
| C1 | Suíte automatizada | ✅ | ✅ | 267 testes |
| C2 | Cobertura | ⚠️ | ⚠️ | Back-end 86,7% / 73,4%; front continua sem medição |
| C3 | Contrato da API | ✅ | ✅ | — |
| C4 | Concorrência | ✅ | ✅ | — |
| C5 | Desempenho | ⚠️ | ⚠️ | Continua só em ambiente local |
| C6 | CI em todo PR | ⚠️ | ✅ | JMeter verde desde o PR #4 |
| C7 | SonarCloud | ❌ | ⚠️ | Quality Gate aprovado no PR #8; falta a análise completa da `main` |
| C8 | Revisão por pares | ❌ | ❌ | Proteção ativa, mas nenhum PR aprovado por outro integrante ainda |
| C9 | Rastreabilidade | ⚠️ | ✅ | Matriz em `docs/rtm.md` |
| C10 | README reproduzível | ⚠️ | ✅ | Pré-requisitos detalhados |
| C11 | Front-end atual | ❌ | ✅ | `main` serve o Angular (PR #4) |
| C12 | Segurança | ⚠️ | ⚠️ | Sem SAST completo nem DAST |
| C13 | Registro de defeitos | ❌ | ✅ | `docs/defeitos/` |

**Resumo:** 8 ✅, 4 ⚠️ e 1 ❌ (antes eram 4 ✅, 5 ⚠️ e 4 ❌).

## 8. Riscos e pendências

| Pendência | Impacto | Próximo passo |
|---|---|---|
| Sem revisão por pares (C8) | Médio: toda a evidência depende de uma pessoa | Outro integrante revisar e aprovar os PRs abertos (#7, #8 e este) |
| SonarCloud só no código novo (C7) | Médio: o código anterior ainda não passou pela análise | Fazer merge do PR #8 e conferir o Quality Gate da `main` |
| Cobertura do front não medida (C2) | Baixo: as regras de negócio estão no back-end | Adicionar `@vitest/coverage-v8` |
| Regras P1–P5 (DEF-010 a DEF-014) | Médio para P1–P3 (reservas podem ficar inválidas) | Decisão do grupo sobre a regra de negócio |
| Carga só em máquina local (C5) | Desconhecido em produção | Repetir em ambiente parecido com produção e com mais usuários |

## 9. Recomendação

Liberar a versão `v1.0.0-rc1` **para avaliação e homologação**, como já registrado na
[decisão de qualidade](decisao-qualidade.md). Para produção, falta pelo menos: revisão por pares nos
PRs, Quality Gate aprovado na análise completa da `main` e teste de carga em ambiente representativo.
