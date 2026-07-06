package tdd20ex.ex18_sistema_de_monitoramento;

interface VerificadorLimites {
    boolean verificarLimiteCPU(double cpu);
    boolean verificarLimiteMemoria(double memoria);
    boolean verificarLimiteDisco(double disco);
}