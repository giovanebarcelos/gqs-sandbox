package tdd20ex.ex02_validador_string;

public class ValidadorString {

    public boolean ehVazia(String str) {
        return str == null || str.trim().isEmpty();
    }

    public boolean temTamanhoMinimo(String str, int min) {
        return str != null && str.length() >= min;
    }

    public boolean ehEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    public boolean ehCPF(String cpf) {
        return cpf != null && cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
    }
}
