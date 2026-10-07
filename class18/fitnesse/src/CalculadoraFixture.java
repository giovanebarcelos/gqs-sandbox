/** Decision Table Slim: entradas via setters, saidas via metodos. POJO, sem extends. */
public class CalculadoraFixture {
    private double a, b;

    public void setA(double a) { this.a = a; }
    public void setB(double b) { this.b = b; }

    public double somar()       { return a + b; }
    public double subtrair()    { return a - b; }
    public double multiplicar() { return a * b; }
    public double dividir()     { return a / b; }
}
