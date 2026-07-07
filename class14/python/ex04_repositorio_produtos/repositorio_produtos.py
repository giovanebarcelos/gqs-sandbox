"""Exercício 04 — Repositório de Produtos (em memória).

Usado para demonstrar fixtures com `yield` (setup + teardown), como no
Slide 7 da Aula 14 (exemplo `banco_de_dados`). Aqui o "banco" é uma
estrutura em memória, mas o padrão (conectar/preparar -> yield -> liberar)
é o mesmo usado com bancos de dados, arquivos temporários, servidores de
teste, etc.
"""


class RepositorioProdutos:
    """Repositório simples de produtos, mantido em memória.

    Simula um recurso que precisa de setup (conexão/preparação) e
    teardown (liberação) explícitos — por isso expõe `fechar()`.
    """

    def __init__(self):
        self._produtos = {}
        self._aberto = True

    def adicionar(self, nome: str, preco: float) -> None:
        self._verificar_aberto()
        self._produtos[nome] = preco

    def remover(self, nome: str) -> None:
        self._verificar_aberto()
        del self._produtos[nome]

    def buscar(self, nome: str):
        self._verificar_aberto()
        return self._produtos.get(nome)

    def listar(self):
        self._verificar_aberto()
        return sorted(self._produtos.keys())

    def total_produtos(self) -> int:
        self._verificar_aberto()
        return len(self._produtos)

    def fechar(self) -> None:
        """Teardown: libera o recurso. Após chamado, o repositório não
        pode mais ser usado (simula fechar uma conexão de banco)."""
        self._produtos.clear()
        self._aberto = False

    def esta_aberto(self) -> bool:
        return self._aberto

    def _verificar_aberto(self):
        if not self._aberto:
            raise RuntimeError("Repositório já foi fechado (teardown já executado)")
