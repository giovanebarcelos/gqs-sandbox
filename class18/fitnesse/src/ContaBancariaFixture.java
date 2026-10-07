/** Decision Table Slim: cada linha e um caso (saldoInicial, valor) -> saidas. */
public class ContaBancariaFixture {
    private double saldoInicial;
    private double valor;

    public void setSaldoInicial(double saldoInicial) { this.saldoInicial = saldoInicial; }
    public void setValor(double valor)               { this.valor = valor; }

    public double depositar() {
        return saldoInicial + valor;
    }

    /** Saque acima do saldo e recusado: devolve o saldo inalterado. */
    public double sacar() {
        return valor > saldoInicial ? saldoInicial : saldoInicial - valor;
    }

    public boolean saqueAprovado() {
        return valor <= saldoInicial;
    }
}
