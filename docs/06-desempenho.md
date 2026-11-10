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

> Executar: suba o MySQL (`docker compose up -d mysql wiremock` ou um MySQL local), inicie a aplicação com o
> perfil `dev` e rode `./mvnw verify -Pdesempenho` (porta diferente: `-Djmeter.port=8081`). Apague
> `target/jmeter` antes de repetir, porque o JMeter não sobrescreve o relatório HTML. O relatório fica em
> `target/jmeter/reports/carga-reservas/index.html`.

### Execução atual, com MySQL (10/11/2026)

20 usuários × 25 iterações, **2.004 amostras, 0 erros (0,00%)**, vazão total de 149 req/s.

| Requisição | Amostras | Média (ms) | p95 (ms) | p99 (ms) | Erros (%) | Vazão (req/s) |
|---|---|---|---|---|---|---|
| 01 Pesquisar salas disponíveis | 500 | 38 | 62 | 75 | 0,00 | 42,4 |
| 02 Agenda do dia | 500 | 21 | 37 | 48 | 0,00 | 42,6 |
| 03 Reserva conflitante (409) | 500 | 32 | 57 | 69 | 0,00 | 42,8 |
| 04 Relatório de utilização | 500 | 22 | 37 | 49 | 0,00 | 43,1 |
| **Total** (inclui 4 requisições de preparação) | 2.004 | 29 | 52 | 69 | 0,00 | 148,7 |

Todas as metas da seção 6.3 foram atingidas: erro < 1%, nenhuma requisição acima de 1000 ms
(máximo de 475 ms, no login com BCrypt) e nenhuma das 500 tentativas conflitantes foi aceita.

**Ambiente da medição:** Intel Core i5-1335U (12 threads), 6 GB de RAM, Ubuntu 26.04.1 LTS,
Java 21, MySQL 8.4.11 local com configuração padrão (buffer pool de 128 MB), aplicação e JMeter na mesma
máquina.

### Comparação com a versão anterior (MongoDB, 08/11/2026)

| Métrica | MongoDB 8.0 | MySQL 8.4 |
|---|---|---|
| Tempo médio (total) | 11 ms | 29 ms |
| p95 (total) | 21 ms | 52 ms |
| Vazão total | 177 req/s | 149 req/s |
| Erros / duplas reservas aceitas | 0 / 0 | 0 / 0 |

O tempo médio aumentou porque cada verificação de conflito agora faz junções (reserva × sala × professor
× itens) e trava as linhas dos recursos com `SELECT ... FOR UPDATE`. Mesmo assim, o p95 fica cerca de 10
vezes abaixo da meta de 500 ms.

## 6.5 Otimizações aplicadas

- Índices compostos `(sala_id, status, inicio)` e `(professor_id, status, inicio)` em `reservas`, e
  `material_id` em `reserva_itens`: a verificação de conflito lê apenas reservas ativas do recurso.
- A detecção de conflito busca no banco somente as reservas sobrepostas ao intervalo, e não a agenda inteira.
- As travas pessimistas são pegas sempre na mesma ordem (sala → professor → materiais), o que evita
  deadlocks e as repetições que eles causariam sob carga.
- Isolamento `READ COMMITTED`: uma transação que esperou a trava enxerga imediatamente a reserva que
  acabou de ser confirmada, sem precisar ser repetida.
- `hibernate.default_batch_fetch_size=100`: listas de reservas carregam itens, salas e professores em
  lotes, evitando o problema de N+1 consultas.
