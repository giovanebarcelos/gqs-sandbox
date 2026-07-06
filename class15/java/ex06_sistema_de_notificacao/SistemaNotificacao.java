package tdd20ex.ex06_sistema_de_notificacao;

public class SistemaNotificacao {
    private ServicoEmail servicoEmail;

    public SistemaNotificacao(ServicoEmail servicoEmail) {
        this.servicoEmail = servicoEmail;
    }

    public boolean enviarNotificacao(String usuario, String mensagem) {
        if (usuario == null || mensagem == null) {
            return false;
        }

        String assunto = "Notificação Importante";
        return servicoEmail.enviarEmail(usuario, assunto, mensagem);
    }
}
