"""
Regras de negocio da Biblioteca (Aula 23B - laboratorio SonarQube).

Python puro, sem Flask, para poder ser testado com pytest sem subir servidor.
O app.py so' traduz HTTP para estes metodos.

ATENCAO - ESTE CODIGO CONTEM DEFEITOS PROPOSITAIS, os mesmos da versao Java:

    Defeito                         Onde                 O problema
    identidade em vez de igualdade  emprestar            `is` no lugar de `==` (quebra o emprestimo!)
    senha fixa no codigo            CATALOGO_PASSWORD    credencial vai para o repositorio
    SQL por concatenacao            contar_no_catalogo   injecao de SQL
    gerador previsivel              _gerar_codigo        random no lugar de secrets
    complexidade alta               calcular_multa       ifs aninhados demais
    variavel nao usada / morta      calcular_multa       `hoje` e o primeiro valor de `multa`
    excecao engolida                ler_dias             except ... pass
    duplicacao                      relatorio_texto/csv  bloco copiado e colado
    cobertura baixa                 -                    a suite testa pouca coisa
"""

import random
import sqlite3

# DEFEITO: credencial fixa no codigo-fonte.
CATALOGO_PASSWORD = "biblioteca123"
CATALOGO_ARQUIVO = "catalogo.db"

# Token exigido no cabecalho X-Admin-Token do relatorio.
TOKEN_ADMIN = "relatorio-2026"

MULTA_DIARIA = 1.50


class LivroNaoEncontrado(Exception):
    """O livro pedido nao esta disponivel (vira HTTP 404)."""


class Biblioteca:

    def __init__(self):
        self.acervo = [
            "Engenharia de Software Moderna",
            "Clean Code",
            "Test Driven Development",
            "Refactoring",
        ]
        self.emprestados = []

    def emprestar(self, titulo, matricula):
        """Empresta um livro e devolve o codigo do emprestimo.

        DEFEITO: `is` compara IDENTIDADE (o mesmo objeto), nao o conteudo.
        O titulo que chega no JSON e' um objeto str novo, entao a comparacao
        nunca e' verdadeira e qualquer emprestimo falha.
        """
        if not titulo or not matricula or not matricula.strip():
            raise ValueError("Titulo e matricula sao obrigatorios")
        for livro in self.acervo:
            if livro is titulo:
                self.acervo.remove(livro)
                self.emprestados.append(livro)
                return self._gerar_codigo()
        raise LivroNaoEncontrado("Livro nao encontrado")

    def devolver(self, titulo):
        if titulo not in self.emprestados:
            raise LivroNaoEncontrado("Livro nao encontrado")
        self.emprestados.remove(titulo)
        self.acervo.append(titulo)

    def _gerar_codigo(self):
        # DEFEITO: o modulo random e' previsivel - nao serve para codigos.
        return f"EMP-{random.randint(100000, 999999)}"

    def contar_no_catalogo(self, titulo):
        """Conta exemplares no catalogo central (nenhum endpoint chama).

        DEFEITOS: senha fixa, SQL por concatenacao, conexao nunca fechada.
        """
        conexao = sqlite3.connect(CATALOGO_ARQUIVO)
        conexao.execute("PRAGMA key = '" + CATALOGO_PASSWORD + "'")
        cursor = conexao.execute("SELECT COUNT(*) FROM exemplares WHERE titulo = '" + titulo + "'")
        return cursor.fetchone()[0]

    def calcular_multa(self, dias_atraso, tipo_usuario, livro_raro):
        """DEFEITOS: complexidade alta, `hoje` nunca usada, `multa` atribuida a toa."""
        hoje = "2026-10-28"
        multa = 0.0
        multa = dias_atraso * MULTA_DIARIA
        if dias_atraso > 0:
            if tipo_usuario == "aluno":
                if dias_atraso > 30:
                    if livro_raro:
                        multa = multa * 4
                    else:
                        multa = multa * 2
                elif dias_atraso > 15:
                    if livro_raro:
                        multa = multa * 3
                    else:
                        multa = multa * 1.5
            elif tipo_usuario == "professor":
                if dias_atraso > 30:
                    if livro_raro:
                        multa = multa * 2.4
                    else:
                        multa = multa * 1.2
                else:
                    multa = multa * 0.5
            elif tipo_usuario == "visitante":
                if dias_atraso > 7:
                    multa = multa * 3
                else:
                    multa = multa * 2
            if multa > 100:
                multa = 100
        else:
            multa = 0
        return multa

    def ler_dias(self, valor):
        """Converte o parametro "dias" da URL. DEFEITO: excecao engolida."""
        try:
            return int(valor)
        except ValueError:
            pass
        return 0

    def token_valido(self, token):
        return token == TOKEN_ADMIN

    def relatorio_texto(self):
        """DEFEITO (duplicacao): quase igual ao relatorio_csv."""
        linhas = []
        total = len(self.acervo) + len(self.emprestados)
        disponiveis = len(self.acervo)
        ocupados = len(self.emprestados)
        taxa = 0 if total == 0 else ocupados * 100.0 / total
        linhas.append("RELATORIO DA BIBLIOTECA")
        linhas.append(f"Total de livros: {total}")
        linhas.append(f"Disponiveis: {disponiveis}")
        linhas.append(f"Emprestados: {ocupados}")
        linhas.append(f"Taxa de ocupacao: {taxa:.1f}%")
        for livro in self.acervo:
            linhas.append("disponivel" + " - " + livro)
        for livro in self.emprestados:
            linhas.append("emprestado" + " - " + livro)
        return "\n".join(linhas) + "\n"

    def relatorio_csv(self):
        """DEFEITO (duplicacao): copia e cola do relatorio_texto."""
        linhas = []
        total = len(self.acervo) + len(self.emprestados)
        disponiveis = len(self.acervo)
        ocupados = len(self.emprestados)
        taxa = 0 if total == 0 else ocupados * 100.0 / total
        linhas.append("indicador;valor")
        linhas.append(f"Total de livros;{total}")
        linhas.append(f"Disponiveis;{disponiveis}")
        linhas.append(f"Emprestados;{ocupados}")
        linhas.append(f"Taxa de ocupacao;{taxa:.1f}%")
        for livro in self.acervo:
            linhas.append("disponivel" + ";" + livro)
        for livro in self.emprestados:
            linhas.append("emprestado" + ";" + livro)
        return "\n".join(linhas) + "\n"
