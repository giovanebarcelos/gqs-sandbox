package tdd20ex.ex20_sistema_de_processamento_de_arquivos_integracao_complexa;

import java.util.Arrays;
import java.util.List;

public class ProcessadorArquivos {
    private ValidadorArquivo validador;
    private ConversorFormato conversor;
    private CompactadorArquivo compactador;
    private ServicoUpload servicoUpload;

    public ProcessadorArquivos(ValidadorArquivo validador, ConversorFormato conversor,
                               CompactadorArquivo compactador, ServicoUpload servicoUpload) {
        this.validador = validador;
        this.conversor = conversor;
        this.compactador = compactador;
        this.servicoUpload = servicoUpload;
    }

    public ResultadoProcessamento processarArquivo(Arquivo arquivo, String formatoDestino,
                                                   boolean deveCompactar, long tamanhoMaximo) {
        long tamanhoOriginal = arquivo.getTamanho();

        // Validar tamanho
        if (!validador.validarTamanho(arquivo, tamanhoMaximo)) {
            return new ResultadoProcessamento(false, null, tamanhoOriginal, 0, "Arquivo muito grande");
        }

        // Validar tipo
        List<String> tiposPermitidos = Arrays.asList("PDF", "DOCX", "TXT", "JPG", "PNG");
        if (!validador.validarTipo(arquivo, tiposPermitidos)) {
            return new ResultadoProcessamento(false, null, tamanhoOriginal, 0, "Tipo de arquivo não permitido");
        }

        try {
            // Converter formato se necessário
            if (!arquivo.getTipo().equalsIgnoreCase(formatoDestino)) {
                arquivo = conversor.converterPara(arquivo, formatoDestino);
            }

            // Compactar se solicitado
            if (deveCompactar) {
                arquivo = compactador.compactar(arquivo);
            }

            // Fazer upload
            String url = servicoUpload.fazerUpload(arquivo);

            return new ResultadoProcessamento(true, url, tamanhoOriginal, arquivo.getTamanho(), "Processamento concluído");

        } catch (Exception e) {
            return new ResultadoProcessamento(false, null, tamanhoOriginal, 0, "Erro no processamento: " + e.getMessage());
        }
    }
}

