"""Fixtures do lab: um navegador novo por teste, screenshot quando falhar.

Opcoes (pytest --help mostra todas):
    --browser=chrome|firefox|edge   navegador (padrao: chrome)
    --headless                      sem janela (CI, WSL, servidor)
O driver e baixado pelo Selenium Manager (Selenium >= 4.6): nao precisa
instalar chromedriver/geckodriver nem webdriver-manager.
"""
from datetime import datetime
from pathlib import Path

import pytest
from selenium import webdriver

PASTA_EVIDENCIAS = Path(__file__).parent / "evidencias"


def pytest_addoption(parser):
    parser.addoption("--browser", default="chrome", choices=["chrome", "firefox", "edge"])
    parser.addoption("--headless", action="store_true", help="executa sem abrir janela")


def _criar_driver(navegador, headless):
    if navegador == "firefox":
        opcoes = webdriver.FirefoxOptions()
        if headless:
            opcoes.add_argument("-headless")
        driver = webdriver.Firefox(options=opcoes)
    else:
        opcoes = webdriver.EdgeOptions() if navegador == "edge" else webdriver.ChromeOptions()
        if headless:
            opcoes.add_argument("--headless=new")
        opcoes.add_argument("--no-sandbox")
        opcoes.add_argument("--disable-dev-shm-usage")
        # evita o popup "senha vazada" do Chrome, que cobre a pagina
        opcoes.add_experimental_option("prefs", {"profile.password_manager_leak_detection": False})
        driver = webdriver.Edge(options=opcoes) if navegador == "edge" else webdriver.Chrome(options=opcoes)
    driver.set_window_size(1280, 800)
    return driver


@pytest.fixture
def driver(request):
    drv = _criar_driver(request.config.getoption("--browser"), request.config.getoption("--headless"))
    yield drv
    # Evidencia: se a fase "call" do teste falhou, salva um screenshot antes de fechar
    relatorio = getattr(request.node, "rep_call", None)
    if relatorio is not None and relatorio.failed:
        PASTA_EVIDENCIAS.mkdir(exist_ok=True)
        nome = f"{request.node.name}-{datetime.now():%Y%m%d-%H%M%S}.png"
        for c in "[]/\\:":
            nome = nome.replace(c, "_")
        drv.save_screenshot(str(PASTA_EVIDENCIAS / nome))
        print(f"\nScreenshot da falha: evidencias/{nome}")
    drv.quit()


@pytest.hookimpl(hookwrapper=True)
def pytest_runtest_makereport(item, call):
    """Guarda o resultado de cada fase no item, para a fixture saber se falhou."""
    resultado = yield
    relatorio = resultado.get_result()
    setattr(item, f"rep_{relatorio.when}", relatorio)
