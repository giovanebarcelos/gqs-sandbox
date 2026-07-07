#!/usr/bin/env python3
"""
GQS1701 - Primeiro teste automatizado com Selenium WebDriver.
Abre o Chrome, navega ate uma pagina publica e estavel de login
(https://the-internet.herokuapp.com/login), preenche o formulario,
autentica e valida a mensagem de sucesso na area segura.

Dependencias:
    pip install selenium webdriver-manager

Uso:
    python3 GQS1701-Selenium_PrimeiroTeste.py            # abre o Chrome normalmente
    python3 GQS1701-Selenium_PrimeiroTeste.py --headless # sem interface grafica (CI/sandbox)

Credenciais de teste do site (publicas, uso didatico):
    usuario: tomsmith
    senha:   SuperSecretPassword!
"""

import sys

from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service
from selenium.common.exceptions import WebDriverException
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait
from webdriver_manager.chrome import ChromeDriverManager

URL_LOGIN = "https://the-internet.herokuapp.com/login"
USUARIO = "tomsmith"
SENHA = "SuperSecretPassword!"


def criar_driver(headless: bool = False):
    """Configura e instancia o ChromeDriver via webdriver-manager (Selenium 4)."""
    options = Options()
    if headless:
        options.add_argument("--headless=new")
    options.add_argument("--no-sandbox")
    options.add_argument("--disable-dev-shm-usage")
    service = Service(ChromeDriverManager().install())
    return webdriver.Chrome(service=service, options=options)


def executar_teste(headless: bool = False) -> bool:
    """Executa o fluxo de login e retorna True se o teste passou."""
    driver = None
    try:
        driver = criar_driver(headless=headless)
    except WebDriverException as erro:
        print("ERRO: nao foi possivel iniciar o Chrome/ChromeDriver neste ambiente.")
        print(f"Detalhe: {erro}")
        print("Verifique se o Google Chrome (ou Chromium) esta instalado e se ha")
        print("conexao com a internet para o webdriver-manager baixar o driver.")
        return False
    except Exception as erro:  # falha inesperada de setup
        print(f"ERRO inesperado ao configurar o driver: {erro}")
        return False

    try:
        driver.get(URL_LOGIN)
        assert "The Internet" in driver.title

        campo_usuario = driver.find_element(By.ID, "username")
        campo_senha = driver.find_element(By.ID, "password")
        campo_usuario.send_keys(USUARIO)
        campo_senha.send_keys(SENHA)

        driver.find_element(By.CSS_SELECTOR, "button[type='submit']").click()

        # Espera explicita pela mensagem de sucesso na area segura
        mensagem = WebDriverWait(driver, 10).until(
            EC.visibility_of_element_located((By.ID, "flash"))
        )
        assert "You logged into a secure area" in mensagem.text
        assert "/secure" in driver.current_url

        print("Teste de login concluido com sucesso!")
        print(f"URL final: {driver.current_url}")
        print(f"Mensagem: {mensagem.text.strip()}")
        return True
    except Exception as erro:
        print(f"FALHA no teste: {erro}")
        return False
    finally:
        driver.quit()


def main():
    headless = "--headless" in sys.argv
    sucesso = executar_teste(headless=headless)
    sys.exit(0 if sucesso else 1)


if __name__ == "__main__":
    main()
