# Changelog

Formato baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/). Versões seguem
[SemVer](https://semver.org/lang/pt-BR/). Uma tag publicada nunca é movida: qualquer correção depois de
`v1.0.0-rc1` sai como `v1.0.0-rc2`.

## [1.0.0-rc1] — 2026-11-13

Primeiro release candidate, liberado **só para avaliação acadêmica e homologação**. A recomendação está no
[relatório final de qualidade](docs/relatorio-qualidade.md), e o pacote de evidências em
[docs/evidencias/README.md](docs/evidencias/README.md).

### Adicionado

- API REST em Java 21 + Spring Boot para alocação de salas, professores e materiais (RF01–RF14):
  autenticação JWT com perfis, cadastros, pesquisa por disponibilidade, criação, alteração e cancelamento
  de reservas, detecção de sobreposição (inclusive na agenda do professor), aprovação de recursos
  restritos, bloqueio por manutenção, retirada e devolução de materiais, auditoria, notificação externa
  simulada e relatório de utilização.
- Front-end Angular 21 (`frontend/`), compilado pelo Maven e servido pelo Spring Boot em `:8080`.
- Documentação da API com springdoc (`/swagger-ui.html`, `/v3/api-docs`) e collection Postman.
- Imagem Docker e `docker-compose.yml` (MySQL 8.4, WireMock e a aplicação).
- Estratégia de testes: JUnit 5 (com testes parametrizados), integração com Testcontainers, API
  caixa-preta com REST Assured e Newman, E2E com Selenium, WireMock, BDD com Cucumber, teste de
  concorrência contra dupla reserva, Vitest no front e carga com JMeter.
- CI no GitHub Actions em todo PR, na `main` e nas tags `v*`: build, testes, cobertura do JaCoCo
  (≥ 80% de linhas e ≥ 70% de desvios), testes e build do front e teste de carga.
- Análise estática no SonarCloud (organização `guilherme3578`) a cada push na `main` (PR #8).
- Documentação de qualidade: matriz de rastreabilidade (`docs/rtm.md`), registro de defeitos
  (`docs/defeitos/`), resultado do teste de carga (`docs/performance/resultado-01.md`), diagramas de
  contexto e de sequência e relatório final de qualidade (PR #9).

### Alterado

- **Incompatível:** a persistência passou de MongoDB para MySQL 8.4 com JPA e Flyway, com FKs, CHECK e
  `SELECT ... FOR UPDATE` contra dupla reserva (PR #3). Bancos MongoDB anteriores não são migrados.
- O front antigo em HTML/JS foi substituído pelo Angular (PR #4).
- A proteção da `main` exige PR e o CI verde.

### Corrigido

Os detalhes de cada defeito, com severidade, causa e commits, estão em
[docs/defeitos/](docs/defeitos/README.md).

| Defeito | Teste | Correção |
|---|---|---|
| DEF-001 Campos booleanos ou numéricos omitidos no JSON geravam 400 | `bb802d6` | `b71e398` |
| DEF-002 Usuário desativado ou rebaixado mantinha o acesso até o token expirar | `0e954de` | `fc6a5ff` |
| DEF-003 Reserva pendente podia ser aprovada com recurso em manutenção | `4716dbc` | `c008c1b` |
| DEF-004 Bloqueio e reserva simultâneos ignoravam um ao outro | `d60994b` | `2673ca5` |
| DEF-005 O limitador de login não descartava tentativas expiradas | `c017cc6` | `2fc4ebe` |
| DEF-006 O plano JMeter nunca era executado no perfil `desempenho` | — | `521dad2` |
| DEF-007 Testes de integração falhavam no CI: o container do banco não subia antes do contexto Spring | — | `7602908`, `da449c3` |
| DEF-008 A aplicação servia o front antigo em vez do Angular | `86ef026` | `ad92b2c` |
| DEF-009 O teste de carga no CI rodava a aplicação a partir do jar que o build sobrescreve | — | `1cbdc13` |

### Pendências conhecidas

- **Sem revisão por pares.** O projeto tem um único autor. O risco foi aceito e está registrado no
  relatório final de qualidade (C8).
- DEF-010 a DEF-014 (regras P1–P5) continuam abertos, aguardando decisão de regra de negócio.
- O front Angular não tem medição de cobertura.
- O teste de carga rodou só em máquina local, com 20 usuários.

[1.0.0-rc1]: https://github.com/Guilherme3578/trabalho-qualidade-de-software/releases/tag/v1.0.0-rc1
