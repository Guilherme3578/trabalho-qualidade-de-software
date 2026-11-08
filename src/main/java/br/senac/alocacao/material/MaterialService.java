package br.senac.alocacao.material;

import br.senac.alocacao.auditoria.AcaoAuditoria;
import br.senac.alocacao.auditoria.AuditoriaService;
import br.senac.alocacao.comum.Filtros;
import br.senac.alocacao.comum.NegocioException;
import br.senac.alocacao.reserva.DisponibilidadeService;
import br.senac.alocacao.reserva.regras.Intervalo;
import br.senac.alocacao.usuario.Usuario;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MaterialService {

    private static final String ENTIDADE = "Material";

    private final MaterialRepository materiais;
    private final MongoTemplate mongo;
    private final DisponibilidadeService disponibilidade;
    private final AuditoriaService auditoria;

    public MaterialService(MaterialRepository materiais, MongoTemplate mongo, DisponibilidadeService disponibilidade,
                           AuditoriaService auditoria) {
        this.materiais = materiais;
        this.mongo = mongo;
        this.disponibilidade = disponibilidade;
        this.auditoria = auditoria;
    }

    /**
     * Pesquisa por categoria, localização, nome e disponibilidade. Com intervalo, devolve a
     * quantidade livre e filtra os que têm ao menos "quantidadeMinima" unidades livres.
     */
    public List<MaterialResponse> pesquisar(String categoria, String localizacao, String nome,
                                            LocalDateTime inicio, LocalDateTime fim, Integer quantidadeMinima) {
        Optional<Intervalo> intervalo = Filtros.intervaloOpcional(inicio, fim);
        int minimo = quantidadeMinima == null ? 1 : quantidadeMinima;
        Criteria c = Criteria.where("ativo").is(true);
        if (Filtros.preenchido(categoria)) {
            c = c.and("categoria").regex(Filtros.igual(categoria));
        }
        if (Filtros.preenchido(localizacao)) {
            c = c.and("localizacao").regex(Filtros.contem(localizacao));
        }
        if (Filtros.preenchido(nome)) {
            c = c.and("nome").regex(Filtros.contem(nome));
        }
        return mongo.find(Query.query(c).with(Sort.by("nome")), Material.class).stream()
                .map(m -> MaterialResponse.de(m,
                        intervalo.map(i -> disponibilidade.quantidadeDisponivel(m, i, null)).orElse(null)))
                .filter(r -> r.quantidadeDisponivel() == null || r.quantidadeDisponivel() >= minimo)
                .toList();
    }

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
        Material m = buscar(id);
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

    private Material buscar(String id) {
        return materiais.findById(id).orElseThrow(() -> NegocioException.naoEncontrado("Material", id));
    }

    private static String descrever(Material m) {
        return m.getNome() + " | " + m.getCategoria() + " | qtd=" + m.getQuantidadeTotal() + " | restrito="
                + m.isRestrito();
    }
}
