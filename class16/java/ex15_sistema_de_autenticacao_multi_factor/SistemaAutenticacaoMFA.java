package tdd20ex.ex15_sistema_de_autenticacao_multi_factor;

public class SistemaAutenticacaoMFA {
    private ValidadorCredenciais validador;
    private GeradorTokenMFA geradorToken;
    private ServicoSMS servicoSMS;

    public SistemaAutenticacaoMFA(ValidadorCredenciais validador, GeradorTokenMFA geradorToken, ServicoSMS servicoSMS) {
        this.validador = validador;
        this.geradorToken = geradorToken;
        this.servicoSMS = servicoSMS;
    }

    public ResultadoAutenticacao autenticar(CredenciaisUsuario credenciais) {
        if (!validador.validarCredenciais(credenciais)) {
            return new ResultadoAutenticacao(false, null, "Credenciais inválidas");
        }

        String telefone = validador.obterTelefone(credenciais.getUsuario());
        if (telefone == null) {
            return new ResultadoAutenticacao(false, null, "Telefone não cadastrado");
        }

        String token = geradorToken.gerarToken();
        String mensagem = "Seu código MFA: " + token;

        if (servicoSMS.enviarSMS(telefone, mensagem)) {
            return new ResultadoAutenticacao(true, token, "Token enviado");
        } else {
            return new ResultadoAutenticacao(false, null, "Falha ao enviar SMS");
        }
    }
}
