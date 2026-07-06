package tdd20ex.ex16_sistema_de_reservas;

interface ServicoNotificacao {
    void enviarConfirmacao(String cliente, Reserva reserva);
}
