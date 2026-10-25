# Changelog

Formato: [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/) · versões: [SemVer](https://semver.org/lang/pt-BR/).

## [1.0.0] — entrega da A3

### Adicionado
- API de viagens, reservas, avaliações, reputação e relatório de ocupação (RF-01 a RF-08)
- Regras RN-01 a RN-05 com valores-limite testados
- 172 testes pytest (105 unitários, 67 de integração) e coleção Postman com 58 requests e 95 asserções
- Pipeline do GitHub Actions: testes + cobertura, Newman, contêiner e mutação manual
- `docker-compose.yml` com os serviços `api`, `testes`, `api-teste` e `newman`
- Script de mutação com 21 mutantes e script de rastreabilidade casos × testes

### Corrigido
- DEF-001: reservas simultâneas ultrapassavam o número de vagas (agora a contagem e a gravação ocorrem na mesma transação)
- DEF-002: conexões SQLite não eram fechadas
- DEF-003: a alteração de viagem aceitava origem e destino vazios
