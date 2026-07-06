package tdd20ex.ex18_sistema_de_monitoramento;

class Metricas {
    private double cpu;
    private double memoria;
    private double disco;

    public Metricas(double cpu, double memoria, double disco) {
        this.cpu = cpu;
        this.memoria = memoria;
        this.disco = disco;
    }

    public double getCpu() { return cpu; }
    public double getMemoria() { return memoria; }
    public double getDisco() { return disco; }
}
