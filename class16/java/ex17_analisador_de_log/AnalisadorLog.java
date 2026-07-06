package tdd20ex.ex17_analisador_de_log;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AnalisadorLog {
    private LeitorArquivo leitor;
    private ProcessadorEstatisticas processador;

    public AnalisadorLog(LeitorArquivo leitor, ProcessadorEstatisticas processador) {
        this.leitor = leitor;
        this.processador = processador;
    }

    public EstatisticasLog analisarArquivo(String caminhoArquivo) {
        List<String> linhas = leitor.lerLinhas(caminhoArquivo);

        if (linhas.isEmpty()) {
            throw new RuntimeException("Arquivo vazio ou não encontrado");
        }

        List<EntradaLog> logs = linhas.stream()
                .map(this::parsearLinha)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return processador.calcularEstatisticas(logs);
    }

    private EntradaLog parsearLinha(String linha) {
        String[] partes = linha.split(" ", 3);
        if (partes.length >= 3) {
            return new EntradaLog(partes[0], partes[1], partes[2]);
        }
        return null;
    }
}
