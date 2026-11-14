# 10. Relatório de testes de back-end

> Gerado automaticamente por `scripts/relatorio_backend.py` a partir dos relatórios do JUnit (`target/surefire-reports`, `target/failsafe-reports`) e do JaCoCo (`target/site/jacoco`), após `./mvnw verify`.

## 10.1 Resumo

**Resultado: ✅ APROVADO**

| Item | Valor |
|---|---|
| Data da geração | 13/11/2026 23:30 |
| Testes executados | 277 |
| Aprovados | 277 |
| Falhas ou erros | 0 |
| Pulados | 0 |
| Tempo somado | 80.8 s |
| Cobertura de linhas | 89.6% (meta: 80%) |
| Cobertura de desvios (branches) | 74.9% |
| Cobertura de métodos | 86.3% |

## 10.2 Resultado por nível da pirâmide

| Nível | Classes | Testes | Falhas | Tempo |
|---|---|---|---|---|
| Unitário | 8 | 136 | 0 | 2.7 s |
| API caixa-preta | 7 | 81 | 0 | 22.6 s |
| Integração (MySQL real + WireMock) | 2 | 30 | 0 | 9.6 s |
| Integridade do banco (MySQL) | 1 | 6 | 0 | 0.6 s |
| Concorrência | 1 | 7 | 0 | 2.6 s |
| BDD (Cucumber) | 1 | 9 | 0 | 6.0 s |
| E2E (Selenium) | 1 | 8 | 0 | 36.7 s |

## 10.3 Resultado por classe de teste

| Nível | Classe | Descrição | Testes | Falhas | Tempo |
|---|---|---|---|---|---|
| Unitário | `LimitadorTentativasLoginTest` | [RNF02] Proteção contra força bruta no login | 5 | ✅ 0 | 0.11 s |
| Unitário | `UtilitariosTest` | Utilitários de filtro e JSON | 11 | ✅ 0 | 0.04 s |
| Unitário | `ProtecaoConcorrenciaTest` | [RF06] Repetição de transações em deadlock ou espera de trava | 4 | ✅ 0 | 2.03 s |
| Unitário | `StatusReservaTest` | [RN05] Máquina de estados da reserva | 71 | ✅ 0 | 0.13 s |
| Unitário | `AvaliadorAprovacaoTest` | [RF07] Avaliação da necessidade de aprovação | 8 | ✅ 0 | 0.04 s |
| Unitário | `CalculadoraOcupacaoTest` | [RN02] Pico de uso simultâneo de materiais | 8 | ✅ 0 | 0.05 s |
| Unitário | `IntervaloTest` | Intervalo de tempo | 14 | ✅ 0 | 0.31 s |
| Unitário | `PoliticaHorarioTest` | Política de horário das reservas | 15 | ✅ 0 | 0.04 s |
| API caixa-preta | `AutenticacaoApiIT` | [RF01] API caixa-preta: autenticação | 5 | ✅ 0 | 1.20 s |
| API caixa-preta | `CadastroPesquisaApiIT` | [RF02][RF03] API caixa-preta: cadastros e pesquisa | 9 | ✅ 0 | 4.09 s |
| API caixa-preta | `DocumentacaoApiIT` | [RF14] Documentação pública da API | 2 | ✅ 0 | 0.91 s |
| API caixa-preta | `RelatorioApiIT` | [RF12] API caixa-preta: relatórios | 3 | ✅ 0 | 1.56 s |
| API caixa-preta | `ReservaApiIT` | [RF04][RF05] API caixa-preta: reservas | 9 | ✅ 0 | 4.48 s |
| API caixa-preta | `ReservasAfetadasApiIT` | [RF02] API caixa-preta: alterações de cadastro que afetam reservas futuras | 7 | ✅ 0 | 3.95 s |
| API caixa-preta | `SegurancaApiIT` | [RNF02][RNF03][RNF04] API caixa-preta: segurança | 46 | ✅ 0 | 6.40 s |
| Integração (MySQL real + WireMock) | `NotificacaoWireMockIT` | [RF11] Integração com API externa de notificação (WireMock) | 5 | ✅ 0 | 2.07 s |
| Integração (MySQL real + WireMock) | `ReservaServiceIT` | Integração: regras de reserva com MySQL real | 25 | ✅ 0 | 7.52 s |
| Integridade do banco (MySQL) | `IntegridadeRelacionalIT` | [RNF07] Banco relacional: integridade garantida pelo MySQL | 6 | ✅ 0 | 0.60 s |
| Concorrência | `ConcorrenciaReservaIT` | [RF06][RN03] Concorrência: solicitações simultâneas | 7 | ✅ 0 | 2.58 s |
| BDD (Cucumber) | `CucumberIT` | CucumberIT | 9 | ✅ 0 | 6.01 s |
| E2E (Selenium) | `FluxoReservaE2EIT` | [RF13] End-to-end: fluxo de reserva pela interface web | 8 | ✅ 0 | 36.67 s |

## 10.4 Falhas

Nenhuma falha. Todos os testes passaram.

## 10.5 Cobertura por pacote

| Pacote | Linhas | Desvios |
|---|---|---|
| `auditoria` | 100.0% | 100.0% |
| `auth` | 98.5% | 90.0% |
| `bloqueio` | 78.9% | 58.3% |
| `comum` | 81.2% | 79.2% |
| `material` | 98.9% | 68.2% |
| `notificacao` | 81.8% | 87.5% |
| `professor` | 84.5% | 50.0% |
| `relatorio` | 85.3% | 50.0% |
| `reserva` | 94.2% | 79.9% |
| `reserva.regras` | 100.0% | 97.5% |
| `sala` | 100.0% | 64.3% |
| `usuario` | 62.8% | 10.0% |
| `web` | 88.6% | 66.7% |

## 10.6 Testes mais lentos

| Classe | Teste | Tempo |
|---|---|---|
| `FluxoReservaE2EIT` | `politicaDeConteudoSemViolacoes` | 5.85 s |
| `FluxoReservaE2EIT` | `escondeSalaOcupada` | 4.21 s |
| `FluxoReservaE2EIT` | `mostraConflito` | 3.91 s |
| `FluxoReservaE2EIT` | `criaReserva` | 3.72 s |
| `FluxoReservaE2EIT` | `recarregaRotaInterna` | 3.04 s |
| `FluxoReservaE2EIT` | `responsivo` | 2.46 s |
| `FluxoReservaE2EIT` | `loginInvalido` | 1.84 s |
| `FluxoReservaE2EIT` | `estilosCarregados` | 1.23 s |
| `NotificacaoWireMockIT` | `timeoutExterno` | 1.19 s |
| `CucumberIT` | `Conclusão após a devolução completa` | 0.81 s |

## 10.7 Lista completa de casos de teste

<details><summary><code>LimitadorTentativasLoginTest</code> — 5 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `liberaAposJanela` | ✅ | 0.002 s |
| `descartaTentativasExpiradas` | ✅ | 0.100 s |
| `bloqueia` | ✅ | 0.002 s |
| `independentes` | ✅ | 0.001 s |
| `sucessoZera` | ✅ | 0.001 s |

</details>

<details><summary><code>UtilitariosTest</code> — 11 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `json` | ✅ | 0.006 s |
| `igual` | ✅ | 0.003 s |
| `filtroEscapado(String, String)[1]` | ✅ | 0.002 s |
| `filtroEscapado(String, String)[2]` | ✅ | 0.001 s |
| `filtroEscapado(String, String)[3]` | ✅ | 0.001 s |
| `filtroEscapado(String, String)[4]` | ✅ | 0.001 s |
| `filtroEscapado(String, String)[5]` | ✅ | 0.001 s |
| `filtroEscapado(String, String)[6]` | ✅ | 0.000 s |
| `intervaloOpcional` | ✅ | 0.003 s |
| `filtroVazio(String)[1]` | ✅ | 0.001 s |
| `filtroVazio(String)[2]` | ✅ | 0.001 s |

</details>

<details><summary><code>ProtecaoConcorrenciaTest</code> — 4 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `naoRepeteNegocio` | ✅ | 0.079 s |
| `repeteAteConseguir` | ✅ | 0.058 s |
| `desiste` | ✅ | 0.605 s |
| `classificacaoTransitoria` | ✅ | 0.006 s |

</details>

<details><summary><code>StatusReservaTest</code> — 71 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `liberamAgenda(StatusReserva)[1]` | ✅ | 0.002 s |
| `liberamAgenda(StatusReserva)[2]` | ✅ | 0.001 s |
| `liberamAgenda(StatusReserva)[3]` | ✅ | 0.001 s |
| `liberamAgenda(StatusReserva)[4]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[1]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[2]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[3]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[4]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[5]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[6]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[7]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[8]` | ✅ | 0.002 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[9]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[10]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[11]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[12]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[13]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[14]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[15]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[16]` | ✅ | 0.002 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[17]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[18]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[19]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[20]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[21]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[22]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[23]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[24]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[25]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[26]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[27]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[28]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[29]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[30]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[31]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[32]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[33]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[34]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[35]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[36]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[37]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[38]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[39]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[40]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[41]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[42]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[43]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[44]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[45]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[46]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[47]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[48]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[49]` | ✅ | 0.001 s |
| `ocupamAgenda(StatusReserva)[1]` | ✅ | 0.000 s |
| `ocupamAgenda(StatusReserva)[2]` | ✅ | 0.000 s |
| `ocupamAgenda(StatusReserva)[3]` | ✅ | 0.001 s |
| `iniciadas(StatusReserva, boolean)[1]` | ✅ | 0.002 s |
| `iniciadas(StatusReserva, boolean)[2]` | ✅ | 0.000 s |
| `iniciadas(StatusReserva, boolean)[3]` | ✅ | 0.000 s |
| `iniciadas(StatusReserva, boolean)[4]` | ✅ | 0.000 s |
| `iniciadas(StatusReserva, boolean)[5]` | ✅ | 0.001 s |
| `iniciadas(StatusReserva, boolean)[6]` | ✅ | 0.000 s |
| `iniciadas(StatusReserva, boolean)[7]` | ✅ | 0.001 s |
| `transicaoInvalidaNaEntidade(StatusReserva, StatusReserva)[1]` | ✅ | 0.004 s |
| `transicaoInvalidaNaEntidade(StatusReserva, StatusReserva)[2]` | ✅ | 0.001 s |
| `transicaoInvalidaNaEntidade(StatusReserva, StatusReserva)[3]` | ✅ | 0.001 s |
| `transicaoInvalidaNaEntidade(StatusReserva, StatusReserva)[4]` | ✅ | 0.001 s |
| `transicaoInvalidaNaEntidade(StatusReserva, StatusReserva)[5]` | ✅ | 0.001 s |
| `transicaoInvalidaNaEntidade(StatusReserva, StatusReserva)[6]` | ✅ | 0.001 s |
| `transicaoInvalidaNaEntidade(StatusReserva, StatusReserva)[7]` | ✅ | 0.001 s |
| `fluxoPrincipal` | ✅ | 0.001 s |

</details>

<details><summary><code>AvaliadorAprovacaoTest</code> — 8 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `acumulaMotivos` | ✅ | 0.012 s |
| `semMotivos` | ✅ | 0.001 s |
| `salaRestrita` | ✅ | 0.003 s |
| `materialRestrito` | ✅ | 0.001 s |
| `semCompetencia` | ✅ | 0.001 s |
| `normalizacao(String)[1]` | ✅ | 0.002 s |
| `normalizacao(String)[2]` | ✅ | 0.001 s |
| `normalizacao(String)[3]` | ✅ | 0.002 s |

</details>

<details><summary><code>CalculadoraOcupacaoTest</code> — 8 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `pico(String, List, int)[1]` | ✅ | 0.009 s |
| `pico(String, List, int)[2]` | ✅ | 0.002 s |
| `pico(String, List, int)[3]` | ✅ | 0.001 s |
| `pico(String, List, int)[4]` | ✅ | 0.001 s |
| `pico(String, List, int)[5]` | ✅ | 0.001 s |
| `pico(String, List, int)[6]` | ✅ | 0.001 s |
| `pico(String, List, int)[7]` | ✅ | 0.001 s |
| `recorte` | ✅ | 0.001 s |

</details>

<details><summary><code>IntervaloTest</code> — 14 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `terminoNaoPosterior(String, String)[1]` | ✅ | 0.121 s |
| `terminoNaoPosterior(String, String)[2]` | ✅ | 0.003 s |
| `terminoNaoPosterior(String, String)[3]` | ✅ | 0.002 s |
| `nulos` | ✅ | 0.015 s |
| `sobreposicao(String, String, String, String, boolean)[1]` | ✅ | 0.023 s |
| `sobreposicao(String, String, String, String, boolean)[2]` | ✅ | 0.002 s |
| `sobreposicao(String, String, String, String, boolean)[3]` | ✅ | 0.002 s |
| `sobreposicao(String, String, String, String, boolean)[4]` | ✅ | 0.002 s |
| `sobreposicao(String, String, String, String, boolean)[5]` | ✅ | 0.002 s |
| `sobreposicao(String, String, String, String, boolean)[6]` | ✅ | 0.003 s |
| `sobreposicao(String, String, String, String, boolean)[7]` | ✅ | 0.002 s |
| `sobreposicao(String, String, String, String, boolean)[8]` | ✅ | 0.002 s |
| `sobreposicao(String, String, String, String, boolean)[9]` | ✅ | 0.002 s |
| `duracao` | ✅ | 0.006 s |

</details>

<details><summary><code>PoliticaHorarioTest</code> — 15 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `invalidos(String, String, String, String)[1]` | ✅ | 0.004 s |
| `invalidos(String, String, String, String)[2]` | ✅ | 0.001 s |
| `invalidos(String, String, String, String)[3]` | ✅ | 0.001 s |
| `invalidos(String, String, String, String)[4]` | ✅ | 0.001 s |
| `invalidos(String, String, String, String)[5]` | ✅ | 0.001 s |
| `invalidos(String, String, String, String)[6]` | ✅ | 0.001 s |
| `invalidos(String, String, String, String)[7]` | ✅ | 0.002 s |
| `invalidos(String, String, String, String)[8]` | ✅ | 0.002 s |
| `invalidos(String, String, String, String)[9]` | ✅ | 0.001 s |
| `devolveIntervalo` | ✅ | 0.001 s |
| `validos(String, String)[1]` | ✅ | 0.001 s |
| `validos(String, String)[2]` | ✅ | 0.001 s |
| `validos(String, String)[3]` | ✅ | 0.001 s |
| `validos(String, String)[4]` | ✅ | 0.001 s |
| `validos(String, String)[5]` | ✅ | 0.001 s |

</details>

<details><summary><code>AutenticacaoApiIT</code> — 5 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `forcaBruta` | ✅ | 0.457 s |
| `eu` | ✅ | 0.163 s |
| `loginValido` | ✅ | 0.162 s |
| `inativo` | ✅ | 0.151 s |
| `mensagemGenerica` | ✅ | 0.248 s |

</details>

<details><summary><code>CadastroPesquisaApiIT</code> — 9 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `pesquisaComCuringasLiterais` | ✅ | 0.539 s |
| `pesquisaMateriais` | ✅ | 0.519 s |
| `booleanosOpcionaisOmitidos` | ✅ | 0.357 s |
| `pesquisaProfessores` | ✅ | 0.504 s |
| `pesquisaSalas` | ✅ | 0.453 s |
| `cadastros` | ✅ | 0.578 s |
| `duplicado` | ✅ | 0.272 s |
| `pesquisaDisponibilidade` | ✅ | 0.506 s |
| `auditoriaCadastro` | ✅ | 0.344 s |

</details>

<details><summary><code>DocumentacaoApiIT</code> — 2 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `swaggerUi` | ✅ | 0.204 s |
| `openApi` | ✅ | 0.687 s |

</details>

<details><summary><code>RelatorioApiIT</code> — 3 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `relatorios` | ✅ | 0.798 s |
| `periodoInvalido` | ✅ | 0.180 s |
| `conflitosEvitados` | ✅ | 0.572 s |

</details>

<details><summary><code>ReservaApiIT</code> — 9 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `retiradaDevolucao` | ✅ | 0.747 s |
| `minhas` | ✅ | 0.529 s |
| `conflito` | ✅ | 0.399 s |
| `aprovacao` | ✅ | 0.658 s |
| `criar` | ✅ | 0.306 s |
| `alterarECancelar` | ✅ | 0.561 s |
| `terminoAntesDoInicio` | ✅ | 0.249 s |
| `historico` | ✅ | 0.477 s |
| `apagarIniciada` | ✅ | 0.537 s |

</details>

<details><summary><code>ReservasAfetadasApiIT</code> — 7 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `capacidadeAbaixoDeReservaFutura` | ✅ | 0.618 s |
| `reservasEncerradasOuCanceladasNaoContam` | ✅ | 0.711 s |
| `estoqueAbaixoDoReservado` | ✅ | 0.752 s |
| `desativarListaReservasAfetadas` | ✅ | 0.652 s |
| `desativarSemReservas` | ✅ | 0.258 s |
| `capacidadeSuficiente` | ✅ | 0.399 s |
| `estoqueIgualAoPico` | ✅ | 0.539 s |

</details>

<details><summary><code>SegurancaApiIT</code> — 46 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `tokenForjado` | ✅ | 0.021 s |
| `validacaoCampos` | ✅ | 0.179 s |
| `rotasDoFrontNaoAbremApi` | ✅ | 0.017 s |
| `parametroInvalido` | ✅ | 0.169 s |
| `usuarioDesativadoPerdeAcesso` | ✅ | 0.181 s |
| `cancelarReservaAlheia` | ✅ | 0.365 s |
| `emissorDoToken` | ✅ | 0.097 s |
| `perfilAtualizadoValeImediatamente` | ✅ | 0.161 s |
| `tokenAdulterado` | ✅ | 0.160 s |
| `autorizacaoPorPerfil(Perfil, String, String)[1]` | ✅ | 0.175 s |
| `autorizacaoPorPerfil(Perfil, String, String)[2]` | ✅ | 0.165 s |
| `autorizacaoPorPerfil(Perfil, String, String)[3]` | ✅ | 0.172 s |
| `autorizacaoPorPerfil(Perfil, String, String)[4]` | ✅ | 0.201 s |
| `autorizacaoPorPerfil(Perfil, String, String)[5]` | ✅ | 0.163 s |
| `autorizacaoPorPerfil(Perfil, String, String)[6]` | ✅ | 0.163 s |
| `autorizacaoPorPerfil(Perfil, String, String)[7]` | ✅ | 0.152 s |
| `autorizacaoPorPerfil(Perfil, String, String)[8]` | ✅ | 0.170 s |
| `autorizacaoPorPerfil(Perfil, String, String)[9]` | ✅ | 0.163 s |
| `autorizacaoPorPerfil(Perfil, String, String)[10]` | ✅ | 0.158 s |
| `autorizacaoPorPerfil(Perfil, String, String)[11]` | ✅ | 0.161 s |
| `nonceDaPolitica` | ✅ | 0.018 s |
| `cabecalhos` | ✅ | 0.013 s |
| `atribuicaoEmMassa` | ✅ | 0.240 s |
| `rotaInexistente` | ✅ | 0.171 s |
| `erroSeguro` | ✅ | 0.170 s |
| `injecaoNoFiltro(String)[1]` | ✅ | 0.166 s |
| `injecaoNoFiltro(String)[2]` | ✅ | 0.186 s |
| `injecaoNoFiltro(String)[3]` | ✅ | 0.159 s |
| `injecaoNoFiltro(String)[4]` | ✅ | 0.160 s |
| `injecaoNoFiltro(String)[5]` | ✅ | 0.170 s |
| `injecaoNoFiltro(String)[6]` | ✅ | 0.167 s |
| `injecaoNoFiltro(String)[7]` | ✅ | 0.177 s |
| `injecaoNoFiltro(String)[8]` | ✅ | 0.158 s |
| `semToken(String, String)[1]` | ✅ | 0.028 s |
| `semToken(String, String)[2]` | ✅ | 0.013 s |
| `semToken(String, String)[3]` | ✅ | 0.015 s |
| `semToken(String, String)[4]` | ✅ | 0.016 s |
| `semToken(String, String)[5]` | ✅ | 0.020 s |
| `semToken(String, String)[6]` | ✅ | 0.024 s |
| `jsonMalformado(String)[1]` | ✅ | 0.163 s |
| `jsonMalformado(String)[2]` | ✅ | 0.157 s |
| `jsonMalformado(String)[3]` | ✅ | 0.172 s |
| `jsonMalformado(String)[4]` | ✅ | 0.162 s |
| `rotasDoFront` | ✅ | 0.019 s |
| `professorSoReservaParaSi` | ✅ | 0.178 s |
| `semHash` | ✅ | 0.179 s |

</details>

<details><summary><code>IntegridadeRelacionalIT</code> — 6 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `checkPeriodo` | ✅ | 0.124 s |
| `salaReferenciadaNaoApaga` | ✅ | 0.122 s |
| `migracoesAplicadas` | ✅ | 0.019 s |
| `checkBloqueioRecurso` | ✅ | 0.095 s |
| `cascataItens` | ✅ | 0.100 s |
| `chaveEstrangeiraSala` | ✅ | 0.113 s |

</details>

<details><summary><code>CucumberIT</code> — 9 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `Retirada dentro da quantidade reservada` | ✅ | 0.697 s |
| `Retirada acima da quantidade reservada é recusada` | ✅ | 0.508 s |
| `Devolução maior que a retirada é recusada` | ✅ | 0.574 s |
| `Não é possível concluir a reserva com materiais pendentes` | ✅ | 0.603 s |
| `Conclusão após a devolução completa` | ✅ | 0.807 s |
| `Example #1.1` | ✅ | 0.544 s |
| `Example #1.2` | ✅ | 0.583 s |
| `Example #1.3` | ✅ | 0.579 s |
| `Example #1.4` | ✅ | 0.581 s |

</details>

<details><summary><code>ConcorrenciaReservaIT</code> — 7 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `mesmaSala()[1]` | ✅ | 0.338 s |
| `mesmaSala()[2]` | ✅ | 0.266 s |
| `mesmaSala()[3]` | ✅ | 0.275 s |
| `viaApi` | ✅ | 0.258 s |
| `estoqueDeMaterial` | ✅ | 0.401 s |
| `bloqueioSimultaneoAReserva` | ✅ | 0.701 s |
| `mesmoProfessor` | ✅ | 0.311 s |

</details>

<details><summary><code>FluxoReservaE2EIT</code> — 8 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `politicaDeConteudoSemViolacoes` | ✅ | 5.846 s |
| `responsivo` | ✅ | 2.459 s |
| `loginInvalido` | ✅ | 1.840 s |
| `estilosCarregados` | ✅ | 1.228 s |
| `escondeSalaOcupada` | ✅ | 4.205 s |
| `criaReserva` | ✅ | 3.718 s |
| `recarregaRotaInterna` | ✅ | 3.038 s |
| `mostraConflito` | ✅ | 3.909 s |

</details>

<details><summary><code>NotificacaoWireMockIT</code> — 5 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `notificaSolicitante` | ✅ | 0.256 s |
| `timeoutExterno` | ✅ | 1.191 s |
| `notificaResponsavel` | ✅ | 0.200 s |
| `falhaExterna` | ✅ | 0.206 s |
| `semNotificacaoEmFalha` | ✅ | 0.189 s |

</details>

<details><summary><code>ReservaServiceIT</code> — 25 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `docenteSemCompetencia` | ✅ | 0.265 s |
| `conflitoAgendaProfessor` | ✅ | 0.323 s |
| `iniciadaNaoApaga` | ✅ | 0.314 s |
| `restritaPrecisaAprovacao` | ✅ | 0.317 s |
| `conflitoMaterial` | ✅ | 0.336 s |
| `alteracao` | ✅ | 0.314 s |
| `materialEmManutencao` | ✅ | 0.286 s |
| `salaEmManutencao` | ✅ | 0.245 s |
| `passado` | ✅ | 0.232 s |
| `capacidade` | ✅ | 0.230 s |
| `alteracaoExigeNovaAprovacao` | ✅ | 0.324 s |
| `alteracaoVersaoAntiga` | ✅ | 0.264 s |
| `semAuditoriaParcial` | ✅ | 0.279 s |
| `conflitoSala` | ✅ | 0.269 s |
| `conflitoRegistrado` | ✅ | 0.310 s |
| `aprovacaoAutomatica` | ✅ | 0.256 s |
| `canceladaLibera` | ✅ | 0.277 s |
| `bloqueioInformaAfetadas` | ✅ | 0.265 s |
| `cicloCompletoAuditado` | ✅ | 0.389 s |
| `movimentacao` | ✅ | 0.461 s |
| `aprovarRecursoEmManutencao` | ✅ | 0.309 s |
| `adjacentes` | ✅ | 0.300 s |
| `naoCompareceu` | ✅ | 0.298 s |
| `naoIniciadaApaga` | ✅ | 0.321 s |
| `auditoriaPorTransicao` | ✅ | 0.265 s |

</details>

## 10.8 Como reproduzir

```bash
./mvnw verify                            # unitários + integração + cobertura (Testcontainers usa Docker)
python3 scripts/relatorio_backend.py     # regenera este relatório
```

Sem Docker, os testes de integração podem usar um MySQL local (banco `alocacao_teste`):

```bash
MYSQL_URL_TESTE='jdbc:mysql://127.0.0.1:3306/alocacao_teste' ./mvnw verify
```
