"""O teste 'Adicionar ao carrinho' do Selenium IDE, com Page Object."""
import pytest

from pages.cart_page import CartPage
from pages.inventory_page import InventoryPage
from pages.login_page import LoginPage


@pytest.fixture
def logado(driver):
    LoginPage(driver).abrir().login("standard_user", "secret_sauce")
    return InventoryPage(driver).carregada()


def test_adicionar_produto_ao_carrinho(driver, logado):
    produto = logado.nome_do_produto(0)

    logado.adicionar_ao_carrinho(0)
    assert logado.itens_no_carrinho() == 1

    logado.ir_para_carrinho()
    assert CartPage(driver).produtos() == [produto]
