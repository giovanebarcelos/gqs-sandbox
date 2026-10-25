# AGENTS.md
CaronaCampus (A3 de GQS) — API de caronas em Python 3.12, Flask, SQLite; testes com pytest, Postman/Newman.

## Onde está cada coisa
- Plano: plano-de-teste.md · Casos: casos-de-teste.md · Relatório: relatorio-testes.md
- Testes: tests/unitarios/ e tests/integracao/ · API: postman/
- Pipeline: .github/workflows/ci.yml · Mutação: scripts/mutacao.py

## Regras
- Testes: `docker compose run --rm testes` (ou `pytest --cov`) — rode antes e depois de qualquer mudança; cobertura mínima 70%
- Todo teste cita o caso no nome (`test_ct07_...`) e o caso existe em casos-de-teste.md: `python scripts/matriz_rastreabilidade.py` confere
- Teste novo só vale se falhar quando o código é quebrado de propósito: acrescente o mutante em scripts/mutacao.py
- Nenhum `datetime.now()` fora de api.py (RelogioAjustavel): use o relógio injetado
- Regra de negócio fica em dominio.py e servico.py; api.py só traduz HTTP
- Nenhuma credencial ou dado pessoal real no repositório; usuários de demo são fictícios
- Commits em Conventional Commits; nunca faça push

## Agent skills
(bloco escrito pela skill setup-matt-pocock-skills)
