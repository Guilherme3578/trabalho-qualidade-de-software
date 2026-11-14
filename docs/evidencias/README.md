# Pacote de evidências — v1.0.0-rc1

> **Commit avaliado:** o commit da tag `v1.0.0-rc1` (`git rev-list -n 1 v1.0.0-rc1`).
>
> Este índice faz parte do próprio commit da tag. Por isso os links usam o nome da tag
> (`blob/v1.0.0-rc1/...`), que sempre resolve nesse commit, e nunca `main`. As execuções do CI e a
> análise do SonarCloud só passam a existir depois do push. A URL exata de cada uma, com o SHA, está nas
> [notas do release v1.0.0-rc1](https://github.com/Guilherme3578/trabalho-qualidade-de-software/releases/tag/v1.0.0-rc1).
>
> A tag nunca é movida. Se alguma evidência precisar de correção, ela sai em `v1.0.0-rc2`.

| # | Evidência | Onde | Como se liga ao commit da tag |
|---|---|---|---|
| E1 | Execução do CI (build, testes, cobertura, front) | [CI filtrado pela tag](https://github.com/Guilherme3578/trabalho-qualidade-de-software/actions/workflows/ci.yml?query=branch%3Av1.0.0-rc1) · [checks do commit](https://github.com/Guilherme3578/trabalho-qualidade-de-software/commit/v1.0.0-rc1) | Run disparado pelo push da tag; o `headSha` é o da tag |
| E2 | Relatório do JaCoCo | Artifact `relatorios-de-teste` da execução E1, pasta `site/jacoco/` (`index.html`, `jacoco.xml`, `jacoco.csv`) | Mesmo run de E1 |
| E3 | Quality Gate do SonarCloud | [Projeto `Guilherme3578_trabalho-qualidade-de-software`](https://sonarcloud.io/summary/overall?id=Guilherme3578_trabalho-qualidade-de-software&branch=main) · [histórico de análises](https://sonarcloud.io/project/activity?id=Guilherme3578_trabalho-qualidade-de-software) | A tag é criada no commit de merge da `main`; a análise da `main` nesse commit tem a mesma revisão (SHA), registrada nas notas do release. O push da tag não roda o Sonar de novo |
| E4 | Resultado do teste de carga | [docs/performance/resultado-01.md](https://github.com/Guilherme3578/trabalho-qualidade-de-software/blob/v1.0.0-rc1/docs/performance/resultado-01.md) · [JMeter filtrado pela tag](https://github.com/Guilherme3578/trabalho-qualidade-de-software/actions/workflows/desempenho.yml?query=branch%3Av1.0.0-rc1), artifact `relatorio-jmeter` | O run do JMeter é do commit da tag. A análise escrita em `resultado-01.md` é da execução local de 09/11 (`b822de8`) |
| E5 | Matriz de rastreabilidade | [docs/rtm.md](https://github.com/Guilherme3578/trabalho-qualidade-de-software/blob/v1.0.0-rc1/docs/rtm.md) | Arquivo do commit da tag, gerado por `scripts/rastreabilidade.py` |
| E6 | Registro de defeitos | [docs/defeitos/](https://github.com/Guilherme3578/trabalho-qualidade-de-software/blob/v1.0.0-rc1/docs/defeitos/README.md) (DEF-001 a DEF-014) | Arquivos do commit da tag |
| E7 | Relatório final de qualidade | [docs/relatorio-qualidade.md](https://github.com/Guilherme3578/trabalho-qualidade-de-software/blob/v1.0.0-rc1/docs/relatorio-qualidade.md) | Arquivo do commit da tag |
| E8 | O que entra na versão | [CHANGELOG.md](https://github.com/Guilherme3578/trabalho-qualidade-de-software/blob/v1.0.0-rc1/CHANGELOG.md) | Arquivo do commit da tag |

## Registros anteriores à tag

Estes documentos registram a avaliação externa feita em `c7992e9`. Eles não foram reescritos e servem
como linha de base. A situação de cada critério no commit da tag está no relatório final (E7, seção 7).

- [docs/entrega/checklist.md](https://github.com/Guilherme3578/trabalho-qualidade-de-software/blob/v1.0.0-rc1/docs/entrega/checklist.md)
- [docs/decisao-qualidade.md](https://github.com/Guilherme3578/trabalho-qualidade-de-software/blob/v1.0.0-rc1/docs/decisao-qualidade.md)

## Como conferir

```bash
git fetch --tags
SHA=$(git rev-list -n 1 v1.0.0-rc1)
gh run list --workflow ci.yml --commit "$SHA"
gh run list --workflow desempenho.yml --commit "$SHA"
gh run download <id-do-run-de-CI> -n relatorios-de-teste
curl -s "https://sonarcloud.io/api/project_analyses/search?project=Guilherme3578_trabalho-qualidade-de-software&branch=main" | grep "$SHA"
```

O `headSha` de cada run e a `revision` da análise do Sonar têm de ser iguais ao SHA da tag.
