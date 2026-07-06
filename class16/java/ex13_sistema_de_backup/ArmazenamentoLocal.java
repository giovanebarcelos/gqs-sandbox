package tdd20ex.ex13_sistema_de_backup;

interface ArmazenamentoLocal {
    boolean salvar(String arquivo, byte[] dados);
    boolean estaDisponivel();
}