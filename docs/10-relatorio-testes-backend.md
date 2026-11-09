# 10. Relatório de testes de back-end

> Gerado automaticamente por `scripts/relatorio_backend.py` a partir dos relatórios do JUnit (`target/surefire-reports`, `target/failsafe-reports`) e do JaCoCo (`target/site/jacoco`), após `./mvnw verify`.

## 10.1 Resumo

**Resultado: ✅ APROVADO**

| Item | Valor |
|---|---|
| Data da geração | 08/11/2026 21:42 |
| Testes executados | 250 |
| Aprovados | 250 |
| Falhas ou erros | 0 |
| Pulados | 0 |
| Tempo somado | 89.5 s |
| Cobertura de linhas | 85.8% (meta: 80%) |
| Cobertura de desvios (branches) | 72.6% |
| Cobertura de métodos | 82.5% |

## 10.2 Resultado por nível da pirâmide

| Nível | Classes | Testes | Falhas | Tempo |
|---|---|---|---|---|
| Unitário | 8 | 134 | 0 | 4.3 s |
| API caixa-preta | 6 | 66 | 0 | 30.8 s |
| Integração (MongoDB real + WireMock) | 2 | 30 | 0 | 15.1 s |
| Concorrência | 1 | 7 | 0 | 3.8 s |
| BDD (Cucumber) | 1 | 9 | 0 | 9.7 s |
| E2E (Selenium) | 1 | 4 | 0 | 25.8 s |

## 10.3 Resultado por classe de teste

| Nível | Classe | Descrição | Testes | Falhas | Tempo |
|---|---|---|---|---|---|
| Unitário | `LimitadorTentativasLoginTest` | [RNF02] Proteção contra força bruta no login | 5 | ✅ 0 | 0.21 s |
| Unitário | `UtilitariosTest` | Utilitários de filtro e JSON | 9 | ✅ 0 | 0.07 s |
| Unitário | `ProtecaoConcorrenciaTest` | [RF06] Repetição de transações em conflito de escrita | 4 | ✅ 0 | 3.30 s |
| Unitário | `StatusReservaTest` | [RN05] Máquina de estados da reserva | 71 | ✅ 0 | 0.13 s |
| Unitário | `AvaliadorAprovacaoTest` | [RF07] Avaliação da necessidade de aprovação | 8 | ✅ 0 | 0.06 s |
| Unitário | `CalculadoraOcupacaoTest` | [RN02] Pico de uso simultâneo de materiais | 8 | ✅ 0 | 0.05 s |
| Unitário | `IntervaloTest` | Intervalo de tempo | 14 | ✅ 0 | 0.47 s |
| Unitário | `PoliticaHorarioTest` | Política de horário das reservas | 15 | ✅ 0 | 0.05 s |
| API caixa-preta | `AutenticacaoApiIT` | [RF01] API caixa-preta: autenticação | 5 | ✅ 0 | 2.05 s |
| API caixa-preta | `CadastroPesquisaApiIT` | [RF02][RF03] API caixa-preta: cadastros e pesquisa | 8 | ✅ 0 | 6.24 s |
| API caixa-preta | `DocumentacaoApiIT` | [RF14] Documentação pública da API | 2 | ✅ 0 | 2.68 s |
| API caixa-preta | `RelatorioApiIT` | [RF12] API caixa-preta: relatórios | 3 | ✅ 0 | 2.54 s |
| API caixa-preta | `ReservaApiIT` | [RF04][RF05] API caixa-preta: reservas | 9 | ✅ 0 | 7.33 s |
| API caixa-preta | `SegurancaApiIT` | [RNF02][RNF03][RNF04] API caixa-preta: segurança | 39 | ✅ 0 | 9.99 s |
| Integração (MongoDB real + WireMock) | `NotificacaoWireMockIT` | [RF11] Integração com API externa de notificação (WireMock) | 5 | ✅ 0 | 2.72 s |
| Integração (MongoDB real + WireMock) | `ReservaServiceIT` | Integração: regras de reserva com MongoDB real | 25 | ✅ 0 | 12.39 s |
| Concorrência | `ConcorrenciaReservaIT` | [RF06][RN03] Concorrência: solicitações simultâneas | 7 | ✅ 0 | 3.76 s |
| BDD (Cucumber) | `CucumberIT` | CucumberIT | 9 | ✅ 0 | 9.67 s |
| E2E (Selenium) | `FluxoReservaE2EIT` | [RF13] End-to-end: fluxo de reserva pela interface web | 4 | ✅ 0 | 25.78 s |

## 10.4 Falhas

Nenhuma falha. Todos os testes passaram.

## 10.5 Cobertura por pacote

| Pacote | Linhas | Desvios |
|---|---|---|
| `auditoria` | 97.4% | 70.0% |
| `auth` | 98.5% | 90.0% |
| `bloqueio` | 75.9% | 46.7% |
| `comum` | 81.7% | 81.2% |
| `material` | 70.1% | 50.0% |
| `notificacao` | 79.8% | 87.5% |
| `professor` | 70.3% | 50.0% |
| `relatorio` | 87.8% | 50.0% |
| `reserva` | 94.1% | 81.2% |
| `reserva.regras` | 100.0% | 95.0% |
| `sala` | 88.8% | 58.3% |
| `usuario` | 62.8% | 10.0% |

## 10.6 Testes mais lentos

| Classe | Teste | Tempo |
|---|---|---|
| `FluxoReservaE2EIT` | `responsivo` | 4.98 s |
| `FluxoReservaE2EIT` | `mostraConflito` | 4.13 s |
| `FluxoReservaE2EIT` | `criaReserva` | 3.95 s |
| `FluxoReservaE2EIT` | `loginInvalido` | 1.97 s |
| `DocumentacaoApiIT` | `openApi` | 1.76 s |
| `CucumberIT` | `Conclusão após a devolução completa` | 1.32 s |
| `NotificacaoWireMockIT` | `timeoutExterno` | 1.32 s |
| `RelatorioApiIT` | `relatorios` | 1.31 s |
| `ConcorrenciaReservaIT` | `bloqueioSimultaneoAReserva` | 1.28 s |
| `ReservaApiIT` | `retiradaDevolucao` | 1.23 s |

## 10.7 Lista completa de casos de teste

<details><summary><code>LimitadorTentativasLoginTest</code> — 5 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `liberaAposJanela` | ✅ | 0.006 s |
| `descartaTentativasExpiradas` | ✅ | 0.190 s |
| `bloqueia` | ✅ | 0.003 s |
| `independentes` | ✅ | 0.001 s |
| `sucessoZera` | ✅ | 0.002 s |

</details>

<details><summary><code>UtilitariosTest</code> — 9 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `json` | ✅ | 0.007 s |
| `igual` | ✅ | 0.006 s |
| `filtroEscapado(String)[1]` | ✅ | 0.004 s |
| `filtroEscapado(String)[2]` | ✅ | 0.003 s |
| `filtroEscapado(String)[3]` | ✅ | 0.002 s |
| `filtroEscapado(String)[4]` | ✅ | 0.003 s |
| `filtroEscapado(String)[5]` | ✅ | 0.002 s |
| `filtroEscapado(String)[6]` | ✅ | 0.003 s |
| `intervaloOpcional` | ✅ | 0.006 s |

</details>

<details><summary><code>ProtecaoConcorrenciaTest</code> — 4 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `rotuloTransitorio` | ✅ | 0.120 s |
| `naoRepeteNegocio` | ✅ | 0.025 s |
| `repeteAteConseguir` | ✅ | 0.053 s |
| `desiste` | ✅ | 0.637 s |

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
| `matrizDeTransicoes(StatusReserva, StatusReserva)[6]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[7]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[8]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[9]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[10]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[11]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[12]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[13]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[14]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[15]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[16]` | ✅ | 0.000 s |
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
| `matrizDeTransicoes(StatusReserva, StatusReserva)[28]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[29]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[30]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[31]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[32]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[33]` | ✅ | 0.001 s |
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
| `matrizDeTransicoes(StatusReserva, StatusReserva)[44]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[45]` | ✅ | 0.001 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[46]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[47]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[48]` | ✅ | 0.000 s |
| `matrizDeTransicoes(StatusReserva, StatusReserva)[49]` | ✅ | 0.001 s |
| `ocupamAgenda(StatusReserva)[1]` | ✅ | 0.001 s |
| `ocupamAgenda(StatusReserva)[2]` | ✅ | 0.000 s |
| `ocupamAgenda(StatusReserva)[3]` | ✅ | 0.000 s |
| `iniciadas(StatusReserva, boolean)[1]` | ✅ | 0.003 s |
| `iniciadas(StatusReserva, boolean)[2]` | ✅ | 0.001 s |
| `iniciadas(StatusReserva, boolean)[3]` | ✅ | 0.000 s |
| `iniciadas(StatusReserva, boolean)[4]` | ✅ | 0.001 s |
| `iniciadas(StatusReserva, boolean)[5]` | ✅ | 0.001 s |
| `iniciadas(StatusReserva, boolean)[6]` | ✅ | 0.001 s |
| `iniciadas(StatusReserva, boolean)[7]` | ✅ | 0.001 s |
| `transicaoInvalidaNaEntidade(StatusReserva, StatusReserva)[1]` | ✅ | 0.004 s |
| `transicaoInvalidaNaEntidade(StatusReserva, StatusReserva)[2]` | ✅ | 0.001 s |
| `transicaoInvalidaNaEntidade(StatusReserva, StatusReserva)[3]` | ✅ | 0.002 s |
| `transicaoInvalidaNaEntidade(StatusReserva, StatusReserva)[4]` | ✅ | 0.001 s |
| `transicaoInvalidaNaEntidade(StatusReserva, StatusReserva)[5]` | ✅ | 0.001 s |
| `transicaoInvalidaNaEntidade(StatusReserva, StatusReserva)[6]` | ✅ | 0.001 s |
| `transicaoInvalidaNaEntidade(StatusReserva, StatusReserva)[7]` | ✅ | 0.002 s |
| `fluxoPrincipal` | ✅ | 0.001 s |

</details>

<details><summary><code>AvaliadorAprovacaoTest</code> — 8 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `acumulaMotivos` | ✅ | 0.019 s |
| `semMotivos` | ✅ | 0.001 s |
| `salaRestrita` | ✅ | 0.004 s |
| `materialRestrito` | ✅ | 0.002 s |
| `semCompetencia` | ✅ | 0.002 s |
| `normalizacao(String)[1]` | ✅ | 0.002 s |
| `normalizacao(String)[2]` | ✅ | 0.001 s |
| `normalizacao(String)[3]` | ✅ | 0.001 s |

</details>

<details><summary><code>CalculadoraOcupacaoTest</code> — 8 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `pico(String, List, int)[1]` | ✅ | 0.007 s |
| `pico(String, List, int)[2]` | ✅ | 0.002 s |
| `pico(String, List, int)[3]` | ✅ | 0.001 s |
| `pico(String, List, int)[4]` | ✅ | 0.001 s |
| `pico(String, List, int)[5]` | ✅ | 0.002 s |
| `pico(String, List, int)[6]` | ✅ | 0.002 s |
| `pico(String, List, int)[7]` | ✅ | 0.002 s |
| `recorte` | ✅ | 0.002 s |

</details>

<details><summary><code>IntervaloTest</code> — 14 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `terminoNaoPosterior(String, String)[1]` | ✅ | 0.193 s |
| `terminoNaoPosterior(String, String)[2]` | ✅ | 0.003 s |
| `terminoNaoPosterior(String, String)[3]` | ✅ | 0.002 s |
| `nulos` | ✅ | 0.024 s |
| `sobreposicao(String, String, String, String, boolean)[1]` | ✅ | 0.020 s |
| `sobreposicao(String, String, String, String, boolean)[2]` | ✅ | 0.002 s |
| `sobreposicao(String, String, String, String, boolean)[3]` | ✅ | 0.002 s |
| `sobreposicao(String, String, String, String, boolean)[4]` | ✅ | 0.002 s |
| `sobreposicao(String, String, String, String, boolean)[5]` | ✅ | 0.002 s |
| `sobreposicao(String, String, String, String, boolean)[6]` | ✅ | 0.003 s |
| `sobreposicao(String, String, String, String, boolean)[7]` | ✅ | 0.002 s |
| `sobreposicao(String, String, String, String, boolean)[8]` | ✅ | 0.001 s |
| `sobreposicao(String, String, String, String, boolean)[9]` | ✅ | 0.002 s |
| `duracao` | ✅ | 0.006 s |

</details>

<details><summary><code>PoliticaHorarioTest</code> — 15 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `invalidos(String, String, String, String)[1]` | ✅ | 0.004 s |
| `invalidos(String, String, String, String)[2]` | ✅ | 0.002 s |
| `invalidos(String, String, String, String)[3]` | ✅ | 0.001 s |
| `invalidos(String, String, String, String)[4]` | ✅ | 0.001 s |
| `invalidos(String, String, String, String)[5]` | ✅ | 0.002 s |
| `invalidos(String, String, String, String)[6]` | ✅ | 0.001 s |
| `invalidos(String, String, String, String)[7]` | ✅ | 0.002 s |
| `invalidos(String, String, String, String)[8]` | ✅ | 0.002 s |
| `invalidos(String, String, String, String)[9]` | ✅ | 0.002 s |
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
| `forcaBruta` | ✅ | 0.768 s |
| `eu` | ✅ | 0.277 s |
| `loginValido` | ✅ | 0.307 s |
| `inativo` | ✅ | 0.273 s |
| `mensagemGenerica` | ✅ | 0.404 s |

</details>

<details><summary><code>CadastroPesquisaApiIT</code> — 8 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `pesquisaMateriais` | ✅ | 0.910 s |
| `booleanosOpcionaisOmitidos` | ✅ | 0.651 s |
| `pesquisaProfessores` | ✅ | 0.837 s |
| `pesquisaSalas` | ✅ | 0.790 s |
| `cadastros` | ✅ | 1.064 s |
| `duplicado` | ✅ | 0.435 s |
| `pesquisaDisponibilidade` | ✅ | 0.876 s |
| `auditoriaCadastro` | ✅ | 0.652 s |

</details>

<details><summary><code>DocumentacaoApiIT</code> — 2 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `swaggerUi` | ✅ | 0.883 s |
| `openApi` | ✅ | 1.759 s |

</details>

<details><summary><code>RelatorioApiIT</code> — 3 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `relatorios` | ✅ | 1.311 s |
| `periodoInvalido` | ✅ | 0.297 s |
| `conflitosEvitados` | ✅ | 0.908 s |

</details>

<details><summary><code>ReservaApiIT</code> — 9 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `retiradaDevolucao` | ✅ | 1.234 s |
| `minhas` | ✅ | 0.889 s |
| `conflito` | ✅ | 0.677 s |
| `aprovacao` | ✅ | 1.028 s |
| `criar` | ✅ | 0.485 s |
| `alterarECancelar` | ✅ | 0.874 s |
| `terminoAntesDoInicio` | ✅ | 0.436 s |
| `historico` | ✅ | 0.830 s |
| `apagarIniciada` | ✅ | 0.849 s |

</details>

<details><summary><code>SegurancaApiIT</code> — 39 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `tokenForjado` | ✅ | 0.055 s |
| `validacaoCampos` | ✅ | 0.316 s |
| `parametroInvalido` | ✅ | 0.306 s |
| `usuarioDesativadoPerdeAcesso` | ✅ | 0.330 s |
| `cancelarReservaAlheia` | ✅ | 0.591 s |
| `perfilAtualizadoValeImediatamente` | ✅ | 0.289 s |
| `tokenAdulterado` | ✅ | 0.270 s |
| `autorizacaoPorPerfil(Perfil, String, String)[1]` | ✅ | 0.305 s |
| `autorizacaoPorPerfil(Perfil, String, String)[2]` | ✅ | 0.278 s |
| `autorizacaoPorPerfil(Perfil, String, String)[3]` | ✅ | 0.283 s |
| `autorizacaoPorPerfil(Perfil, String, String)[4]` | ✅ | 0.283 s |
| `autorizacaoPorPerfil(Perfil, String, String)[5]` | ✅ | 0.296 s |
| `autorizacaoPorPerfil(Perfil, String, String)[6]` | ✅ | 0.284 s |
| `autorizacaoPorPerfil(Perfil, String, String)[7]` | ✅ | 0.273 s |
| `autorizacaoPorPerfil(Perfil, String, String)[8]` | ✅ | 0.293 s |
| `autorizacaoPorPerfil(Perfil, String, String)[9]` | ✅ | 0.287 s |
| `autorizacaoPorPerfil(Perfil, String, String)[10]` | ✅ | 0.281 s |
| `autorizacaoPorPerfil(Perfil, String, String)[11]` | ✅ | 0.294 s |
| `cabecalhos` | ✅ | 0.049 s |
| `atribuicaoEmMassa` | ✅ | 0.357 s |
| `rotaInexistente` | ✅ | 0.371 s |
| `erroSeguro` | ✅ | 0.295 s |
| `injecaoNoFiltro(String)[1]` | ✅ | 0.300 s |
| `injecaoNoFiltro(String)[2]` | ✅ | 0.292 s |
| `injecaoNoFiltro(String)[3]` | ✅ | 0.292 s |
| `injecaoNoFiltro(String)[4]` | ✅ | 0.279 s |
| `injecaoNoFiltro(String)[5]` | ✅ | 0.290 s |
| `semToken(String, String)[1]` | ✅ | 0.049 s |
| `semToken(String, String)[2]` | ✅ | 0.037 s |
| `semToken(String, String)[3]` | ✅ | 0.036 s |
| `semToken(String, String)[4]` | ✅ | 0.032 s |
| `semToken(String, String)[5]` | ✅ | 0.039 s |
| `semToken(String, String)[6]` | ✅ | 0.037 s |
| `jsonMalformado(String)[1]` | ✅ | 0.284 s |
| `jsonMalformado(String)[2]` | ✅ | 0.284 s |
| `jsonMalformado(String)[3]` | ✅ | 0.300 s |
| `jsonMalformado(String)[4]` | ✅ | 0.289 s |
| `professorSoReservaParaSi` | ✅ | 0.287 s |
| `semHash` | ✅ | 0.285 s |

</details>

<details><summary><code>CucumberIT</code> — 9 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `Retirada dentro da quantidade reservada` | ✅ | 1.127 s |
| `Retirada acima da quantidade reservada é recusada` | ✅ | 0.825 s |
| `Devolução maior que a retirada é recusada` | ✅ | 1.014 s |
| `Não é possível concluir a reserva com materiais pendentes` | ✅ | 0.948 s |
| `Conclusão após a devolução completa` | ✅ | 1.324 s |
| `Example #1.1` | ✅ | 0.924 s |
| `Example #1.2` | ✅ | 0.896 s |
| `Example #1.3` | ✅ | 0.904 s |
| `Example #1.4` | ✅ | 0.920 s |

</details>

<details><summary><code>ConcorrenciaReservaIT</code> — 7 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `mesmaSala()[1]` | ✅ | 0.375 s |
| `mesmaSala()[2]` | ✅ | 0.400 s |
| `mesmaSala()[3]` | ✅ | 0.387 s |
| `viaApi` | ✅ | 0.430 s |
| `estoqueDeMaterial` | ✅ | 0.528 s |
| `bloqueioSimultaneoAReserva` | ✅ | 1.283 s |
| `mesmoProfessor` | ✅ | 0.312 s |

</details>

<details><summary><code>FluxoReservaE2EIT</code> — 4 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `responsivo` | ✅ | 4.984 s |
| `loginInvalido` | ✅ | 1.972 s |
| `criaReserva` | ✅ | 3.946 s |
| `mostraConflito` | ✅ | 4.130 s |

</details>

<details><summary><code>NotificacaoWireMockIT</code> — 5 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `notificaSolicitante` | ✅ | 0.414 s |
| `timeoutExterno` | ✅ | 1.316 s |
| `notificaResponsavel` | ✅ | 0.316 s |
| `falhaExterna` | ✅ | 0.317 s |
| `semNotificacaoEmFalha` | ✅ | 0.327 s |

</details>

<details><summary><code>ReservaServiceIT</code> — 25 testes</summary>

| Teste | Situação | Tempo |
|---|---|---|
| `docenteSemCompetencia` | ✅ | 0.514 s |
| `conflitoAgendaProfessor` | ✅ | 0.561 s |
| `iniciadaNaoApaga` | ✅ | 0.534 s |
| `restritaPrecisaAprovacao` | ✅ | 0.559 s |
| `conflitoMaterial` | ✅ | 0.619 s |
| `alteracao` | ✅ | 0.551 s |
| `materialEmManutencao` | ✅ | 0.479 s |
| `salaEmManutencao` | ✅ | 0.441 s |
| `passado` | ✅ | 0.386 s |
| `capacidade` | ✅ | 0.410 s |
| `alteracaoExigeNovaAprovacao` | ✅ | 0.503 s |
| `alteracaoVersaoAntiga` | ✅ | 0.456 s |
| `semAuditoriaParcial` | ✅ | 0.481 s |
| `conflitoSala` | ✅ | 0.467 s |
| `conflitoRegistrado` | ✅ | 0.437 s |
| `aprovacaoAutomatica` | ✅ | 0.450 s |
| `canceladaLibera` | ✅ | 0.530 s |
| `bloqueioInformaAfetadas` | ✅ | 0.462 s |
| `cicloCompletoAuditado` | ✅ | 0.550 s |
| `movimentacao` | ✅ | 0.557 s |
| `aprovarRecursoEmManutencao` | ✅ | 0.472 s |
| `adjacentes` | ✅ | 0.498 s |
| `naoCompareceu` | ✅ | 0.484 s |
| `naoIniciadaApaga` | ✅ | 0.455 s |
| `auditoriaPorTransicao` | ✅ | 0.434 s |

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
