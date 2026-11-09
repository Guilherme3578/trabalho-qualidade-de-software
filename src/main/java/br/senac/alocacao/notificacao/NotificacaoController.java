package br.senac.alocacao.notificacao;

import br.senac.alocacao.usuario.UsuarioAtual;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notificacoes")
@Tag(name = "Notificações", description = "Notificações do usuário autenticado")
public class NotificacaoController {
    private final NotificacaoService servico;
    private final UsuarioAtual usuarioAtual;

    public NotificacaoController(NotificacaoService servico, UsuarioAtual usuarioAtual) {
        this.servico = servico;
        this.usuarioAtual = usuarioAtual;
    }

    @GetMapping
    public List<Notificacao> minhas() {
        return servico.minhas(usuarioAtual.obter().getId());
    }

    @PatchMapping("/{id}/lida")
    public Notificacao marcarLida(@PathVariable String id) {
        return servico.marcarLida(id, usuarioAtual.obter().getId());
    }
}
