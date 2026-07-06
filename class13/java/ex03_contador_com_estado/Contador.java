package tdd20ex.ex03_contador_com_estado;

public class Contador {
    private int valor = 0;

    public void incrementar() {
        valor++;
    }

    public void decrementar() {
        valor--;
    }

    public void reset() {
        valor = 0;
    }

    public int getValor() {
        return valor;
    }

    public void definirValor(int valor) {
        this.valor = valor;
    }
}