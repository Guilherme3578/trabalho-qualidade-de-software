# 6. Desempenho (JMeter)

> 📝 **Rascunho para revisão.** Preencher a seção 6.4 com os números da execução real.

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
> `./mvnw verify -Pdesempenho`. Copiar os números de `target/jmeter/reports/.../index.html`.

| Requisição | Amostras | Média (ms) | p95 (ms) | p99 (ms) | Erros (%) | Vazão (req/s) |
|---|---|---|---|---|---|---|
| 01 Pesquisar salas disponíveis | | | | | | |
| 02 Agenda do dia | | | | | | |
| 03 Reserva conflitante (409) | | | | | | |
| 04 Relatório de utilização | | | | | | |

**Ambiente da medição:** (CPU, memória, SO, versão do MongoDB)

## 6.5 Otimizações aplicadas

- Índices compostos `(salaId, status, inicio)`, `(professorId, status, inicio)` e
  `(itens.materialId, status, inicio)`: a verificação de conflito consulta apenas reservas ativas do recurso.
- A detecção de conflito lê do banco somente as reservas sobrepostas ao intervalo, e não a agenda inteira.
- O "toque" no recurso é feito **depois** das leituras, o que reduz a janela de disputa entre transações.
