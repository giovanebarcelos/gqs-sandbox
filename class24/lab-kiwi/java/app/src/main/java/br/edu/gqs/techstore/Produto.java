package br.edu.gqs.techstore;

import java.math.BigDecimal;

public record Produto(int id, String nome, BigDecimal preco) {
}
