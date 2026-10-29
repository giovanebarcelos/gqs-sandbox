package br.edu.gqs.frete;

/** Peso ou valor do pedido fora das regras (R3). */
public class FreteInvalidoException extends RuntimeException {

    public FreteInvalidoException(String mensagem) {
        super(mensagem);
    }
}
