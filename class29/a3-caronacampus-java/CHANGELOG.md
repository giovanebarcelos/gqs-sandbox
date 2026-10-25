# Changelog

Formato: [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/) · versões: [SemVer](https://semver.org/lang/pt-BR/).

## [1.0.0] — entrega da A3 (versão Java)

### Adicionado
- Mesma API, regras e coleção Postman da versão Python, em Java 21 (JDK `HttpServer`, SQLite via JDBC)
- 149 testes JUnit 5 (83 unitários, 66 de integração) com Mockito e JaCoCo (mínimo de 70% no `mvn verify`)
- `scripts/mutacao.sh` com 20 mutantes, executado dentro do contêiner
- `docker-compose.yml` com os serviços `api`, `testes`, `api-teste` e `newman`
