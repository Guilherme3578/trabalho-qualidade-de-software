# 10. Relatório de testes de back-end

> Gerado automaticamente por `scripts/relatorio_backend.py` a partir dos relatórios do JUnit (`target/surefire-reports`, `target/failsafe-reports`) e do JaCoCo (`target/site/jacoco`), após `./mvnw verify`.

## 10.1 Resumo

**Resultado: ✅ APROVADO**

| Item | Valor |
|---|---|
| Data da geração | 08/11/2026 21:30 |
| Testes executados | 245 |
| Aprovados | 245 |
| Falhas ou erros | 0 |
| Pulados | 0 |
| Tempo somado | 88.7 s |
| Cobertura de linhas | 85.6% (meta: 80%) |
| Cobertura de desvios (branches) | 72.3% |
| Cobertura de métodos | 82.2% |

## 10.2 Resultado por nível da pirâmide

| Nível | Classes | Testes | Falhas | Tempo |
|---|---|---|---|---|
| Unitário | 8 | 133 | 0 | 3.3 s |
| API caixa-preta | 6 | 64 | 0 | 30.7 s |
| Integração (MongoDB real + WireMock) | 2 | 29 | 0 | 17.0 s |
| Concorrência | 1 | 6 | 0 | 2.5 s |
| BDD (Cucumber) | 1 | 9 | 0 | 9.9 s |
| E2E (Selenium) | 1 | 4 | 0 | 25.4 s |

## 10.3 Resultado por classe de teste

| Nível | Classe | Descrição | Testes | Falhas | Tempo |
|---|---|---|---|---|---|
| Unitário | `LimitadorTentativasLoginTest` | [RNF02] Proteção contra força bruta no login | 4 | ✅ 0 | 0.02 s |
| Unitário | `UtilitariosTest` | Utilitários de filtro e JSON | 9 | ✅ 0 | 0.05 s |
| Unitário | `ProtecaoConcorrenciaTest` | [RF06] Repetição de transações em conflito de escrita | 4 | ✅ 0 | 2.49 s |
| Unitário | `StatusReservaTest` | [RN05] Máquina de estados da reserva | 71 | ✅ 0 | 0.12 s |
| Unitário | `AvaliadorAprovacaoTest` | [RF07] Avaliação da necessidade de aprovação | 8 | ✅ 0 | 0.06 s |
| Unitário | `CalculadoraOcupacaoTest` | [RN02] Pico de uso simultâneo de materiais | 8 | ✅ 0 | 0.04 s |
| Unitário | `IntervaloTest` | Intervalo de tempo | 14 | ✅ 0 | 0.41 s |
| Unitário | `PoliticaHorarioTest` | Política de horário das reservas | 15 | ✅ 0 | 0.05 s |
| API caixa-preta | `AutenticacaoApiIT` | [RF01] API caixa-preta: autenticação | 5 | ✅ 0 | 2.09 s |
| API caixa-preta | `CadastroPesquisaApiIT` | [RF02][RF03] API caixa-preta: cadastros e pesquisa | 8 | ✅ 0 | 6.19 s |
| API caixa-preta | `DocumentacaoApiIT` | [RF14] Documentação pública da API | 2 | ✅ 0 | 2.84 s |
| API caixa-preta | `RelatorioApiIT` | [RF12] API caixa-preta: relatórios | 3 | ✅ 0 | 2.47 s |
| API caixa-preta | `ReservaApiIT` | [RF04][RF05] API caixa-preta: reservas | 9 | ✅ 0 | 7.41 s |
| API caixa-preta | `SegurancaApiIT` | [RNF02][RNF03][RNF04] API caixa-preta: segurança | 37 | ✅ 0 | 9.69 s |
| Integração (MongoDB real + WireMock) | `NotificacaoWireMockIT` | [RF11] Integração com API externa de notificação (WireMock) | 5 | ✅ 0 | 2.72 s |
| Integração (MongoDB real + WireMock) | `ReservaServiceIT` | Integração: regras de reserva com MongoDB real | 24 | ✅ 0 | 14.28 s |
| Concorrência | `ConcorrenciaReservaIT` | [RF06][RN03] Concorrência: solicitações simultâneas | 6 | ✅ 0 | 2.50 s |
| BDD (Cucumber) | `CucumberIT` | CucumberIT | 9 | ✅ 0 | 9.91 s |
| E2E (Selenium) | `FluxoReservaE2EIT` | [RF13] End-to-end: fluxo de reserva pela interface web | 4 | ✅ 0 | 25.37 s |

## 10.4 Falhas

Nenhuma falha. Todos os testes passaram.

## 10.5 Cobertura por pacote

| Pacote | Linhas | Desvios |
|---|---|---|
| `auditoria` | 97.4% | 70.0% |
| `auth` | 98.4% | 88.9% |
| `bloqueio` | 74.4% | 41.7% |
| `comum` | 81.7% | 81.2% |
| `material` | 70.1% | 50.0% |
| `notificacao` | 79.8% | 87.5% |
| `professor` | 70.3% | 50.0% |
| `relatorio` | 87.8% | 50.0% |
| `reserva` | 93.9% | 81.0% |
| `reserva.regras` | 100.0% | 95.0% |
| `sala` | 88.8% | 58.3% |
| `usuario` | 62.8% | 10.0% |

## 10.6 Testes mais lentos

| Classe | Teste | Tempo |
|---|---|---|
| `FluxoReservaE2EIT` | `responsivo` | 4.73 s |
| `FluxoReservaE2EIT` | `mostraConflito` | 4.29 s |
| `FluxoReservaE2EIT` | `criaReserva` | 3.95 s |
| `FluxoReservaE2EIT` | `loginInvalido` | 1.97 s |
| `DocumentacaoApiIT` | `openApi` | 1.85 s |
| `CucumberIT` | `Conclusão após a devolução completa` | 1.32 s |
| `NotificacaoWireMockIT` | `timeoutExterno` | 1.31 s |
| `RelatorioApiIT` | `relatorios` | 1.26 s |
| `ReservaApiIT` | `retiradaDevolucao` | 1.24 s |
| `CucumberIT` | `Retirada dentro da quantidade reservada` | 1.11 s |

## 10.7 Lista completa de casos de teste

<details><summary><code>LimitadorTentativasLoginTest</code> — 4 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `liberaAposJanela` | ✅ | 0.004 s |
| `bloqueia` | ✅ | 0.003 s |
| `independentes` | ✅ | 0.004 s |
| `sucessoZera` | ✅ | 0.003 s |

</details>

<details><summary><code>UtilitariosTest</code> — 9 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `json` | ✅ | 0.007 s |
| `igual` | ✅ | 0.006 s |
| `filtroEscapado(String)[1]` | ✅ | 0.002 s |
| `filtroEscapado(String)[2]` | ✅ | 0.001 s |
| `filtroEscapado(String)[3]` | ✅ | 0.002 s |
| `filtroEscapado(String)[4]` | ✅ | 0.002 s |
| `filtroEscapado(String)[5]` | ✅ | 0.002 s |
| `filtroEscapado(String)[6]` | ✅ | 0.002 s |
| `intervaloOpcional` | ✅ | 0.005 s |

</details>

<details><summary><code>ProtecaoConcorrenciaTest</code> — 4 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `rotuloTransitorio` | ✅ | 0.071 s |
| `naoRepeteNegocio` | ✅ | 0.016 s |
| `repeteAteConseguir` | ✅ | 0.074 s |
| `desiste` | ✅ | 0.701 s |

</details>

<details><summary><code>StatusReservaTest</code> — 71 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `liberamAgenda(StatusReserva)[1]` | ✅ | 0.001 s |
| `liberamAgenda(StatusReserva)[2]` | ✅ | 0.001 s |
| `liberamAgenda(StatusReserva)[3]` | ✅ | 0.001 s |
| `liberamAgenda(StatusReserva)[4]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[1]` | ✅ | 0.002 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[2]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[3]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[4]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[5]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[6]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[7]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[8]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[9]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[10]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[11]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[12]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[13]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[14]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[15]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[16]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[17]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[18]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[19]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[20]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[21]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[22]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[23]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[24]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[25]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[26]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[27]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[28]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[29]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[30]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[31]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[32]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[33]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[34]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[35]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[36]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[37]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[38]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[39]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[40]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[41]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[42]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[43]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[44]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[45]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[46]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[47]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[48]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[49]` | ✅ | 0.001 s |
| `ocupamAgenda(StatusReserva)[1]` | ✅ | 0.001 s |
| `ocupamAgenda(StatusReserva)[2]` | ✅ | 0.000 s |
| `ocupamAgenda(StatusReserva)[3]` | ✅ | 0.001 s |
| `iniciadas(StatusReserva, boolean)[1]` | ✅ | 0.003 s |
| `iniciadas(StatusReserva, boolean)[2]` | ✅ | 0.001 s |
| `iniciadas(StatusReserva, boolean)[3]` | ✅ | 0.000 s |
| `iniciadas(StatusReserva, boolean)[4]` | ✅ | 0.001 s |
| `iniciadas(StatusReserva, boolean)[5]` | ✅ | 0.001 s |
| `iniciadas(StatusReserva, boolean)[6]` | ✅ | 0.000 s |
| `iniciadas(StatusReserva, boolean)[7]` | ✅ | 0.001 s |
| `transicaoInvalidaNaEntidade(StatusReserva, StatusReserva)[1]` | ✅ | 0.003 s |
| `transicaoInvalidaNaEntidade(StatusReserva, StatusReserva)[2]` | ✅ | 0.002 s |
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
| `acumulaMotivos` | ✅ | 0.017 s |
| `semMotivos` | ✅ | 0.001 s |
| `salaRestrita` | ✅ | 0.003 s |
| `materialRestrito` | ✅ | 0.002 s |
| `semCompetencia` | ✅ | 0.001 s |
| `normalizacao(String)[1]` | ✅ | 0.003 s |
| `normalizacao(String)[2]` | ✅ | 0.002 s |
| `normalizacao(String)[3]` | ✅ | 0.002 s |

</details>

<details><summary><code>CalculadoraOcupacaoTest</code> — 8 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `pico(String, List, int)[1]` | ✅ | 0.006 s |
| `pico(String, List, int)[2]` | ✅ | 0.002 s |
| `pico(String, List, int)[3]` | ✅ | 0.001 s |
| `pico(String, List, int)[4]` | ✅ | 0.001 s |
| `pico(String, List, int)[5]` | ✅ | 0.001 s |
| `pico(String, List, int)[6]` | ✅ | 0.001 s |
| `pico(String, List, int)[7]` | ✅ | 0.001 s |
| `recorte` | ✅ | 0.002 s |

</details>

<details><summary><code>IntervaloTest</code> — 14 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `terminoNaoPosterior(String, String)[1]` | ✅ | 0.177 s |
| `terminoNaoPosterior(String, String)[2]` | ✅ | 0.002 s |
| `terminoNaoPosterior(String, String)[3]` | ✅ | 0.002 s |
| `nulos` | ✅ | 0.013 s |
| `sobreposicao(String, String, String, String, boolean)[1]` | ✅ | 0.018 s |
| `sobreposicao(String, String, String, String, boolean)[2]` | ✅ | 0.001 s |
| `sobreposicao(String, String, String, String, boolean)[3]` | ✅ | 0.002 s |
| `sobreposicao(String, String, String, String, boolean)[4]` | ✅ | 0.002 s |
| `sobreposicao(String, String, String, String, boolean)[5]` | ✅ | 0.002 s |
| `sobreposicao(String, String, String, String, boolean)[6]` | ✅ | 0.002 s |
| `sobreposicao(String, String, String, String, boolean)[7]` | ✅ | 0.002 s |
| `sobreposicao(String, String, String, String, boolean)[8]` | ✅ | 0.002 s |
| `sobreposicao(String, String, String, String, boolean)[9]` | ✅ | 0.002 s |
| `duracao` | ✅ | 0.006 s |

</details>

<details><summary><code>PoliticaHorarioTest</code> — 15 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `invalidos(String, String, String, String)[1]` | ✅ | 0.004 s |
| `invalidos(String, String, String, String)[2]` | ✅ | 0.002 s |
| `invalidos(String, String, String, String)[3]` | ✅ | 0.002 s |
| `invalidos(String, String, String, String)[4]` | ✅ | 0.001 s |
| `invalidos(String, String, String, String)[5]` | ✅ | 0.002 s |
| `invalidos(String, String, String, String)[6]` | ✅ | 0.002 s |
| `invalidos(String, String, String, String)[7]` | ✅ | 0.001 s |
| `invalidos(String, String, String, String)[8]` | ✅ | 0.001 s |
| `invalidos(String, String, String, String)[9]` | ✅ | 0.001 s |
| `devolveIntervalo` | ✅ | 0.001 s |
| `validos(String, String)[1]` | ✅ | 0.002 s |
| `validos(String, String)[2]` | ✅ | 0.001 s |
| `validos(String, String)[3]` | ✅ | 0.001 s |
| `validos(String, String)[4]` | ✅ | 0.001 s |
| `validos(String, String)[5]` | ✅ | 0.001 s |

</details>

<details><summary><code>AutenticacaoApiIT</code> — 5 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `forcaBruta` | ✅ | 0.794 s |
| `eu` | ✅ | 0.305 s |
| `loginValido` | ✅ | 0.305 s |
| `inativo` | ✅ | 0.256 s |
| `mensagemGenerica` | ✅ | 0.413 s |

</details>

<details><summary><code>CadastroPesquisaApiIT</code> — 8 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `pesquisaMateriais` | ✅ | 0.922 s |
| `booleanosOpcionaisOmitidos` | ✅ | 0.703 s |
| `pesquisaProfessores` | ✅ | 0.868 s |
| `pesquisaSalas` | ✅ | 0.757 s |
| `cadastros` | ✅ | 0.989 s |
| `duplicado` | ✅ | 0.453 s |
| `pesquisaDisponibilidade` | ✅ | 0.846 s |
| `auditoriaCadastro` | ✅ | 0.604 s |

</details>

<details><summary><code>DocumentacaoApiIT</code> — 2 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `swaggerUi` | ✅ | 0.957 s |
| `openApi` | ✅ | 1.846 s |

</details>

<details><summary><code>RelatorioApiIT</code> — 3 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `relatorios` | ✅ | 1.260 s |
| `periodoInvalido` | ✅ | 0.285 s |
| `conflitosEvitados` | ✅ | 0.909 s |

</details>

<details><summary><code>ReservaApiIT</code> — 9 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `retiradaDevolucao` | ✅ | 1.242 s |
| `minhas` | ✅ | 0.893 s |
| `conflito` | ✅ | 0.726 s |
| `aprovacao` | ✅ | 0.998 s |
| `criar` | ✅ | 0.525 s |
| `alterarECancelar` | ✅ | 0.902 s |
| `terminoAntesDoInicio` | ✅ | 0.428 s |
| `historico` | ✅ | 0.858 s |
| `apagarIniciada` | ✅ | 0.810 s |

</details>

<details><summary><code>SegurancaApiIT</code> — 37 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `tokenForjado` | ✅ | 0.045 s |
| `validacaoCampos` | ✅ | 0.347 s |
| `parametroInvalido` | ✅ | 0.321 s |
| `cancelarReservaAlheia` | ✅ | 0.603 s |
| `tokenAdulterado` | ✅ | 0.281 s |
| `autorizacaoPorPerfil(Perfil, String, String)[1]` | ✅ | 0.328 s |
| `autorizacaoPorPerfil(Perfil, String, String)[2]` | ✅ | 0.288 s |
| `autorizacaoPorPerfil(Perfil, String, String)[3]` | ✅ | 0.277 s |
| `autorizacaoPorPerfil(Perfil, String, String)[4]` | ✅ | 0.299 s |
| `autorizacaoPorPerfil(Perfil, String, String)[5]` | ✅ | 0.281 s |
| `autorizacaoPorPerfil(Perfil, String, String)[6]` | ✅ | 0.277 s |
| `autorizacaoPorPerfil(Perfil, String, String)[7]` | ✅ | 0.273 s |
| `autorizacaoPorPerfil(Perfil, String, String)[8]` | ✅ | 0.282 s |
| `autorizacaoPorPerfil(Perfil, String, String)[9]` | ✅ | 0.271 s |
| `autorizacaoPorPerfil(Perfil, String, String)[10]` | ✅ | 0.411 s |
| `autorizacaoPorPerfil(Perfil, String, String)[11]` | ✅ | 0.304 s |
| `cabecalhos` | ✅ | 0.048 s |
| `atribuicaoEmMassa` | ✅ | 0.348 s |
| `rotaInexistente` | ✅ | 0.312 s |
| `erroSeguro` | ✅ | 0.291 s |
| `injecaoNoFiltro(String)[1]` | ✅ | 0.286 s |
| `injecaoNoFiltro(String)[2]` | ✅ | 0.290 s |
| `injecaoNoFiltro(String)[3]` | ✅ | 0.283 s |
| `injecaoNoFiltro(String)[4]` | ✅ | 0.289 s |
| `injecaoNoFiltro(String)[5]` | ✅ | 0.280 s |
| `semToken(String, String)[1]` | ✅ | 0.048 s |
| `semToken(String, String)[2]` | ✅ | 0.041 s |
| `semToken(String, String)[3]` | ✅ | 0.035 s |
| `semToken(String, String)[4]` | ✅ | 0.033 s |
| `semToken(String, String)[5]` | ✅ | 0.045 s |
| `semToken(String, String)[6]` | ✅ | 0.034 s |
| `jsonMalformado(String)[1]` | ✅ | 0.300 s |
| `jsonMalformado(String)[2]` | ✅ | 0.285 s |
| `jsonMalformado(String)[3]` | ✅ | 0.449 s |
| `jsonMalformado(String)[4]` | ✅ | 0.362 s |
| `professorSoReservaParaSi` | ✅ | 0.281 s |
| `semHash` | ✅ | 0.286 s |

</details>

<details><summary><code>CucumberIT</code> — 9 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `Retirada dentro da quantidade reservada` | ✅ | 1.107 s |
| `Retirada acima da quantidade reservada é recusada` | ✅ | 0.824 s |
| `Devolução maior que a retirada é recusada` | ✅ | 0.965 s |
| `Não é possível concluir a reserva com materiais pendentes` | ✅ | 1.016 s |
| `Conclusão após a devolução completa` | ✅ | 1.318 s |
| `Example #1.1` | ✅ | 0.917 s |
| `Example #1.2` | ✅ | 0.915 s |
| `Example #1.3` | ✅ | 1.003 s |
| `Example #1.4` | ✅ | 0.909 s |

</details>

<details><summary><code>ConcorrenciaReservaIT</code> — 6 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `mesmaSala()[1]` | ✅ | 0.435 s |
| `mesmaSala()[2]` | ✅ | 0.390 s |
| `mesmaSala()[3]` | ✅ | 0.335 s |
| `viaApi` | ✅ | 0.351 s |
| `estoqueDeMaterial` | ✅ | 0.650 s |
| `mesmoProfessor` | ✅ | 0.291 s |

</details>

<details><summary><code>FluxoReservaE2EIT</code> — 4 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `responsivo` | ✅ | 4.726 s |
| `loginInvalido` | ✅ | 1.965 s |
| `criaReserva` | ✅ | 3.952 s |
| `mostraConflito` | ✅ | 4.289 s |

</details>

<details><summary><code>NotificacaoWireMockIT</code> — 5 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `notificaSolicitante` | ✅ | 0.383 s |
| `timeoutExterno` | ✅ | 1.314 s |
| `notificaResponsavel` | ✅ | 0.331 s |
| `falhaExterna` | ✅ | 0.321 s |
| `semNotificacaoEmFalha` | ✅ | 0.337 s |

</details>

<details><summary><code>ReservaServiceIT</code> — 24 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `docenteSemCompetencia` | ✅ | 0.597 s |
| `conflitoAgendaProfessor` | ✅ | 0.753 s |
| `iniciadaNaoApaga` | ✅ | 0.599 s |
| `restritaPrecisaAprovacao` | ✅ | 0.737 s |
| `conflitoMaterial` | ✅ | 0.724 s |
| `alteracao` | ✅ | 0.636 s |
| `materialEmManutencao` | ✅ | 0.660 s |
| `salaEmManutencao` | ✅ | 0.576 s |
| `passado` | ✅ | 0.490 s |
| `capacidade` | ✅ | 0.536 s |
| `alteracaoExigeNovaAprovacao` | ✅ | 0.799 s |
| `alteracaoVersaoAntiga` | ✅ | 0.474 s |
| `semAuditoriaParcial` | ✅ | 0.620 s |
| `conflitoSala` | ✅ | 0.509 s |
| `conflitoRegistrado` | ✅ | 0.531 s |
| `aprovacaoAutomatica` | ✅ | 0.462 s |
| `canceladaLibera` | ✅ | 0.620 s |
| `bloqueioInformaAfetadas` | ✅ | 0.479 s |
| `cicloCompletoAuditado` | ✅ | 0.716 s |
| `movimentacao` | ✅ | 0.649 s |
| `adjacentes` | ✅ | 0.522 s |
| `naoCompareceu` | ✅ | 0.446 s |
| `naoIniciadaApaga` | ✅ | 0.530 s |
| `auditoriaPorTransicao` | ✅ | 0.500 s |

</details>

## 10.8 Como reproduzir

```bash
./mvnw verify                            # unitários + integração + cobertura (Testcontainers usa Docker)
python3 scripts/relatorio_backend.py     # regenera este relatório
```

Sem Docker, os testes de integração podem usar um MongoDB local configurado como replica set:

```bash
MONGODB_URI_TESTE='mongodb://127.0.0.1:27017/alocacao_teste?replicaSet=rs0' ./mvnw verify
```
