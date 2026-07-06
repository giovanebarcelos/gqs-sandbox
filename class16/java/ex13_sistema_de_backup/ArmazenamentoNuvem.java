package tdd20ex.ex13_sistema_de_backup;

interface ArmazenamentoNuvem {
    boolean upload(String arquivo, byte[] dados);
}
