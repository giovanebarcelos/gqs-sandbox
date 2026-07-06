package tdd20ex.ex20_sistema_de_processamento_de_arquivos_integracao_complexa;

interface CompactadorArquivo {
    Arquivo compactar(Arquivo arquivo);
    double calcularTaxaCompressao(long tamanhoOriginal, long tamanhoCompactado);
}
