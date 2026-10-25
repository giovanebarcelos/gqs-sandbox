package br.edu.carona;

import br.edu.carona.Erros.ConflitoDeRegra;
import br.edu.carona.Erros.DadoInvalido;
import br.edu.carona.Erros.MotoristaSuspenso;
import br.edu.carona.Erros.NaoEncontrado;
import br.edu.carona.Erros.SemPermissao;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/** Casos de uso do CaronaCampus. Depende só das regras e da porta {@link RepositorioCarona}. */
public class ServicoCarona {
    private final RepositorioCarona repo;
    private final Supplier<LocalDateTime> relogio;

    public ServicoCarona(RepositorioCarona repo, Supplier<LocalDateTime> relogio) {
        this.repo = repo;
        this.relogio = relogio;
    }

    public RepositorioCarona repositorio() { return repo; }

    // -- viagens (CRUD) -----------------------------------------------------------
    /** RF-01. Aplica RN-01, RN-02 e RN-05 (motorista suspenso não cria viagem). */
    public Viagem criarViagem(long motoristaId, String origem, String destino, LocalDateTime saida,
                              int vagas, BigDecimal custoTotal) {
        origem = origem == null ? "" : origem.strip();
        destino = destino == null ? "" : destino.strip();
        exigirTrechoValido(origem, destino);
        Regras.validarVagas(vagas);
        Regras.validarSaida(saida, relogio.get());
        if (custoTotal.signum() <= 0) {
            throw new DadoInvalido("custo_total deve ser positivo");
        }
        if (Regras.estaSuspenso(repo.notasDoMotorista(motoristaId))) {
            throw new MotoristaSuspenso("motorista suspenso por avaliação baixa");
        }
        return repo.inserirViagem(new Viagem(0, motoristaId, origem, destino, saida, vagas, custoTotal, false));
    }

    private static void exigirTrechoValido(String origem, String destino) {
        if (origem.isEmpty() || destino.isEmpty()) {
            throw new DadoInvalido("origem e destino são obrigatórios");
        }
        if (origem.equalsIgnoreCase(destino)) {
            throw new DadoInvalido("origem e destino devem ser diferentes");
        }
    }

    public Viagem obterViagem(long viagemId) {
        Viagem v = repo.obterViagem(viagemId).orElse(null);
        if (v == null || v.cancelada()) {
            throw new NaoEncontrado("viagem não encontrada");
        }
        return v;
    }

    public List<Viagem> listarViagens() { return repo.listarViagens(); }

    /** RF-02. Só o motorista altera, e só enquanto não há reservas ativas. Campos ausentes ficam como estão. */
    public Viagem atualizarViagem(long viagemId, long usuarioId, String origem, String destino, Integer vagas,
                                  BigDecimal custoTotal, LocalDateTime saida) {
        Viagem v = viagemDoMotorista(viagemId, usuarioId);
        if (!repo.reservasAtivas(viagemId).isEmpty()) {
            throw new ConflitoDeRegra("a viagem já tem reservas: não pode ser alterada");
        }
        if (origem != null) {
            v.origem(origem.strip());
        }
        if (destino != null) {
            v.destino(destino.strip());
        }
        if (vagas != null) {
            v.vagas(Regras.validarVagas(vagas));
        }
        if (custoTotal != null) {
            if (custoTotal.signum() <= 0) {
                throw new DadoInvalido("custo_total deve ser positivo");
            }
            v.custoTotal(custoTotal);
        }
        if (saida != null) {
            v.saida(Regras.validarSaida(saida, relogio.get()));
        }
        exigirTrechoValido(v.origem(), v.destino());
        repo.atualizarViagem(v);
        return v;
    }

    /** RF-02. Cancelamento lógico; recusado se houver reservas ativas. */
    public void excluirViagem(long viagemId, long usuarioId) {
        Viagem v = viagemDoMotorista(viagemId, usuarioId);
        if (!repo.reservasAtivas(viagemId).isEmpty()) {
            throw new ConflitoDeRegra("a viagem já tem reservas: cancele-as antes");
        }
        v.cancelada(true);
        repo.atualizarViagem(v);
    }

    private Viagem viagemDoMotorista(long viagemId, long usuarioId) {
        Viagem v = obterViagem(viagemId);
        if (v.motoristaId() != usuarioId) {
            throw new SemPermissao("somente o motorista pode alterar a viagem");
        }
        return v;
    }

    // -- reservas -----------------------------------------------------------------
    /** RF-03. RN-01 (vaga disponível), sem reserva dupla, sem reservar a própria viagem. */
    public Reserva reservar(long viagemId, long passageiroId) {
        Viagem v = obterViagem(viagemId);
        if (v.iniciada(relogio.get())) {
            throw new ConflitoDeRegra("a viagem já começou");
        }
        if (v.motoristaId() == passageiroId) {
            throw new ConflitoDeRegra("o motorista não pode reservar a própria viagem");
        }
        List<Reserva> ativas = repo.reservasAtivas(viagemId);
        if (ativas.stream().anyMatch(r -> r.passageiroId() == passageiroId)) {
            throw new ConflitoDeRegra("passageiro já tem reserva nesta viagem");
        }
        if (ativas.size() >= v.vagas()) {
            throw new ConflitoDeRegra("viagem lotada");
        }
        // A checagem acima é só para dar a mensagem cedo; quem garante é o repositório.
        return repo.reservarSeHaVaga(Reserva.nova(viagemId, passageiroId, v.valorPorVaga()), v.vagas())
            .orElseThrow(() -> new ConflitoDeRegra("viagem lotada"));
    }

    /** RF-04. RN-04: taxa de 50% se faltar menos de 60 min para a saída. */
    public Reserva cancelarReserva(long reservaId, long usuarioId) {
        Reserva r = repo.obterReserva(reservaId).orElseThrow(() -> new NaoEncontrado("reserva não encontrada"));
        if (r.passageiroId() != usuarioId) {
            throw new SemPermissao("somente o passageiro pode cancelar a reserva");
        }
        if (r.cancelada()) {
            throw new ConflitoDeRegra("reserva já cancelada");
        }
        Viagem v = repo.obterViagem(r.viagemId()).orElseThrow();
        r.taxa(Regras.taxaDeCancelamento(r.valor(), v.saida(), relogio.get()));
        r.cancelada(true);
        repo.atualizarReserva(r);
        return r;
    }

    // -- avaliações e reputação ---------------------------------------------------
    /** RF-05. Só quem tinha reserva ativa, depois da saída, uma vez por viagem. */
    public void avaliar(long viagemId, long passageiroId, int nota) {
        Regras.validarNota(nota);
        Viagem v = obterViagem(viagemId);
        if (!v.iniciada(relogio.get())) {
            throw new ConflitoDeRegra("só é possível avaliar depois da saída");
        }
        if (repo.reservasAtivas(viagemId).stream().noneMatch(r -> r.passageiroId() == passageiroId)) {
            throw new SemPermissao("só quem viajou pode avaliar");
        }
        if (repo.avaliacaoExiste(viagemId, passageiroId)) {
            throw new ConflitoDeRegra("viagem já avaliada por este passageiro");
        }
        repo.inserirAvaliacao(viagemId, passageiroId, nota);
    }

    /** RF-06. Média, quantidade e se o motorista está suspenso (RN-05). */
    public Map<String, Object> reputacao(long motoristaId) {
        if (repo.obterUsuario(motoristaId).isEmpty()) {
            throw new NaoEncontrado("usuário não encontrado");
        }
        List<Integer> notas = repo.notasDoMotorista(motoristaId);
        BigDecimal media = Regras.media(notas);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("motorista_id", motoristaId);
        m.put("avaliacoes", notas.size());
        m.put("media", media == null ? null : media.toPlainString());
        m.put("suspenso", Regras.estaSuspenso(notas));
        return m;
    }

    // -- relatório ----------------------------------------------------------------
    /** RF-07. Ocupação das viagens abertas. */
    public Map<String, Object> relatorioOcupacao() {
        List<long[]> linhas = repo.ocupacaoPorViagem();
        long vagas = linhas.stream().mapToLong(l -> l[1]).sum();
        long ocupadas = linhas.stream().mapToLong(l -> l[2]).sum();
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("viagens", linhas.size());
        m.put("vagas", vagas);
        m.put("ocupadas", ocupadas);
        m.put("taxa_ocupacao", vagas == 0 ? 0.0 : Math.round(1000.0 * ocupadas / vagas) / 10.0);
        m.put("por_viagem", linhas.stream().map(l -> {
            Map<String, Object> o = new LinkedHashMap<>();
            o.put("viagem_id", l[0]);
            o.put("vagas", l[1]);
            o.put("ocupadas", l[2]);
            return o;
        }).toList());
        return m;
    }
}
