package tdd20ex.ex10_gerenciador_de_estoque;

import java.util.Optional;

interface RepositorioProduto {
    Optional<Produto> buscarPorCodigo(String codigo);
    void atualizar(Produto produto);
}
