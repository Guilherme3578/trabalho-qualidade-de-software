package br.senac.alocacao.usuario;

import br.senac.alocacao.auditoria.AcaoAuditoria;
import br.senac.alocacao.auditoria.AuditoriaService;
import br.senac.alocacao.comum.CodigoErro;
import br.senac.alocacao.comum.NegocioException;
import br.senac.alocacao.professor.ProfessorRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioService {

    private static final String ENTIDADE = "Usuario";

    private final UsuarioRepository usuarios;
    private final ProfessorRepository professores;
    private final PasswordEncoder encoder;
    private final AuditoriaService auditoria;
    private final Clock clock;

    public UsuarioService(UsuarioRepository usuarios, ProfessorRepository professores, PasswordEncoder encoder,
                          AuditoriaService auditoria, Clock clock) {
        this.usuarios = usuarios;
        this.professores = professores;
        this.encoder = encoder;
        this.auditoria = auditoria;
        this.clock = clock;
    }

    public List<UsuarioResponse> listar() {
        return usuarios.findAll(Sort.by("nome")).stream().map(UsuarioResponse::de).toList();
    }

    @Transactional
    public UsuarioResponse criar(UsuarioRequest req, Usuario ator) {
        String professorId = null;
        if (req.perfil() == Perfil.PROFESSOR) {
            if (req.professorId() == null || !professores.existsById(req.professorId())) {
                throw NegocioException.regra(CodigoErro.VALIDACAO,
                        "Usuários com perfil PROFESSOR devem estar vinculados a um professor cadastrado.");
            }
            professorId = req.professorId();
        }
        Usuario u = usuarios.save(new Usuario(req.nome(), req.email(), encoder.encode(req.senha()), req.perfil(),
                professorId, LocalDateTime.now(clock)));
        auditoria.registrar(ENTIDADE, u.getId(), AcaoAuditoria.CRIACAO, ator, null, u.getPerfil().name(),
                u.getEmail());
        return UsuarioResponse.de(u);
    }

    @Transactional
    public UsuarioResponse definirAtivo(String id, boolean ativo, Usuario ator) {
        if (id.equals(ator.getId()) && !ativo) {
            throw NegocioException.regra(CodigoErro.VALIDACAO, "Você não pode desativar o próprio usuário.");
        }
        Usuario u = usuarios.findById(id).orElseThrow(() -> NegocioException.naoEncontrado("Usuário", id));
        u.setAtivo(ativo);
        usuarios.save(u);
        auditoria.registrar(ENTIDADE, id, ativo ? AcaoAuditoria.ATIVACAO : AcaoAuditoria.DESATIVACAO, ator,
                null, null, u.getEmail());
        return UsuarioResponse.de(u);
    }
}
