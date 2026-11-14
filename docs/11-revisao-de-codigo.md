# 11. Revisão de código antes do front-end

> Varredura feita em 08/11/2026 sobre o back-end inteiro (serviços, segurança,
> concorrência, configuração) e o JavaScript atual da interface.

## 11.1 Como a revisão foi feita

1. Leitura dos pontos críticos: criação, alteração e aprovação de reservas, bloqueios de manutenção,
   autenticação, autorização e tratamento de erros.
2. Execução de toda a suíte de testes contra um banco real (na época, MongoDB; hoje, MySQL 8.4), incluindo os testes de integração,
   concorrência, BDD e E2E, que ainda não tinham rodado.
3. Execução da collection do Postman contra a aplicação no perfil `dev`.
4. Para cada defeito confirmado: primeiro um teste que reproduz o problema e falha (commit `test:`), depois a
   correção que o faz passar (commit `fix:`). O histórico do Git mostra essa ordem.

## 11.2 Defeitos encontrados e corrigidos

| # | Defeito | Gravidade | Requisito | Teste que reproduz | Correção |
|---|---|---|---|---|---|
| 1 | Omitir um campo `boolean`/`int` no JSON (ex.: `restrita`) gerava 400 "corpo malformado" em vez do valor padrão ou da mensagem de validação. Causa: o Jackson 3 (Spring Boot 4) recusa nulos em primitivos por padrão. | Média | RF02, RNF03 | `CadastroPesquisaApiIT.booleanosOpcionaisOmitidos`, `SegurancaApiIT.validacaoCampos` | `spring.jackson.deserialization.fail-on-null-for-primitives: false` |
| 2 | Um usuário desativado, ou com perfil rebaixado, mantinha o acesso por até 2 h, porque o perfil vinha do token JWT. | Alta | RNF02 | `SegurancaApiIT.usuarioDesativadoPerdeAcesso`, `SegurancaApiIT.perfilAtualizadoValeImediatamente` | O conversor do JWT carrega o usuário do banco a cada requisição: inativo → 401; perfil vem do cadastro atual |
| 3 | Uma reserva pendente podia ser aprovada mesmo com a sala, o professor ou um material em manutenção no horário. | Alta | RN04, RF08 | `ReservaServiceIT.aprovarRecursoEmManutencao` | `aprovar` revalida os bloqueios e roda com o controle de concorrência |
| 4 | **Condição de corrida:** bloqueio e reserva criados ao mesmo tempo eram aceitos sem enxergar um ao outro. A reserva ficava dentro da manutenção e o bloqueio dizia "nenhuma reserva afetada". | Alta | RN03, RN04 | `ConcorrenciaReservaIT.bloqueioSimultaneoAReserva` (20 rodadas) | A criação de bloqueio usa `ProtecaoConcorrencia` e "toca" o documento do recurso, como as reservas |
| 5 | O limitador de tentativas de login nunca descartava as tentativas expiradas. Tentar e-mails aleatórios fazia a memória crescer sem limite. | Média | RNF02 | `LimitadorTentativasLoginTest.descartaTentativasExpiradas` | Remoção das janelas expiradas quando o mapa atinge 1000 e-mails |

Depois das correções: **250 testes automatizados aprovados** (134 unitários e 116 de integração, API,
concorrência, BDD e E2E), **85,8% de cobertura de linhas** e **59 requisições / 124 asserções do Postman
aprovadas**. Os detalhes estão em [09-relatorio-testes-api.md](09-relatorio-testes-api.md) e
[10-relatorio-testes-backend.md](10-relatorio-testes-backend.md).

## 11.3 Pontos verificados sem defeito

| Área | O que foi conferido |
|---|---|
| Dupla reserva | 20 pedidos simultâneos para a mesma sala, professor ou estoque: só os pedidos possíveis são aceitos (também via HTTP) |
| Injeção de SQL | Consultas JPQL parametrizadas; curingas do LIKE escapados (após a migração para MySQL) |
| Login | Mensagem genérica para e-mail inexistente e senha errada; comparação com hash fictício para não revelar, pelo tempo de resposta, se o e-mail existe |
| Erros | Problem Details sem stack trace, mensagem interna ou nome de exceção |
| JWT | Assinatura HS256 com segredo de no mínimo 32 caracteres (a aplicação não sobe sem ele); token adulterado ou forjado → 401 |
| Front-end atual | Todos os dados vindos da API passam por `esc()` antes de ir para `innerHTML`; CSP com `script-src 'self'` |

## 11.4 Pendências de regra de negócio (decididas e corrigidas)

Estes pontos dependiam de decisão de regra de negócio e ficaram sem alteração até o autor decidir, em
13/11/2026. Cada um virou um defeito com teste (`test:`) e correção (`fix:`):

| # | Situação | Decisão | Defeito |
|---|---|---|---|
| P1 | Reduzir a **capacidade de uma sala** abaixo do número de alunos de reservas futuras era aceito | Recusar com 409 `RESERVAS_AFETADAS`, listando as reservas | [DEF-010](defeitos/DEF-010.md) |
| P2 | Reduzir a **quantidade total de um material** abaixo do que já está reservado era aceito | Recusar com 409 `RESERVAS_AFETADAS`, listando as reservas | [DEF-011](defeitos/DEF-011.md) |
| P3 | **Desativar** sala, professor ou material não informava as reservas futuras | Aceitar e listar as reservas afetadas na resposta (`reservasAfetadas`) | [DEF-012](defeitos/DEF-012.md) |
| P4 | O `issuer` do token não era verificado na leitura | Validar `iss = alocacao-salas` no `JwtDecoder` | [DEF-013](defeitos/DEF-013.md) |
| P5 | A CSP permitia `style-src 'unsafe-inline'` | Nonce por requisição, repassado ao Angular por `ngCspNonce` | [DEF-014](defeitos/DEF-014.md) |

## 11.5 Pendências de ambiente (resolvidas)

- Docker: sem acesso ao Docker na máquina de desenvolvimento, os testes de integração rodam com um MySQL
  local (`MYSQL_URL_TESTE`, ver [10-relatorio-testes-backend.md](10-relatorio-testes-backend.md)). No CI
  eles rodam com Testcontainers.
- SonarCloud: organização e chave do projeto configuradas no `pom.xml` (PR #8).
- Repositório no GitHub: publicado, com CI em todo pull request.

Os defeitos desta revisão estão no [registro de defeitos](defeitos/README.md) como DEF-001 a DEF-005, e
as pendências P1–P5 como DEF-010 a DEF-014 (corrigidas, ver §11.4).
