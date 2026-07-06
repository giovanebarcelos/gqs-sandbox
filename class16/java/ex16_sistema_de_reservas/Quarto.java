package tdd20ex.ex16_sistema_de_reservas;

class Quarto {
    private String numero;
    private boolean disponivel;
    private double precoDiaria;

    public Quarto(String numero, boolean disponivel, double precoDiaria) {
        this.numero = numero;
        this.disponivel = disponivel;
        this.precoDiaria = precoDiaria;
    }

    public String getNumero() { return numero; }
    public boolean isDisponivel() { return disponivel; }
    public double getPrecoDiaria() { return precoDiaria; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }
}