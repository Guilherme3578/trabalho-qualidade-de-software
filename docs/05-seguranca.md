# 5. Segurança

> 📝 **Rascunho para revisão.** Referências: OWASP Top 10 (2021) e OWASP API Security Top 10 (2023).

| Ameaça | Controle implementado | Evidência (teste) |
|---|---|---|
| Acesso sem autenticação (API2) | Toda rota `/api/**` exige JWT, exceto login e documentação | `SegurancaApiIT.semToken` |
| Token adulterado ou forjado | Assinatura HS256 validada com segredo de ≥ 32 caracteres, vindo de variável de ambiente | `tokenAdulterado`, `tokenForjado` |
| Escalada de privilégio (API5) | Autorização em **2 níveis**: por rota (`SecurityConfig`, antes de qualquer validação) e por método (`@PreAuthorize`) | `autorizacaoPorPerfil` (11 casos) |
| Acesso a objeto de outro usuário (API1/BOLA) | Só o dono altera, cancela ou apaga a própria reserva; professor só reserva na própria agenda | `cancelarReservaAlheia`, `professorSoReservaParaSi` |
| Força bruta no login (API4) | 5 falhas em 15 min bloqueiam o e-mail (HTTP 429) | `LimitadorTentativasLoginTest`, `AutenticacaoApiIT.forcaBruta` |
| Enumeração de usuários | Mesma mensagem para e-mail inexistente, senha errada ou usuário inativo; BCrypt executado sempre | `mensagemGenerica` |
| Senhas expostas | Somente o hash BCrypt é armazenado; DTOs nunca expõem o hash; `toString` dos requests omite a senha | `semHash`, `loginValido` |
| Injeção de SQL (A03) | Consultas JPQL com parâmetros nomeados (nunca concatenação); curingas `%` e `_` do LIKE escapados em `Filtros.contem` | `injecaoNoFiltro`, `pesquisaComCuringasLiterais`, `UtilitariosTest.filtroEscapado` |
| Atribuição em massa (API3) | DTOs (`record`) com apenas os campos permitidos; status e id nunca vêm do cliente | `atribuicaoEmMassa` |
| Entrada inválida | Bean Validation com mensagens em português; tamanhos máximos em todos os textos e listas | `validacaoCampos`, `jsonMalformado`, `parametroInvalido` |
| Vazamento de informação em erros (A05) | `ApiExceptionHandler` + `server.error.include-stacktrace=never`; Problem Details com `codigo` e `correlationId` | `erroSeguro`, `rotaInexistente` |
| XSS / clickjacking | CSP `default-src 'self'`, `X-Frame-Options: DENY`, `nosniff`; front-end escapa todo dado antes de inserir no HTML | `cabecalhos` |
| Injeção em logs | `X-Correlation-Id` do cliente só é aceito se tiver formato seguro | — |
| Repúdio | Auditoria com autor, data, estado anterior/novo e correlationId de cada mudança | `ReservaServiceIT.auditoria*` |
| Container com privilégios | Imagem Docker roda com usuário sem privilégios (`USER app`) | `Dockerfile` |

## Pontos de melhoria (fora do escopo)

- Limite de tentativas em memória: em várias instâncias, usar armazenamento compartilhado (ex.: Redis).
- Token armazenado em `sessionStorage`: em produção, avaliar cookie `HttpOnly` + proteção CSRF.
- Rotação do segredo JWT e uso de chaves assimétricas (RS256).
