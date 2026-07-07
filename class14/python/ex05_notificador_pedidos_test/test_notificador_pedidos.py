"""Testes pytest para o Exercício 05 — Notificador de Pedidos.

Demonstra a fixture `mocker` do plugin pytest-mock (Slide 11 e novos
slides de mocking): substituímos o método `enviar` do serviço externo
de e-mail por um dublê controlado, sem depender de rede real.

Requer: pip install pytest-mock
"""
from notificador_pedidos import NotificadorPedidos, ServicoEmailExterno


def test_notificar_pedido_confirmado_chama_servico_de_email(mocker):
    servico = ServicoEmailExterno()
    # mocker.patch.object substitui o método `enviar` da instância real
    # por um Mock que retorna True e registra como foi chamado.
    mock_enviar = mocker.patch.object(servico, "enviar", return_value=True)

    notificador = NotificadorPedidos(servico_email=servico)
    resultado = notificador.notificar_pedido_confirmado("cliente@teste.com", "PED-001")

    assert resultado is True
    mock_enviar.assert_called_once_with(
        "cliente@teste.com",
        "Pedido PED-001 confirmado",
        "Seu pedido PED-001 foi confirmado com sucesso.",
    )


def test_notificar_pedido_cancelado_inclui_motivo_no_corpo(mocker):
    servico = ServicoEmailExterno()
    mock_enviar = mocker.patch.object(servico, "enviar", return_value=True)

    notificador = NotificadorPedidos(servico_email=servico)
    notificador.notificar_pedido_cancelado("cliente@teste.com", "PED-002", "Estoque indisponível")

    # assert_called_once_with valida os argumentos exatos passados ao dublê
    mock_enviar.assert_called_once_with(
        "cliente@teste.com",
        "Pedido PED-002 cancelado",
        "Seu pedido PED-002 foi cancelado. Motivo: Estoque indisponível",
    )


def test_notificador_nao_chama_servico_real_sem_mock_deveria_falhar():
    """Sem mock, `ServicoEmailExterno.enviar` lança NotImplementedError —
    prova de que os testes acima realmente estão testando com dublê,
    e não acidentalmente chamando uma implementação real."""
    import pytest

    notificador = NotificadorPedidos()  # usa ServicoEmailExterno real, sem mock
    with pytest.raises(NotImplementedError):
        notificador.notificar_pedido_confirmado("cliente@teste.com", "PED-003")


def test_mock_registra_numero_de_chamadas(mocker):
    servico = ServicoEmailExterno()
    mock_enviar = mocker.patch.object(servico, "enviar", return_value=True)

    notificador = NotificadorPedidos(servico_email=servico)
    notificador.notificar_pedido_confirmado("a@teste.com", "PED-010")
    notificador.notificar_pedido_confirmado("b@teste.com", "PED-011")

    assert mock_enviar.call_count == 2
