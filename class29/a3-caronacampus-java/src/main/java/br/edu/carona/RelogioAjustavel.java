package br.edu.carona;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/** Relógio do servidor; em modo de teste aceita avanço manual (POST /teste/avancar-relogio). */
public class RelogioAjustavel implements Supplier<LocalDateTime> {
    private final Supplier<LocalDateTime> base;
    private final AtomicReference<Duration> deslocamento = new AtomicReference<>(Duration.ZERO);

    public RelogioAjustavel(Supplier<LocalDateTime> base) { this.base = base; }

    public RelogioAjustavel() { this(() -> LocalDateTime.now(ZoneOffset.UTC)); }

    @Override
    public LocalDateTime get() {
        return base.get().withNano(0).plus(deslocamento.get());
    }

    public void avancar(long minutos) {
        deslocamento.updateAndGet(d -> d.plusMinutes(minutos));
    }
}
