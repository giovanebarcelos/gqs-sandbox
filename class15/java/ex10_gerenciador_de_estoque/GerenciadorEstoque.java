package tdd20ex.ex10_gerenciador_de_estoque;

import java.util.Optional;

public class GerenciadorEstoque {
    private RepositorioProduto repositorio;

    public GerenciadorEstoque(RepositorioProduto repositorio) {
        this.repositorio = repositorio;
    }

    public boolean verificarDisponibilidade(String codigo, int quantidadeDesejada) {
        Optional<Produto> produto = repositorio.buscarPorCodigo(codigo);
        return produto.map(p -> p.getQuantidade() >= quantidadeDesejada).orElse(false);
    }

    public boolean reservarItens(String codigo, int quantidade) {
        Optional<Produto> produtoOpt = repositorio.buscarPorCodigo(codigo);

        if (produtoOpt.isEmpty()) {
            return false;
        }

        Produto produto = produtoOpt.get();
        if (produto.getQuantidade() < quantidade) {
            return false;
        }

        produto.setQuantidade(produto.getQuantidade() - quantidade);
        repositorio.atualizar(produto);
        return true;
    }
}
