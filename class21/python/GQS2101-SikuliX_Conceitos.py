#!/usr/bin/env python3
"""
GQS2101 - Conceitos de automacao visual (estilo SikuliX), em Python puro.

O SikuliX real depende do runtime Jython embutido no proprio SikuliX IDE
(nao existe `pip install sikuli` para CPython - ver Aula 21, Slide 4).
Por isso, este script NAO importa a biblioteca `sikuli`: ele MODELA a
logica central do SikuliX (busca de imagem na tela por similaridade,
click, type, wait, exists) usando uma "tela simulada" (mock), para que o
aluno entenda o algoritmo sem precisar de ambiente grafico.

Uso:
    python3 GQS2101-SikuliX_Conceitos.py
"""

import time
from dataclasses import dataclass
from typing import Optional


@dataclass
class Regiao:
    """Representa a regiao da tela (real ou simulada) onde uma imagem foi encontrada."""
    x: int
    y: int
    largura: int
    altura: int
    similaridade: float

    def centro(self) -> tuple:
        return (self.x + self.largura // 2, self.y + self.altura // 2)


class Settings:
    """Equivalente conceitual de org.sikuli.script.Settings."""
    MinSimilarity = 0.7  # limiar padrao (0.0 a 1.0) - equivalente ao Settings.MinSimilarity do SikuliX


class ImageMatcher:
    """
    Simula o reconhecimento de imagem do SikuliX.

    Em vez de varrer pixels de uma captura de tela real, mantemos um
    "mapa da tela simulada": um dicionario que associa o nome do arquivo
    de imagem de referencia a uma Regiao com uma similaridade pre-calculada
    (como se um algoritmo de correlacao de template ja tivesse rodado).
    Isso reproduz o comportamento observavel de find/click/type/wait/exists
    sem exigir interface grafica.
    """

    def __init__(self, min_similarity: float = None):
        self.min_similarity = (
            min_similarity if min_similarity is not None else Settings.MinSimilarity
        )
        # "tela simulada": imagem de referencia -> (regiao, similaridade encontrada)
        self._tela: dict[str, Regiao] = {}

    def registrar_elemento(self, imagem: str, x: int, y: int, largura: int,
                            altura: int, similaridade: float) -> None:
        """Adiciona um elemento visivel na tela simulada (setup do cenario de teste)."""
        self._tela[imagem] = Regiao(x, y, largura, altura, similaridade)

    def find(self, imagem: str) -> Optional[Regiao]:
        """
        Procura a imagem na tela simulada.
        Retorna a Regiao se a similaridade encontrada for >= min_similarity,
        caso contrario retorna None (equivalente a FindFailed no SikuliX real).
        """
        regiao = self._tela.get(imagem)
        if regiao is None:
            return None
        if regiao.similaridade < self.min_similarity:
            return None
        return regiao

    def exists(self, imagem: str) -> bool:
        return self.find(imagem) is not None

    def click(self, imagem: str) -> bool:
        regiao = self.find(imagem)
        if regiao is None:
            print(f"  [click] FALHOU: '{imagem}' nao encontrada "
                  f"(similaridade minima exigida: {self.min_similarity:.2f})")
            return False
        cx, cy = regiao.centro()
        print(f"  [click] '{imagem}' encontrada em ({cx}, {cy}) "
              f"com similaridade {regiao.similaridade:.2f} -> clique simulado")
        return True

    def type(self, imagem: str, texto: str) -> bool:
        if not self.click(imagem):
            return False
        print(f"  [type] digitando '{texto}' no campo '{imagem}'")
        return True

    def wait(self, imagem: str, timeout: float, intervalo: float = 0.3) -> Optional[Regiao]:
        """
        Simula o polling do wait() do SikuliX: tenta encontrar a imagem
        repetidamente ate o timeout. Aqui nao ha "chegada tardia" real do
        elemento (tela simulada e estatica), mas o laco documenta o
        comportamento esperado do SikuliX real.
        """
        decorrido = 0.0
        while decorrido < timeout:
            regiao = self.find(imagem)
            if regiao is not None:
                print(f"  [wait] '{imagem}' apareceu apos {decorrido:.1f}s")
                return regiao
            time.sleep(intervalo)
            decorrido += intervalo
        print(f"  [wait] TIMEOUT: '{imagem}' nao apareceu em {timeout}s")
        return None


def demo_login_calculadora():
    """Demonstra um fluxo tipico: login simulado + calculo 12 + 34 na calculadora."""
    matcher = ImageMatcher(min_similarity=0.8)

    # Cenario 1: tela de login com boa qualidade de imagem (similaridade alta)
    matcher.registrar_elemento("campo-usuario.png", 100, 200, 150, 30, similaridade=0.95)
    matcher.registrar_elemento("campo-senha.png", 100, 250, 150, 30, similaridade=0.93)
    matcher.registrar_elemento("btn-login.png", 100, 300, 80, 30, similaridade=0.90)
    matcher.registrar_elemento("msg-boas-vindas.png", 100, 350, 200, 20, similaridade=0.85)

    print("=" * 60)
    print("  CENARIO 1: Login (imagens com boa similaridade)")
    print("=" * 60)
    matcher.type("campo-usuario.png", "admin")
    matcher.type("campo-senha.png", "senha123")
    matcher.click("btn-login.png")
    if matcher.exists("msg-boas-vindas.png"):
        print("  Login OK!")
    else:
        print("  Falha no login")

    # Cenario 2: calculadora - imagem de um botao degradada (baixa similaridade,
    # por exemplo por causa de DPI diferente do usado na captura original)
    print()
    print("=" * 60)
    print("  CENARIO 2: Calculadora 12 + 34 (botao '=' com baixa similaridade)")
    print("=" * 60)
    matcher2 = ImageMatcher(min_similarity=Settings.MinSimilarity)
    matcher2.registrar_elemento("btn-1.png", 10, 10, 40, 40, similaridade=0.92)
    matcher2.registrar_elemento("btn-2.png", 60, 10, 40, 40, similaridade=0.91)
    matcher2.registrar_elemento("btn-3.png", 110, 10, 40, 40, similaridade=0.90)
    matcher2.registrar_elemento("btn-4.png", 10, 60, 40, 40, similaridade=0.88)
    matcher2.registrar_elemento("btn-plus.png", 160, 10, 40, 40, similaridade=0.85)
    # Simula um botao "=" cuja imagem de referencia foi capturada em outra
    # resolucao de tela: a similaridade cai abaixo do MinSimilarity padrao.
    matcher2.registrar_elemento("btn-igual.png", 160, 110, 40, 40, similaridade=0.55)

    matcher2.click("btn-1.png")
    matcher2.click("btn-2.png")
    matcher2.click("btn-plus.png")
    matcher2.click("btn-3.png")
    matcher2.click("btn-4.png")
    sucesso = matcher2.click("btn-igual.png")

    if not sucesso:
        print("\n  Mitigacao: reduzir Settings.MinSimilarity ou recapturar a imagem")
        print("  'btn-igual.png' na resolucao/DPI atual do ambiente de teste.")
        matcher2.min_similarity = 0.5
        print(f"  Novo MinSimilarity = {matcher2.min_similarity:.2f}")
        matcher2.click("btn-igual.png")

    # Cenario 3: wait() por um elemento ausente (demonstra timeout)
    print()
    print("=" * 60)
    print("  CENARIO 3: wait() por elemento que nunca aparece")
    print("=" * 60)
    matcher3 = ImageMatcher()
    matcher3.wait("dialogo-carregamento-fim.png", timeout=1.0, intervalo=0.3)


def main():
    print("GQS2101 - Conceitos de Automacao Visual (modelo do SikuliX)")
    demo_login_calculadora()
    print("\nExecucao concluida sem erros.")


if __name__ == "__main__":
    main()
