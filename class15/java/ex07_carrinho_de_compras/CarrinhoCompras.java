package tdd20ex.ex07_carrinho_de_compras;

import java.util.ArrayList;
import java.util.List;

public class CarrinhoCompras {
    private List<Item> itens = new ArrayList<>();
    private CalculadoraDesconto calculadora;

    public CarrinhoCompras(CalculadoraDesconto calculadora) {
        this.calculadora = calculadora;
    }

    public void adicionarItem(Item item) {
        itens.add(item);
    }

    public double calcularTotal() {
        return itens.stream().mapToDouble(Item::getPreco).sum();
    }

    public double calcularTotalComDesconto(String tipoCliente) {
        double total = calcularTotal();
        double desconto = calculadora.calcularDesconto(total, tipoCliente);
        return total - desconto;
    }

    public int getQuantidadeItens() {
        return itens.size();
    }
}
