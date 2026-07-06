package tdd20ex.ex16_sistema_de_reservas;

class Reserva {
    private String id;
    private String quartoNumero;
    private String cliente;
    private int dias;
    private double valorTotal;

    public Reserva(String id, String quartoNumero, String cliente, int dias, double valorTotal) {
        this.id = id;
        this.quartoNumero = quartoNumero;
        this.cliente = cliente;
        this.dias = dias;
        this.valorTotal = valorTotal;
    }

    // getters...
    public String getId() { return id; }
    public double getValorTotal() { return valorTotal; }
    public String getCliente() { return cliente; }
    public String getQuartoNumero() { return quartoNumero; }
}
