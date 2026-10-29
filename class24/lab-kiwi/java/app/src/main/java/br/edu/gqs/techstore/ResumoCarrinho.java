package br.edu.gqs.techstore;

import java.math.BigDecimal;

public record ResumoCarrinho(BigDecimal subtotal, BigDecimal desconto, BigDecimal frete, BigDecimal total) {
}
