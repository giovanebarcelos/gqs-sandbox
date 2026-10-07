// Fixture Slim (POJO). Salvar como ImcFixture.java em fitnesse/src/ (copia pronta em repository/class18/fitnesse/src/).
/** Decision Table Slim do Lab IMC: entradas peso/altura, saidas imc e classificacao. */
public class ImcFixture {
    private double peso;
    private double altura;

    public void setPeso(double peso)     { this.peso = peso; }
    public void setAltura(double altura) { this.altura = altura; }

    public double imc() {
        return Math.round(peso / (altura * altura) * 10.0) / 10.0;
    }

    public String classificacao() {
        double imc = peso / (altura * altura);
        if (imc < 18.5) return "Abaixo do peso";
        if (imc < 25.0) return "Normal";
        if (imc < 30.0) return "Sobrepeso";
        return "Obesidade";
    }
}
