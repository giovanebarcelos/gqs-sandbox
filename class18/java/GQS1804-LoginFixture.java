/**
 * GQS1804 - Fixture Slim para o fluxo de login (FitNesse).
 * Demonstra uma Script Table Slim (POJO, sem herança de fit.*).
 * Usada no Slide 13/15 de Aula_18_FitNesse_Testes_Aceitacao.md.
 */
class LoginFixture {

    private String username;
    private String password;
    private String mensagem;

    public void enterUsername(String username) {
        this.username = username;
    }

    public void enterPassword(String password) {
        this.password = password;
    }

    public void clickLogin() {
        if ("joao".equals(username) && "123456".equals(password)) {
            mensagem = "Bem-vindo, " + username + "!";
        } else {
            mensagem = "Usuário ou senha inválidos";
        }
    }

    public String mensagem() {
        return mensagem;
    }
}
