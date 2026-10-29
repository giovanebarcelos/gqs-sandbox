package br.edu.gqs.biblioteca;

/** O livro pedido nao esta disponivel no acervo (vira HTTP 404). */
public class LivroNaoEncontradoException extends RuntimeException {
    public LivroNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
