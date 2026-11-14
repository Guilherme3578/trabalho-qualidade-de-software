# Decisão de qualidade — v1.0.0-rc2

| Item | Valor |
|---|---|
| Data | 14/11/2026 |
| Repositório | https://github.com/Guilherme3578/trabalho-qualidade-de-software (público) |
| Tag avaliada | `v1.0.0-rc2` (anotada), no commit da tag (`git rev-list -n 1 v1.0.0-rc2`) |
| Código avaliado | `0d4a652` (merge do PR #10 na `main`). Os commits entre ele e a tag alteram só `docs/`, `CHANGELOG.md` e o upload de artefatos no `ci.yml`. Nenhum arquivo Java ou TypeScript muda, então o produto da tag é o mesmo que foi avaliado |
| Relator | Claude Code (assistente de IA), a pedido do autor. A decisão passa a valer quando o responsável assina a [seção 7](#7-declaração-do-integrante) |
| Avaliação anterior | Rc1, com a `main` em `c7992e9`: texto preservado na tag [`v1.0.0-rc1`](https://github.com/Guilherme3578/trabalho-qualidade-de-software/blob/v1.0.0-rc1/docs/decisao-qualidade.md) |

## 1. Decisão

**LIBERAR COM RESSALVAS para avaliação acadêmica e homologação. A evidência não sustenta liberar para produção.**

Toda a evidência foi produzida em **ambiente de teste**:

- runners do GitHub Actions;
- banco MySQL descartável, criado pelo Testcontainers;
- carga de 20 usuários virtuais;
- uma máquina de desenvolvimento.

Essa evidência mostra que o sistema **faz o que a especificação pede nas condições testadas**. Ela não mostra como ele se comporta com usuários, dados, rede e infraestrutura reais. Esse ambiente nunca existiu para este projeto.

Bloquear não seria proporcional. Todos os critérios de produto têm evidência objetiva, reproduzível e ligada a um commit:

- testes;
- cobertura;
- análise estática;
- concorrência;
- carga nominal;
- rastreabilidade.

E não há defeito aberto.

## 2. Critérios e evidência

Legenda: ✅ atendido, com evidência · ⚠️ atendido em parte ou com limitação registrada · ❌ sem evidência

| # | Critério | Meta | Evidência (commit `0d4a652`, salvo indicação) | Situação |
|---|---|---|---|---|
| C1 | Suíte automatizada do back-end passa | 0 falhas | CI da `main`, [run 43556658613](https://github.com/Guilherme3578/trabalho-qualidade-de-software/actions/runs/43556658613): **281 testes** (137 no Surefire e 144 no Failsafe), 0 falhas, 0 erros e 0 ignorados. Inclui integração com MySQL 8.4 real, concorrência, BDD (Cucumber) e E2E (Selenium) | ✅ |
| C2 | Cobertura de código | Back ≥ 80% de linhas e ≥ 70% de desvios; front ≥ 80% | Back (JaCoCo, artefato `relatorios-de-teste` do mesmo run): **89,7% de linhas e 75,3% de desvios**, com `All coverage checks have been met`. Front (Vitest, mesmo run): **200 testes em 17 arquivos**, 84,31% de linhas, 90,13% de desvios e 85,85% de instruções. Desde o PR #11, o build falha abaixo de 80% | ✅ |
| C3 | Análise estática (SonarCloud) | Quality Gate aprovado na análise completa | Análise da `main` na revisão `0d4a652` (14/11, 04:29 UTC): **Quality Gate OK**. 0 bugs, 0 vulnerabilidades, 0 code smells, 0 security hotspots, 0,0% de duplicação. Nota A em confiabilidade, segurança e manutenibilidade. 4.728 linhas de código. Os 12 apontamentos anteriores foram tratados no PR #10 ([08 §8.5](08-ci-analise-estatica.md)) | ✅ |
| C4 | Integridade e concorrência (dupla reserva) | Nenhuma reserva duplicada | Testes de concorrência com 20 pedidos simultâneos, mais FKs e CHECK testados contra MySQL real (incluídos em C1). Na carga, 500 de 500 tentativas conflitantes foram recusadas com `409` ([resultado-01](performance/resultado-01.md)) | ✅ |
| C5 | Desempenho | Erro < 1%, p95 ≤ 500 ms, máximo ≤ 1000 ms | JMeter no CI, 20 usuários × 25 iterações. No [PR #10](https://github.com/Guilherme3578/trabalho-qualidade-de-software/actions/runs/43546188572): 2.004 requisições, 0 erros, p95 de 41 ms, máximo de 274 ms. No [PR #12](https://github.com/Guilherme3578/trabalho-qualidade-de-software/actions/runs/43546777831): 2.004 requisições, 0 erros, p95 de 34 ms, máximo de 255 ms. **Limitações:** a carga é pequena e roda num runner compartilhado. Ela não rodou em `0d4a652` (o workflow roda em PRs e no push de tag), então a medição do commit da tag sai no push de `v1.0.0-rc2` | ⚠️ |
| C6 | CI em todo PR e na `main` | Verde | Os PRs #10, #11 e #12 passaram em CI, front, SonarCloud e JMeter. Os três merges na `main` têm CI verde (runs 43556604553, 43556636213 e 43556658613) | ✅ |
| C7 | Registro de defeitos | Todo defeito com ID, severidade, status e correção | [docs/defeitos](defeitos/README.md): 14 defeitos. Os 6 de severidade alta foram fechados. DEF-010 a DEF-014 estão corrigidos e entraram na `main` com CI verde; a mudança do status para "Fechado" está no PR do release. 0 abertos. Os defeitos de código seguiram a ordem `test:` (teste que falha) → `fix:` (correção) | ✅ |
| C8 | Rastreabilidade requisito → teste | Matriz em dia, 31 de 31 requisitos com teste | 31 de 31 requisitos têm teste. Rodar `scripts/rastreabilidade.py` de novo em `0d4a652` **altera** `docs/rtm.md`: faltam os testes do PR #10 (RF07 passa de 13 para 14 testes, RNF07 de 7 para 11). A cobertura dos requisitos continua completa; só a contagem está defasada | ⚠️ |
| C9 | Segurança | Ameaças mapeadas, cada uma com controle e teste | 15 ameaças com controle e teste ([05](05-seguranca.md)), com base no OWASP Top 10 e no OWASP API Top 10. Quatro defeitos de segurança foram corrigidos (DEF-002, 005, 013 e 014). A CSP não tem `unsafe-inline`, e o E2E verifica isso no navegador. SAST coberto por C3. **Não houve DAST nem teste de invasão** | ⚠️ |
| C10 | Revisão por pares | ≥ 1 revisão independente por PR | **Nenhuma.** O projeto é individual, e os 12 PRs foram abertos e mergeados pela mesma pessoa. Hoje a `main` **não tem proteção de branch** (a API do GitHub devolve `Branch not protected`). Isso contradiz a seção 6 de [relatorio-qualidade.md](relatorio-qualidade.md), que diz que a proteção existe | ❌ |
| C11 | Reprodutibilidade | Avaliador externo chega ao build verde pelo README | Um avaliador externo chegou ao build verde num clone limpo em 13/11 ([checklist](entrega/checklist.md)), e os pré-requisitos foram acrescentados ao README depois disso. O CI repete o build do zero a cada push. Ninguém de fora refez o clone limpo depois do rc1 | ⚠️ |
| C12 | Operação em produção | Implantação, HTTPS, backup, monitoramento e gestão de segredos verificados | **Não há ambiente de produção nem de homologação real.** Nada disso foi implantado nem testado | ❌ |

**Resumo:** 7 ✅, 3 ⚠️ e 2 ❌. Os dois ❌ (C10 e C12) são de **processo e operação**, não de comportamento do produto. Eles não impedem o uso acadêmico, mas impedem afirmar que o sistema está pronto para produção.

## 3. Riscos aceitos

Os riscos valem **apenas** para o escopo da decisão (avaliação e homologação). Nenhum foi aceito para produção.

| # | Risco | Impacto | Justificativa da aceitação | Responsável |
|---|---|---|---|---|
| R1 | Sem revisão independente (C10): defeitos de lógica e de design podem passar sem uma segunda opinião | **Médio** | Num projeto de um único autor, não há outro revisor. Compensações: CI em todo PR, Quality Gate do SonarCloud, ordem `test:` → `fix:` e auto-revisão em [11](11-revisao-de-codigo.md). **Nada disso equivale a uma revisão independente.** A avaliação do professor é a primeira revisão externa | Guilherme Israel |
| R2 | `main` sem proteção de branch (C10): um push direto pode pular o CI | **Médio** no processo; **baixo** no produto avaliado, porque os três merges desta versão têm CI verde | A ausência é verificável, e a evidência desta versão não depende dela. O relatório de qualidade precisa ser corrigido, ou a proteção recriada (*Settings → Branches*: PR obrigatório e check `Build, testes e cobertura`) | Guilherme Israel |
| R3 | Desempenho medido só com 20 usuários em runner compartilhado, e não no commit da tag (C5) | **Baixo** em avaliação; **desconhecido** em produção | A meta tem folga grande (p95 de 34–41 ms contra 500 ms) nos dois PRs que mais mudaram o código. O push da tag roda o JMeter no commit exato, e o resultado vai para as notas do release | Guilherme Israel |
| R4 | Sem DAST nem teste de invasão (C9) | **Baixo** em avaliação, porque não há dados reais nem exposição pública; **alto** em produção | SAST limpo (C3) e testes automatizados dos ataques mapeados (401, 403, BOLA, força bruta, injeção de SQL, JWT adulterado ou de outro emissor) | Guilherme Israel |
| R5 | Nunca implantado (C12): HTTPS, backup, monitoramento e rotação do segredo do JWT não verificados | **Nenhum** para avaliação; **impeditivo** para produção | Fora do escopo da disciplina. Registrado para não ser confundido com evidência | Guilherme Israel |
| R6 | Matriz de rastreabilidade defasada em 5 testes (C8) | **Baixo**: nenhum requisito fica sem teste | Basta regenerar com `scripts/rastreabilidade.py` depois de um `verify` | Guilherme Israel |
| R7 | Instabilidade intermitente em E2E: uma falha local em 13/11 (tempo esgotado em `header.topo` e e-mail duplicado), que não se repetiu em 3 execuções locais nem no CI | **Baixo**: pode gerar um vermelho falso | Os testes estão verdes em todas as execuções do CI desta versão. Se voltar a acontecer, registrar como defeito | Guilherme Israel |
| R8 | Actions com Node 20 e `setup-java@v4`, ambos descontinuados (avisos do CI) | **Baixo**: o CI pode quebrar sem nenhuma mudança no código | Ainda funcionam; trocar pelas versões novas no próximo ciclo | Guilherme Israel |

## 4. Justificativa

A decisão segue o que foi **medido**, não o que se espera. O que foi medido e está ligado a um commit e a um run:

- testes;
- cobertura do back e do front;
- análise estática completa;
- concorrência;
- carga nominal;
- rastreabilidade;
- defeitos.

Isso sustenta a conclusão de que o produto se comporta como especificado no ambiente de teste. Com essa base, ele pode ser entregue para avaliação e homologação.

Testes locais e de CI não demonstram prontidão para produção. Eles não medem:

- carga realista;
- infraestrutura;
- segurança dinâmica;
- operação;
- revisão por outra pessoa.

Por isso a palavra "produção" não aparece como conclusão positiva neste documento. Bloquear também não se justifica, porque todos os critérios de produto têm evidência e não há defeito aberto.

Em relação ao rc1, cinco critérios passaram a ter evidência completa:

- análise estática (SonarCloud);
- cobertura do front;
- registro de defeitos;
- front atual servido na `main`;
- CI em todo PR.

Apareceram dois achados novos. A `main` está sem proteção de branch. A matriz de rastreabilidade está defasada.

## 5. Condições para considerar produção

1. Implantar num ambiente de homologação real, com HTTPS, backup, monitoramento e o segredo do JWT gerenciado fora do repositório.
2. Repetir a carga nesse ambiente, com volume realista e buscando o ponto de saturação.
3. Rodar um DAST (por exemplo, OWASP ZAP) contra esse ambiente.
4. Ter revisão independente, de outra pessoa, das partes críticas: reserva, concorrência e segurança.
5. Recriar a proteção da `main` e regenerar a matriz de rastreabilidade.

## 6. Responsável pela decisão

**Guilherme Israel**, único autor e responsável pela qualidade do projeto. O relator só reuniu a evidência e redigiu este documento.

## 7. Declaração do integrante

O projeto tem **um único integrante**. As contas Git "Guilherme Israel" (autor de 91 commits na `main`) e "K1byl_" (os 12 merges de PR) são da mesma pessoa e usam o mesmo e-mail.

O texto abaixo foi redigido pelo relator **a partir do histórico do Git**. O integrante deve conferir, corrigir o que for preciso e assinar.

> **Guilherme Israel** declara que foi o único integrante do projeto. Foi responsável pelo escopo, pelos requisitos e pelas regras de negócio, incluindo as decisões das pendências P1–P5 em 13/11/2026. Também foi responsável pela arquitetura (Spring Boot, MySQL e Angular), pela abertura e pelo merge de todos os PRs (#1 a #12), pela configuração do SonarCloud e do repositório, e pela apresentação em sala.
>
> Declara ainda que usou o assistente de IA **Claude Code** em grande parte da implementação, dos testes e da documentação, e que isso está registrado no próprio Git: 90 commits da `main` trazem `Co-Authored-By: Claude`. O integrante revisou e aceitou cada PR antes do merge, e responde pelo conteúdo entregue.
>
> Assinatura: ____________________ · Data: ___/___/2026
