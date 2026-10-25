package br.edu.carona;

import java.io.IOException;
import java.util.List;

/** Ponto de entrada: monta as peças, cria os usuários fictícios de demonstração e sobe o servidor. */
public final class App {
    private App() {}

    /** Usuários fictícios da demonstração (nenhum dado real): matrícula, nome, senha. */
    public static final List<String[]> USUARIOS_DEMO = List.of(
        new String[] {"2026001", "Ana Motorista", "ana123"},
        new String[] {"2026002", "Bruno Passageiro", "bruno123"},
        new String[] {"2026003", "Carla Passageira", "carla123"});

    public static Api montar(String caminhoDb, boolean modoTeste, RelogioAjustavel relogio) {
        RepositorioSqlite repo = new RepositorioSqlite(caminhoDb);
        for (String[] u : USUARIOS_DEMO) {
            if (repo.usuarioPorMatricula(u[0]).isEmpty()) {
                repo.criarUsuario(u[0], u[1], Senhas.hash(u[2]));
            }
        }
        return new Api(new ServicoCarona(repo, relogio), relogio, modoTeste);
    }

    public static void main(String[] args) throws IOException {
        String db = System.getenv().getOrDefault("CARONA_DB", "carona.db");
        boolean modoTeste = "1".equals(System.getenv("CARONA_MODO_TESTE"));
        int porta = Integer.parseInt(System.getenv().getOrDefault("PORT", "5000"));
        int escolhida = montar(db, modoTeste, new RelogioAjustavel()).iniciar(porta);
        System.out.println("CaronaCampus (Java) em http://0.0.0.0:" + escolhida);
    }
}
