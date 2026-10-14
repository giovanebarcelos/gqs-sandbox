# class19B/postman — Gabarito do Simulado da A1 (Postman)

Correção do Tema 3 do Simulado da A1 feita no Postman (Aula 19B). Testada com Newman 6.2.3 contra as APIs de `A1/Simulado/WorkSpace/3-Bruno/` nos environments `Node` e `Python`.

```bash
# API Node (mudar app.listen(5000) para app.listen(3000) no index.js) e API Python (porta 5000)
npm install -g newman newman-reporter-htmlextra
newman run SimuladoA1.postman_collection.json -e Node.postman_environment.json --env-var token=abc123
newman run SimuladoA1.postman_collection.json -e Python.postman_environment.json
newman run SimuladoA1-Dados.postman_collection.json -e Node.postman_environment.json -d pedidos.csv
```

Pastas: `Q12` (GET /api/status), `Q13` (POST /api/orders 400 e 201), `Q14` (GET /api/ping, POST /api/process), `Encadeamento` (orders → process com `orderId`) e `Contrato` (JSON Schema).
