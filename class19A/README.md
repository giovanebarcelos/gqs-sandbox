# class19A — Bruno: Testes de API REST

Material da **Aula 19A**. A collection foi executada com `@usebruno/cli` 4.2.1 contra as APIs da A1.

## Como executar

```bash
# Terminal 1 — API Node (A1/Prova/WorkSpace/3-Bruno/api-node); ajustar PORT = 3000 no index.js
npm install && npm start
# Terminal 2 — API Python (porta 5000)
pip install -r requirements.txt && python app.py

# Terminal 3 — CLI
npm install -g @usebruno/cli
cd bruno/Biblioteca
bru run Saude Livros Emprestimos -r --env Local --env-var token=abc123
bru run Dados -r --env Local --csv-file-path Dados/dados.csv
```

No `WorkSpace` da A1, Node e Flask usam a porta 5000; mude o Node para 3000 (ou ajuste `environments/Local.bru`).
