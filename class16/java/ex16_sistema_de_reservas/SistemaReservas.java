package tdd20ex.ex16_sistema_de_reservas;

import java.util.Optional;

public class SistemaReservas {
    private RepositorioQuarto repositorioQuarto;
    private ProcessadorPagamento processadorPagamento;
    private ServicoNotificacao servicoNotificacao;

    public SistemaReservas(RepositorioQuarto repositorioQuarto,
                           ProcessadorPagamento processadorPagamento,
                           ServicoNotificacao servicoNotificacao) {
        this.repositorioQuarto = repositorioQuarto;
        this.processadorPagamento = processadorPagamento;
        this.servicoNotificacao = servicoNotificacao;
    }

    public Reserva criarReserva(String numeroQuarto, String cliente, int dias, String cartao) {
        Optional<Quarto> quartoOpt = repositorioQuarto.buscarPorNumero(numeroQuarto);

        if (quartoOpt.isEmpty()) {
            throw new RuntimeException("Quarto não encontrado");
        }

        Quarto quarto = quartoOpt.get();
        if (!quarto.isDisponivel()) {
            throw new RuntimeException("Quarto não disponível");
        }

        double valorTotal = quarto.getPrecoDiaria() * dias;

        if (!processadorPagamento.processarPagamento(cartao, valorTotal)) {
            throw new RuntimeException("Falha no pagamento");
        }

        quarto.setDisponivel(false);
        repositorioQuarto.atualizar(quarto);

        String reservaId = "RES_" + System.currentTimeMillis();
        Reserva reserva = new Reserva(reservaId, numeroQuarto, cliente, dias, valorTotal);

        servicoNotificacao.enviarConfirmacao(cliente, reserva);

        return reserva;
    }
}
