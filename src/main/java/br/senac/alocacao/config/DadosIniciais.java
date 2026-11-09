package br.senac.alocacao.config;

import br.senac.alocacao.material.Material;
import br.senac.alocacao.material.MaterialRepository;
import br.senac.alocacao.professor.Professor;
import br.senac.alocacao.professor.ProfessorRepository;
import br.senac.alocacao.sala.Sala;
import br.senac.alocacao.sala.SalaRepository;
import br.senac.alocacao.sala.TipoSala;
import br.senac.alocacao.usuario.Perfil;
import br.senac.alocacao.usuario.Usuario;
import br.senac.alocacao.usuario.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Set;

@Component
@Profile("dev")
public class DadosIniciais implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DadosIniciais.class);
    static final String SENHA_DEMO = "Senha123";

    private final UsuarioRepository usuarios;
    private final ProfessorRepository professores;
    private final SalaRepository salas;
    private final MaterialRepository materiais;
    private final PasswordEncoder encoder;
    private final Clock clock;

    public DadosIniciais(UsuarioRepository usuarios, ProfessorRepository professores, SalaRepository salas,
                         MaterialRepository materiais, PasswordEncoder encoder, Clock clock) {
        this.usuarios = usuarios;
        this.professores = professores;
        this.salas = salas;
        this.materiais = materiais;
        this.encoder = encoder;
        this.clock = clock;
    }

    @Override
    public void run(String... args) {
        if (usuarios.count() > 0) {
            return;
        }
        Professor ana = professores.save(new Professor("Ana Souza", "ana.souza@senac.br", "Computação",
                Set.of("Qualidade de Software", "Engenharia de Software")));
        professores.save(new Professor("Bruno Lima", "bruno.lima@senac.br", "Computação",
                Set.of("Banco de Dados", "Programação Orientada a Objetos")));
        professores.save(new Professor("Carla Mendes", "carla.mendes@senac.br", "Design",
                Set.of("Design de Interfaces")));

        salas.save(new Sala("A101", "Sala de aula A101", TipoSala.SALA_AULA, 40, "Bloco A - 1º andar", false));
        salas.save(new Sala("A102", "Sala de aula A102", TipoSala.SALA_AULA, 30, "Bloco A - 1º andar", false));
        salas.save(new Sala("LAB1", "Laboratório de Informática 1", TipoSala.LABORATORIO, 25, "Bloco B - térreo", false));
        salas.save(new Sala("LAB-RED", "Laboratório de Redes", TipoSala.LABORATORIO, 20, "Bloco B - 2º andar", true));
        salas.save(new Sala("AUD", "Auditório Principal", TipoSala.AUDITORIO, 200, "Bloco C", true));

        materiais.save(new Material("Projetor multimídia", "Audiovisual", null, 5, "Almoxarifado A", false));
        materiais.save(new Material("Notebook", "Informática", null, 20, "Almoxarifado B", false));
        materiais.save(new Material("Kit de robótica", "Laboratório", null, 4, "Almoxarifado B", true));

        LocalDateTime agora = LocalDateTime.now(clock);
        String hash = encoder.encode(SENHA_DEMO);
        usuarios.save(new Usuario("Administrador", "admin@senac.br", hash, Perfil.ADMIN, null, agora));
        usuarios.save(new Usuario("Rita Responsável", "responsavel@senac.br", hash, Perfil.RESPONSAVEL, null, agora));
        usuarios.save(new Usuario("Carlos Coordenador", "coordenador@senac.br", hash, Perfil.COORDENADOR, null, agora));
        usuarios.save(new Usuario("Ana Souza", "ana.souza@senac.br", hash, Perfil.PROFESSOR, ana.getId(), agora));

        log.info("Dados de demonstração criados. Usuários: admin@, responsavel@, coordenador@, ana.souza@senac.br "
                + "(senha {})", SENHA_DEMO);
    }
}
