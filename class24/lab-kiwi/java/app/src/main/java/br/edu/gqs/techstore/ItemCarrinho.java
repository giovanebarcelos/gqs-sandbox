package br.edu.gqs.techstore;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Um item do carrinho: {"produto_id": 1, "quantidade": 2}. */
public record ItemCarrinho(@JsonProperty("produto_id") Integer produtoId, Integer quantidade) {
}
