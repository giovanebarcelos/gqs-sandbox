# AGENTS.md
<Nome do sistema em teste> — <linguagem e framework de teste, ex.: Python 3.12, pytest>.

## Onde está cada coisa
- Plano de teste: plano-de-teste.md · Casos de teste: casos-de-teste.md
- Testes automatizados: tests/unitarios/ e tests/integracao/
- API: postman/ (ou bruno/) · Performance: jmeter/ · Pipeline: .github/workflows/ci.yml
- Relatório: relatorio-testes.md

## Regras
- Testes: `pytest tests/ -v --cov=src` — rode antes e depois de qualquer mudança (cobertura mínima: 70%)
- Todo teste automatizado cita o ID do caso de teste (CT-01…): rastreabilidade
- Teste novo só vale se falhar quando o código é quebrado de propósito
- Nada de dados pessoais reais nos dados de teste; segredos só no GitHub Secrets
- Commits em Conventional Commits; nunca faça push

## Agent skills
(bloco escrito pela skill setup-matt-pocock-skills)
