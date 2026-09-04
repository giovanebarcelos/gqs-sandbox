#!/usr/bin/env python3
"""
GQS23B01 - Sistema de emprestimo de uma biblioteca (versao Python).

ATENCAO - ESTE CODIGO CONTEM DEFEITOS PROPOSITAIS.

E o equivalente Python do `java/src/main/java/GQS23B01-Biblioteca.java`: os
mesmos defeitos, escritos de forma idiomatica em Python, para que o aluno
compare como o SonarQube reporta o MESMO problema em duas linguagens
(Etapa 6 do laboratorio da Aula 23B).

Mapa dos defeitos plantados:
  S2068/S6437  Credencial fixa no codigo         -> DB_PASSWORD
  S3649        SQL montado por interpolacao      -> buscar_por_titulo
  S5754/S110   except generico que engole o erro -> buscar_por_titulo
  S3776        Complexidade cognitiva alta       -> calcular_multa
  S1192        Literal duplicado 3x ou mais      -> "Livro nao encontrado"
  S1481        Variavel local nao utilizada      -> calcular_multa
  S5717        Argumento default mutavel         -> registrar_historico
  (dup)        Bloco duplicado                   -> relatorio_texto/csv

Uso:
    python3 GQS23B01-Biblioteca.py

Cobertura (a partir desta pasta):
    pytest --cov=. --cov-report=xml --cov-report=term-missing
"""
import sqlite3

# DEFEITO: credencial fixa no codigo-fonte
DB_PASSWORD = "biblioteca123"

CAMINHO_BD = "biblioteca.db"
MULTA_DIARIA = 1.50


class Biblioteca:
    """Controla o acervo e os emprestimos de uma biblioteca."""

    def __init__(self):
        self.acervo = [
            "Engenharia de Software Moderna",
            "Clean Code",
            "Test Driven Development",
        ]
        self.emprestados = []

    def buscar_por_titulo(self, titulo):
        """DEFEITOS: SQL por interpolacao (injecao) e except generico vazio."""
        try:
            conexao = sqlite3.connect(CAMINHO_BD)
            cursor = conexao.cursor()
            cursor.execute(f"SELECT titulo FROM livros WHERE titulo = '{titulo}'")
            linha = cursor.fetchone()
            if linha:
                return linha[0]
        except:
            pass
        return "Livro nao encontrado"

    def emprestar(self, titulo, matricula):
        """Registra o emprestimo de um livro do acervo."""
        if titulo is None or matricula is None:
            raise ValueError("Titulo e matricula sao obrigatorios")
        if titulo in self.acervo:
            self.acervo.remove(titulo)
            self.emprestados.append(titulo)
            return "Emprestimo registrado: " + titulo
        return "Livro nao encontrado"

    def devolver(self, titulo):
        """Devolve um livro ao acervo."""
        if titulo in self.emprestados:
            self.emprestados.remove(titulo)
            self.acervo.append(titulo)
            return "Devolucao registrada: " + titulo
        return "Livro nao encontrado"

    def calcular_multa(self, dias_atraso, tipo_usuario, livro_raro):
        """DEFEITOS: complexidade cognitiva alta e variavel nao utilizada."""
        hoje = "2026-09-03"
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
        return round(multa, 2)

    def registrar_historico(self, titulo, historico=[]):
        """DEFEITO S5717: lista mutavel como valor default do argumento."""
        historico.append(titulo)
        return historico

    def relatorio_texto(self):
        """DEFEITO (duplicacao): quase identico a relatorio_csv."""
        linhas = []
        linhas.append("=========================================")
        linhas.append("RELATORIO DO ACERVO - BIBLIOTECA CENTRAL")
        linhas.append("=========================================")
        linhas.append("Disponiveis: " + str(len(self.acervo)))
        linhas.append("Emprestados: " + str(len(self.emprestados)))
        linhas.append("Total geral: " + str(len(self.acervo) + len(self.emprestados)))
        linhas.append("Multa diaria vigente: " + str(MULTA_DIARIA))
        linhas.append("-----------------------------------------")
        contador = 0
        for livro in self.acervo:
            contador = contador + 1
            linhas.append(str(contador) + ". " + livro + " [DISPONIVEL]")
        for livro in self.emprestados:
            contador = contador + 1
            linhas.append(str(contador) + ". " + livro + " [EMPRESTADO]")
        linhas.append("-----------------------------------------")
        linhas.append("Itens listados: " + str(contador))
        return "\n".join(linhas)

    def relatorio_csv(self):
        """DEFEITO (duplicacao): quase identico a relatorio_texto."""
        linhas = []
        linhas.append("=========================================")
        linhas.append("RELATORIO DO ACERVO - BIBLIOTECA CENTRAL")
        linhas.append("=========================================")
        linhas.append("Disponiveis: " + str(len(self.acervo)))
        linhas.append("Emprestados: " + str(len(self.emprestados)))
        linhas.append("Total geral: " + str(len(self.acervo) + len(self.emprestados)))
        linhas.append("Multa diaria vigente: " + str(MULTA_DIARIA))
        linhas.append("-----------------------------------------")
        contador = 0
        for livro in self.acervo:
            contador = contador + 1
            linhas.append(str(contador) + "; " + livro + "; DISPONIVEL")
        for livro in self.emprestados:
            contador = contador + 1
            linhas.append(str(contador) + "; " + livro + "; EMPRESTADO")
        linhas.append("-----------------------------------------")
        linhas.append("Itens listados; " + str(contador))
        return "\n".join(linhas)

    def total_disponivel(self):
        return len(self.acervo)


def main():
    biblioteca = Biblioteca()
    print("Acervo inicial:", biblioteca.total_disponivel())
    print(biblioteca.emprestar("Clean Code", "2026001"))
    print("Multa (10 dias, aluno): R$", biblioteca.calcular_multa(10, "aluno", False))
    print(biblioteca.relatorio_texto())


if __name__ == "__main__":
    main()
