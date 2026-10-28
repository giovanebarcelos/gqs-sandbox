"""
Regras de negocio da Biblioteca - VERSAO CORRIGIDA (gabarito da Etapa 10).

Copie para python/biblioteca/regras.py.

    Defeito                         Correcao
    `is` no lugar de `==`           list.remove() compara com == (igualdade)
    senha fixa / SQL concatenado    contar_no_catalogo removido: era codigo morto.
                                    Se fosse necessario: parametros (?) e senha do ambiente
    random previsivel               secrets.randbelow
    complexidade / variaveis mortas calcular_multa com tabela de fatores
    excecao engolida                ler_dias levanta ValueError (vira HTTP 400)
    duplicacao                      relatorio_texto e relatorio_csv usam _montar_relatorio
    token fixo                      TOKEN_ADMIN vem do ambiente (o Sonar NAO apontou este!)
"""

import secrets

MULTA_DIARIA = 1.50
MULTA_MAXIMA = 100
NAO_ENCONTRADO = "Livro nao encontrado"

# tipo de usuario -> (limite de dias, fator ate o limite, fator acima, fator acima e raro)
FATORES = {
    "aluno": (30, 1, 2, 4),
    "professor": (30, 0.5, 1.2, 2.4),
    "visitante": (7, 2, 3, 3),
}


class LivroNaoEncontrado(Exception):
    """O livro pedido nao esta disponivel (vira HTTP 404)."""


class Biblioteca:

    def __init__(self, token_admin=""):
        self.token_admin = token_admin
        self.acervo = [
            "Engenharia de Software Moderna",
            "Clean Code",
            "Test Driven Development",
            "Refactoring",
        ]
        self.emprestados = []

    def emprestar(self, titulo, matricula):
        if not titulo or not matricula or not matricula.strip():
            raise ValueError("Titulo e matricula sao obrigatorios")
        if titulo not in self.acervo:          # `in` e remove() usam ==
            raise LivroNaoEncontrado(NAO_ENCONTRADO)
        self.acervo.remove(titulo)
        self.emprestados.append(titulo)
        return f"EMP-{100000 + secrets.randbelow(900000)}"

    def devolver(self, titulo):
        if titulo not in self.emprestados:
            raise LivroNaoEncontrado(NAO_ENCONTRADO)
        self.emprestados.remove(titulo)
        self.acervo.append(titulo)

    def calcular_multa(self, dias_atraso, tipo_usuario, livro_raro):
        """Multa = dias x 1,50 x fator do tipo de usuario, limitada a R$ 100,00."""
        if dias_atraso <= 0:
            return 0
        base = dias_atraso * MULTA_DIARIA
        if tipo_usuario not in FATORES:
            return min(base, MULTA_MAXIMA)
        limite, ate_limite, acima, acima_raro = FATORES[tipo_usuario]
        if dias_atraso > limite:
            fator = acima_raro if livro_raro else acima
        elif tipo_usuario == "aluno" and dias_atraso > 15:
            fator = 3 if livro_raro else 1.5
        else:
            fator = ate_limite
        return min(base * fator, MULTA_MAXIMA)

    def ler_dias(self, valor):
        """Converte o parametro "dias"; texto invalido vira ValueError (HTTP 400)."""
        try:
            return int(valor)
        except ValueError as erro:
            raise ValueError("O parametro dias deve ser um numero inteiro") from erro

    def token_valido(self, token):
        return bool(self.token_admin) and token == self.token_admin

    def relatorio_texto(self):
        return self._montar_relatorio("RELATORIO DA BIBLIOTECA", ": ", " - ")

    def relatorio_csv(self):
        return self._montar_relatorio("indicador;valor", ";", ";")

    def _montar_relatorio(self, cabecalho, sep_indicador, sep_livro):
        total = len(self.acervo) + len(self.emprestados)
        ocupados = len(self.emprestados)
        taxa = 0 if total == 0 else ocupados * 100.0 / total
        linhas = [
            cabecalho,
            f"Total de livros{sep_indicador}{total}",
            f"Disponiveis{sep_indicador}{len(self.acervo)}",
            f"Emprestados{sep_indicador}{ocupados}",
            f"Taxa de ocupacao{sep_indicador}{taxa:.1f}%",
        ]
        linhas.extend(f"disponivel{sep_livro}{livro}" for livro in self.acervo)
        linhas.extend(f"emprestado{sep_livro}{livro}" for livro in self.emprestados)
        return "\n".join(linhas) + "\n"
