#!/usr/bin/env python3
"""
GQS0601 - Gerador de Caso de Teste (template IEEE 829).
Cria um template completo de caso de teste.
Uso: python3 GQS0601-GeradorCasoTeste.py
"""


def gerar_caso():
    print("=" * 60)
    print("  GQS0601 - GERADOR DE CASO DE TESTE")
    print("=" * 60)

    caso = {
        "ID": input("ID do caso (ex: CT-001): ") or "CT-001",
        "Título": input("Título: ") or "Validar login com credenciais corretas",
        "Módulo": input("Módulo: ") or "Autenticação",
        "Pré-condição": input("Pré-condição: ") or "Usuário cadastrado como 'joao'",
        "Dados de entrada": input("Dados de entrada: ") or 'usuário: "joao", senha: "123"',
        "Passos": input("Passos (separados por ;): ") or "1. Abrir /login; 2. Preencher campos; 3. Clicar Entrar",
        "Resultado esperado": input("Resultado esperado: ") or "Redirecionar para /dashboard",
        "Pós-condição": input("Pós-condição: ") or "Sessão criada, último_login atualizado",
        "Prioridade": input("Prioridade (Crítica/Alta/Média/Baixa): ") or "Alta",
    }

    print("\n" + "=" * 60)
    print("  CASO DE TESTE GERADO")
    print("=" * 60)
    for chave, valor in caso.items():
        print(f"  {chave}: {valor}")
    print("=" * 60)


if __name__ == "__main__":
    gerar_caso()
