#!/usr/bin/env python3
"""
GQS21B01 - Conceitos de automacao mobile (estilo Appium), em Python puro.

O Appium real depende de um servidor Node.js rodando (Appium Server) e de
um driver de plataforma (UiAutomator2 para Android, XCUITest para iOS)
conectado a um emulador/dispositivo real (ver Aula 21B, Slides 6-8).

Este script NAO importa "appium" nem abre um driver de verdade: ele MODELA
a logica central de uma sessao Appium (localizar elemento por resource-id,
clicar, ler texto, aguardar) usando uma "tela simulada" (mock), para que o
aluno entenda o fluxo find -> click -> assert sem precisar de emulador
instalado.

Uso:
    python3 GQS21B01-Appium_Conceitos.py
"""

import time
from dataclasses import dataclass, field
from typing import Optional


@dataclass
class Elemento:
    """Representa um elemento nativo (real ou simulado) identificado por resource-id."""
    resource_id: str
    texto: str = ""
    visivel: bool = True

    def get_text(self) -> str:
        return self.texto


class AndroidDriverSimulado:
    """
    Simula um AndroidDriver do Appium conectado a um app.

    Em vez de se conectar a um Appium Server real via HTTP/WebDriver
    Protocol, mantemos uma "tela simulada": um dicionario que associa
    resource-id a um Elemento, como se a arvore de acessibilidade do
    Android ja tivesse sido consultada (equivalente ao que o driver
    UiAutomator2 faria de verdade).
    """

    def __init__(self, app_package: str, app_activity: str):
        self.app_package = app_package
        self.app_activity = app_activity
        self.em_execucao = False
        self._tela: dict[str, Elemento] = {}

    def registrar_elemento(self, resource_id: str, texto: str = "", visivel: bool = True) -> None:
        """Adiciona um elemento visivel na tela simulada (setup do cenario de teste)."""
        self._tela[resource_id] = Elemento(resource_id, texto, visivel)

    def launch_app(self) -> None:
        self.em_execucao = True
        print(f"  [launchApp] {self.app_package}/{self.app_activity} iniciado")

    def find_element_by_id(self, resource_id: str) -> Optional[Elemento]:
        elemento = self._tela.get(resource_id)
        if elemento is None or not elemento.visivel:
            return None
        return elemento

    def click(self, resource_id: str) -> bool:
        elemento = self.find_element_by_id(resource_id)
        if elemento is None:
            print(f"  [click] FALHOU: elemento '{resource_id}' nao encontrado ou nao visivel")
            return False
        print(f"  [click] '{resource_id}' tocado (texto atual: '{elemento.texto}')")
        return True

    def get_text(self, resource_id: str) -> Optional[str]:
        elemento = self.find_element_by_id(resource_id)
        if elemento is None:
            return None
        return elemento.get_text()

    def wait_for_visible(self, resource_id: str, timeout: float, intervalo: float = 0.2) -> bool:
        """Simula o polling equivalente a um WebDriverWait do Appium."""
        decorrido = 0.0
        while decorrido < timeout:
            elemento = self.find_element_by_id(resource_id)
            if elemento is not None:
                print(f"  [wait] '{resource_id}' visivel apos {decorrido:.1f}s")
                return True
            time.sleep(intervalo)
            decorrido += intervalo
        print(f"  [wait] TIMEOUT: '{resource_id}' nao ficou visivel em {timeout}s")
        return False

    def quit(self) -> None:
        self.em_execucao = False
        print(f"  [quit] sessao encerrada")


def demo_calculadora_12_mais_34():
    """Demonstra o fluxo tipico de automacao Appium: abrir app, tocar botoes, validar resultado."""
    PKG = "com.google.android.calculator"

    driver = AndroidDriverSimulado(app_package=PKG, app_activity=".Calculator")
    driver.launch_app()

    # Cenario: botoes da calculadora presentes e resultado correto
    driver.registrar_elemento(f"{PKG}:id/digit_1", texto="1")
    driver.registrar_elemento(f"{PKG}:id/digit_2", texto="2")
    driver.registrar_elemento(f"{PKG}:id/op_add", texto="+")
    driver.registrar_elemento(f"{PKG}:id/digit_3", texto="3")
    driver.registrar_elemento(f"{PKG}:id/digit_4", texto="4")
    driver.registrar_elemento(f"{PKG}:id/eq", texto="=")
    driver.registrar_elemento(f"{PKG}:id/result_final", texto="46")

    print("=" * 60)
    print("  CENARIO 1: Calculadora 12 + 34 (todos os elementos presentes)")
    print("=" * 60)
    for resource_id in ["digit_1", "digit_2", "op_add", "digit_3", "digit_4", "eq"]:
        driver.click(f"{PKG}:id/{resource_id}")

    resultado = driver.get_text(f"{PKG}:id/result_final")
    if resultado == "46":
        print(f"  PASSOU: resultado = {resultado}")
    else:
        print(f"  FALHOU: esperado 46, obtido {resultado}")

    # Cenario 2: elemento nao encontrado (ex: resource-id mudou entre versoes do app)
    print()
    print("=" * 60)
    print("  CENARIO 2: resource-id incorreto (simula quebra por versao do app)")
    print("=" * 60)
    driver2 = AndroidDriverSimulado(app_package=PKG, app_activity=".Calculator")
    driver2.launch_app()
    driver2.registrar_elemento(f"{PKG}:id/digit_1", texto="1")
    # "digit_2" nao foi registrado -> simula id que mudou de nome em uma nova versao
    sucesso = driver2.click(f"{PKG}:id/digit_2")
    if not sucesso:
        print("  Mitigacao: reconfirmar o resource-id no Appium Inspector")
        print("  apos atualizacao do app (ver Aula 21B, Slide 9).")

    # Cenario 3: wait_for_visible por um elemento que nunca aparece (timeout)
    print()
    print("=" * 60)
    print("  CENARIO 3: waitForVisible por elemento que nunca aparece")
    print("=" * 60)
    driver3 = AndroidDriverSimulado(app_package=PKG, app_activity=".Calculator")
    driver3.launch_app()
    driver3.wait_for_visible(f"{PKG}:id/dialogo_erro", timeout=1.0, intervalo=0.3)

    driver.quit()
    driver2.quit()
    driver3.quit()


def main():
    print("GQS21B01 - Conceitos de Automacao Mobile (modelo do Appium)")
    demo_calculadora_12_mais_34()
    print("\nExecucao concluida sem erros.")


if __name__ == "__main__":
    main()
