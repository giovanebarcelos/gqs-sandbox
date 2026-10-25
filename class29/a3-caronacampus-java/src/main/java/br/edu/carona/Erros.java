package br.edu.carona;

/** Erros de regra de negócio; cada um carrega um código estável e o status HTTP correspondente. */
public final class Erros {
    private Erros() {}

    public abstract static class ErroDeDominio extends RuntimeException {
        private final String codigo;
        private final int status;

        protected ErroDeDominio(String codigo, int status, String mensagem) {
            super(mensagem);
            this.codigo = codigo;
            this.status = status;
        }

        public String codigo() { return codigo; }
        public int status() { return status; }
    }

    public static final class DadoInvalido extends ErroDeDominio {
        public DadoInvalido(String m) { super("dado_invalido", 400, m); }
    }

    public static final class NaoEncontrado extends ErroDeDominio {
        public NaoEncontrado(String m) { super("nao_encontrado", 404, m); }
    }

    public static final class SemPermissao extends ErroDeDominio {
        public SemPermissao(String m) { super("sem_permissao", 403, m); }
    }

    public static final class ConflitoDeRegra extends ErroDeDominio {
        public ConflitoDeRegra(String m) { super("conflito_de_regra", 409, m); }
    }

    public static final class MotoristaSuspenso extends ErroDeDominio {
        public MotoristaSuspenso(String m) { super("motorista_suspenso", 403, m); }
    }
}
