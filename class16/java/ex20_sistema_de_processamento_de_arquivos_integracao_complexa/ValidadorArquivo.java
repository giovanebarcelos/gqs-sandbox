package tdd20ex.ex20_sistema_de_processamento_de_arquivos_integracao_complexa;

import java.util.List;

interface ValidadorArquivo {
    boolean validarTamanho(Arquivo arquivo, long tamanhoMaximo);
    boolean validarTipo(Arquivo arquivo, List<String> tiposPermitidos);
}
