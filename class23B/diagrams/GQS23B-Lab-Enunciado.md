# Laboratório GQS23B — SonarQube do zero ao Quality Gate

**Aula 23B — Garantia da Qualidade de Software** | Prof. Giovane Barcelos
**Duração estimada:** 90 a 110 minutos | **Pré-requisitos:** Docker, JDK 17+, Maven 3.8+, Python 3.10+

---

## O que você vai construir

Um servidor SonarQube local analisando dois projetos reais — um em Java, um em Python — com cobertura de testes importada, um Quality Gate próprio e a análise rodando no CI.

O código de partida (`repository/class23B/java` e `repository/class23B/python`) **tem defeitos propositais**. A suíte de testes **passa inteira**. O objetivo do laboratório é justamente mostrar que uma suíte verde não é prova de código são: a análise estática encontra 20 problemas que teste nenhum pegaria.

**Números de referência** (medidos numa execução real com SonarQube Community Build 26.9, JaCoCo 0.8.13, JDK 25 e Maven 3.9.11 — os seus devem bater com estes):

| Métrica | Java, antes | Java, depois | Python, antes |
|---|---|---|---|
| Problemas (issues) | 20 | **0** | 7 |
| Bugs / Vulnerabilidades / Code Smells | 3 / 2 / 15 | 0 / 0 / 0 | 0 / 1 / 6 |
| Cobertura | 44,7% | **84,0%** | 50,0% |
| Linhas duplicadas | 14,3% | **0,0%** | 13,7% |
| Débito técnico | 2h14 | **0** | 53 min |
| Confiabilidade / Segurança / Manutenibilidade | E / E / A | **A / A / A** | A / C / A |
| Quality Gate "GQS — Código Legado" | ✗ ERROR | ✓ **OK** | ✗ ERROR |

---

## Etapa 1 — Subir o SonarQube (15 min)

### 1.1 O jeito rápido (aula)

```bash
docker run -d --name gqs-sonarqube -p 9000:9000 sonarqube:community
```

Banco H2 embutido, dados somem quando o container é removido. Serve para a aula.

### 1.2 O jeito de produção (opcional)

```bash
cp GQS23B-.env.example .env      # e troque a senha dentro do .env
docker compose -f GQS23B-docker-compose.yml up -d
```

SonarQube + PostgreSQL em volumes nomeados: os dados sobrevivem a um `down`.

### 1.3 Esperar ficar pronto

O SonarQube leva de 1 a 3 minutos para subir (ele inicia um Elasticsearch interno). Não adianta abrir o navegador antes:

```bash
curl -s http://localhost:9000/api/system/status
# {"id":"...","version":"26.9.0.129388","status":"STARTING"}
# ... repita até:
# {"id":"...","version":"26.9.0.129388","status":"UP"}
```

**Se o container morrer sozinho**, quase sempre é o limite de memória virtual do Linux. Corrija com:

```bash
sudo sysctl -w vm.max_map_count=524288
sudo sysctl -w fs.file-max=131072
```

### 1.4 Primeiro acesso

Abra <http://localhost:9000> e entre com `admin` / `admin`. O SonarQube **obriga** a trocar a senha no primeiro login, e ela precisa ter **no mínimo 12 caracteres** — anote a nova senha.

**Entregável da etapa:** print da tela inicial do SonarQube já autenticado.

---

## Etapa 2 — Gerar um token de análise (10 min)

O scanner nunca usa login e senha: usa um **token**.

**Pela interface:** clique no avatar (canto superior direito) → *My Account* → *Security* → *Generate Tokens* → tipo *User Token* → *Generate*. **Copie o token agora**: ele não é exibido de novo.

**Pela Web API** (mesma coisa, automatizável):

```bash
curl -s -u admin:'SuaNovaSenha123' -X POST \
  "http://localhost:9000/api/user_tokens/generate" -d "name=lab-gqs23b"
# {"login":"admin","name":"lab-gqs23b","token":"squ_2f8c...","createdAt":"..."}
```

Guarde-o numa variável de ambiente — **nunca** dentro de um arquivo do projeto:

```bash
export SONAR_TOKEN=squ_2f8c...
```

**Entregável:** o token exportado no seu shell (não cole o token no relatório).

---

## Etapa 3 — Analisar o projeto Java (20 min)

```bash
cd repository/class23B/java

# 1) Testes + cobertura. Gera target/site/jacoco/jacoco.xml
mvn clean verify

# 2) Análise. O pom.xml já traz projectKey, projectName e o caminho do jacoco.xml
mvn sonar:sonar -Dsonar.token=$SONAR_TOKEN
```

Ao final, o log imprime:

```
ANALYSIS SUCCESSFUL, you can find the results at: http://localhost:9000/dashboard?id=gqs-biblioteca-java
```

### 3.1 Leia o dashboard e responda

Abra o link. Você deve ver **20 problemas, 44,7% de cobertura e 14,3% de duplicação**. Percorra a aba *Issues* e preencha:

| Regra | Onde está | O que o Sonar diz | Qualidade impactada |
|---|---|---|---|
| `java:S6437` | | | |
| `java:S2095` (2 ocorrências) | | | |
| `java:S2077` | | | |
| `java:S108` | | | |
| `java:S4973` | | | |
| `java:S3776` | | | |
| `java:S1192` | | | |
| `java:S1481` / `java:S1854` | | | |
| `java:S106` (4 ocorrências) | | | |
| `java:S101` / `java:S1220` | | | |

Clique em qualquer problema e depois em *Why is this an issue?* — a explicação da regra, com exemplo de código certo e errado, é parte do produto.

### 3.2 Duas perguntas para pensar antes de corrigir

1. **`java:S4973`** aponta `livro == titulo` na comparação de Strings. Todos os testes passam. **Por que passam?** (Dica: o que o Java faz com literais de String iguais?) E em que situação real esse código quebraria?
2. **`java:S101`** manda renomear a classe `GQS23B01_Biblioteca` e **`java:S1220`** manda movê-la para um pacote. Esses dois "problemas" existem por causa da convenção de nomes do repositório da disciplina. **São defeitos de verdade?** O que fazer com um achado desses num projeto real?

**Entregável:** tabela preenchida + respostas das duas perguntas.

---

## Etapa 4 — Por que o Quality Gate passou? (15 min)

Olhe o topo do dashboard. Apesar dos 20 problemas, o Quality Gate está **Passed**.

Consulte o motivo pela API:

```bash
curl -s -u $SONAR_TOKEN: \
  "http://localhost:9000/api/qualitygates/project_status?projectKey=gqs-biblioteca-java"
# {"projectStatus":{"status":"OK","conditions":[],"caycStatus":"compliant"}}
```

`"conditions":[]` — **nenhuma condição foi avaliada**. O gate padrão "Sonar way" só olha o **New Code** (metodologia *Clean as You Code*), e num projeto recém-importado ainda não há código novo em relação a nada.

Isso não é um defeito do SonarQube: é a estratégia dele para código legado (veja `GQS23B-FluxoCleanAsYouCode.mmd`). Mas, para este laboratório, queremos reprovar o código antigo também.

### 4.1 Criar o gate do curso

```bash
cd ../diagrams
export SONAR_TOKEN=squ_...        # se ainda não estiver exportado
./GQS23B-criar-quality-gate.sh http://localhost:9000 gqs-biblioteca-java
```

O script cria o Quality Gate **"GQS — Código Legado"** com condições sobre o *Overall Code*:

| Métrica | Operador | Limite |
|---|---|---|
| `coverage` | menor que | 80 |
| `duplicated_lines_density` | maior que | 3 |
| `reliability_rating` | pior que | A |
| `security_rating` | pior que | A |
| `sqale_rating` | pior que | A |

> Ao criar um gate, o SonarQube **adiciona sozinho** as condições obrigatórias sobre New Code (`new_violations`, `new_coverage`, `new_duplicated_lines_density`) — é o mínimo *Clean as You Code*. Você verá essas linhas a mais no resultado.

### 4.2 Reanalisar e conferir a reprovação

```bash
cd ../java
mvn sonar:sonar -Dsonar.token=$SONAR_TOKEN
sleep 10
curl -s -u $SONAR_TOKEN: \
  "http://localhost:9000/api/qualitygates/project_status?projectKey=gqs-biblioteca-java"
```

Agora o status é **ERROR**, com `reliability_rating=5` (E), `security_rating=5` (E), `coverage=44.7` e `duplicated_lines_density=14.3` reprovados.

**Entregável:** saída do `project_status` mostrando `"status":"ERROR"` e as condições reprovadas.

---

## Etapa 5 — Corrigir até o gate ficar verde (30 min)

Corrija os 20 problemas **e** eleve a cobertura acima de 80%. Trabalhe em ordem de risco: primeiro Segurança (Blocker), depois Confiabilidade, por último Manutenibilidade.

Roteiro sugerido:

1. **`S6437`** — tire a senha do código; leia de variável de ambiente.
2. **`S2077` + `S2095` + `S108`** — troque a concatenação por `PreparedStatement` com parâmetro ligado, use `try-with-resources` e registre a exceção num logger.
3. **`S4973`** — compare Strings com `equals()`. Escreva **antes** um teste que passe um título montado em tempo de execução (`new StringBuilder("Clean").append(" Code").toString()`): com `==` ele falha, com `equals()` ele passa. É TDD aplicado a um achado de análise estática (Aula 16).
4. **`S1192`** — extraia a constante `LIVRO_NAO_ENCONTRADO`.
5. **`S3776`** — quebre `calcularMulta` em métodos pequenos (`fatorPorPerfil`, `fatorAluno`, `arredondar`). A complexidade cognitiva cai de 35 para 3.
6. **`S1481` / `S1854`** — remova a variável `hoje` e a atribuição morta de `multa`.
7. **Duplicação** — faça `relatorioTexto` e `relatorioCsv` chamarem um único `montarRelatorio(separador, formato)`.
8. **`S106`** — mova o `main` para uma classe `BibliotecaDemo` separada, com logger.
9. **`S101` / `S1220`** — mova para o pacote `com.gqs.biblioteca` e renomeie a classe para `Biblioteca`.
10. **Cobertura** — escreva os testes que faltam. `@ParameterizedTest` com `@CsvSource` cobre os perfis de multa em poucas linhas.

Sobre o `main`: ele não é testável por unidade, e chamá-lo dentro de um teste só para inflar o número seria enganar a métrica. O correto é **declarar a exclusão** e justificá-la:

```xml
<sonar.coverage.exclusions>**/BibliotecaDemo.java</sonar.coverage.exclusions>
```

Rode de novo `mvn clean verify && mvn sonar:sonar -Dsonar.token=$SONAR_TOKEN`. A meta é: **0 problemas, cobertura ≥ 80%, duplicação 0%, ratings A/A/A e Quality Gate `OK`**.

**Entregável:** print do dashboard verde + o `project_status` com `"status":"OK"`.

---

## Etapa 6 — Analisar o projeto Python (20 min)

Aqui o scanner não é um plugin de build: é um binário separado, configurado por `sonar-project.properties`.

```bash
cd repository/class23B/python

# 1) Testes + cobertura em XML (é este arquivo que o Sonar lê)
pip install pytest pytest-cov
pytest --cov=. --cov-report=xml --cov-report=term-missing

# 2) Análise com o scanner CLI em container (não precisa instalar nada)
docker run --rm --network=host \
  -e SONAR_TOKEN=$SONAR_TOKEN \
  -v "$PWD:/usr/src" \
  sonarsource/sonar-scanner-cli
```

Resultado esperado: **7 problemas, 50,0% de cobertura, 13,7% de duplicação**, Segurança com nota **C**.

### 6.1 Comparação entre as duas linguagens

O código Python é o **mesmo programa**, com os **mesmos defeitos**. Preencha:

| Defeito plantado | Regra em Java | Regra em Python | Detectado nos dois? |
|---|---|---|---|
| Senha fixa no código | `S6437` | `S2068` | |
| SQL por concatenação/f-string | `S2077` | — | |
| Exceção engolida | `S108` | `S5754` | |
| Complexidade cognitiva | `S3776` | `S3776` | |
| Literal duplicado | `S1192` | `S1192` | |
| Variável não usada | `S1481` | `S1481` | |
| Argumento default mutável | — | `S5717` | |

**Duas descobertas importantes desta etapa:**

1. **A injeção de SQL no Python não foi acusada.** Não é bug do laboratório: a *taint analysis* (rastrear o dado da entrada do usuário até o `execute`) só existe a partir da **Developer Edition**. O Community Build detecta padrões — senha fixa, `except` vazio —, mas não segue o fluxo do dado. A regra `S2077` que disparou em Java é de outra natureza: ela marca a query formatada dinamicamente, sem rastrear a origem do dado.
2. **Se a constante se chamasse `SENHA_BD` em vez de `DB_PASSWORD`, a regra `S2068` não teria disparado** — a detecção de segredos usa padrões de nome em inglês. Um bom motivo a mais para nomear identificadores em inglês em projeto que passa por SAST.

**Entregável:** tabela preenchida + um parágrafo comentando as duas descobertas.

---

## Etapa 7 — Levar a análise para o CI (15 min)

Copie `GQS23B-github-actions-sonar.yml` para `.github/workflows/qualidade.yml` no seu repositório e cadastre os secrets `SONAR_TOKEN` e `SONAR_HOST_URL` (Settings → Secrets and variables → Actions).

Três detalhes que fazem a diferença entre um workflow que funciona e um que engana:

1. **`fetch-depth: 0` no checkout.** Sem o histórico completo o Sonar não consegue atribuir as linhas aos autores nem calcular o New Code corretamente.
2. **O passo do Quality Gate é obrigatório.** Sem `sonarqube-quality-gate-action`, o workflow envia o relatório e **passa sempre** — vira um painel bonito que não bloqueia nada.
3. **O runner precisa alcançar o servidor.** Um SonarQube em `localhost:9000` na sua máquina não é visível para o runner do GitHub. Use o SonarQube Cloud, um servidor exposto ou um self-hosted runner.

Para Jenkins, use `GQS23B-Jenkinsfile` — lá o `waitForQualityGate abortPipeline: true` faz o mesmo papel, e exige um **webhook** configurado no SonarQube apontando para o Jenkins.

**Entregável:** print da execução do workflow, com o passo do Quality Gate visível.

---

## Encerramento — desmontar o ambiente

```bash
docker stop gqs-sonarqube && docker rm gqs-sonarqube
docker rmi sonarqube:community sonarsource/sonar-scanner-cli   # opcional
# se usou o compose:
docker compose -f GQS23B-docker-compose.yml down -v
```

---

## Critérios de avaliação

| Critério | Peso |
|---|---|
| Etapas 1-3: servidor no ar, token e análise Java concluída, tabela de regras preenchida | 25% |
| Etapa 4: explicação correta de por que o gate padrão passou + gate próprio criado e reprovando | 20% |
| Etapa 5: 0 problemas, cobertura ≥ 80% e gate `OK`, com as correções justificadas | 30% |
| Etapa 6: análise Python e comparação entre as duas linguagens | 15% |
| Etapa 7: workflow de CI com o passo de Quality Gate | 10% |
