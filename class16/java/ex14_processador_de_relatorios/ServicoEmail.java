package tdd20ex.ex14_processador_de_relatorios;

interface ServicoEmail {
    boolean enviarRelatorio(String destinatario, String conteudo, String formato);
}

