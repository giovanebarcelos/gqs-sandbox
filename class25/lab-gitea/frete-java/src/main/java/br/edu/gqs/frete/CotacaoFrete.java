package br.edu.gqs.frete;

import java.math.BigDecimal;

/** Resultado de uma cotacao - vira o JSON de resposta da API. */
public record CotacaoFrete(Regiao regiao, double pesoKg, BigDecimal valorPedido,
                           BigDecimal frete, boolean gratis, int prazoDias) {
}
