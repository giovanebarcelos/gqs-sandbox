package br.edu.gqs.frete;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Regras de frete da LojaOnline.
 *
 * <pre>
 *   R1  frete = base da regiao + R$ 2,50 por kg (ou fracao) acima de 1 kg
 *   R2  pedidos a partir de R$ 200,00 tem frete gratis
 *   R3  peso aceito: maior que 0 e no maximo 30 kg
 *   R4  regiao desconhecida e' rejeitada
 * </pre>
 */
public class CalculadoraFrete {

    static final BigDecimal LIMITE_FRETE_GRATIS = new BigDecimal("200.00");
    static final BigDecimal ADICIONAL_POR_KG = new BigDecimal("2.50");
    static final double PESO_MAXIMO_KG = 30;

    public CotacaoFrete calcular(String regiaoTexto, double pesoKg, BigDecimal valorPedido) {
        Regiao regiao = Regiao.de(regiaoTexto);
        if (pesoKg <= 0 || pesoKg > PESO_MAXIMO_KG) {
            throw new FreteInvalidoException("Peso deve ser maior que 0 e no maximo 30 kg");
        }
        if (valorPedido == null || valorPedido.signum() < 0) {
            throw new FreteInvalidoException("Valor do pedido invalido");
        }

        boolean gratis = valorPedido.compareTo(LIMITE_FRETE_GRATIS) > 0;
        BigDecimal frete = gratis ? BigDecimal.ZERO : regiao.getFreteBase().add(adicionalPorPeso(pesoKg));

        return new CotacaoFrete(regiao, pesoKg, valorPedido,
                frete.setScale(2, RoundingMode.HALF_UP), gratis, regiao.getPrazoDias());
    }

    /** R$ 2,50 por kg ou fracao acima do primeiro: 2,3 kg cobra 2 kg extras. */
    private static BigDecimal adicionalPorPeso(double pesoKg) {
        long kgExtras = (long) Math.ceil(pesoKg - 1);
        return kgExtras <= 0 ? BigDecimal.ZERO : ADICIONAL_POR_KG.multiply(BigDecimal.valueOf(kgExtras));
    }
}
