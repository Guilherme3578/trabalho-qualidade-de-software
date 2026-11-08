# 8. Integração contínua e análise estática

> 📝 **Rascunho para revisão.**

## 8.1 GitHub Actions

| Workflow | Quando roda | O que faz |
|---|---|---|
| `.github/workflows/ci.yml` | Todo **pull request**, push na `main` e manualmente | Java 21 → `./mvnw verify` (todos os testes + verificação de cobertura) → publica relatórios → análise SonarCloud |
| `.github/workflows/desempenho.yml` | Todo pull request e manualmente | Sobe MongoDB e WireMock, inicia a aplicação e executa o plano JMeter |

Os runners `ubuntu-latest` já têm Docker, então Testcontainers funciona sem configuração extra.

**Proteção da branch `main`** (configurar em *Settings → Branches*):

- exigir pull request antes do merge;
- exigir os checks `Build, testes e cobertura` e `SonarCloud Code Analysis` aprovados.

## 8.2 Cobertura (JaCoCo)

- Testes unitários e de integração gravam no **mesmo** `target/jacoco.exec`, então a cobertura é a soma das duas suítes.
- `jacoco:check` faz o build **falhar** com menos de 80% de linhas ou 70% de ramos.
- Ficam fora da medição: configuração (`config/**`), classe `main` e dados de demonstração.
- Relatório: `target/site/jacoco/index.html` (o XML é enviado ao SonarCloud).

## 8.3 SonarCloud — passo a passo de configuração

1. Publicar o repositório no GitHub.
2. Entrar em <https://sonarcloud.io> com a conta do GitHub → **Analyze new project** → selecionar o repositório.
3. Em *Administration → Analysis Method*, **desligar** a "Automatic Analysis" (a análise será feita pelo CI).
4. Copiar a **organization key** e a **project key** para o `pom.xml`
   (`sonar.organization` e `sonar.projectKey`).
5. Em *My Account → Security*, gerar um token.
6. No GitHub, em *Settings → Secrets and variables → Actions*, criar o secret `SONAR_TOKEN` com o token.
7. Abrir um pull request: o SonarCloud comenta o resultado do **Quality Gate** no PR.

## 8.4 Evidências a anexar

- [ ] Print de um PR com os checks verdes (CI + SonarCloud)
- [ ] Print do painel do SonarCloud (bugs, vulnerabilidades, code smells, cobertura, duplicação)
- [ ] Print do relatório JaCoCo
- [ ] Artefato `relatorio-jmeter` da execução do workflow de desempenho
