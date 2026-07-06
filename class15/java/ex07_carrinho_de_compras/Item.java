package tdd20ex.ex07_carrinho_de_compras;

class Item {
    private String nome;
    private double preco;

    public Item(String nome, double preco) {
        this.nome = nome;
        this.preco = preco;
    }

    public double getPreco() { return preco; }
    public String getNome() { return nome; }
}
