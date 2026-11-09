# language: pt

@RF09
Funcionalidade: Retirada e devolução de materiais
  Como responsável pelo almoxarifado
  Quero registrar a retirada e a devolução dos materiais de cada reserva
  Para saber onde está cada equipamento e impedir que uma reserva seja concluída com pendências

  Contexto:
    Dado que existe o material "Projetor" com 3 unidades
    E existe uma reserva em uso com 2 unidades de "Projetor"

  Cenário: Retirada dentro da quantidade reservada
    Quando o responsável registra a retirada de 2 unidades de "Projetor"
    Então a operação é aceita
    E constam 2 unidades retiradas e 0 devolvidas de "Projetor"

  Cenário: Retirada acima da quantidade reservada é recusada
    Quando o responsável registra a retirada de 3 unidades de "Projetor"
    Então a operação é recusada com o código "MOVIMENTACAO_INVALIDA"
    E a mensagem contém "Retirada acima do reservado"

  Cenário: Devolução maior que a retirada é recusada
    Dado que o responsável já retirou 1 unidades de "Projetor"
    Quando o responsável registra a devolução de 2 unidades de "Projetor"
    Então a operação é recusada com o código "MOVIMENTACAO_INVALIDA"

  Cenário: Não é possível concluir a reserva com materiais pendentes
    Dado que o responsável já retirou 2 unidades de "Projetor"
    Quando o solicitante conclui a reserva
    Então a operação é recusada com o código "MATERIAIS_PENDENTES"

  Cenário: Conclusão após a devolução completa
    Dado que o responsável já retirou 2 unidades de "Projetor"
    E que o responsável já devolveu 2 unidades de "Projetor"
    Quando o solicitante conclui a reserva
    Então a reserva fica com status "CONCLUIDA"
    E o histórico da reserva registra "RETIRADA_MATERIAL" e "DEVOLUCAO_MATERIAL"

  Esquema do Cenário: Somente o responsável registra movimentações
    Quando um usuário com perfil "<perfil>" registra a retirada de 1 unidades de "Projetor"
    Então a resposta HTTP é <status>

    Exemplos:
      | perfil      | status |
      | PROFESSOR   | 403    |
      | COORDENADOR | 403    |
      | ADMIN       | 403    |
      | RESPONSAVEL | 200    |
