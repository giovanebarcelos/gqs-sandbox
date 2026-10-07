public class Usuario {
    private String nome;
    private int idade;
    private String sexo;
    private double altura;
    private double peso;
    private double metaPeso;
    private int minutosAtividades;

    public Usuario(String nome, int idade, String sexo, double altura, double peso) {
        this.nome = nome;
        this.idade = idade;
        this.sexo = sexo;
        this.altura = altura;
        this.peso = peso;
    }

    public String getNome()  { return nome; }
    public int getIdade()    { return idade; }
    public String getSexo()  { return sexo; }
    public double getAltura(){ return altura; }
    public double getPeso()  { return peso; }
    public void setPeso(double peso) { this.peso = peso; }
    public double getMetaPeso() { return metaPeso; }
    public void setMetaPeso(double metaPeso) { this.metaPeso = metaPeso; }
    public int getMinutosAtividades() { return minutosAtividades; }
    public void addMinutos(int minutos) { this.minutosAtividades += minutos; }

    public double calcularIMC() {
        return peso / (altura * altura);
    }

    @Override
    public String toString() {
        return "Nome=" + nome + ", Idade=" + idade + ", Sexo=" + sexo
             + ", Altura=" + altura + ", Peso=" + peso;
    }
}
