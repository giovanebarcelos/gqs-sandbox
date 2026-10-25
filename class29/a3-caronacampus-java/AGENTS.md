# AGENTS.md
CaronaCampus (A3 de GQS, versão Java) — API de caronas em Java 21, JDK HttpServer, SQLite (JDBC); testes com JUnit 5, Mockito e JaCoCo; Postman/Newman.

## Onde está cada coisa
- Regras: src/main/java/br/edu/carona/Regras.java · Casos de uso: ServicoCarona.java · Persistência: RepositorioSqlite.java · HTTP: Api.java
- Testes: src/test/java/br/edu/carona/ · API: postman/ (a MESMA coleção do exemplo Python)
- Plano, casos e relatório de testes: neste repositório (adaptados da versão Python)

## Regras
- Testes: `docker compose run --rm testes` (`mvn verify`: JUnit + JaCoCo com mínimo de 70%) — rode antes e depois de qualquer mudança
- Todo teste cita o caso no nome do método (`ct07_...`) e o caso existe em casos-de-teste.md
- Teste novo só vale se falhar quando o código é quebrado de propósito: acrescente o mutante em scripts/mutacao.sh
- Nenhum `LocalDateTime.now()` fora de RelogioAjustavel: use o relógio injetado
- Regra de negócio fica em Regras.java e ServicoCarona.java; Api.java só traduz HTTP
- Nenhuma credencial ou dado pessoal real no repositório; usuários de demo são fictícios
- Commits em Conventional Commits; nunca faça push

## Agent skills
(bloco escrito pela skill setup-matt-pocock-skills)
