#!/usr/bin/env python3
"""
GQS0401 - Gerador de Estrutura de Plano de Teste (IEEE 829).
Gera um template completo de plano de teste.
Uso: python3 GQS0401-GeradorPlanTeste.py
"""

TEMPLATE = """# Plano de Teste - {projeto}

**Versão:** 1.0 | **Data:** {data} | **Responsável:** {responsavel}

## 1. Identificador
PT-{projeto}-v1.0

## 2. Itens de Teste
{itens}

## 3. Funcionalidades a Testar
{funcionalidades_testar}

## 4. Funcionalidades a NÃO Testar
{funcionalidades_nao_testar}

## 5. Abordagem
- Níveis: {niveis}
- Técnicas: {tecnicas}
- Ferramentas: {ferramentas}

## 6. Critérios de Aprovação/Reprovação
{ criterios_aprovacao}

## 7. Critérios de Suspensão e Retomada
{suspensao}

## 8. Entregáveis
{entregaveis}

## 9. Recursos
{recursos}

## 10. Cronograma
{cronograma}

## 11. Riscos
{riscos}
"""


def gerar_plano():
    from datetime import date
    projeto = input("Nome do projeto: ") or "MeuProjeto"
    responsavel = input("Responsável: ") or "Eng. QA"
    data = date.today().isoformat()

    plano = TEMPLATE.format(
        projeto=projeto,
        data=data,
        responsavel=responsavel,
        itens="- Módulo de autenticação\n- Módulo de cadastro",
        funcionalidades_testar="- Login\n- Cadastro de usuário\n- Recuperação de senha",
        funcionalidades_nao_testar="- Relatórios (terceirizado)\n- Performance (fase separada)",
        niveis="Unitário, Integração, Sistema",
        tecnicas="Caixa-preta (partição de equivalência, BVA)",
        ferramentas="JUnit 5, pytest, Selenium",
        criterios_aprovacao="- 95% dos casos de teste passando\n- Nenhum bug crítico em aberto",
        suspensao="- Se ambiente de teste ficar indisponível por mais de 4h",
        entregaveis="- Plano de Teste\n- Casos de Teste\n- Relatório de Execução",
        recursos="- 1 Analista de Teste\n- 1 Engenheiro de Automação\n- Servidor Staging",
        cronograma="- Semana 1: Planejamento\n- Semana 2: Design\n- Semana 3-4: Execução",
        riscos="- Risco: indisponibilidade do ambiente\n- Mitigação: ambiente reserva em cloud",
    )
    print("\n" + "=" * 60)
    print(plano)
    print("=" * 60)


if __name__ == "__main__":
    print("GQS0401 - Gerador de Plano de Teste (IEEE 829)")
    gerar_plano()
