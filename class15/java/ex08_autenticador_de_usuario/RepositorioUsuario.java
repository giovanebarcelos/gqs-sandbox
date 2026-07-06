package tdd20ex.ex08_autenticador_de_usuario;

import java.util.Optional;

interface RepositorioUsuario {
    Optional<Usuario> buscarPorEmail(String email);
}
