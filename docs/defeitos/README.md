# Registro de defeitos

Cada defeito tem um arquivo próprio com passos para reproduzir, causa, correção e os commits do teste
e da correção. Defeitos de código seguem a ordem `test:` (teste que falha) → `fix:` (correção que o faz
passar), visível no histórico do Git.

## Escalas

| Severidade | Critério |
|---|---|
| Crítica | Perda ou corrupção de dados, ou sistema indisponível, sem contorno |
| Alta | Regra de negócio ou de segurança violada, ou evidência de qualidade falsa |
| Média | Comportamento incorreto com contorno, ou risco operacional limitado |
| Baixa | Endurecimento ou melhoria sem impacto funcional observado |

| Status | Significado |
|---|---|
| Aberto | Confirmado, ainda sem correção (ou aguardando decisão de regra de negócio) |
| Corrigido | Correção commitada, ainda não verificada na `main` |
| Fechado | Correção na `main` e teste que reproduz passando no CI |

## Defeitos

| ID | Defeito | Severidade | Status | Requisitos | Teste (`test:`) | Correção (`fix:`) | PR |
|---|---|---|---|---|---|---|---|
| [DEF-001](DEF-001.md) | Campos booleanos ou numéricos omitidos no JSON geram 400 | Média | Fechado | RF02, RNF03 | [`bb802d6`](https://github.com/Guilherme3578/trabalho-qualidade-de-software/commit/bb802d6) | [`b71e398`](https://github.com/Guilherme3578/trabalho-qualidade-de-software/commit/b71e398) | — |
| [DEF-002](DEF-002.md) | Usuário desativado ou com perfil rebaixado mantém o acesso até o token expirar | Alta | Fechado | RNF02, RF01 | [`0e954de`](https://github.com/Guilherme3578/trabalho-qualidade-de-software/commit/0e954de) | [`fc6a5ff`](https://github.com/Guilherme3578/trabalho-qualidade-de-software/commit/fc6a5ff) | — |
| [DEF-003](DEF-003.md) | Reserva pendente pode ser aprovada com recurso em manutenção | Alta | Fechado | RN04, RF08, RF07 | [`4716dbc`](https://github.com/Guilherme3578/trabalho-qualidade-de-software/commit/4716dbc) | [`c008c1b`](https://github.com/Guilherme3578/trabalho-qualidade-de-software/commit/c008c1b) | — |
| [DEF-004](DEF-004.md) | Bloqueio de manutenção e reserva criados ao mesmo tempo ignoram um ao outro | Alta | Fechado | RN03, RN04, RF08 | [`d60994b`](https://github.com/Guilherme3578/trabalho-qualidade-de-software/commit/d60994b) | [`2673ca5`](https://github.com/Guilherme3578/trabalho-qualidade-de-software/commit/2673ca5) | — |
| [DEF-005](DEF-005.md) | Limitador de tentativas de login nunca descarta tentativas expiradas | Média | Fechado | RNF02 | [`c017cc6`](https://github.com/Guilherme3578/trabalho-qualidade-de-software/commit/c017cc6) | [`2fc4ebe`](https://github.com/Guilherme3578/trabalho-qualidade-de-software/commit/2fc4ebe) | — |
| [DEF-006](DEF-006.md) | Plano JMeter nunca era executado no perfil `desempenho` | Alta | Fechado | RNF01 | — | [`521dad2`](https://github.com/Guilherme3578/trabalho-qualidade-de-software/commit/521dad2) | #2 |
| [DEF-007](DEF-007.md) | Testes de integração falham no CI: container do banco não iniciado antes do contexto Spring | Alta | Fechado | — (todos os testes de integração) | — | [`7602908`](https://github.com/Guilherme3578/trabalho-qualidade-de-software/commit/7602908), [`da449c3`](https://github.com/Guilherme3578/trabalho-qualidade-de-software/commit/da449c3) | #1 |
| [DEF-008](DEF-008.md) | A aplicação serve o front antigo em vez do Angular | Alta | Fechado | RF13 | [`86ef026`](https://github.com/Guilherme3578/trabalho-qualidade-de-software/commit/86ef026) | [`ad92b2c`](https://github.com/Guilherme3578/trabalho-qualidade-de-software/commit/ad92b2c) | #4 |
| [DEF-009](DEF-009.md) | Teste de carga no CI roda a aplicação a partir do `.jar` que o build sobrescreve | Média | Fechado | RNF01 | — | [`1cbdc13`](https://github.com/Guilherme3578/trabalho-qualidade-de-software/commit/1cbdc13) | #4 |
| DEF-010 | Reduzir a capacidade da sala abaixo dos alunos de reservas futuras é aceito | Média | Aberto (aguardando decisão) | RN09 | — | — | — |
| DEF-011 | Reduzir a quantidade total de um material abaixo do já reservado é aceito | Média | Aberto (aguardando decisão) | RF09, RN02 | — | — | — |
| DEF-012 | Desativar sala, professor ou material não afeta reservas futuras já feitas | Média | Aberto (aguardando decisão) | RF02, RF04 | — | — | — |
| DEF-013 | O `issuer` do JWT não é verificado na leitura do token | Baixa | Aberto (aguardando decisão) | RNF02 | — | — | — |
| DEF-014 | A CSP permite `style-src 'unsafe-inline'` | Baixa | Aberto (aguardando decisão) | RNF02 | — | — | — |

DEF-010 a DEF-014 são as pendências P1–P5 de [11 — revisão de código](../11-revisao-de-codigo.md) §11.4.
Dependem de uma decisão de regra de negócio, por isso não foram corrigidas sem combinar com o grupo.
Ganham arquivo próprio quando forem tratadas.

## Resumo

| Severidade | Fechados | Abertos | Total |
|---|---|---|---|
| Crítica | 0 | 0 | 0 |
| Alta | 6 | 0 | 6 |
| Média | 3 | 3 | 6 |
| Baixa | 0 | 2 | 2 |
| **Total** | **9** | **5** | **14** |

## Como registrar um defeito novo

1. Copie um arquivo existente para `DEF-NNN.md` com o próximo número e preencha os campos.
2. Commit `test:` com um teste que reproduz o defeito e falha.
3. Commit `fix:` com a correção que faz o teste passar, citando o ID na mensagem.
4. Atualize o status e os commits no arquivo e nesta tabela.
