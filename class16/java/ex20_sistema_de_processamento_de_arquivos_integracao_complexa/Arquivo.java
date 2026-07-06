package tdd20ex.ex20_sistema_de_processamento_de_arquivos_integracao_complexa;

class Arquivo {
    private String nome;
    private String tipo;
    private byte[] conteudo;
    private long tamanho;

    public Arquivo(String nome, String tipo, byte[] conteudo) {
        this.nome = nome;
        this.tipo = tipo;
        this.conteudo = conteudo;
        this.tamanho = conteudo.length;
    }

    public String getNome() { return nome; }
    public String getTipo() { return tipo; }
    public byte[] getConteudo() { return conteudo; }
    public long getTamanho() { return tamanho; }
    public void setConteudo(byte[] conteudo) {
        this.conteudo = conteudo;
        this.tamanho = conteudo.length;
    }
}
