# Decisão de qualidade

> **Data:** 13/11/2026 · **Relator:** Claude Code, escrevendo a pedido do grupo. A decisão só vale depois que os integrantes revisarem e assinarem a seção 7.
> **Objeto avaliado:** `https://github.com/Guilherme3578/trabalho-qualidade-de-software`, branch `main`, commit `c7992e9` (merge do PR #3).
> **Tag avaliada:** `TAG_A_DEFINIR`. Nenhuma tag existe no repositório hoje (`git ls-remote --tags origin` não devolve nada). A tag precisa ser criada **neste commit** antes da entrega, para que a avaliação e o código entregue sejam a mesma coisa.
> **Pacote de evidências:** `docs/entrega/checklist.md` (execução externa num clone limpo, 13/11), `docs/10-relatorio-testes-backend.md`, `docs/09-relatorio-testes-api.md`, `docs/06-desempenho.md`, `docs/12-relatorio-tecnico.md`, `docs/04-matriz-rastreabilidade.md`, runs do GitHub Actions (`gh run list`) e o histórico do Git.

> **Atualização de 13/11/2026:** C6, C10 e C11 foram resolvidos depois desta análise. O PR #4 entrou
> na `main` (`fd027c8`) com o CI e o JMeter verdes, a `main` serve o front Angular, e o README lista os
> pré-requisitos. O risco R3 deixa de existir, e R6 fica reduzido. C7, C8 e C13 continuam ❌.

## 1. Decisão

**LIBERAR COM RESSALVAS, só para avaliação acadêmica e homologação. Produção está BLOQUEADA.**

A evidência funcional é forte e pode ser reproduzida: a suíte automatizada passa num clone limpo e no CI, a cobertura fica acima das metas e a carga nominal roda sem erros. Isso permite entregar a versão para avaliação e demonstração.

Essa evidência **não sustenta "pronto para produção"**:

- Ela vem de testes em ambiente local ou de CI, com base de dados descartável e carga de 20 usuários. O sistema nunca rodou num ambiente real, com dados, usuários e tráfego reais.
- Dois controles que a própria estratégia de qualidade exige **não têm evidência nenhuma**: a análise estática (SonarCloud/Quality Gate) e a revisão por pares.
- A `main` avaliada ainda serve o front antigo. O front Angular está no PR #4, que não foi mergeado.

## 2. Critérios e evidência

Legenda: ✅ atendido com evidência · ⚠️ atendido em parte · ❌ sem evidência

| # | Critério | Meta | Evidência | Situação |
|---|---|---|---|---|
| C1 | Suíte automatizada do back-end passa | 0 falhas | Clone limpo, 13/11: 262 testes (136 unitários e 126 de integração), 0 falhas, 2 min 57 s (`checklist.md` §1, passo 6). CI verde na `main` (run 42804045729). | ✅ |
| C2 | Cobertura de código (JaCoCo) | ≥ 80% linhas / ≥ 70% desvios | 86,4% / 73,2%, com `All coverage checks have been met` (clone limpo e `10-relatorio-testes-backend.md` §10.5). Só cobre o back-end: o front Angular não tem medição de cobertura. | ⚠️ |
| C3 | Contrato da API (caixa-preta) | 0 falhas | Newman: 59 requisições, 124 asserções, 0 falhas (`09-relatorio-testes-api.md`, 09/11). Testes REST Assured incluídos em C1. | ✅ |
| C4 | Integridade e concorrência (dupla reserva, RF06) | Nenhuma reserva duplicada | Testes de concorrência e de integridade do banco contra MySQL real via Testcontainers (incluídos em C1). `SELECT ... FOR UPDATE`, FKs e CHECK (`02-arquitetura.md`). | ✅ |
| C5 | Desempenho | p95 ≤ 500 ms, erro 0% | Clone limpo: 2.004 requisições, 0% de erro, média de 38 ms, p95 de 64 ms (`checklist.md` §1, passo 10). Cenário de 20 usuários em máquina local; nada foi medido em ambiente de produção. | ⚠️ |
| C6 | CI em todo PR | Verde | CI verde no PR #4 (run 43350817240). **O JMeter falhou no PR #4** (run 43350817027). A correção `1cbdc13` está só na máquina local. | ⚠️ |
| C7 | Análise estática / Quality Gate (SonarCloud) | Quality Gate aprovado | **Nunca rodou.** O passo aparece como `skipped` em todos os runs (sem o secret `SONAR_TOKEN`), e o `pom.xml` ainda tem `sonar.organization=SUA_ORGANIZACAO`. | ❌ |
| C8 | Revisão por pares | ≥ 1 aprovação de outro integrante por PR | **Nenhuma.** Os PRs #1–#4 foram abertos e mergeados pela mesma conta, com 0 aprovações. Não há proteção de branch, e 18 commits entraram direto na `main`. Os commits de `K1byl_` usam o mesmo e-mail do autor principal. | ❌ |
| C9 | Rastreabilidade requisito → teste | Matriz em dia | Rodar `scripts/rastreabilidade.py` de novo não alterou nada (`checklist.md` §1, passo 11). O arquivo está em `docs/04-matriz-rastreabilidade.md`, não no caminho pedido (`docs/rtm.md`). | ⚠️ |
| C10 | Reprodutibilidade pelo README | Um avaliador externo chega ao `verify` verde | Chega, mas só sabendo o que o README não diz: Docker é obrigatório e é preciso Compose v2, Chrome e Node (`checklist.md` §1, passos 2, 3 e 7). | ⚠️ |
| C11 | Entrega do front-end atual | `main` serve o Angular | A `main` serve o front antigo em HTML/JS. A correção está no PR #4, aberto. | ❌ |
| C12 | Segurança | Ameaças mapeadas e testadas | Testes de 401/403 por perfil, token adulterado, força bruta (429) e injeção de SQL (`05-seguranca.md`, incluídos em C1). O documento é curto (27 linhas) e ainda está marcado como rascunho. Não houve análise SAST/DAST, porque depende de C7. | ⚠️ |
| C13 | Registro de defeitos | Defeitos com ID, severidade e status | Não existe como documento próprio. Os defeitos estão espalhados por `11-revisao-de-codigo.md`, `12-relatorio-tecnico.md` e mensagens de commit. | ❌ |

**Resumo:** 4 critérios ✅, 5 ⚠️ e 4 ❌. Os ❌ são de **processo e de entrega** (C7, C8, C11, C13), não de comportamento funcional. Por isso a liberação vale só para avaliação. Para produção, C7, C8 e C11 são impeditivos.

## 3. Riscos aceitos para a liberação em avaliação/homologação

Os riscos abaixo só valem para o escopo da seção 1. Nenhum deles foi aceito para produção.

| # | Risco | Impacto | Justificativa da aceitação | Responsável |
|---|---|---|---|---|
| R1 | Sem análise estática (C7): podem existir code smells, vulnerabilidades ou duplicações que ninguém detectou | **Alto** em produção; **baixo** em avaliação, porque não há dados reais nem exposição pública da aplicação | Os testes de segurança cobrem os ataques principais (autorização, JWT, força bruta, injeção de SQL). Rodar o Sonar depende de configurar a conta e o secret, o que não cabe no prazo de entrega | `INTEGRANTE_A_DEFINIR` |
| R2 | Sem revisão por pares (C8): defeitos de lógica e de design passam sem uma segunda opinião | **Médio**: a evidência de qualidade depende de uma única pessoa | Parte do risco é compensada pela suíte automatizada (262 testes) e pela revisão de código registrada em `11-revisao-de-codigo.md`. **Não é um controle equivalente** | `INTEGRANTE_A_DEFINIR` |
| R3 | A `main` serve o front antigo (C11) e o JMeter falhou no PR #4 (C6) | **Médio**: quem avaliar pela interface vê uma versão desatualizada (o problema já aconteceu na apresentação em sala) | A API, que é o objeto principal dos testes, é a mesma nos dois fronts. A correção está pronta no PR #4 | `INTEGRANTE_A_DEFINIR` |
| R4 | Desempenho medido só com 20 usuários em máquina local (C5) | **Baixo** em avaliação; **desconhecido** em produção | A meta de p95 foi atingida com folga (64 ms contra 500 ms) no cenário definido em `06-desempenho.md` | `INTEGRANTE_A_DEFINIR` |
| R5 | O front Angular não tem cobertura medida (C2) | **Baixo**: há 40 testes Vitest passando, mas não se sabe quanto do código eles cobrem | O back-end, onde estão as regras de negócio, tem cobertura acima da meta | `INTEGRANTE_A_DEFINIR` |
| R6 | O README está incompleto (C10) | **Baixo**: o avaliador perde tempo, mas consegue executar | Os passos que faltam estão documentados em `docs/entrega/checklist.md` | `INTEGRANTE_A_DEFINIR` |

## 4. O que precisa acontecer antes de considerar produção

1. Rodar o SonarCloud com Quality Gate aprovado (C7).
2. Proteger a `main`, exigindo uma aprovação de outro integrante e o CI verde, e revisar o PR #4 com essas regras (C8).
3. Fazer merge do PR #4 com o JMeter verde (C6 e C11). Para isso, a branch local divergente precisa ser publicada, o que exige push forçado.
4. Criar o registro de defeitos (C13).
5. Rodar um teste de carga num ambiente parecido com o de produção, com volume realista (C5).

## 5. Justificativa

A decisão segue a evidência. O que foi **medido e reproduzido por terceiros** (testes, cobertura do back-end, contrato da API, concorrência e carga nominal) sustenta que o sistema **funciona como especificado no ambiente de teste**. Por isso ele pode ser liberado para avaliação.

O que **não foi medido** (análise estática, revisão independente, comportamento em ambiente real, cobertura do front) não pode virar conclusão positiva. Testes locais e de CI mostram que o sistema funciona nas condições testadas. Eles não mostram que ele aguenta produção. Bloquear tudo ignoraria a evidência funcional que existe. Liberar para produção afirmaria mais do que a evidência mostra.

## 6. Responsável pela decisão

`INTEGRANTE_A_DEFINIR` (responsável pela qualidade no grupo). O relator só redigiu este documento. A decisão passa a valer quando esse integrante assinar.

## 7. Declaração dos integrantes

> Cada integrante preenche a própria linha. O Git registra um único autor (as contas "Guilherme Israel" e "K1byl_" usam o mesmo e-mail), então o relator **não** atribuiu tarefas a ninguém para não registrar contribuições que não aconteceram.

| Integrante | O que fez (declaração própria) | Evidência (commits, PRs, documentos) |
|---|---|---|
| `NOME_1` | | |
| `NOME_2` | | |
| `NOME_3` | | |
| `NOME_4` | | |

---

> **Nota de 2026-11-13 (acréscimo, texto original preservado):** o SonarCloud passou a rodar no CI desde o merge do PR #8 (`pom.xml` com a organização `guilherme3578`, secret `SONAR_TOKEN` cadastrado). O Quality Gate foi aprovado. Os apontamentos e o tratamento de cada um estão em [08 §8.5](08-ci-analise-estatica.md#85-apontamentos-do-sonarcloud-e-tratamento).
