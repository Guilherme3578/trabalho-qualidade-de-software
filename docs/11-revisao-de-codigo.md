# 11. Revisão de código antes do front-end

> 📝 **Rascunho para revisão.** Varredura feita em 08/11/2026 sobre o back-end inteiro (serviços, segurança,
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

## 11.4 Pendências que precisam de decisão (não alteradas)

Estes pontos são decisões de regra de negócio, por isso não foram mudados sem combinar:

| # | Situação | Risco | Sugestão |
|---|---|---|---|
| P1 | Reduzir a **capacidade de uma sala** abaixo do número de alunos de reservas futuras é aceito. | Reserva fica inválida pela RN09 | Recusar com a lista de reservas afetadas, ou só avisar (como o bloqueio faz) |
| P2 | Reduzir a **quantidade total de um material** abaixo do que já está reservado é aceito. | Mais itens reservados do que existem | Mesmo tratamento da P1 |
| P3 | **Desativar** sala, professor ou material não afeta as reservas futuras já feitas. | Reserva de recurso inativo | Listar as reservas afetadas na resposta, ou cancelá-las com notificação |
| P4 | O `issuer` do token não é verificado na leitura (só a assinatura e a validade). | Baixo, porque a chave é exclusiva da aplicação | Validar `iss = alocacao-salas` no `JwtDecoder` |
| P5 | A CSP permite `style-src 'unsafe-inline'`. | Baixo | Rever na reconstrução do front-end |

## 11.5 Pendências de ambiente

- Docker: o usuário do Linux ainda não está no grupo `docker`. Até lá, os testes de integração rodam com o
  MySQL local (`MYSQL_URL_TESTE`, ver [10-relatorio-testes-backend.md](10-relatorio-testes-backend.md)).
- SonarCloud: trocar `sonar.organization` e `sonar.projectKey` no `pom.xml` pelos do projeto criado.
- Repositório no GitHub: necessário para o GitHub Actions e o SonarCloud rodarem nos pull requests.
