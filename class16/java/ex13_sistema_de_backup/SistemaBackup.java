package tdd20ex.ex13_sistema_de_backup;

public class SistemaBackup {
    private ArmazenamentoLocal local;
    private ArmazenamentoNuvem nuvem;

    public SistemaBackup(ArmazenamentoLocal local, ArmazenamentoNuvem nuvem) {
        this.local = local;
        this.nuvem = nuvem;
    }

    public String realizarBackup(String nomeArquivo, byte[] dados) {
        if (local.estaDisponivel() && local.salvar(nomeArquivo, dados)) {
            return "BACKUP_LOCAL_" + nomeArquivo;
        }

        if (nuvem.upload(nomeArquivo, dados)) {
            return "BACKUP_NUVEM_" + nomeArquivo;
        }

        throw new RuntimeException("Falha em todos os backups");
    }
}
