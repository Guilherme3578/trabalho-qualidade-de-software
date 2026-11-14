package br.senac.alocacao.material;

import br.senac.alocacao.auditoria.AcaoAuditoria;
import br.senac.alocacao.auditoria.AuditoriaService;
import br.senac.alocacao.bloqueio.TipoRecurso;
import br.senac.alocacao.comum.CodigoErro;
import br.senac.alocacao.comum.Filtros;
import br.senac.alocacao.comum.NegocioException;
import br.senac.alocacao.reserva.DisponibilidadeService;
import br.senac.alocacao.reserva.Reserva;
import br.senac.alocacao.reserva.regras.Intervalo;
import br.senac.alocacao.usuario.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MaterialService {
    private static final String ENTIDADE = "Material";

    private final MaterialRepository materiais;
    private final DisponibilidadeService disponibilidade;
    private final AuditoriaService auditoria;
    private final Clock clock;

    public MaterialService(MaterialRepository materiais, DisponibilidadeService disponibilidade,
                           AuditoriaService auditoria, Clock clock) {
        this.materiais = materiais;
        this.disponibilidade = disponibilidade;
        this.auditoria = auditoria;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<MaterialResponse> pesquisar(String categoria, String localizacao, String nome,
                                            LocalDateTime inicio, LocalDateTime fim, Integer quantidadeMinima) {
        Optional<Intervalo> intervalo = Filtros.intervaloOpcional(inicio, fim);
        int minimo = quantidadeMinima == null ? 1 : quantidadeMinima;
        return materiais.pesquisar(Filtros.igual(categoria), Filtros.contem(localizacao), Filtros.contem(nome))
                .stream()
                .map(m -> MaterialResponse.de(m,
                        intervalo.map(i -> disponibilidade.quantidadeDisponivel(m, i, null)).orElse(null)))
                .filter(r -> r.quantidadeDisponivel() == null || r.quantidadeDisponivel() >= minimo)
                .toList();
    }

    @Transactional(readOnly = true)
    public MaterialResponse obter(String id) {
        return MaterialResponse.de(buscar(id), null);
    }

    @Transactional
    public MaterialResponse criar(MaterialRequest req, Usuario ator) {
        Material m = materiais.save(new Material(req.nome(), req.categoria(), req.patrimonio(), req.quantidadeTotal(),
                req.localizacao(), req.restrito()));
        auditoria.registrar(ENTIDADE, m.getId(), AcaoAuditoria.CRIACAO, ator, null, null, descrever(m));
        return MaterialResponse.de(m, null);
    }

    @Transactional
    public MaterialResponse atualizar(String id, MaterialRequest req, Usuario ator) {
        Material m = materiais.travar(id).orElseThrow(() -> NegocioException.naoEncontrado("Material", id));
        garantirEstoqueParaReservasFuturas(m, req.quantidadeTotal());
        String antes = descrever(m);
        m.atualizar(req.nome(), req.categoria(), req.patrimonio(), req.quantidadeTotal(), req.localizacao(),
                req.restrito());
        m = materiais.save(m);
        auditoria.registrar(ENTIDADE, id, AcaoAuditoria.ALTERACAO, ator, antes, descrever(m), null);
        return MaterialResponse.de(m, null);
    }

    @Transactional
    public MaterialResponse definirAtivo(String id, boolean ativo, Usuario ator) {
        Material m = buscar(id);
        m.setAtivo(ativo);
        materiais.save(m);
        auditoria.registrar(ENTIDADE, id, ativo ? AcaoAuditoria.ATIVACAO : AcaoAuditoria.DESATIVACAO, ator,
                null, null, null);
        return MaterialResponse.de(m, null);
    }

    private void garantirEstoqueParaReservasFuturas(Material m, int novaQuantidade) {
        if (novaQuantidade >= m.getQuantidadeTotal()) {
            return;
        }
        int maiorPico = 0;
        List<String> afetadas = new ArrayList<>();
        for (Reserva r : disponibilidade.reservasFuturas(TipoRecurso.MATERIAL, m.getId(), LocalDateTime.now(clock))) {
            int pico = disponibilidade.picoDeUso(m.getId(), r.intervalo(), null);
            if (pico > novaQuantidade) {
                afetadas.add(r.getId());
                maiorPico = Math.max(maiorPico, pico);
            }
        }
        if (afetadas.isEmpty()) {
            return;
        }
        throw NegocioException.conflito(CodigoErro.RESERVAS_AFETADAS,
                "O estoque de " + m.getNome() + " não pode ser reduzido para " + novaQuantidade + ": há "
                        + afetadas.size() + " reserva(s) futura(s) que usam até " + maiorPico
                        + " unidades ao mesmo tempo. Remaneje ou cancele essas reservas antes.",
                Map.of("reservas", afetadas));
    }

    private Material buscar(String id) {
        return materiais.findById(id).orElseThrow(() -> NegocioException.naoEncontrado("Material", id));
    }

    private static String descrever(Material m) {
        return m.getNome() + " | " + m.getCategoria() + " | qtd=" + m.getQuantidadeTotal() + " | restrito="
                + m.isRestrito();
    }
}
