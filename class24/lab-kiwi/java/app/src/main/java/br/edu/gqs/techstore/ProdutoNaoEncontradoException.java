package br.edu.gqs.techstore;

/** RN05 - o produto informado nao existe no catalogo (vira HTTP 404). */
public class ProdutoNaoEncontradoException extends RuntimeException {
    public ProdutoNaoEncontradoException(int produtoId) {
        super("Produto " + produtoId + " nao encontrado");
    }
}
