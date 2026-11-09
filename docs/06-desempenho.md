# 6. Desempenho (JMeter)

> 📝 **Rascunho para revisão.** Seção 6.4 preenchida com a execução de 08/11/2026.

## 6.1 Objetivo (RNF01)

Verificar que a API mantém tempo de resposta adequado e não apresenta erros com usuários simultâneos.
O teste também verifica, **sob carga**, que a detecção de conflito continua correta.

## 6.2 Cenário — `src/test/jmeter/carga-reservas.jmx`

| Etapa | Descrição |
|---|---|
| Preparação (1 usuário) | Login do coordenador, descoberta de sala e professor, criação de uma reserva base |
| Carga | **20 usuários** simultâneos (rampa de 10 s), **25 iterações** cada = 2.000 requisições |
| 01 | Pesquisar salas disponíveis (consulta com verificação de disponibilidade) |
| 02 | Agenda do dia |
| 03 | **Tentativa de reserva conflitante**: deve receber `409` em 100% das vezes |
| 04 | Relatório de utilização |

Parâmetros ajustáveis: `-Jusuarios=50 -Jloops=40 -Jhost=... -Jport=...`

## 6.3 Critérios de aceite

| Métrica | Meta | Como é verificada |
|---|---|---|
| Taxa de erro | < 1% | `errorRateThresholdInPercent` no `pom.xml` (o build falha) |
| Tempo máximo por requisição | ≤ 1000 ms | Duration Assertion (conta como erro) |
| Percentil 95 | ≤ 500 ms | Dashboard HTML do JMeter |
| Dupla reserva sob carga | 0 aceitas | Response Assertion `409` na etapa 03 |

## 6.4 Resultados

> Executar: `docker compose up -d mongo wiremock`, iniciar a aplicação com o perfil `dev` e rodar
> `./mvnw verify -Pdesempenho`. O relatório HTML fica em `target/jmeter/reports/carga-reservas/index.html`.

Execução de 08/11/2026: 20 usuários × 25 iterações, **2.004 amostras, 0 erros (0,00%)**, vazão total de 177 req/s.

| Requisição | Amostras | Média (ms) | p95 (ms) | p99 (ms) | Erros (%) | Vazão (req/s) |
|---|---|---|---|---|---|---|
| 01 Pesquisar salas disponíveis | 500 | 16 | 26 | 36 | 0,00 | 48,7 |
| 02 Agenda do dia | 500 | 6 | 9 | 13 | 0,00 | 48,9 |
| 03 Reserva conflitante (409) | 500 | 14 | 22 | 29 | 0,00 | 48,9 |
| 04 Relatório de utilização | 500 | 8 | 13 | 19 | 0,00 | 49,1 |
| **Total** (inclui 4 requisições de preparação) | 2.004 | 11 | 21 | 31 | 0,00 | 177,4 |

Todas as metas da seção 6.3 foram atingidas: erro < 1%, nenhuma requisição acima de 1000 ms
(máximo de 347 ms, no login com BCrypt) e nenhuma das 500 tentativas conflitantes foi aceita.

**Ambiente da medição:** Intel Core i5-1335U (12 threads), 6 GB de RAM, Ubuntu 26.04.1 LTS,
Java 21, MongoDB 8.0.32 em replica set local, aplicação e JMeter na mesma máquina.

## 6.5 Otimizações aplicadas

- Índices compostos `(salaId, status, inicio)`, `(professorId, status, inicio)` e
  `(itens.materialId, status, inicio)`: a verificação de conflito consulta apenas reservas ativas do recurso.
- A detecção de conflito lê do banco somente as reservas sobrepostas ao intervalo, e não a agenda inteira.
- O "toque" no recurso é feito **depois** das leituras, o que reduz a janela de disputa entre transações.
