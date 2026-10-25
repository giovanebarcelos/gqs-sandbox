# AGENTS.md
Gestão de Tarefas (A3) — Python 3.12, pytest.   <!-- troque pela sua stack: Java 17 + JUnit 5 + Maven -->

## Onde está cada coisa
- Plano: plano-de-teste.md · Casos: casos-de-teste.md · Relatório: relatorio-testes.md
- Testes: tests/unitarios/ e tests/integracao/ · API: postman/
- Pipeline: .github/workflows/ci.yml

## Regras
- Testes: `pytest tests/ -v --cov=src` — rode antes e depois de qualquer mudança (cobertura mínima: 70%)
- Todo teste automatizado cita o ID do caso de teste (CT-01…): rastreabilidade
- Teste novo só vale se falhar quando o código é quebrado de propósito
- Nenhuma credencial ou dado pessoal real no repositório; segredos só no GitHub Secrets
- Commits em Conventional Commits; nunca faça push

## Agent skills
(bloco escrito pela skill setup-matt-pocock-skills)
