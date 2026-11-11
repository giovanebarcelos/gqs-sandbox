"""Login do SauceDemo: o teste 'Login valido' e 'Login bloqueado' do Selenium IDE,
reescritos com Page Object e data-driven (pytest.mark.parametrize)."""
import pytest

from pages.inventory_page import InventoryPage
from pages.login_page import LoginPage


def test_login_valido(driver):
    LoginPage(driver).abrir().login("standard_user", "secret_sauce")

    inventario = InventoryPage(driver).carregada()
    assert inventario.titulo() == "Products"
    assert driver.current_url.endswith("/inventory.html")


CENARIOS_INVALIDOS = [
    pytest.param("locked_out_user", "secret_sauce",
                 "Sorry, this user has been locked out", id="usuario-bloqueado"),
    pytest.param("standard_user", "senha_errada",
                 "Username and password do not match", id="senha-errada"),
    pytest.param("", "secret_sauce", "Username is required", id="usuario-vazio"),
    pytest.param("standard_user", "", "Password is required", id="senha-vazia"),
]


@pytest.mark.parametrize("usuario, senha, mensagem", CENARIOS_INVALIDOS)
def test_login_invalido(driver, usuario, senha, mensagem):
    pagina = LoginPage(driver).abrir()
    pagina.login(usuario, senha)

    assert mensagem in pagina.mensagem_erro()
    assert "inventory" not in driver.current_url
