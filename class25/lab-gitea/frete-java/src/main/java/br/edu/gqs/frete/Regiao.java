package br.edu.gqs.frete;

import java.math.BigDecimal;
import java.util.Locale;

/** Regioes atendidas pela LojaOnline, com o frete base e o prazo de entrega. */
public enum Regiao {

    SUL("15.00", 3),
    SUDESTE("18.00", 2),
    CENTRO_OESTE("25.00", 5),
    NORDESTE("30.00", 7),
    NORTE("35.00", 9);

    private final BigDecimal freteBase;
    private final int prazoDias;

    Regiao(String freteBase, int prazoDias) {
        this.freteBase = new BigDecimal(freteBase);
        this.prazoDias = prazoDias;
    }

    public BigDecimal getFreteBase() {
        return freteBase;
    }

    public int getPrazoDias() {
        return prazoDias;
    }

    /** Aceita "sul", "Sul", "centro-oeste"... Texto desconhecido: IllegalArgumentException. */
    public static Regiao de(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("Informe a regiao");
        }
        String nome = texto.trim().toUpperCase(Locale.ROOT).replace('-', '_');
        for (Regiao regiao : values()) {
            if (regiao.name().equals(nome)) {
                return regiao;
            }
        }
        throw new IllegalArgumentException("Regiao invalida: " + texto);
    }
}
