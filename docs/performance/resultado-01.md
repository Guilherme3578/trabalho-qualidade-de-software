# Resultado do teste de carga nº 01

| Item | Valor |
|---|---|
| Plano | [`src/test/jmeter/carga-reservas.jmx`](../../src/test/jmeter/carga-reservas.jmx) (cenário e critérios em [06 — desempenho](../06-desempenho.md) §6.2–6.3) |
| Data e hora | 09/11/2026, 23h31min15s às 23h31min29s (horário de Brasília). O arquivo de resultados leva a data UTC: `20261110-carga-reservas.csv` |
| Versão testada | Branch `feat/migracao-mysql` (PR #3), registrada no commit `b822de8` |
| Comando | `./mvnw verify -Pdesempenho -Djmeter.port=8091`, com a aplicação no perfil `dev` |
| Carga | 20 usuários simultâneos, rampa de 10 s, 25 iterações cada, mais 4 requisições de preparação |
| Ambiente | Intel Core i5-1335U (12 threads), 6 GB de RAM, Ubuntu 26.04.1 LTS, Java 21, MySQL 8.4.11 local com configuração padrão (buffer pool de 128 MB); aplicação e JMeter na mesma máquina |
| Resultado | ✅ **APROVADO**: todos os critérios atingidos |

## Resultado por requisição

Fonte: `target/jmeter/reports/carga-reservas/statistics.json`. Tempos em milissegundos.

| Requisição | Amostras | Erros | Média | Mín. | Máx. | p90 | p95 | p99 | Vazão (req/s) |
|---|---|---|---|---|---|---|---|---|---|
| 01 Pesquisar salas disponíveis | 500 | 0 | 38 | 19 | 103 | 54 | 62 | 75 | 42,4 |
| 02 Agenda do dia | 500 | 0 | 21 | 10 | 90 | 32 | 37 | 48 | 42,6 |
| 03 Tentativa de reserva conflitante (espera 409) | 500 | 0 | 32 | 13 | 103 | 48 | 57 | 69 | 42,8 |
| 04 Relatório de utilização | 500 | 0 | 22 | 10 | 89 | 33 | 37 | 49 | 43,1 |
| Preparação: login do coordenador | 1 | 0 | 475 | 475 | 475 | — | — | — | — |
| Preparação: descobre sala, professor, cria reserva base | 3 | 0 | 154 | 67 | 284 | — | — | — | — |
| **Total** | **2.004** | **0** | **29** | **10** | **475** | **44** | **52** | **69** | **148,7** |

## Critérios de aceite

| Critério (§6.3) | Meta | Medido | Situação |
|---|---|---|---|
| Taxa de erro | < 1% | 0,00% (0 de 2.004) | ✅ |
| Tempo máximo por requisição | ≤ 1000 ms | 475 ms (login, por causa do BCrypt); 103 ms nas requisições de carga | ✅ |
| Percentil 95 | ≤ 500 ms | 52 ms no total; 62 ms na pior requisição | ✅ |
| Dupla reserva sob carga | 0 aceitas | 500 de 500 tentativas conflitantes receberam `409` | ✅ |

## Análise

- O p95 ficou cerca de 10 vezes abaixo da meta. A requisição mais cara é a pesquisa de salas
  disponíveis, que cruza salas, reservas e bloqueios do período.
- A tentativa conflitante (etapa 03) passa por `SELECT ... FOR UPDATE` em todas as 500 execuções e,
  mesmo assim, fica em 32 ms de média: a trava é pega e liberada sem fila perceptível com 20 usuários.
- Comparado com a versão em MongoDB (08/11), a média subiu de 11 para 29 ms e a vazão caiu de 177 para
  149 req/s, por causa das junções e das travas de linha. O detalhamento está em
  [06 — desempenho](../06-desempenho.md) §6.4.

## Reproduções posteriores

| Data | Onde | Resultado |
|---|---|---|
| 13/11/2026 | Clone limpo, máquina do avaliador ([checklist](../entrega/checklist.md) §1, passo 10) | 2.004 requisições, 0% de erro, média 38 ms, p95 64 ms |
| A cada PR | Job `jmeter` do workflow **Desempenho (JMeter)** no GitHub Actions | O build falha se o erro passar de 1% (`errorRateThresholdInPercent`) |

## Limitações

- Carga nominal de 20 usuários numa máquina de desenvolvimento, com o JMeter disputando CPU com a
  aplicação e o banco. Não mede o comportamento em produção nem o limite de saturação.
- Banco com poucos dados (os de demonstração do perfil `dev` mais o que o próprio teste cria).
