package br.senac.alocacao.usuario;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByPerfilAndAtivoTrue(Perfil perfil);
}
