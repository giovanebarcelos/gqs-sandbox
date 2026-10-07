// Fixture Slim (POJO). Salvar como DescontoFixture.java (copia pronta em fitnesse/src/).
/** Decision Table Slim do Slide 3: valor e percentual de desconto -> valor final. */
public class DescontoFixture {
    private double valor;
    private double percentual;

    public void setValor(double valor)           { this.valor = valor; }
    public void setPercentual(double percentual) { this.percentual = percentual; }

    public double valorFinal() {
        return valor - valor * percentual / 100.0;
    }
}
