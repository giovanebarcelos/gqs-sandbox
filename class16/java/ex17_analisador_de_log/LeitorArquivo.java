package tdd20ex.ex17_analisador_de_log;

import java.util.List;

interface LeitorArquivo {
    List<String> lerLinhas(String caminhoArquivo);
}
