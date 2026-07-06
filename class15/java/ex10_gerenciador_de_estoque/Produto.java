package tdd20ex.ex10_gerenciador_de_estoque;

class Produto {
    private String codigo;
    private String nome;
    private int quantidade;

    public Produto(String codigo, String nome, int quantidade) {
        this.codigo = codigo;
        this.nome = nome;
        this.quantidade = quantidade;
    }

    // getters e setters...
    public String getCodigo() { return codigo; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
}
