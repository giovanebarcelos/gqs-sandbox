package br.edu.carona;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Viagem oferecida por um motorista. Mutável só pelo serviço (alteração e cancelamento lógico). */
public class Viagem {
    private long id;
    private final long motoristaId;
    private String origem;
    private String destino;
    private LocalDateTime saida;
    private int vagas;
    private BigDecimal custoTotal;
    private boolean cancelada;

    public Viagem(long id, long motoristaId, String origem, String destino, LocalDateTime saida,
                  int vagas, BigDecimal custoTotal, boolean cancelada) {
        this.id = id;
        this.motoristaId = motoristaId;
        this.origem = origem;
        this.destino = destino;
        this.saida = saida;
        this.vagas = vagas;
        this.custoTotal = custoTotal;
        this.cancelada = cancelada;
    }

    public long id() { return id; }
    public void definirId(long id) { this.id = id; }
    public long motoristaId() { return motoristaId; }
    public String origem() { return origem; }
    public void origem(String v) { this.origem = v; }
    public String destino() { return destino; }
    public void destino(String v) { this.destino = v; }
    public LocalDateTime saida() { return saida; }
    public void saida(LocalDateTime v) { this.saida = v; }
    public int vagas() { return vagas; }
    public void vagas(int v) { this.vagas = v; }
    public BigDecimal custoTotal() { return custoTotal; }
    public void custoTotal(BigDecimal v) { this.custoTotal = v; }
    public boolean cancelada() { return cancelada; }
    public void cancelada(boolean v) { this.cancelada = v; }

    public BigDecimal valorPorVaga() { return Regras.precoPorVaga(custoTotal, vagas); }

    public boolean iniciada(LocalDateTime agora) { return !agora.isBefore(saida); }
}
