package br.edu.carona;

import java.math.BigDecimal;

public class Reserva {
    private long id;
    private final long viagemId;
    private final long passageiroId;
    private final BigDecimal valor;
    private boolean cancelada;
    private BigDecimal taxa;

    public Reserva(long id, long viagemId, long passageiroId, BigDecimal valor, boolean cancelada, BigDecimal taxa) {
        this.id = id;
        this.viagemId = viagemId;
        this.passageiroId = passageiroId;
        this.valor = valor;
        this.cancelada = cancelada;
        this.taxa = taxa;
    }

    public static Reserva nova(long viagemId, long passageiroId, BigDecimal valor) {
        return new Reserva(0, viagemId, passageiroId, valor, false, new BigDecimal("0.00"));
    }

    public long id() { return id; }
    public void definirId(long id) { this.id = id; }
    public long viagemId() { return viagemId; }
    public long passageiroId() { return passageiroId; }
    public BigDecimal valor() { return valor; }
    public boolean cancelada() { return cancelada; }
    public void cancelada(boolean v) { this.cancelada = v; }
    public BigDecimal taxa() { return taxa; }
    public void taxa(BigDecimal v) { this.taxa = v; }
}
