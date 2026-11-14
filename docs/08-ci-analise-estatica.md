# 8. Integração contínua e análise estática

## 8.1 GitHub Actions

| Workflow | Quando roda | O que faz |
|---|---|---|
| `.github/workflows/ci.yml` | Todo **pull request**, push na `main` e manualmente | Java 21 → `./mvnw verify` (todos os testes + verificação de cobertura) → publica relatórios → análise SonarCloud |
| `.github/workflows/desempenho.yml` | Todo pull request e manualmente | Sobe MySQL e WireMock, inicia a aplicação e executa o plano JMeter |

Os runners `ubuntu-latest` já têm Docker, então Testcontainers funciona sem configuração extra.

**Proteção da branch `main`** (*Settings → Branches*), ativa desde 13/11/2026:

- exige pull request com **1 aprovação** antes do merge;
- exige o check `Build, testes e cobertura` aprovado e a branch atualizada com a `main`.

O check `SonarCloud Code Analysis` aparece nos PRs, mas ainda não é obrigatório.

## 8.2 Cobertura (JaCoCo)

- Testes unitários e de integração gravam no **mesmo** `target/jacoco.exec`, então a cobertura é a soma das duas suítes.
- `jacoco:check` faz o build **falhar** com menos de 80% de linhas ou 70% de ramos.
- Ficam fora da medição: configuração (`config/**`), classe `main` e dados de demonstração.
- Relatório: `target/site/jacoco/index.html` (o XML é enviado ao SonarCloud).

## 8.3 SonarCloud — configuração

Configurado em 13/11/2026 (PR #8): organização `guilherme3578`, projeto
`Guilherme3578_trabalho-qualidade-de-software` e secret `SONAR_TOKEN` no repositório. Na primeira
análise, a do PR #8, o **Quality Gate foi aprovado** (0 bugs, 0 vulnerabilidades, 0 code smells, 0%
de duplicação no código novo). Passos usados, para refazer em outro repositório:

1. Publicar o repositório no GitHub.
2. Entrar em <https://sonarcloud.io> com a conta do GitHub → **Analyze new project** → selecionar o repositório.
3. Em *Administration → Analysis Method*, **desligar** a "Automatic Analysis" (a análise será feita pelo CI).
4. Copiar a **organization key** e a **project key** para o `pom.xml`
   (`sonar.organization` e `sonar.projectKey`).
5. Em *My Account → Security*, gerar um token.
6. No GitHub, em *Settings → Secrets and variables → Actions*, criar o secret `SONAR_TOKEN` com o token.
7. Abrir um pull request: o SonarCloud comenta o resultado do **Quality Gate** no PR.

## 8.4 Evidências

| Evidência | Onde | Situação |
|---|---|---|
| PR com os checks verdes (CI, front-end, JMeter, SonarCloud) | [PR #8](https://github.com/Guilherme3578/trabalho-qualidade-de-software/pull/8) | ✅ |
| Painel do SonarCloud | [sonarcloud.io](https://sonarcloud.io/dashboard?id=Guilherme3578_trabalho-qualidade-de-software) | ✅ Análise do `main` rodando no CI desde o merge do PR #8 (2026-11-13). Quality Gate aprovado; apontamentos tratados em §8.5 |
| Relatório JaCoCo | Artefato `relatorios-de-teste` de cada run do CI; resumo em [10](10-relatorio-testes-backend.md) §10.5 | ✅ |
| Relatório do JMeter | Artefato `relatorio-jmeter` do workflow de desempenho; resultado em [performance/resultado-01](performance/resultado-01.md) | ✅ |
| Prints para a apresentação | — | A fazer pelo grupo |

## 8.5 Apontamentos do SonarCloud e tratamento

Primeira análise completa do `main` (commit `89f4292`, 2026-11-13): Quality Gate **aprovado**, cobertura de 84,1% (86,7% das linhas e 73,4% dos branches), 0% de duplicação e 0 code smells. Porém havia 6 bugs (confiabilidade D) e 6 vulnerabilidades (segurança E) em código anterior ao período avaliado pelo Quality Gate. Todos foram tratados na branch `fix/apontamentos-sonarcloud`:

| Regra | Onde | Decisão | Evidência |
|---|---|---|---|
| java:S8947 (BUG) | `Sala`, `Professor`, `Material` — `atualizar` era `final` | **Defeito real, corrigido.** Num proxy do Hibernate (`getReference`), o método `final` rodava no próprio proxy e a alteração se perdia sem erro. O método agora não é `final` e delega para um `preencher` privado, que também é usado no construtor. | `AtualizacaoViaProxyIT` (falhava 3/3 antes da correção) |
| javabugs:S2259 (BUG) | `AvaliadorAprovacao:25` | **Defeito real, corrigido.** Disciplina nula lançava `NullPointerException`. Agora gera o motivo de validação pelo responsável. | `AvaliadorAprovacaoTest.disciplinaNula` |
| java:S8700 (BUG) | `Intervalo.duracao`, `RelatorioService.horas` | **Falso positivo, suprimido com `@SuppressWarnings`.** As reservas são validadas para o mesmo dia, entre 07:00 e 23:00 (`PoliticaHorario`). Nenhuma atravessa uma troca de horário de verão, que ocorre de madrugada, então a duração em horário de parede é igual à duração real. A capacidade usada no relatório também é medida em horário de parede. | `PoliticaHorarioTest` |
| java:S6437 (VULNERABILIDADE) | `DadosIniciais:70` — senha de demonstração no código | **Corrigido.** A senha passou para `app.dados-iniciais.senha` no `application-dev.yml` (variável `SENHA_DEMO`, padrão `Senha123`), restrita ao perfil `dev`. | — |
| java:S4502 (VULNERABILIDADE) | `SecurityConfig` — CSRF desligado | **Seguro, suprimido com `@SuppressWarnings`.** A API é stateless (`SessionCreationPolicy.STATELESS`) e autentica só por JWT no header `Authorization`. O front guarda o token no `sessionStorage`, e não em cookie. Sem credencial enviada automaticamente pelo navegador, não existe vetor de CSRF. | `SecurityConfig`, `frontend/src/app/services/auth.service.ts` |
| githubactions:S6505 (VULNERABILIDADE) ×2 | `ci.yml` — `npm install -g` e `npm ci` | **Corrigido.** Agora usam `--ignore-scripts`. Testes e build do front conferidos com npm 11.6.2. | Job "Front-end (Angular)" |
| githubactions:S8264 (VULNERABILIDADE) | `ci.yml` — permissões no nível do workflow | **Corrigido.** O workflow passou a ter `permissions: {}`, e cada job declara só o que usa. | — |
| githubactions:S7637 (VULNERABILIDADE) | `ci.yml` — `setup-chrome@v1` | **Corrigido.** Todas as actions, nos dois workflows, foram fixadas por SHA de commit, com a versão ao lado. | — |

