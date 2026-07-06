package tdd20ex.ex06_sistema_de_notificacao;

public interface ServicoEmail {
    boolean enviarEmail(String destinatario, String assunto, String corpo);
}
