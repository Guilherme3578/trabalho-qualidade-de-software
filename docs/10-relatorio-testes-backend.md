# 10. Relatório de testes de back-end

> Gerado automaticamente por `scripts/relatorio_backend.py` a partir dos relatórios do JUnit (`target/surefire-reports`, `target/failsafe-reports`) e do JaCoCo (`target/site/jacoco`), após `./mvnw verify`.

## 10.1 Resumo

**Resultado: ✅ APROVADO**

| Item | Valor |
|---|---|
| Data da geração | 09/11/2026 23:34 |
| Testes executados | 262 |
| Aprovados | 262 |
| Falhas ou erros | 0 |
| Pulados | 0 |
| Tempo somado | 103.0 s |
| Cobertura de linhas | 86.4% (meta: 80%) |
| Cobertura de desvios (branches) | 73.2% |
| Cobertura de métodos | 82.9% |

## 10.2 Resultado por nível da pirâmide

| Nível | Classes | Testes | Falhas | Tempo |
|---|---|---|---|---|
| Unitário | 8 | 136 | 0 | 3.8 s |
| API caixa-preta | 6 | 70 | 0 | 34.7 s |
| Integração (MySQL real + WireMock) | 2 | 30 | 0 | 16.1 s |
| Integridade do banco (MySQL) | 1 | 6 | 0 | 1.0 s |
| Concorrência | 1 | 7 | 0 | 4.7 s |
| BDD (Cucumber) | 1 | 9 | 0 | 10.2 s |
| E2E (Selenium) | 1 | 4 | 0 | 32.6 s |

## 10.3 Resultado por classe de teste

| Nível | Classe | Descrição | Testes | Falhas | Tempo |
|---|---|---|---|---|---|
| Unitário | `LimitadorTentativasLoginTest` | [RNF02] Proteção contra força bruta no login | 5 | ✅ 0 | 0.19 s |
| Unitário | `UtilitariosTest` | Utilitários de filtro e JSON | 11 | ✅ 0 | 0.05 s |
| Unitário | `ProtecaoConcorrenciaTest` | [RF06] Repetição de transações em deadlock ou espera de trava | 4 | ✅ 0 | 2.73 s |
| Unitário | `StatusReservaTest` | [RN05] Máquina de estados da reserva | 71 | ✅ 0 | 0.15 s |
| Unitário | `AvaliadorAprovacaoTest` | [RF07] Avaliação da necessidade de aprovação | 8 | ✅ 0 | 0.10 s |
| Unitário | `CalculadoraOcupacaoTest` | [RN02] Pico de uso simultâneo de materiais | 8 | ✅ 0 | 0.07 s |
| Unitário | `IntervaloTest` | Intervalo de tempo | 14 | ✅ 0 | 0.42 s |
| Unitário | `PoliticaHorarioTest` | Política de horário das reservas | 15 | ✅ 0 | 0.07 s |
| API caixa-preta | `AutenticacaoApiIT` | [RF01] API caixa-preta: autenticação | 5 | ✅ 0 | 2.16 s |
| API caixa-preta | `CadastroPesquisaApiIT` | [RF02][RF03] API caixa-preta: cadastros e pesquisa | 9 | ✅ 0 | 7.62 s |
| API caixa-preta | `DocumentacaoApiIT` | [RF14] Documentação pública da API | 2 | ✅ 0 | 3.15 s |
| API caixa-preta | `RelatorioApiIT` | [RF12] API caixa-preta: relatórios | 3 | ✅ 0 | 2.79 s |
| API caixa-preta | `ReservaApiIT` | [RF04][RF05] API caixa-preta: reservas | 9 | ✅ 0 | 8.09 s |
| API caixa-preta | `SegurancaApiIT` | [RNF02][RNF03][RNF04] API caixa-preta: segurança | 42 | ✅ 0 | 10.90 s |
| Integração (MySQL real + WireMock) | `NotificacaoWireMockIT` | [RF11] Integração com API externa de notificação (WireMock) | 5 | ✅ 0 | 2.89 s |
| Integração (MySQL real + WireMock) | `ReservaServiceIT` | Integração: regras de reserva com MySQL real | 25 | ✅ 0 | 13.17 s |
| Integridade do banco (MySQL) | `IntegridadeRelacionalIT` | [RNF07] Banco relacional: integridade garantida pelo MySQL | 6 | ✅ 0 | 1.00 s |
| Concorrência | `ConcorrenciaReservaIT` | [RF06][RN03] Concorrência: solicitações simultâneas | 7 | ✅ 0 | 4.69 s |
| BDD (Cucumber) | `CucumberIT` | CucumberIT | 9 | ✅ 0 | 10.23 s |
| E2E (Selenium) | `FluxoReservaE2EIT` | [RF13] End-to-end: fluxo de reserva pela interface web | 4 | ✅ 0 | 32.57 s |

## 10.4 Falhas

Nenhuma falha. Todos os testes passaram.

## 10.5 Cobertura por pacote

| Pacote | Linhas | Desvios |
|---|---|---|
| `auditoria` | 100.0% | 100.0% |
| `auth` | 98.5% | 90.0% |
| `bloqueio` | 78.9% | 58.3% |
| `comum` | 81.1% | 79.2% |
| `material` | 72.5% | 50.0% |
| `notificacao` | 79.8% | 87.5% |
| `professor` | 72.1% | 33.3% |
| `relatorio` | 85.3% | 50.0% |
| `reserva` | 93.9% | 79.4% |
| `reserva.regras` | 100.0% | 95.0% |
| `sala` | 88.6% | 16.7% |
| `usuario` | 62.8% | 10.0% |

## 10.6 Testes mais lentos

| Classe | Teste | Tempo |
|---|---|---|
| `FluxoReservaE2EIT` | `responsivo` | 5.15 s |
| `FluxoReservaE2EIT` | `mostraConflito` | 4.04 s |
| `FluxoReservaE2EIT` | `criaReserva` | 3.90 s |
| `DocumentacaoApiIT` | `openApi` | 2.15 s |
| `FluxoReservaE2EIT` | `loginInvalido` | 1.90 s |
| `RelatorioApiIT` | `relatorios` | 1.44 s |
| `CucumberIT` | `Conclusão após a devolução completa` | 1.37 s |
| `ReservaApiIT` | `retiradaDevolucao` | 1.34 s |
| `NotificacaoWireMockIT` | `timeoutExterno` | 1.34 s |
| `ConcorrenciaReservaIT` | `bloqueioSimultaneoAReserva` | 1.30 s |

## 10.7 Lista completa de casos de teste

<details><summary><code>LimitadorTentativasLoginTest</code> — 5 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `liberaAposJanela` | ✅ | 0.003 s |
| `descartaTentativasExpiradas` | ✅ | 0.172 s |
| `bloqueia` | ✅ | 0.003 s |
| `independentes` | ✅ | 0.001 s |
| `sucessoZera` | ✅ | 0.001 s |

</details>

<details><summary><code>UtilitariosTest</code> — 11 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `json` | ✅ | 0.004 s |
| `igual` | ✅ | 0.003 s |
| `filtroEscapado(String, String)[1]` | ✅ | 0.003 s |
| `filtroEscapado(String, String)[2]` | ✅ | 0.001 s |
| `filtroEscapado(String, String)[3]` | ✅ | 0.001 s |
| `filtroEscapado(String, String)[4]` | ✅ | 0.001 s |
| `filtroEscapado(String, String)[5]` | ✅ | 0.001 s |
| `filtroEscapado(String, String)[6]` | ✅ | 0.002 s |
| `intervaloOpcional` | ✅ | 0.004 s |
| `filtroVazio(String)[1]` | ✅ | 0.002 s |
| `filtroVazio(String)[2]` | ✅ | 0.001 s |

</details>

<details><summary><code>ProtecaoConcorrenciaTest</code> — 4 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `naoRepeteNegocio` | ✅ | 0.142 s |
| `repeteAteConseguir` | ✅ | 0.068 s |
| `desiste` | ✅ | 0.724 s |
| `classificacaoTransitoria` | ✅ | 0.003 s |

</details>

<details><summary><code>StatusReservaTest</code> — 71 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `liberamAgenda(StatusReserva)[1]` | ✅ | 0.001 s |
| `liberamAgenda(StatusReserva)[2]` | ✅ | 0.001 s |
| `liberamAgenda(StatusReserva)[3]` | ✅ | 0.001 s |
| `liberamAgenda(StatusReserva)[4]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[1]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[2]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[3]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[4]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[5]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[6]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[7]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[8]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[9]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[10]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[11]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[12]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[13]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[14]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[15]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[16]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[17]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[18]` | ✅ | 0.002 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[19]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[20]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[21]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[22]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[23]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[24]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[25]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[26]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[27]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[28]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[29]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[30]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[31]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[32]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[33]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[34]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[35]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[36]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[37]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[38]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[39]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[40]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[41]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[42]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[43]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[44]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[45]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[46]` | ✅ | 0.002 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[47]` | ✅ | 0.002 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[48]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[49]` | ✅ | 0.001 s |
| `ocupamAgenda(StatusReserva)[1]` | ✅ | 0.001 s |
| `ocupamAgenda(StatusReserva)[2]` | ✅ | 0.001 s |
| `ocupamAgenda(StatusReserva)[3]` | ✅ | 0.001 s |
| `iniciadas(StatusReserva, boolean)[1]` | ✅ | 0.005 s |
| `iniciadas(StatusReserva, boolean)[2]` | ✅ | 0.002 s |
| `iniciadas(StatusReserva, boolean)[3]` | ✅ | 0.001 s |
| `iniciadas(StatusReserva, boolean)[4]` | ✅ | 0.001 s |
| `iniciadas(StatusReserva, boolean)[5]` | ✅ | 0.002 s |
| `iniciadas(StatusReserva, boolean)[6]` | ✅ | 0.001 s |
| `iniciadas(StatusReserva, boolean)[7]` | ✅ | 0.000 s |
| `transicaoInvalidaNaEntidade(StatusReserva, StatusReserva)[1]` | ✅ | 0.012 s |
| `transicaoInvalidaNaEntidade(StatusReserva, StatusReserva)[2]` | ✅ | 0.001 s |
| `transicaoInvalidaNaEntidade(StatusReserva, StatusReserva)[3]` | ✅ | 0.001 s |
| `transicaoInvalidaNaEntidade(StatusReserva, StatusReserva)[4]` | ✅ | 0.001 s |
| `transicaoInvalidaNaEntidade(StatusReserva, StatusReserva)[5]` | ✅ | 0.001 s |
| `transicaoInvalidaNaEntidade(StatusReserva, StatusReserva)[6]` | ✅ | 0.000 s |
| `transicaoInvalidaNaEntidade(StatusReserva, StatusReserva)[7]` | ✅ | 0.001 s |
| `fluxoPrincipal` | ✅ | 0.001 s |

</details>

<details><summary><code>AvaliadorAprovacaoTest</code> — 8 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `acumulaMotivos` | ✅ | 0.028 s |
| `semMotivos` | ✅ | 0.002 s |
| `salaRestrita` | ✅ | 0.005 s |
| `materialRestrito` | ✅ | 0.002 s |
| `semCompetencia` | ✅ | 0.002 s |
| `normalizacao(String)[1]` | ✅ | 0.003 s |
| `normalizacao(String)[2]` | ✅ | 0.002 s |
| `normalizacao(String)[3]` | ✅ | 0.003 s |

</details>

<details><summary><code>CalculadoraOcupacaoTest</code> — 8 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `pico(String, List, int)[1]` | ✅ | 0.011 s |
| `pico(String, List, int)[2]` | ✅ | 0.003 s |
| `pico(String, List, int)[3]` | ✅ | 0.002 s |
| `pico(String, List, int)[4]` | ✅ | 0.002 s |
| `pico(String, List, int)[5]` | ✅ | 0.003 s |
| `pico(String, List, int)[6]` | ✅ | 0.002 s |
| `pico(String, List, int)[7]` | ✅ | 0.002 s |
| `recorte` | ✅ | 0.002 s |

</details>

<details><summary><code>IntervaloTest</code> — 14 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `terminoNaoPosterior(String, String)[1]` | ✅ | 0.177 s |
| `terminoNaoPosterior(String, String)[2]` | ✅ | 0.002 s |
| `terminoNaoPosterior(String, String)[3]` | ✅ | 0.002 s |
| `nulos` | ✅ | 0.015 s |
| `sobreposicao(String, String, String, String, boolean)[1]` | ✅ | 0.017 s |
| `sobreposicao(String, String, String, String, boolean)[2]` | ✅ | 0.001 s |
| `sobreposicao(String, String, String, String, boolean)[3]` | ✅ | 0.001 s |
| `sobreposicao(String, String, String, String, boolean)[4]` | ✅ | 0.002 s |
| `sobreposicao(String, String, String, String, boolean)[5]` | ✅ | 0.004 s |
| `sobreposicao(String, String, String, String, boolean)[6]` | ✅ | 0.004 s |
| `sobreposicao(String, String, String, String, boolean)[7]` | ✅ | 0.002 s |
| `sobreposicao(String, String, String, String, boolean)[8]` | ✅ | 0.003 s |
| `sobreposicao(String, String, String, String, boolean)[9]` | ✅ | 0.003 s |
| `duracao` | ✅ | 0.010 s |

</details>

<details><summary><code>PoliticaHorarioTest</code> — 15 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `invalidos(String, String, String, String)[1]` | ✅ | 0.007 s |
| `invalidos(String, String, String, String)[2]` | ✅ | 0.003 s |
| `invalidos(String, String, String, String)[3]` | ✅ | 0.002 s |
| `invalidos(String, String, String, String)[4]` | ✅ | 0.001 s |
| `invalidos(String, String, String, String)[5]` | ✅ | 0.002 s |
| `invalidos(String, String, String, String)[6]` | ✅ | 0.002 s |
| `invalidos(String, String, String, String)[7]` | ✅ | 0.002 s |
| `invalidos(String, String, String, String)[8]` | ✅ | 0.001 s |
| `invalidos(String, String, String, String)[9]` | ✅ | 0.002 s |
| `devolveIntervalo` | ✅ | 0.001 s |
| `validos(String, String)[1]` | ✅ | 0.003 s |
| `validos(String, String)[2]` | ✅ | 0.002 s |
| `validos(String, String)[3]` | ✅ | 0.001 s |
| `validos(String, String)[4]` | ✅ | 0.001 s |
| `validos(String, String)[5]` | ✅ | 0.001 s |

</details>

<details><summary><code>AutenticacaoApiIT</code> — 5 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `forcaBruta` | ✅ | 0.857 s |
| `eu` | ✅ | 0.296 s |
| `loginValido` | ✅ | 0.283 s |
| `inativo` | ✅ | 0.273 s |
| `mensagemGenerica` | ✅ | 0.422 s |

</details>

<details><summary><code>CadastroPesquisaApiIT</code> — 9 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `pesquisaComCuringasLiterais` | ✅ | 1.053 s |
| `pesquisaMateriais` | ✅ | 0.952 s |
| `booleanosOpcionaisOmitidos` | ✅ | 0.692 s |
| `pesquisaProfessores` | ✅ | 0.925 s |
| `pesquisaSalas` | ✅ | 0.832 s |
| `cadastros` | ✅ | 1.057 s |
| `duplicado` | ✅ | 0.490 s |
| `pesquisaDisponibilidade` | ✅ | 0.909 s |
| `auditoriaCadastro` | ✅ | 0.654 s |

</details>

<details><summary><code>DocumentacaoApiIT</code> — 2 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `swaggerUi` | ✅ | 0.945 s |
| `openApi` | ✅ | 2.155 s |

</details>

<details><summary><code>RelatorioApiIT</code> — 3 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `relatorios` | ✅ | 1.435 s |
| `periodoInvalido` | ✅ | 0.316 s |
| `conflitosEvitados` | ✅ | 0.999 s |

</details>

<details><summary><code>ReservaApiIT</code> — 9 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `retiradaDevolucao` | ✅ | 1.338 s |
| `minhas` | ✅ | 1.011 s |
| `conflito` | ✅ | 0.799 s |
| `aprovacao` | ✅ | 1.147 s |
| `criar` | ✅ | 0.529 s |
| `alterarECancelar` | ✅ | 0.945 s |
| `terminoAntesDoInicio` | ✅ | 0.437 s |
| `historico` | ✅ | 1.005 s |
| `apagarIniciada` | ✅ | 0.832 s |

</details>

<details><summary><code>SegurancaApiIT</code> — 42 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `tokenForjado` | ✅ | 0.038 s |
| `validacaoCampos` | ✅ | 0.323 s |
| `parametroInvalido` | ✅ | 0.291 s |
| `usuarioDesativadoPerdeAcesso` | ✅ | 0.333 s |
| `cancelarReservaAlheia` | ✅ | 0.635 s |
| `perfilAtualizadoValeImediatamente` | ✅ | 0.304 s |
| `tokenAdulterado` | ✅ | 0.268 s |
| `autorizacaoPorPerfil(Perfil, String, String)[1]` | ✅ | 0.309 s |
| `autorizacaoPorPerfil(Perfil, String, String)[2]` | ✅ | 0.273 s |
| `autorizacaoPorPerfil(Perfil, String, String)[3]` | ✅ | 0.288 s |
| `autorizacaoPorPerfil(Perfil, String, String)[4]` | ✅ | 0.303 s |
| `autorizacaoPorPerfil(Perfil, String, String)[5]` | ✅ | 0.292 s |
| `autorizacaoPorPerfil(Perfil, String, String)[6]` | ✅ | 0.310 s |
| `autorizacaoPorPerfil(Perfil, String, String)[7]` | ✅ | 0.307 s |
| `autorizacaoPorPerfil(Perfil, String, String)[8]` | ✅ | 0.276 s |
| `autorizacaoPorPerfil(Perfil, String, String)[9]` | ✅ | 0.272 s |
| `autorizacaoPorPerfil(Perfil, String, String)[10]` | ✅ | 0.278 s |
| `autorizacaoPorPerfil(Perfil, String, String)[11]` | ✅ | 0.301 s |
| `cabecalhos` | ✅ | 0.040 s |
| `atribuicaoEmMassa` | ✅ | 0.370 s |
| `rotaInexistente` | ✅ | 0.295 s |
| `erroSeguro` | ✅ | 0.305 s |
| `injecaoNoFiltro(String)[1]` | ✅ | 0.284 s |
| `injecaoNoFiltro(String)[2]` | ✅ | 0.282 s |
| `injecaoNoFiltro(String)[3]` | ✅ | 0.288 s |
| `injecaoNoFiltro(String)[4]` | ✅ | 0.287 s |
| `injecaoNoFiltro(String)[5]` | ✅ | 0.270 s |
| `injecaoNoFiltro(String)[6]` | ✅ | 0.285 s |
| `injecaoNoFiltro(String)[7]` | ✅ | 0.322 s |
| `injecaoNoFiltro(String)[8]` | ✅ | 0.292 s |
| `semToken(String, String)[1]` | ✅ | 0.044 s |
| `semToken(String, String)[2]` | ✅ | 0.037 s |
| `semToken(String, String)[3]` | ✅ | 0.032 s |
| `semToken(String, String)[4]` | ✅ | 0.035 s |
| `semToken(String, String)[5]` | ✅ | 0.032 s |
| `semToken(String, String)[6]` | ✅ | 0.026 s |
| `jsonMalformado(String)[1]` | ✅ | 0.280 s |
| `jsonMalformado(String)[2]` | ✅ | 0.287 s |
| `jsonMalformado(String)[3]` | ✅ | 0.274 s |
| `jsonMalformado(String)[4]` | ✅ | 0.300 s |
| `professorSoReservaParaSi` | ✅ | 0.300 s |
| `semHash` | ✅ | 0.312 s |

</details>

<details><summary><code>IntegridadeRelacionalIT</code> — 6 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `checkPeriodo` | ✅ | 0.196 s |
| `salaReferenciadaNaoApaga` | ✅ | 0.190 s |
| `migracoesAplicadas` | ✅ | 0.027 s |
| `checkBloqueioRecurso` | ✅ | 0.148 s |
| `cascataItens` | ✅ | 0.180 s |
| `chaveEstrangeiraSala` | ✅ | 0.209 s |

</details>

<details><summary><code>CucumberIT</code> — 9 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `Retirada dentro da quantidade reservada` | ✅ | 1.184 s |
| `Retirada acima da quantidade reservada é recusada` | ✅ | 0.875 s |
| `Devolução maior que a retirada é recusada` | ✅ | 1.009 s |
| `Não é possível concluir a reserva com materiais pendentes` | ✅ | 1.037 s |
| `Conclusão após a devolução completa` | ✅ | 1.368 s |
| `Example #1.1` | ✅ | 0.931 s |
| `Example #1.2` | ✅ | 0.937 s |
| `Example #1.3` | ✅ | 0.961 s |
| `Example #1.4` | ✅ | 0.945 s |

</details>

<details><summary><code>ConcorrenciaReservaIT</code> — 7 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `mesmaSala()[1]` | ✅ | 0.550 s |
| `mesmaSala()[2]` | ✅ | 0.454 s |
| `mesmaSala()[3]` | ✅ | 0.491 s |
| `viaApi` | ✅ | 0.451 s |
| `estoqueDeMaterial` | ✅ | 0.849 s |
| `bloqueioSimultaneoAReserva` | ✅ | 1.303 s |
| `mesmoProfessor` | ✅ | 0.529 s |

</details>

<details><summary><code>FluxoReservaE2EIT</code> — 4 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `responsivo` | ✅ | 5.147 s |
| `loginInvalido` | ✅ | 1.901 s |
| `criaReserva` | ✅ | 3.902 s |
| `mostraConflito` | ✅ | 4.036 s |

</details>

<details><summary><code>NotificacaoWireMockIT</code> — 5 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `notificaSolicitante` | ✅ | 0.424 s |
| `timeoutExterno` | ✅ | 1.336 s |
| `notificaResponsavel` | ✅ | 0.352 s |
| `falhaExterna` | ✅ | 0.358 s |
| `semNotificacaoEmFalha` | ✅ | 0.371 s |

</details>

<details><summary><code>ReservaServiceIT</code> — 25 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `docenteSemCompetencia` | ✅ | 0.554 s |
| `conflitoAgendaProfessor` | ✅ | 0.584 s |
| `iniciadaNaoApaga` | ✅ | 0.564 s |
| `restritaPrecisaAprovacao` | ✅ | 0.533 s |
| `conflitoMaterial` | ✅ | 0.558 s |
| `alteracao` | ✅ | 0.537 s |
| `materialEmManutencao` | ✅ | 0.496 s |
| `salaEmManutencao` | ✅ | 0.442 s |
| `passado` | ✅ | 0.412 s |
| `capacidade` | ✅ | 0.416 s |
| `alteracaoExigeNovaAprovacao` | ✅ | 0.570 s |
| `alteracaoVersaoAntiga` | ✅ | 0.478 s |
| `semAuditoriaParcial` | ✅ | 0.559 s |
| `conflitoSala` | ✅ | 0.496 s |
| `conflitoRegistrado` | ✅ | 0.522 s |
| `aprovacaoAutomatica` | ✅ | 0.461 s |
| `canceladaLibera` | ✅ | 0.576 s |
| `bloqueioInformaAfetadas` | ✅ | 0.483 s |
| `cicloCompletoAuditado` | ✅ | 0.641 s |
| `movimentacao` | ✅ | 0.693 s |
| `aprovarRecursoEmManutencao` | ✅ | 0.478 s |
| `adjacentes` | ✅ | 0.471 s |
| `naoCompareceu` | ✅ | 0.481 s |
| `naoIniciadaApaga` | ✅ | 0.535 s |
| `auditoriaPorTransicao` | ✅ | 0.494 s |

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
