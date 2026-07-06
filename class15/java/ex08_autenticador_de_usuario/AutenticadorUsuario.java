package tdd20ex.ex08_autenticador_de_usuario;

import java.util.Optional;

public class AutenticadorUsuario {
    private RepositorioUsuario repositorio;
    private CriptografadorSenha criptografador;

    public AutenticadorUsuario(RepositorioUsuario repositorio, CriptografadorSenha criptografador) {
        this.repositorio = repositorio;
        this.criptografador = criptografador;
    }

    public String autenticar(String email, String senha) {
        Optional<Usuario> usuario = repositorio.buscarPorEmail(email);

        if (usuario.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado");
        }

        if (!criptografador.verificar(senha, usuario.get().getSenhaHash())) {
            throw new RuntimeException("Senha incorreta");
        }

        return "TOKEN_" + email + "_" + System.currentTimeMillis();
    }
}
