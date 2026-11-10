# Matriz de rastreabilidade

> Arquivo **gerado automaticamente** por `scripts/rastreabilidade.py` — não edite à mão.
> Cada teste cita os requisitos que verifica no próprio nome (ex.: `[RF05][RN02]`).

Última execução encontrada em `target/`: **262 testes, 0 falha(s).**

## Resumo

| Requisito | Descrição | Qtde. de testes | Níveis |
|---|---|---|---|
| RF01 | Autenticação e autorização por perfil | 5 | API caixa-preta |
| RF02 | Cadastro e consulta de salas, professores e materiais | 4 | API caixa-preta |
| RF03 | Pesquisa por tipo, capacidade, localização, competência e disponibilidade | 9 | API caixa-preta, Unitário |
| RF04 | Criação, alteração e cancelamento de reservas | 7 | API caixa-preta, End-to-end, Integração |
| RF05 | Detecção de sobreposição de horários, inclusive na agenda do professor | 6 | API caixa-preta, End-to-end, Integração |
| RF06 | Proteção contra dupla reserva em solicitações simultâneas | 11 | Concorrência, Unitário |
| RF07 | Aprovação obrigatória para recursos restritos | 13 | API caixa-preta, Integração, Unitário |
| RF08 | Bloqueio de recursos em manutenção | 5 | Concorrência, Integração |
| RF09 | Registro de retirada e devolução de materiais e equipamentos | 8 | API caixa-preta, BDD, Integração |
| RF10 | Histórico auditável de mudanças | 2 | API caixa-preta |
| RF11 | Notificação simulada / integração com API externa | 6 | Integração |
| RF12 | Relatório de utilização por recurso, carga horária alocada e conflitos evitados | 5 | API caixa-preta, Integração |
| RF13 | Interface responsiva e mensagens de erro compreensíveis | 7 | API caixa-preta, End-to-end |
| RF14 | Documentação de API ou dos fluxos públicos | 3 | API caixa-preta |
| RN01 | O término deve ser posterior ao início | 4 | API caixa-preta, Unitário |
| RN02 | Reservas do mesmo recurso não podem se sobrepor — sala, material ou professor | 15 | Integração, Unitário |
| RN03 | Duas solicitações simultâneas devem produzir uma única reserva aceita | 6 | Concorrência |
| RN04 | Recursos em manutenção não podem ser reservados | 4 | Concorrência, Integração |
| RN05 | Ciclo de estados SOLICITADA → APROVADA → EM_USO → CONCLUIDA; alternativos REJEITADA, CANCELADA, NAO_COMPARECEU | 8 | Integração, Unitário |
| RN06 | Somente responsáveis aprovam recursos restritos | 5 | API caixa-preta, Integração, Unitário |
| RN07 | Reservas iniciadas não podem ser apagadas | 5 | API caixa-preta, Integração, Unitário |
| RN08 | Toda mudança de estado deve gerar auditoria | 3 | API caixa-preta, Integração |
| RN09 | Quantidade de alunos não pode exceder a capacidade da sala | 1 | Integração |
| RN10 | Reservas no futuro, no mesmo dia, entre 07:00 e 23:00, com duração entre 30 min e 6 h | 4 | Integração, Unitário |
| RNF01 | Desempenho sob carga | 1 | JMeter |
| RNF02 | Autorização e proteção de acesso | 20 | API caixa-preta, Unitário |
| RNF03 | Validação de entrada | 11 | API caixa-preta, Unitário |
| RNF04 | Tratamento seguro de erros | 5 | API caixa-preta, Unitário |
| RNF05 | Cobertura de testes | 1 | JaCoCo |
| RNF06 | Qualidade estática | 1 | SonarCloud |
| RNF07 | Integridade dos dados no banco relacional | 7 | Integração (banco) |

## Detalhamento

### RF01 — Autenticação e autorização por perfil

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| API caixa-preta | `AutenticacaoApiIT` | API caixa-preta: autenticação |
| API caixa-preta | `AutenticacaoApiIT` | Credenciais válidas devolvem token Bearer e perfil, sem o hash da senha |
| API caixa-preta | `AutenticacaoApiIT` | Senha errada e e-mail inexistente devolvem a mesma mensagem genérica |
| API caixa-preta | `AutenticacaoApiIT` | Usuário desativado não consegue entrar |
| API caixa-preta | `AutenticacaoApiIT` | /api/auth/eu devolve o usuário do token |

### RF02 — Cadastro e consulta de salas, professores e materiais

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| API caixa-preta | `CadastroPesquisaApiIT` | API caixa-preta: cadastros e pesquisa |
| API caixa-preta | `CadastroPesquisaApiIT` | ADMIN cadastra sala, professor e material; demais perfis consultam |
| API caixa-preta | `CadastroPesquisaApiIT` | Campos booleanos opcionais omitidos assumem false em vez de erro de formato |
| API caixa-preta | `CadastroPesquisaApiIT` | Código de sala duplicado devolve 409 compreensível |

### RF03 — Pesquisa por tipo, capacidade, localização, competência e disponibilidade

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| API caixa-preta | `CadastroPesquisaApiIT` | API caixa-preta: cadastros e pesquisa |
| API caixa-preta | `CadastroPesquisaApiIT` | Pesquisa de salas por tipo, capacidade e localização |
| API caixa-preta | `CadastroPesquisaApiIT` | Pesquisa por disponibilidade exclui salas ocupadas e em manutenção |
| API caixa-preta | `CadastroPesquisaApiIT` | Pesquisa de professores por competência e disponibilidade |
| API caixa-preta | `CadastroPesquisaApiIT` | Pesquisa de materiais informa a quantidade disponível no período |
| Unitário | `UtilitariosTest` | filtro vazio \ |
| Unitário | `UtilitariosTest` | Filtros vazios não restringem a pesquisa |
| Unitário | `UtilitariosTest` | Igualdade remove espaços nas pontas (comparação sem maiúsculas é feita na consulta) |
| Unitário | `UtilitariosTest` | Disponibilidade exige início e término juntos |

### RF04 — Criação, alteração e cancelamento de reservas

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| API caixa-preta | `ReservaApiIT` | API caixa-preta: reservas |
| API caixa-preta | `ReservaApiIT` | Criar reserva devolve 201 com os dados e status APROVADA |
| API caixa-preta | `ReservaApiIT` | Alterar e cancelar a própria reserva |
| API caixa-preta | `ReservaApiIT` | minhas=true filtra as reservas do usuário logado |
| End-to-end | `FluxoReservaE2EIT` | Professor entra, cria reserva e vê confirmação |
| Integração | `ReservaServiceIT` | Alteração revalida conflitos ignorando a própria reserva |
| Integração | `ReservaServiceIT` | Alteração com versão desatualizada é recusada |

### RF05 — Detecção de sobreposição de horários, inclusive na agenda do professor

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| API caixa-preta | `ReservaApiIT` | API caixa-preta: reservas |
| API caixa-preta | `ReservaApiIT` | Conflito devolve 409 com mensagem compreensível e dados do conflito |
| End-to-end | `FluxoReservaE2EIT` | Segunda reserva no mesmo horário mostra mensagem de conflito compreensível |
| Integração | `ReservaServiceIT` | Mesma sala em horário sobreposto é recusada com CONFLITO_SALA |
| Integração | `ReservaServiceIT` | Mesmo professor em salas diferentes no mesmo horário é recusado |
| Integração | `ReservaServiceIT` | Material: soma no pico não pode passar do estoque |

### RF06 — Proteção contra dupla reserva em solicitações simultâneas

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| Concorrência | `ConcorrenciaReservaIT` | Concorrência: solicitações simultâneas |
| Concorrência | `ConcorrenciaReservaIT` | rodada {currentRepetition}/{totalRepetitions} |
| Concorrência | `ConcorrenciaReservaIT` | 20 pedidos simultâneos para a mesma sala → 1 aceito, 19 recusados |
| Concorrência | `ConcorrenciaReservaIT` | Mesmo professor em 20 salas diferentes ao mesmo tempo → 1 aceito |
| Concorrência | `ConcorrenciaReservaIT` | Estoque de 3 projetores e 20 pedidos simultâneos de 1 → exatamente 3 aceitos |
| Concorrência | `ConcorrenciaReservaIT` | Via HTTP: 10 requisições simultâneas à API → um 201 e nove 409 |
| Unitário | `ProtecaoConcorrenciaTest` | Repetição de transações em deadlock ou espera de trava |
| Unitário | `ProtecaoConcorrenciaTest` | Deadlock do MySQL é repetido até a operação conseguir |
| Unitário | `ProtecaoConcorrenciaTest` | Falhas de trava são transitórias; violação de integridade não é |
| Unitário | `ProtecaoConcorrenciaTest` | Erros de negócio não são repetidos |
| Unitário | `ProtecaoConcorrenciaTest` | Após o limite de tentativas, devolve erro CONCORRENCIA compreensível |

### RF07 — Aprovação obrigatória para recursos restritos

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| API caixa-preta | `ReservaApiIT` | Fluxo de aprovação de sala restrita pelo responsável |
| Integração | `ReservaServiceIT` | Recurso comum e docente habilitado: aprovação automática |
| Integração | `ReservaServiceIT` | Sala restrita fica SOLICITADA até o responsável aprovar |
| Integração | `ReservaServiceIT` | Professor sem competência fica SOLICITADA (validação docente) |
| Integração | `ReservaServiceIT` | Alterar para sala restrita devolve a reserva para SOLICITADA |
| Unitário | `AvaliadorAprovacaoTest` | Avaliação da necessidade de aprovação |
| Unitário | `AvaliadorAprovacaoTest` | Sem recurso restrito e docente habilitado → aprovação automática |
| Unitário | `AvaliadorAprovacaoTest` | Sala restrita exige aprovação |
| Unitário | `AvaliadorAprovacaoTest` | Material restrito exige aprovação |
| Unitário | `AvaliadorAprovacaoTest` | Professor sem a competência da disciplina exige validação do responsável |
| Unitário | `AvaliadorAprovacaoTest` | \ |
| Unitário | `AvaliadorAprovacaoTest` | Comparação ignora maiúsculas e espaços (refatoração do ciclo 3) |
| Unitário | `AvaliadorAprovacaoTest` | Todos os motivos são acumulados |

### RF08 — Bloqueio de recursos em manutenção

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| Concorrência | `ConcorrenciaReservaIT` | Bloqueio e reserva simultâneos: a reserva é recusada ou aparece como afetada |
| Integração | `ReservaServiceIT` | Sala em manutenção não pode ser reservada |
| Integração | `ReservaServiceIT` | Material em manutenção não pode ser reservado |
| Integração | `ReservaServiceIT` | Bloqueio informa reservas já existentes afetadas |
| Integração | `ReservaServiceIT` | Reserva pendente não pode ser aprovada se o recurso entrou em manutenção |

### RF09 — Registro de retirada e devolução de materiais e equipamentos

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| API caixa-preta | `ReservaApiIT` | Retirada e devolução de material pelo responsável |
| Integração | `ReservaServiceIT` | Retirada acima do reservado e conclusão com pendência são recusadas |
| BDD | `retirada_devolucao.feature` | Retirada dentro da quantidade reservada |
| BDD | `retirada_devolucao.feature` | Retirada acima da quantidade reservada é recusada |
| BDD | `retirada_devolucao.feature` | Devolução maior que a retirada é recusada |
| BDD | `retirada_devolucao.feature` | Não é possível concluir a reserva com materiais pendentes |
| BDD | `retirada_devolucao.feature` | Conclusão após a devolução completa |
| BDD | `retirada_devolucao.feature` | Somente o responsável registra movimentações |

### RF10 — Histórico auditável de mudanças

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| API caixa-preta | `CadastroPesquisaApiIT` | Alteração de cadastro fica registrada na auditoria com antes/depois |
| API caixa-preta | `ReservaApiIT` | Histórico da reserva lista cada mudança de estado |

### RF11 — Notificação simulada / integração com API externa

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| Integração | `NotificacaoWireMockIT` | Integração com API externa de notificação (WireMock) |
| Integração | `NotificacaoWireMockIT` | Reserva pendente notifica o responsável com o contrato JSON esperado |
| Integração | `NotificacaoWireMockIT` | Aprovação notifica o solicitante |
| Integração | `NotificacaoWireMockIT` | API externa com erro 500: reserva é mantida e a falha fica registrada |
| Integração | `NotificacaoWireMockIT` | API externa lenta (acima do timeout): reserva é mantida e a falha registrada |
| Integração | `NotificacaoWireMockIT` | Operação recusada não gera notificação (evento só após commit) |

### RF12 — Relatório de utilização por recurso, carga horária alocada e conflitos evitados

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| API caixa-preta | `RelatorioApiIT` | API caixa-preta: relatórios |
| API caixa-preta | `RelatorioApiIT` | Utilização, carga horária e conflitos evitados refletem as reservas |
| API caixa-preta | `RelatorioApiIT` | Conflitos evitados são contabilizados por tipo |
| API caixa-preta | `RelatorioApiIT` | Período inválido devolve mensagem compreensível |
| Integração | `ReservaServiceIT` | Conflitos recusados são registrados para o relatório |

### RF13 — Interface responsiva e mensagens de erro compreensíveis

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| API caixa-preta | `ReservaApiIT` | Conflito devolve 409 com mensagem compreensível e dados do conflito |
| API caixa-preta | `SegurancaApiIT` | Campos inválidos devolvem 400 com a lista de mensagens em português |
| End-to-end | `FluxoReservaE2EIT` | End-to-end: fluxo de reserva pela interface web |
| End-to-end | `FluxoReservaE2EIT` | Professor entra, cria reserva e vê confirmação |
| End-to-end | `FluxoReservaE2EIT` | Segunda reserva no mesmo horário mostra mensagem de conflito compreensível |
| End-to-end | `FluxoReservaE2EIT` | Credenciais inválidas exibem mensagem de erro |
| End-to-end | `FluxoReservaE2EIT` | Interface responsiva: sem rolagem horizontal em tela de celular (390px) |

### RF14 — Documentação de API ou dos fluxos públicos

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| API caixa-preta | `DocumentacaoApiIT` | Documentação pública da API |
| API caixa-preta | `DocumentacaoApiIT` | Especificação OpenAPI publicada com os principais fluxos e o esquema de segurança |
| API caixa-preta | `DocumentacaoApiIT` | Swagger UI acessível sem autenticação |

### RN01 — O término deve ser posterior ao início

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| API caixa-preta | `ReservaApiIT` | Término antes do início devolve 422 com mensagem clara |
| Unitário | `IntervaloTest` | início {0} e término {1} → recusado |
| Unitário | `IntervaloTest` | O término deve ser posterior ao início |
| Unitário | `IntervaloTest` | Início ou término ausente é recusado |

### RN02 — Reservas do mesmo recurso não podem se sobrepor — sala, material ou professor

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| Integração | `ReservaServiceIT` | Mesma sala em horário sobreposto é recusada com CONFLITO_SALA |
| Integração | `ReservaServiceIT` | Mesmo professor em salas diferentes no mesmo horário é recusado |
| Integração | `ReservaServiceIT` | Reservas adjacentes (fim = início) são permitidas |
| Integração | `ReservaServiceIT` | Material: soma no pico não pode passar do estoque |
| Integração | `ReservaServiceIT` | Reserva cancelada libera o horário |
| Unitário | `StatusReservaTest` | {0} ocupa agenda |
| Unitário | `StatusReservaTest` | Estados que bloqueiam a agenda do recurso |
| Unitário | `StatusReservaTest` | {0} libera agenda |
| Unitário | `StatusReservaTest` | Estados finais liberam a agenda |
| Unitário | `CalculadoraOcupacaoTest` | Pico de uso simultâneo de materiais |
| Unitário | `CalculadoraOcupacaoTest` | {0} → pico {2} |
| Unitário | `CalculadoraOcupacaoTest` | Cálculo do pico por varredura de eventos |
| Unitário | `CalculadoraOcupacaoTest` | Reserva que começa antes da janela é recortada |
| Unitário | `IntervaloTest` | {0}-{1} x {2}-{3} → sobrepõe={4} |
| Unitário | `IntervaloTest` | Detecção de sobreposição entre intervalos |

### RN03 — Duas solicitações simultâneas devem produzir uma única reserva aceita

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| Concorrência | `ConcorrenciaReservaIT` | Concorrência: solicitações simultâneas |
| Concorrência | `ConcorrenciaReservaIT` | rodada {currentRepetition}/{totalRepetitions} |
| Concorrência | `ConcorrenciaReservaIT` | 20 pedidos simultâneos para a mesma sala → 1 aceito, 19 recusados |
| Concorrência | `ConcorrenciaReservaIT` | Mesmo professor em 20 salas diferentes ao mesmo tempo → 1 aceito |
| Concorrência | `ConcorrenciaReservaIT` | Estoque de 3 projetores e 20 pedidos simultâneos de 1 → exatamente 3 aceitos |
| Concorrência | `ConcorrenciaReservaIT` | Via HTTP: 10 requisições simultâneas à API → um 201 e nove 409 |

### RN04 — Recursos em manutenção não podem ser reservados

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| Concorrência | `ConcorrenciaReservaIT` | Bloqueio e reserva simultâneos: a reserva é recusada ou aparece como afetada |
| Integração | `ReservaServiceIT` | Sala em manutenção não pode ser reservada |
| Integração | `ReservaServiceIT` | Material em manutenção não pode ser reservado |
| Integração | `ReservaServiceIT` | Reserva pendente não pode ser aprovada se o recurso entrou em manutenção |

### RN05 — Ciclo de estados SOLICITADA → APROVADA → EM_USO → CONCLUIDA; alternativos REJEITADA, CANCELADA, NAO_COMPARECEU

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| Integração | `ReservaServiceIT` | Ciclo completo SOLICITADA → APROVADA → EM_USO → CONCLUIDA com auditoria de cada passo |
| Integração | `ReservaServiceIT` | Não comparecimento só após a tolerância de 15 minutos |
| Unitário | `StatusReservaTest` | Máquina de estados da reserva |
| Unitário | `StatusReservaTest` | {0} → {1} |
| Unitário | `StatusReservaTest` | Todas as 49 combinações de transição seguem a tabela |
| Unitário | `StatusReservaTest` | Fluxo principal SOLICITADA → APROVADA → EM_USO → CONCLUIDA |
| Unitário | `StatusReservaTest` | {0} → {1} é recusada |
| Unitário | `StatusReservaTest` | Transições inválidas lançam TRANSICAO_INVALIDA |

### RN06 — Somente responsáveis aprovam recursos restritos

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| API caixa-preta | `ReservaApiIT` | Fluxo de aprovação de sala restrita pelo responsável |
| API caixa-preta | `SegurancaApiIT` | Autorização por perfil |
| Integração | `ReservaServiceIT` | Sala restrita fica SOLICITADA até o responsável aprovar |
| Unitário | `AvaliadorAprovacaoTest` | Sala restrita exige aprovação |
| Unitário | `AvaliadorAprovacaoTest` | Material restrito exige aprovação |

### RN07 — Reservas iniciadas não podem ser apagadas

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| API caixa-preta | `ReservaApiIT` | DELETE de reserva iniciada devolve 409 |
| Integração | `ReservaServiceIT` | Reserva iniciada não pode ser apagada |
| Integração | `ReservaServiceIT` | Reserva não iniciada pode ser apagada e a exclusão é auditada |
| Unitário | `StatusReservaTest` | {0} iniciada={1} |
| Unitário | `StatusReservaTest` | Identificação de reservas iniciadas (não podem ser apagadas) |

### RN08 — Toda mudança de estado deve gerar auditoria

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| API caixa-preta | `ReservaApiIT` | Histórico da reserva lista cada mudança de estado |
| Integração | `ReservaServiceIT` | Toda mudança de estado gera exatamente um evento de auditoria |
| Integração | `ReservaServiceIT` | Operação recusada não deixa auditoria parcial (mesma transação) |

### RN09 — Quantidade de alunos não pode exceder a capacidade da sala

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| Integração | `ReservaServiceIT` | Quantidade de alunos acima da capacidade é recusada |

### RN10 — Reservas no futuro, no mesmo dia, entre 07:00 e 23:00, com duração entre 30 min e 6 h

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| Integração | `ReservaServiceIT` | Reserva no passado é recusada |
| Unitário | `PoliticaHorarioTest` | {0} até {1} → aceito |
| Unitário | `PoliticaHorarioTest` | Horários válidos são aceitos |
| Unitário | `PoliticaHorarioTest` | Validação devolve o intervalo normalizado |

### RNF01 — Desempenho sob carga

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| JMeter | `src/test/jmeter/carga-reservas.jmx` | 20 usuários simultâneos; build falha com erro > 1%; Duration Assertion de 1000 ms |

### RNF02 — Autorização e proteção de acesso

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| API caixa-preta | `AutenticacaoApiIT` | Após 5 falhas o login é bloqueado temporariamente (429) |
| API caixa-preta | `SegurancaApiIT` | API caixa-preta: segurança |
| API caixa-preta | `SegurancaApiIT` | {0} {1} sem token → 401 |
| API caixa-preta | `SegurancaApiIT` | Rotas protegidas exigem autenticação |
| API caixa-preta | `SegurancaApiIT` | Token adulterado é rejeitado |
| API caixa-preta | `SegurancaApiIT` | Usuário desativado perde o acesso imediatamente, mesmo com token ainda válido |
| API caixa-preta | `SegurancaApiIT` | Mudança de perfil vale imediatamente: o perfil antigo do token não é aceito |
| API caixa-preta | `SegurancaApiIT` | Token forjado com outra chave e perfil ADMIN é rejeitado |
| API caixa-preta | `SegurancaApiIT` | {0} não pode {1} {2} → 403 |
| API caixa-preta | `SegurancaApiIT` | Autorização por perfil |
| API caixa-preta | `SegurancaApiIT` | Professor não pode reservar na agenda de outro professor |
| API caixa-preta | `SegurancaApiIT` | Usuário não pode cancelar reserva de outro usuário |
| API caixa-preta | `SegurancaApiIT` | Cabeçalhos de segurança presentes nas respostas |
| API caixa-preta | `SegurancaApiIT` | Lista de usuários (ADMIN) nunca devolve o hash da senha |
| Unitário | `LimitadorTentativasLoginTest` | Proteção contra força bruta no login |
| Unitário | `LimitadorTentativasLoginTest` | Bloqueia após o número máximo de falhas |
| Unitário | `LimitadorTentativasLoginTest` | Tentativas expiradas são descartadas para a memória não crescer sem limite |
| Unitário | `LimitadorTentativasLoginTest` | Libera após a janela expirar |
| Unitário | `LimitadorTentativasLoginTest` | Login bem-sucedido zera o contador |
| Unitário | `LimitadorTentativasLoginTest` | Contadores são independentes por e-mail |

### RNF03 — Validação de entrada

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| API caixa-preta | `CadastroPesquisaApiIT` | Curingas e SQL na pesquisa são tratados como texto literal |
| API caixa-preta | `SegurancaApiIT` | API caixa-preta: segurança |
| API caixa-preta | `SegurancaApiIT` | Campos inválidos devolvem 400 com a lista de mensagens em português |
| API caixa-preta | `SegurancaApiIT` | corpo malformado: {0} |
| API caixa-preta | `SegurancaApiIT` | JSON malformado devolve 400 sem detalhes internos |
| API caixa-preta | `SegurancaApiIT` | Parâmetro de data em formato inválido devolve 400 legível |
| API caixa-preta | `SegurancaApiIT` | busca com \ |
| API caixa-preta | `SegurancaApiIT` | Injeção de SQL e curingas em filtros não retornam dados indevidos |
| API caixa-preta | `SegurancaApiIT` | Campos extras no corpo (ex.: status) são ignorados — sem atribuição em massa |
| Unitário | `UtilitariosTest` | entrada \ |
| Unitário | `UtilitariosTest` | Filtros de texto escapam curingas do LIKE (o valor segue como parâmetro, sem SQL injection) |

### RNF04 — Tratamento seguro de erros

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| API caixa-preta | `AutenticacaoApiIT` | Senha errada e e-mail inexistente devolvem a mesma mensagem genérica |
| API caixa-preta | `SegurancaApiIT` | API caixa-preta: segurança |
| API caixa-preta | `SegurancaApiIT` | Erros incluem correlationId e não expõem stack trace |
| API caixa-preta | `SegurancaApiIT` | Rota inexistente devolve 404 no formato padrão |
| Unitário | `UtilitariosTest` | JSON gerado escapa caracteres especiais |

### RNF05 — Cobertura de testes

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| JaCoCo | `pom.xml (jacoco:check)` | build falha com cobertura < 80% de linhas ou < 70% de ramos |

### RNF06 — Qualidade estática

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| SonarCloud | `.github/workflows/ci.yml` | análise estática e Quality Gate em cada pull request |

### RNF07 — Integridade dos dados no banco relacional

| Nível | Classe / arquivo | Caso de teste |
|---|---|---|
| Integração (banco) | `IntegridadeRelacionalIT` | Banco relacional: integridade garantida pelo MySQL |
| Integração (banco) | `IntegridadeRelacionalIT` | O esquema é criado pelas migrações versionadas do Flyway |
| Integração (banco) | `IntegridadeRelacionalIT` | Chave estrangeira impede reserva apontando para sala inexistente |
| Integração (banco) | `IntegridadeRelacionalIT` | Sala com reservas não pode ser apagada diretamente no banco |
| Integração (banco) | `IntegridadeRelacionalIT` | CHECK impede período invertido mesmo fora da API |
| Integração (banco) | `IntegridadeRelacionalIT` | CHECK de bloqueio exige exatamente o recurso do tipo informado |
| Integração (banco) | `IntegridadeRelacionalIT` | Apagar a reserva remove itens e motivos em cascata |

