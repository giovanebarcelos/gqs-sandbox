# class19A — Bruno: Testes de API REST

Material da **Aula 19A**. O passo a passo da aula é a **correção (gabarito) do Tema 3 do Simulado da A1**; a collection `bruno/SimuladoA1` foi executada com `@usebruno/cli` 4.2.1 contra as APIs do simulado (`A1/Simulado/WorkSpace/3-Bruno/`), nos ambientes `Node` e `Python`.

## Como executar

```bash
# Terminal 1 — API Node (A1/Simulado/WorkSpace/3-Bruno/api-node); mudar app.listen(5000) para app.listen(3000) no index.js
npm install && node index.js
# Terminal 2 — API Python (porta 5000)
pip install -r requirements.txt && python app.py

# Terminal 3 — CLI
npm install -g @usebruno/cli
cd bruno/SimuladoA1
bru run Q12 Q13 Q14 -r --env Node                  # gabarito do simulado na API Node
bru run Q12 Q13 Q14 -r --env Python                # o mesmo gabarito na API Python
bru run Q12 Q13 Q14 Encadeamento Contrato -r --env Node --env-var token=abc123
bru run Dados -r --env Node --csv-file-path Dados/dados.csv
```

No `WorkSpace` do simulado, Node e Flask usam a porta 5000; mude o Node para 3000 (ou rode uma API por vez e use só o ambiente `Python`).
