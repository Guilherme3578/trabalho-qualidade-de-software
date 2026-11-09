# 10. Relatório de testes de back-end

> Gerado automaticamente por `scripts/relatorio_backend.py` a partir dos relatórios do JUnit (`target/surefire-reports`, `target/failsafe-reports`) e do JaCoCo (`target/site/jacoco`), após `./mvnw verify`.

## 10.1 Resumo

**Resultado: ✅ APROVADO**

| Item | Valor |
|---|---|
| Data da geração | 08/11/2026 23:33 |
| Testes executados | 250 |
| Aprovados | 250 |
| Falhas ou erros | 0 |
| Pulados | 0 |
| Tempo somado | 56.2 s |
| Cobertura de linhas | 85.2% (meta: 80%) |
| Cobertura de desvios (branches) | 72.6% |
| Cobertura de métodos | 82.5% |

## 10.2 Resultado por nível da pirâmide

| Nível | Classes | Testes | Falhas | Tempo |
|---|---|---|---|---|
| Unitário | 8 | 134 | 0 | 2.9 s |
| API caixa-preta | 6 | 66 | 0 | 18.2 s |
| Integração (MongoDB real + WireMock) | 2 | 30 | 0 | 9.5 s |
| Concorrência | 1 | 7 | 0 | 2.1 s |
| BDD (Cucumber) | 1 | 9 | 0 | 5.8 s |
| E2E (Selenium) | 1 | 4 | 0 | 17.6 s |

## 10.3 Resultado por classe de teste

| Nível | Classe | Descrição | Testes | Falhas | Tempo |
|---|---|---|---|---|---|
| Unitário | `LimitadorTentativasLoginTest` | [RNF02] Proteção contra força bruta no login | 5 | ✅ 0 | 0.11 s |
| Unitário | `UtilitariosTest` | Utilitários de filtro e JSON | 9 | ✅ 0 | 0.03 s |
| Unitário | `ProtecaoConcorrenciaTest` | [RF06] Repetição de transações em conflito de escrita | 4 | ✅ 0 | 2.27 s |
| Unitário | `StatusReservaTest` | [RN05] Máquina de estados da reserva | 71 | ✅ 0 | 0.08 s |
| Unitário | `AvaliadorAprovacaoTest` | [RF07] Avaliação da necessidade de aprovação | 8 | ✅ 0 | 0.04 s |
| Unitário | `CalculadoraOcupacaoTest` | [RN02] Pico de uso simultâneo de materiais | 8 | ✅ 0 | 0.03 s |
| Unitário | `IntervaloTest` | Intervalo de tempo | 14 | ✅ 0 | 0.31 s |
| Unitário | `PoliticaHorarioTest` | Política de horário das reservas | 15 | ✅ 0 | 0.05 s |
| API caixa-preta | `AutenticacaoApiIT` | [RF01] API caixa-preta: autenticação | 5 | ✅ 0 | 1.21 s |
| API caixa-preta | `CadastroPesquisaApiIT` | [RF02][RF03] API caixa-preta: cadastros e pesquisa | 8 | ✅ 0 | 3.76 s |
| API caixa-preta | `DocumentacaoApiIT` | [RF14] Documentação pública da API | 2 | ✅ 0 | 1.64 s |
| API caixa-preta | `RelatorioApiIT` | [RF12] API caixa-preta: relatórios | 3 | ✅ 0 | 1.55 s |
| API caixa-preta | `ReservaApiIT` | [RF04][RF05] API caixa-preta: reservas | 9 | ✅ 0 | 4.31 s |
| API caixa-preta | `SegurancaApiIT` | [RNF02][RNF03][RNF04] API caixa-preta: segurança | 39 | ✅ 0 | 5.73 s |
| Integração (MongoDB real + WireMock) | `NotificacaoWireMockIT` | [RF11] Integração com API externa de notificação (WireMock) | 5 | ✅ 0 | 2.04 s |
| Integração (MongoDB real + WireMock) | `ReservaServiceIT` | Integração: regras de reserva com MongoDB real | 25 | ✅ 0 | 7.46 s |
| Concorrência | `ConcorrenciaReservaIT` | [RF06][RN03] Concorrência: solicitações simultâneas | 7 | ✅ 0 | 2.15 s |
| BDD (Cucumber) | `CucumberIT` | CucumberIT | 9 | ✅ 0 | 5.79 s |
| E2E (Selenium) | `FluxoReservaE2EIT` | [RF13] End-to-end: fluxo de reserva pela interface web | 4 | ✅ 0 | 17.63 s |

## 10.4 Falhas

Nenhuma falha. Todos os testes passaram.

## 10.5 Cobertura por pacote

| Pacote | Linhas | Desvios |
|---|---|---|
| `auditoria` | 97.4% | 70.0% |
| `auth` | 98.6% | 90.0% |
| `bloqueio` | 74.7% | 46.7% |
| `comum` | 78.5% | 81.2% |
| `material` | 69.2% | 50.0% |
| `notificacao` | 79.6% | 87.5% |
| `professor` | 69.3% | 50.0% |
| `relatorio` | 88.8% | 50.0% |
| `reserva` | 93.8% | 81.2% |
| `reserva.regras` | 100.0% | 95.0% |
| `sala` | 87.7% | 58.3% |
| `usuario` | 61.2% | 10.0% |

## 10.6 Testes mais lentos

| Classe | Teste | Tempo |
|---|---|---|
| `FluxoReservaE2EIT` | `responsivo` | 4.22 s |
| `FluxoReservaE2EIT` | `mostraConflito` | 3.32 s |
| `FluxoReservaE2EIT` | `criaReserva` | 2.64 s |
| `FluxoReservaE2EIT` | `loginInvalido` | 1.47 s |
| `NotificacaoWireMockIT` | `timeoutExterno` | 1.19 s |
| `DocumentacaoApiIT` | `openApi` | 1.05 s |
| `RelatorioApiIT` | `relatorios` | 0.83 s |
| `ConcorrenciaReservaIT` | `bloqueioSimultaneoAReserva` | 0.81 s |
| `CucumberIT` | `Conclusão após a devolução completa` | 0.80 s |
| `ReservaApiIT` | `retiradaDevolucao` | 0.70 s |

## 10.7 Lista completa de casos de teste

<details><summary><code>LimitadorTentativasLoginTest</code> — 5 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `liberaAposJanela` | ✅ | 0.003 s |
| `descartaTentativasExpiradas` | ✅ | 0.100 s |
| `bloqueia` | ✅ | 0.001 s |
| `independentes` | ✅ | 0.001 s |
| `sucessoZera` | ✅ | 0.001 s |

</details>

<details><summary><code>UtilitariosTest</code> — 9 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `json` | ✅ | 0.004 s |
| `igual` | ✅ | 0.002 s |
| `filtroEscapado(String)[1]` | ✅ | 0.002 s |
| `filtroEscapado(String)[2]` | ✅ | 0.001 s |
| `filtroEscapado(String)[3]` | ✅ | 0.002 s |
| `filtroEscapado(String)[4]` | ✅ | 0.001 s |
| `filtroEscapado(String)[5]` | ✅ | 0.002 s |
| `filtroEscapado(String)[6]` | ✅ | 0.001 s |
| `intervaloOpcional` | ✅ | 0.003 s |

</details>

<details><summary><code>ProtecaoConcorrenciaTest</code> — 4 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `rotuloTransitorio` | ✅ | 0.053 s |
| `naoRepeteNegocio` | ✅ | 0.014 s |
| `repeteAteConseguir` | ✅ | 0.057 s |
| `desiste` | ✅ | 0.683 s |

</details>

<details><summary><code>StatusReservaTest</code> — 71 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `liberamAgenda(StatusReserva)[1]` | ✅ | 0.000 s |
| `liberamAgenda(StatusReserva)[2]` | ✅ | 0.001 s |
| `liberamAgenda(StatusReserva)[3]` | ✅ | 0.001 s |
| `liberamAgenda(StatusReserva)[4]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[1]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[2]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[3]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[4]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[5]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[6]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[7]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[8]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[9]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[10]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[11]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[12]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[13]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[14]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[15]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[16]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[17]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[18]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[19]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[20]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[21]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[22]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[23]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[24]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[25]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[26]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[27]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[28]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[29]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[30]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[31]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[32]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[33]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[34]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[35]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[36]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[37]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[38]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[39]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[40]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[41]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[42]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[43]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[44]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[45]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[46]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[47]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[48]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[49]` | ✅ | 0.001 s |
| `ocupamAgenda(StatusReserva)[1]` | ✅ | 0.000 s |
| `ocupamAgenda(StatusReserva)[2]` | ✅ | 0.000 s |
| `ocupamAgenda(StatusReserva)[3]` | ✅ | 0.001 s |
| `iniciadas(StatusReserva, boolean)[1]` | ✅ | 0.001 s |
| `iniciadas(StatusReserva, boolean)[2]` | ✅ | 0.001 s |
| `iniciadas(StatusReserva, boolean)[3]` | ✅ | 0.001 s |
| `iniciadas(StatusReserva, boolean)[4]` | ✅ | 0.001 s |
| `iniciadas(StatusReserva, boolean)[5]` | ✅ | 0.000 s |
| `iniciadas(StatusReserva, boolean)[6]` | ✅ | 0.000 s |
| `iniciadas(StatusReserva, boolean)[7]` | ✅ | 0.000 s |
| `transicaoInvalidaNaEntidade(StatusReserva, StatusReserva)[1]` | ✅ | 0.002 s |
| `transicaoInvalidaNaEntidade(StatusReserva, StatusReserva)[2]` | ✅ | 0.001 s |
| `transicaoInvalidaNaEntidade(StatusReserva, StatusReserva)[3]` | ✅ | 0.001 s |
| `transicaoInvalidaNaEntidade(StatusReserva, StatusReserva)[4]` | ✅ | 0.001 s |
| `transicaoInvalidaNaEntidade(StatusReserva, StatusReserva)[5]` | ✅ | 0.000 s |
| `transicaoInvalidaNaEntidade(StatusReserva, StatusReserva)[6]` | ✅ | 0.001 s |
| `transicaoInvalidaNaEntidade(StatusReserva, StatusReserva)[7]` | ✅ | 0.001 s |
| `fluxoPrincipal` | ✅ | 0.001 s |

</details>

<details><summary><code>AvaliadorAprovacaoTest</code> — 8 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `acumulaMotivos` | ✅ | 0.009 s |
| `semMotivos` | ✅ | 0.001 s |
| `salaRestrita` | ✅ | 0.002 s |
| `materialRestrito` | ✅ | 0.001 s |
| `semCompetencia` | ✅ | 0.001 s |
| `normalizacao(String)[1]` | ✅ | 0.003 s |
| `normalizacao(String)[2]` | ✅ | 0.001 s |
| `normalizacao(String)[3]` | ✅ | 0.002 s |

</details>

<details><summary><code>CalculadoraOcupacaoTest</code> — 8 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `pico(String, List, int)[1]` | ✅ | 0.004 s |
| `pico(String, List, int)[2]` | ✅ | 0.001 s |
| `pico(String, List, int)[3]` | ✅ | 0.001 s |
| `pico(String, List, int)[4]` | ✅ | 0.000 s |
| `pico(String, List, int)[5]` | ✅ | 0.001 s |
| `pico(String, List, int)[6]` | ✅ | 0.001 s |
| `pico(String, List, int)[7]` | ✅ | 0.000 s |
| `recorte` | ✅ | 0.001 s |

</details>

<details><summary><code>IntervaloTest</code> — 14 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `terminoNaoPosterior(String, String)[1]` | ✅ | 0.147 s |
| `terminoNaoPosterior(String, String)[2]` | ✅ | 0.001 s |
| `terminoNaoPosterior(String, String)[3]` | ✅ | 0.002 s |
| `nulos` | ✅ | 0.009 s |
| `sobreposicao(String, String, String, String, boolean)[1]` | ✅ | 0.012 s |
| `sobreposicao(String, String, String, String, boolean)[2]` | ✅ | 0.001 s |
| `sobreposicao(String, String, String, String, boolean)[3]` | ✅ | 0.001 s |
| `sobreposicao(String, String, String, String, boolean)[4]` | ✅ | 0.001 s |
| `sobreposicao(String, String, String, String, boolean)[5]` | ✅ | 0.000 s |
| `sobreposicao(String, String, String, String, boolean)[6]` | ✅ | 0.001 s |
| `sobreposicao(String, String, String, String, boolean)[7]` | ✅ | 0.000 s |
| `sobreposicao(String, String, String, String, boolean)[8]` | ✅ | 0.001 s |
| `sobreposicao(String, String, String, String, boolean)[9]` | ✅ | 0.000 s |
| `duracao` | ✅ | 0.004 s |

</details>

<details><summary><code>PoliticaHorarioTest</code> — 15 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `invalidos(String, String, String, String)[1]` | ✅ | 0.004 s |
| `invalidos(String, String, String, String)[2]` | ✅ | 0.001 s |
| `invalidos(String, String, String, String)[3]` | ✅ | 0.001 s |
| `invalidos(String, String, String, String)[4]` | ✅ | 0.001 s |
| `invalidos(String, String, String, String)[5]` | ✅ | 0.000 s |
| `invalidos(String, String, String, String)[6]` | ✅ | 0.001 s |
| `invalidos(String, String, String, String)[7]` | ✅ | 0.001 s |
| `invalidos(String, String, String, String)[8]` | ✅ | 0.002 s |
| `invalidos(String, String, String, String)[9]` | ✅ | 0.002 s |
| `devolveIntervalo` | ✅ | 0.002 s |
| `validos(String, String)[1]` | ✅ | 0.002 s |
| `validos(String, String)[2]` | ✅ | 0.002 s |
| `validos(String, String)[3]` | ✅ | 0.001 s |
| `validos(String, String)[4]` | ✅ | 0.001 s |
| `validos(String, String)[5]` | ✅ | 0.001 s |

</details>

<details><summary><code>AutenticacaoApiIT</code> — 5 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `forcaBruta` | ✅ | 0.446 s |
| `eu` | ✅ | 0.163 s |
| `loginValido` | ✅ | 0.170 s |
| `inativo` | ✅ | 0.165 s |
| `mensagemGenerica` | ✅ | 0.255 s |

</details>

<details><summary><code>CadastroPesquisaApiIT</code> — 8 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `pesquisaMateriais` | ✅ | 0.531 s |
| `booleanosOpcionaisOmitidos` | ✅ | 0.378 s |
| `pesquisaProfessores` | ✅ | 0.550 s |
| `pesquisaSalas` | ✅ | 0.479 s |
| `cadastros` | ✅ | 0.584 s |
| `duplicado` | ✅ | 0.316 s |
| `pesquisaDisponibilidade` | ✅ | 0.530 s |
| `auditoriaCadastro` | ✅ | 0.375 s |

</details>

<details><summary><code>DocumentacaoApiIT</code> — 2 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `swaggerUi` | ✅ | 0.557 s |
| `openApi` | ✅ | 1.052 s |

</details>

<details><summary><code>RelatorioApiIT</code> — 3 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `relatorios` | ✅ | 0.827 s |
| `periodoInvalido` | ✅ | 0.192 s |
| `conflitosEvitados` | ✅ | 0.520 s |

</details>

<details><summary><code>ReservaApiIT</code> — 9 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `retiradaDevolucao` | ✅ | 0.702 s |
| `minhas` | ✅ | 0.513 s |
| `conflito` | ✅ | 0.398 s |
| `aprovacao` | ✅ | 0.577 s |
| `criar` | ✅ | 0.299 s |
| `alterarECancelar` | ✅ | 0.528 s |
| `terminoAntesDoInicio` | ✅ | 0.252 s |
| `historico` | ✅ | 0.509 s |
| `apagarIniciada` | ✅ | 0.513 s |

</details>

<details><summary><code>SegurancaApiIT</code> — 39 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `tokenForjado` | ✅ | 0.022 s |
| `validacaoCampos` | ✅ | 0.182 s |
| `parametroInvalido` | ✅ | 0.163 s |
| `usuarioDesativadoPerdeAcesso` | ✅ | 0.171 s |
| `cancelarReservaAlheia` | ✅ | 0.366 s |
| `perfilAtualizadoValeImediatamente` | ✅ | 0.184 s |
| `tokenAdulterado` | ✅ | 0.162 s |
| `autorizacaoPorPerfil(Perfil, String, String)[1]` | ✅ | 0.177 s |
| `autorizacaoPorPerfil(Perfil, String, String)[2]` | ✅ | 0.173 s |
| `autorizacaoPorPerfil(Perfil, String, String)[3]` | ✅ | 0.155 s |
| `autorizacaoPorPerfil(Perfil, String, String)[4]` | ✅ | 0.161 s |
| `autorizacaoPorPerfil(Perfil, String, String)[5]` | ✅ | 0.187 s |
| `autorizacaoPorPerfil(Perfil, String, String)[6]` | ✅ | 0.157 s |
| `autorizacaoPorPerfil(Perfil, String, String)[7]` | ✅ | 0.159 s |
| `autorizacaoPorPerfil(Perfil, String, String)[8]` | ✅ | 0.154 s |
| `autorizacaoPorPerfil(Perfil, String, String)[9]` | ✅ | 0.164 s |
| `autorizacaoPorPerfil(Perfil, String, String)[10]` | ✅ | 0.173 s |
| `autorizacaoPorPerfil(Perfil, String, String)[11]` | ✅ | 0.168 s |
| `cabecalhos` | ✅ | 0.025 s |
| `atribuicaoEmMassa` | ✅ | 0.209 s |
| `rotaInexistente` | ✅ | 0.165 s |
| `erroSeguro` | ✅ | 0.177 s |
| `injecaoNoFiltro(String)[1]` | ✅ | 0.160 s |
| `injecaoNoFiltro(String)[2]` | ✅ | 0.167 s |
| `injecaoNoFiltro(String)[3]` | ✅ | 0.169 s |
| `injecaoNoFiltro(String)[4]` | ✅ | 0.164 s |
| `injecaoNoFiltro(String)[5]` | ✅ | 0.175 s |
| `semToken(String, String)[1]` | ✅ | 0.021 s |
| `semToken(String, String)[2]` | ✅ | 0.024 s |
| `semToken(String, String)[3]` | ✅ | 0.031 s |
| `semToken(String, String)[4]` | ✅ | 0.022 s |
| `semToken(String, String)[5]` | ✅ | 0.017 s |
| `semToken(String, String)[6]` | ✅ | 0.021 s |
| `jsonMalformado(String)[1]` | ✅ | 0.155 s |
| `jsonMalformado(String)[2]` | ✅ | 0.172 s |
| `jsonMalformado(String)[3]` | ✅ | 0.174 s |
| `jsonMalformado(String)[4]` | ✅ | 0.161 s |
| `professorSoReservaParaSi` | ✅ | 0.171 s |
| `semHash` | ✅ | 0.157 s |

</details>

<details><summary><code>CucumberIT</code> — 9 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `Retirada dentro da quantidade reservada` | ✅ | 0.656 s |
| `Retirada acima da quantidade reservada é recusada` | ✅ | 0.474 s |
| `Devolução maior que a retirada é recusada` | ✅ | 0.547 s |
| `Não é possível concluir a reserva com materiais pendentes` | ✅ | 0.576 s |
| `Conclusão após a devolução completa` | ✅ | 0.799 s |
| `Example #1.1` | ✅ | 0.542 s |
| `Example #1.2` | ✅ | 0.596 s |
| `Example #1.3` | ✅ | 0.536 s |
| `Example #1.4` | ✅ | 0.546 s |

</details>

<details><summary><code>ConcorrenciaReservaIT</code> — 7 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `mesmaSala()[1]` | ✅ | 0.243 s |
| `mesmaSala()[2]` | ✅ | 0.173 s |
| `mesmaSala()[3]` | ✅ | 0.158 s |
| `viaApi` | ✅ | 0.220 s |
| `estoqueDeMaterial` | ✅ | 0.350 s |
| `bloqueioSimultaneoAReserva` | ✅ | 0.809 s |
| `mesmoProfessor` | ✅ | 0.172 s |

</details>

<details><summary><code>FluxoReservaE2EIT</code> — 4 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `responsivo` | ✅ | 4.216 s |
| `loginInvalido` | ✅ | 1.466 s |
| `criaReserva` | ✅ | 2.636 s |
| `mostraConflito` | ✅ | 3.316 s |

</details>

<details><summary><code>NotificacaoWireMockIT</code> — 5 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `notificaSolicitante` | ✅ | 0.219 s |
| `timeoutExterno` | ✅ | 1.186 s |
| `notificaResponsavel` | ✅ | 0.223 s |
| `falhaExterna` | ✅ | 0.189 s |
| `semNotificacaoEmFalha` | ✅ | 0.200 s |

</details>

<details><summary><code>ReservaServiceIT</code> — 25 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `docenteSemCompetencia` | ✅ | 0.291 s |
| `conflitoAgendaProfessor` | ✅ | 0.294 s |
| `iniciadaNaoApaga` | ✅ | 0.323 s |
| `restritaPrecisaAprovacao` | ✅ | 0.354 s |
| `conflitoMaterial` | ✅ | 0.359 s |
| `alteracao` | ✅ | 0.303 s |
| `materialEmManutencao` | ✅ | 0.310 s |
| `salaEmManutencao` | ✅ | 0.254 s |
| `passado` | ✅ | 0.243 s |
| `capacidade` | ✅ | 0.219 s |
| `alteracaoExigeNovaAprovacao` | ✅ | 0.294 s |
| `alteracaoVersaoAntiga` | ✅ | 0.256 s |
| `semAuditoriaParcial` | ✅ | 0.358 s |
| `conflitoSala` | ✅ | 0.293 s |
| `conflitoRegistrado` | ✅ | 0.309 s |
| `aprovacaoAutomatica` | ✅ | 0.272 s |
| `canceladaLibera` | ✅ | 0.346 s |
| `bloqueioInformaAfetadas` | ✅ | 0.286 s |
| `cicloCompletoAuditado` | ✅ | 0.315 s |
| `movimentacao` | ✅ | 0.332 s |
| `aprovarRecursoEmManutencao` | ✅ | 0.273 s |
| `adjacentes` | ✅ | 0.282 s |
| `naoCompareceu` | ✅ | 0.287 s |
| `naoIniciadaApaga` | ✅ | 0.288 s |
| `auditoriaPorTransicao` | ✅ | 0.252 s |

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
