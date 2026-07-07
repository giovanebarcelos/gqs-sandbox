"""Exercício 05 — Notificador de Pedidos (mocking com pytest-mock).

`NotificadorPedidos` depende de `ServicoEmailExterno`, que representaria
uma chamada real a uma API/SMTP externa. Em teste unitário não queremos
enviar e-mails de verdade nem depender de rede — por isso o teste
(`test_notificador_pedidos.py`) usa a fixture `mocker` do pytest-mock
para substituir `enviar()` por um dublê, na mesma linha do Slide 11
("Plugins Úteis — pytest-mock") e da Aula 15 (Mocks, Stubs e Doubles).
"""


class ServicoEmailExterno:
    """Simula um serviço externo de envio de e-mail (ex.: uma API SaaS
    de e-mail transacional). Em produção, `enviar` faria uma chamada de
    rede real; em teste, será substituído por um mock."""

    def enviar(self, destinatario: str, assunto: str, corpo: str) -> bool:
        raise NotImplementedError(
            "Chamada real a um serviço externo de e-mail (SMTP/API) — "
            "não deve ser executada durante os testes unitários."
        )


class NotificadorPedidos:
    """Notifica clientes sobre o status de seus pedidos."""

    def __init__(self, servico_email: ServicoEmailExterno = None):
        self.servico_email = servico_email or ServicoEmailExterno()

    def notificar_pedido_confirmado(self, cliente_email: str, numero_pedido: str) -> bool:
        assunto = f"Pedido {numero_pedido} confirmado"
        corpo = f"Seu pedido {numero_pedido} foi confirmado com sucesso."
        return self.servico_email.enviar(cliente_email, assunto, corpo)

    def notificar_pedido_cancelado(self, cliente_email: str, numero_pedido: str, motivo: str) -> bool:
        assunto = f"Pedido {numero_pedido} cancelado"
        corpo = f"Seu pedido {numero_pedido} foi cancelado. Motivo: {motivo}"
        return self.servico_email.enviar(cliente_email, assunto, corpo)
