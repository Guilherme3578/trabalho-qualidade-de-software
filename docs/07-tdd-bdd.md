# 7. TDD e BDD

> Para a evidência ficar completa, os commits devem mostrar a ordem
> teste → implementação (ver 7.3).

## 7.1 BDD — Retirada e devolução de materiais (RF09)

Os critérios de aceite foram escritos **antes** da implementação, em linguagem de negócio (Gherkin em português):
`src/test/resources/features/retirada_devolucao.feature`.

| Cenário | Regra validada |
|---|---|
| Retirada dentro da quantidade reservada | Fluxo feliz |
| Retirada acima da quantidade reservada | Não retirar mais do que foi reservado |
| Devolução maior que a retirada | Não devolver mais do que foi retirado |
| Concluir com materiais pendentes | A reserva só é concluída depois de todas as devoluções |
| Conclusão após devolução completa | Fluxo feliz + auditoria das movimentações |
| Somente o responsável registra (4 exemplos) | Autorização por perfil |

- Os passos (`RetiradaDevolucaoSteps`) usam **somente a API HTTP**, como um usuário real.
- Execução: `CucumberIT`. Relatório: `target/cucumber/relatorio.html`.

## 7.2 TDD — Validação da alocação docente e de recursos restritos (RF07)

A classe `AvaliadorAprovacao` foi desenvolvida em ciclos **vermelho → verde → refatorar**.
`AvaliadorAprovacaoTest` está organizado em `@Nested` por ciclo:

| Ciclo | Teste escrito primeiro (vermelho) | Implementação mínima (verde) | Refatoração |
|---|---|---|---|
| 1 | Sem recurso restrito e docente habilitado → lista vazia | Retornar lista vazia | — |
| 2 | Sala restrita / material restrito → motivo correspondente | Verificar `isRestrita()` e `isRestrito()` | Acumular motivos em uma lista |
| 3 | Professor sem a competência → motivo | `competencias.contains(disciplina)` | Comparação sem diferenciar maiúsculas e espaços (`possuiCompetencia`), coberta por teste parametrizado |

## 7.3 Como registrar a evidência no Git

Sugestão de sequência de commits (um por passo):

```
test: cenários BDD de retirada/devolução (falhando)
feat: registrar retirada e devolução de materiais
test(tdd): ciclo 1 - aprovação automática sem restrições (vermelho)
feat(tdd): ciclo 1 - implementação mínima (verde)
test(tdd): ciclo 2 - recursos restritos exigem aprovação (vermelho)
feat(tdd): ciclo 2 - verificação de sala e material restritos (verde)
test(tdd): ciclo 3 - competência docente (vermelho)
feat(tdd): ciclo 3 - validação de competência (verde)
refactor(tdd): normalização de maiúsculas/espaços na competência
```

Capturas de tela do teste falhando e depois passando (IntelliJ ou `./mvnw test`) podem ser salvas em `docs/evidencias/`.
