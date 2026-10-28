package br.edu.gqs.techstore;

/** Entrada que viola RN02 ou RN04 (vira HTTP 400). */
public class CarrinhoInvalidoException extends RuntimeException {
    public CarrinhoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
