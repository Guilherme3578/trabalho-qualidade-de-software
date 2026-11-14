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
| Painel do SonarCloud | [sonarcloud.io](https://sonarcloud.io/dashboard?id=Guilherme3578_trabalho-qualidade-de-software) | ⚠️ Análise do projeto inteiro só depois do merge do PR #8 |
| Relatório JaCoCo | Artefato `relatorios-de-teste` de cada run do CI; resumo em [10](10-relatorio-testes-backend.md) §10.5 | ✅ |
| Relatório do JMeter | Artefato `relatorio-jmeter` do workflow de desempenho; resultado em [performance/resultado-01](performance/resultado-01.md) | ✅ |
| Prints para a apresentação | — | A fazer pelo grupo |
