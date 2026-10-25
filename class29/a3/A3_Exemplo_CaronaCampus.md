# A3 — Projeto Exemplo: CaronaCampus  

> **Documento de referência.** Este projeto é um exemplo completo de A3 para a disciplina de Garantia da Qualidade de Software. Ele mostra o nível de profundidade esperado em cada critério da rubrica. Use-o como guia, **não como modelo a ser copiado**: o seu projeto deve refletir o sistema, as regras e as decisões da sua dupla.  
>  
> **O que é real e o que é ilustrativo.** O código, os 172 testes, a coleção Postman, os números de cobertura, de mutação e de desempenho, os defeitos e as falhas de teste descritos abaixo foram **produzidos e executados de verdade** (repositório completo em [`a3-caronacampus`](https://github.com/giovanebarcelos/gqs-sandbox/tree/main/class29/a3-caronacampus)). A dupla, os nomes, as datas e o histórico de autores do `git log` são **fictícios**: o histórico foi **reconstruído em ordem lógica** a partir dos estados reais do código (inclusive os estados com defeito), e nem todo commit intermediário tem a suíte verde. Os registros de uso de IA misturam erros reais da construção deste exemplo (marcados) e linhas ilustrativas. O exemplo foi elaborado pelo professor com apoio de IA (Claude Code); no seu projeto, a seção **Uso de IA** deve contar o que realmente aconteceu.  
>  
> **O que NÃO foi executado:** o workflow do GitHub Actions. O repositório de referência vive numa subpasta de outro repositório, e o Actions só lê `.github/workflows` da raiz. Todos os passos do workflow foram executados à mão com os mesmos comandos (seção 6.3). **No seu projeto, a execução verde na aba Actions é obrigatória.**  

---  

## Identificação do Projeto  

| Campo | Conteúdo |  
|---|---|  
| **Título** | CaronaCampus — caronas solidárias entre alunos do mesmo campus |  
| **Dupla** | Fred · Wilma (e Barney · Betty, num exemplo com 4 integrantes para o histórico) |  
| **Período** | 2026/2 |  
| **Sistema testado** | API REST de viagens, reservas, avaliações e relatório, com 5 regras de negócio |  
| **Linguagem e stack** | Python 3.12 · Flask · SQLite · pytest · pytest-cov · Postman/Newman · Docker Compose · GitHub Actions |  
| **Versão em Java** | Java 21 · JDK `HttpServer` · SQLite/JDBC · JUnit 5 · Mockito · JaCoCo (seção 12) |  
| **Repositório** | `github.com/<dupla>/caronacampus` (tag `v1.0.0`) — exemplo em [`a3-caronacampus`](https://github.com/giovanebarcelos/gqs-sandbox/tree/main/class29/a3-caronacampus) |  

---  

## 1. O Sistema Alvo e a Escolha do Tema  

### 1.1 O sistema  

Alunos do mesmo campus oferecem e dividem caronas. O **motorista** cria uma viagem (origem, destino, saída, vagas, custo); o **passageiro** reserva uma vaga; o preço é dividido; cancelar em cima da hora custa uma taxa; motoristas muito mal avaliados são suspensos. O sistema é uma **API HTTP** com SQLite, com `593` linhas de código-fonte: pequeno **de propósito**. O esforço do projeto está em **testá-lo**, não em programá-lo.  

### 1.2 Regras de negócio (as que dão o que testar)  

| ID | Regra | Valores-limite |  
|---|---|---|  
| RN-01 | De 1 a 6 vagas; não se reserva além da lotação | 0, 1, 6, 7 · a última vaga e a seguinte |  
| RN-02 | Saída entre 30 min e 14 dias à frente | 29 min 59 s, 30 min, 14 dias, 14 dias + 1 s |  
| RN-03 | Preço por vaga = custo ÷ (vagas + 1), arredondado **para cima**, entre R$ 2,00 e R$ 25,00 | piso, teto, R$ 10,00 ÷ 3 = R$ 3,34 |  
| RN-04 | Cancelar com ≥ 60 min: sem taxa · com menos: 50% · depois da saída: proibido | 60 min 00 s, 59 min 59 s, 0 s |  
| RN-05 | Com 5+ avaliações e média < 3,0, o motorista é suspenso | 4 avaliações, 5 avaliações, média exata 3,0 |  

### 1.3 Verificação do tema contra os critérios da A3  

| Critério | Como o CaronaCampus atende |  
|---|---|  
| **Regras de negócio** | 5 regras com número e valores-limite, cada uma com um "lado de cá" e um "lado de lá" do limite |  
| **Interface testável por API** | 13 rotas (uma delas só no modo de teste), com CRUD de viagens |  
| **Persistência** | SQLite, com restrições (`CHECK`, chave estrangeira, índice único parcial) que também são testadas |  
| **Código de vocês** | Escrito pela dupla, o que permite quebrá-lo de propósito |  
| **Tamanho certo** | 8 requisitos funcionais |  
| **Originalidade** | Não é Gestão de Tarefas, nem o checkout do delivery do Projeto Parcial A1 |  

---  

## 2. Plano de Teste  

*Arquivo `plano-de-teste.md` do repositório, na íntegra.*  

### 2.1 Identificação  

| Campo | Conteúdo |  
|---|---|  
| **Sistema sob teste** | CaronaCampus — API de caronas solidárias entre alunos do mesmo campus |  
| **Versão testada** | `v1.0.0` (Python 3.12, Flask 3.1, SQLite) |  
| **Responsável** | Grupo (4 integrantes) · revisão cruzada entre duplas |  
| **Documentos relacionados** | `casos-de-teste.md`, `relatorio-testes.md`, `postman/`, `.github/workflows/ci.yml` |  

### 2.2 Escopo  

**Dentro do escopo (o que será testado):**  

- As **cinco regras de negócio** (RN-01 a RN-05), nos valores-limite  
- O ciclo das viagens e reservas pela **API HTTP** (CRUD, reserva, cancelamento, avaliação, relatório)  
- **Autenticação** por token e permissões (quem pode alterar o quê)  
- **Persistência** em SQLite e **concorrência** na última vaga  
- Execução em **contêiner** (`docker compose`) e no pipeline de CI  

**Fora do escopo (e por quê):**  

| Item | Motivo |  
|---|---|  
| Interface gráfica | O sistema entregue é uma API; não há tela |  
| Pagamento real | Os valores são apenas calculados; não há cobrança |  
| Desempenho sob carga (JMeter) | Medimos só o RNF-01 com um script simples; carga contínua fica para a próxima versão |  
| Segurança avançada (OWASP, força bruta, expiração de token) | Declarado como risco aceito (R-04) |  
| Aplicativo móvel | Não existe nesta versão |  

### 2.3 Itens a testar (requisitos)  

#### 2.3.1 Funcionais  

| ID | Requisito | Prioridade |  
|---|---|---|  
| RF-01 | Criar viagem (origem, destino, saída, vagas, custo) | Alta |  
| RF-02 | Consultar, listar, alterar e cancelar viagem | Alta |  
| RF-03 | Reservar uma vaga em uma viagem | Alta |  
| RF-04 | Cancelar a própria reserva (com taxa quando em cima da hora) | Alta |  
| RF-05 | Avaliar o motorista (nota de 1 a 5) após a viagem | Média |  
| RF-06 | Consultar a reputação do motorista | Média |  
| RF-07 | Relatório de ocupação das viagens abertas | Baixa |  
| RF-08 | Autenticar com matrícula e senha | Alta |  

#### 2.3.2 Regras de negócio  

| ID | Regra | Valores-limite a testar |  
|---|---|---|  
| RN-01 | Uma viagem tem de 1 a 6 vagas; não se reserva além da lotação | 0, 1, 6, 7 vagas; a última vaga e a seguinte |  
| RN-02 | A saída fica entre 30 min e 14 dias à frente (limites inclusivos) | 29 min 59 s, 30 min, 14 dias, 14 dias + 1 s |  
| RN-03 | Preço por vaga = custo ÷ (vagas + 1), arredondado **para cima** ao centavo, entre R$ 2,00 e R$ 25,00 | Piso, teto, arredondamento (R$ 10,00 ÷ 3 = R$ 3,34) |  
| RN-04 | Cancelar com ≥ 60 min de antecedência: sem taxa; com menos: taxa de 50%; após a saída: proibido | 60 min 00 s, 59 min 59 s, 0 s |  
| RN-05 | Com 5 ou mais avaliações, média < 3,0 suspende o motorista (não cria viagem) | 4 avaliações, 5 avaliações, média exata 3,0 |  

#### 2.3.3 Não funcionais  

| ID | Requisito (mensurável) | Como medir |  
|---|---|---|  
| RNF-01 | `GET /viagens` com 1.000 viagens responde em ≤ 300 ms (p95) | `scripts/medir_desempenho.py` |  
| RNF-02 | Todo endpoint, exceto `/saude` e `/auth/login`, exige token; senhas guardadas com hash | Testes CT-15 (parametrizado nos 10 endpoints) |  
| RNF-03 | Valores monetários em `Decimal`, 2 casas, sem erro de ponto flutuante | CT-04, CT-09 |  
| RNF-04 | `docker compose up --build` e `docker compose run --rm testes` funcionam numa máquina só com Docker | Execução em cópia limpa |  
| RNF-05 | Nunca existem mais reservas ativas que vagas, mesmo com requisições simultâneas | CT-06 (6 threads na última vaga) |  

### 2.4 Estratégia de teste  

**Pirâmide (Aula 05):** muitos testes unitários rápidos, poucos de integração, uma coleção de API.  

| Nível | O que cobre | Ferramenta | Pasta |  
|---|---|---|---|  
| **Unitário** | Regras puras (limites) e o serviço isolado, com repositório e relógio substituídos por dublês | pytest, `unittest.mock` | `tests/unitarios/` |  
| **Integração** | Serviço + SQLite real; API Flask + SQLite; concorrência | pytest, cliente de teste do Flask | `tests/integracao/` |  
| **API (sistema)** | Contrato HTTP, fluxos completos, mensagens de erro | Postman + Newman | `postman/` |  
| **Qualidade do teste** | Os testes detectam defeito? | Mutação manual (21 mutantes) | `scripts/mutacao.py` |  

**Técnicas de projeto de casos (Aulas 08 e 10):** partição de equivalência e **análise de valor-limite** para RN-01 a RN-05; **tabela de decisão** para as reservas inválidas (CT-07); **transição de estados** para a reserva (ativa → cancelada); **baseada em risco** para priorizar concorrência e autenticação.  

**Relógio controlado:** nenhuma regra chama `datetime.now()`. Os testes injetam um relógio fixo, o que torna os limites de minutos testáveis sem `sleep`.  

### 2.5 Critérios  

| Tipo | Critério |  
|---|---|  
| **Entrada** | Código compilando/importando; ambiente Docker disponível; casos de teste revisados por outra dupla |  
| **Saída (aprovação)** | 100% dos casos executados · 0 defeitos de severidade Alta ou Crítica abertos · cobertura de instrução ≥ 70% (meta 90%) · pipeline verde · coleção Newman sem falhas |  
| **Suspensão** | Ambiente não sobe, ou falha em mais de 30% dos testes por causa de um único defeito de ambiente |  
| **Retomada** | Ambiente corrigido e smoke test (`/saude` + login) verde |  

### 2.6 Ambiente e ferramentas  

| Item | Versão |  
|---|---|  
| Contêiner | `python:3.12-slim`, Docker Compose |  
| Biblioteca de testes | pytest 9, pytest-cov 7 |  
| Testes de API | Postman Collection v2.1, Newman (imagem `postman/newman`) |  
| Pipeline | GitHub Actions (`.github/workflows/ci.yml`) |  
| Dados | Usuários fictícios criados na montagem (`2026001`, `2026002`, `2026003`); nenhum dado pessoal real |  

### 2.7 Riscos de teste  

| ID | Risco | Prob. | Impacto | Mitigação |  
|---|---|---|---|---|  
| R-01 | Testes de tempo dependem do relógio real e ficam instáveis | Alta | Alto | Relógio injetado; modo de teste com `/teste/avancar-relogio` (desligado fora do compose de testes) |  
| R-02 | Condição de corrida na última vaga passa despercebida | Média | Alto | Teste de concorrência com 6 threads e barreira; repetido 200 vezes após a correção |  
| R-03 | Cobertura alta com testes fracos (assertions vazias) | Média | Alto | Teste de mutação manual e revisão das asserções |  
| R-04 | Falhas de segurança fora do escopo (token sem expiração) | Alta | Médio | Declarado no relatório como risco aceito; recomendado para a próxima versão |  
| R-05 | Dublê de repositório divergir do SQLite real | Média | Médio | O mesmo comportamento é conferido nos testes de integração |  

### 2.8 Cronograma e responsabilidades  

| Semana | Atividade | Quem |  
|---|---|---|  
| 1 | Plano de teste e levantamento de requisitos testáveis | Todos |  
| 2 | Casos de teste e revisão cruzada | Duplas A e B |  
| 3 | Testes unitários e de integração | Dupla A |  
| 4 | Coleção Postman/Newman e pipeline | Dupla B |  
| 5 | Mutação, relatório e ensaio da demonstração | Todos |  

### 2.9 Métricas coletadas  

Casos executados/aprovados, testes automatizados, cobertura de instrução e de ramo, mutantes detectados, defeitos por severidade, densidade de defeitos (por 100 linhas de código-fonte), tempo de execução da suíte e do Newman.  

---  

## 3. Casos de Teste  

*Arquivo `casos-de-teste.md` do repositório, na íntegra. São 16 casos (a rubrica exige 15).*  

| ID | Requisito | Técnica | Pré-condição | Dados e passos | Resultado esperado | Automação |  
|---|---|---|---|---|---|---|  
| **CT-01** | RF-01, RF-02 | Partição de equivalência (classe válida) | Ana autenticada | 1. Criar viagem `Campus → Centro`, saída em 3 h, 3 vagas, R$ 12,00. 2. Consultar por id e listar | 201; viagem devolvida com `valor_por_vaga = 3.00`; aparece na listagem; origem/destino vazios ou iguais, custo ≤ 0 e corpo ausente → 400 | pytest + Postman |  
| **CT-02** | RN-01 | Valor-limite | Ana autenticada | Criar viagens com 0, 1, 6 e 7 vagas (e vagas não inteiras) | 1 e 6 aceitas; 0, 7, `2.5`, `"3"`, `true` → 400 `dado_invalido` | pytest + Postman |  
| **CT-03** | RN-02 | Valor-limite | Ana autenticada | Saída em 29 min 59 s, 30 min, 14 dias e 14 dias + 1 s; saída no passado | 30 min e 14 dias aceitas (limites inclusivos); 29 min 59 s, 14 dias + 1 s e passado → 400 | pytest + Postman |  
| **CT-04** | RN-03, RNF-03 | Partição + valor-limite | — | Custo ÷ (vagas + 1): 12,00/3 vagas; 10,00/2; 10,01/4; 2,00/6; 100,00/1; 50,00/1; 4,00/1; 3,98/1; custo 0 e negativo | 3,00 · 3,34 (para cima) · 2,01 · 2,00 (piso) · 25,00 (teto) · 25,00 · 2,00 · 2,00; custo ≤ 0 → 400; tipo `Decimal` com 2 casas | pytest + Postman |  
| **CT-05** | RF-03 | Partição (classe válida) | Viagem de 3 vagas da Ana; Bruno autenticado | Bruno reserva a viagem | 201; `valor = 3.00`, `cancelada = false` | pytest + Postman |  
| **CT-06** | RN-01, RNF-05 | Valor-limite + concorrência | Viagem de 3 vagas | Reservar 3 vezes (3 passageiros); tentar a 4ª; cancelar uma e reservar de novo; 6 threads disputando a **única** vaga | 3 aceitas; 4ª → 409 "viagem lotada"; vaga liberada é reocupável; na disputa exatamente 1 vence e 5 recebem 409 | pytest + Postman |  
| **CT-07** | RF-03 | Tabela de decisão | Viagem aberta | Reservar: (a) a própria viagem; (b) duas vezes o mesmo passageiro; (c) viagem que já saiu; (d) viagem inexistente; (e) depois de cancelar a anterior | (a), (b), (c) → 409 sem gravar nada; (d) → 404; (e) aceita | pytest + Postman |  
| **CT-08** | RN-04 | Valor-limite | Reserva de R$ 3,00 | Cancelar faltando 5 h, 61 min e **exatamente 60 min** | Taxa `0.00` nos três; reserva marcada como cancelada e gravada | pytest + Postman |  
| **CT-09** | RN-04, RNF-03 | Valor-limite | Reserva de R$ 3,00 | Cancelar faltando 59 min 59 s, 30 min e 1 s; reserva de R$ 3,01 | Taxa `1.50` (50%); R$ 3,01 → `1.51` (meio centavo para cima) | pytest + Postman |  
| **CT-10** | RN-04, RF-04 | Valor-limite + transição de estados | Reserva ativa | Cancelar na hora exata da saída e depois; cancelar reserva alheia; cancelar duas vezes; reserva inexistente | Saída ou depois → 409 e a reserva **continua ativa**; alheia → 403; repetida → 409; inexistente → 404 | pytest + Postman |  
| **CT-11** | RF-05 | Partição + tabela de decisão | Viagem com reserva de Bruno | Avaliar: antes da saída; nota 0/6/3.5/texto; por quem não viajou; por quem cancelou a reserva; duas vezes | Só passageiro com reserva **ativa**, depois da saída, nota inteira de 1 a 5 e uma vez por viagem → 201; demais → 400/403/409 | pytest + Postman |  
| **CT-12** | RN-05, RF-06 | Valor-limite | Ana com viagens concluídas | Notas: 4 avaliações péssimas; 5 avaliações com média 2,8; 5 notas 3 (média exata 3,0); 6 avaliações com média 2,5 | Suspensa somente com ≥ 5 avaliações **e** média < 3,0; reputação informa `avaliacoes`, `media` e `suspenso` | pytest + Postman |  
| **CT-13** | RN-05, RF-01 | Transição de estados | Ana suspensa (CT-12) | Ana tenta criar viagem; depois, Ana com média exata 3,0 tenta criar | Suspensa → 403 `motorista_suspenso`; média 3,0 → 201 | pytest + Postman |  
| **CT-14** | RF-02 | Partição + tabela de decisão | Viagem da Ana | Alterar vagas/custo/destino/saída; alterar por outro usuário; alterar com reserva ativa; cancelar; consultar depois | Dono e sem reservas → 200; outro usuário → 403; com reserva → 409; cancelada → 404 na consulta; dados inválidos → 400 | pytest + Postman |  
| **CT-15** | RF-08, RNF-02 | Partição (classe válida/inválida) | — | Login válido; senha errada; matrícula inexistente; corpo vazio; cada um dos 10 endpoints protegidos sem token; token inventado; `Basic` no lugar de `Bearer`; `/saude` sem token | Login válido → 200 com token; inválidos → 401; endpoints protegidos sem token → 401 `nao_autenticado`; `/saude` → 200 | pytest + Postman |  
| **CT-16** | RF-07 | Partição + oráculo por soma | 2 viagens (4 e 2 vagas), reservas variadas | Consultar o relatório; viagem sem reservas; sem viagens | `viagens`, `vagas`, `ocupadas` e `taxa_ocupacao` coerentes (ocupadas ≤ vagas; soma por viagem = total); sem viagens → taxa 0 (sem divisão por zero) | pytest + Postman |  

---  

## 4. Testes Automatizados  

### 4.1 Estrutura e números  

| Pasta | Conteúdo | Testes |  
|---|---|---:|  
| `tests/unitarios/test_regras.py` | Regras puras, nos valores-limite (parametrizado) | 63 |  
| `tests/unitarios/test_servico_com_dubles.py` | `ServicoCarona` isolado, com `Mock` do repositório | 40 |  
| `tests/unitarios/test_regras_do_projeto.py` | Guardas do `AGENTS.md` (dependências, relógio) | 2 |  
| `tests/integracao/test_repositorio_sqlite.py` | Serviço + SQLite real, relógio controlado | 23 |  
| `tests/integracao/test_api.py` | API Flask + SQLite (contrato HTTP, autenticação) | 39 |  
| `tests/integracao/test_app.py` | Montagem real do app | 4 |  
| `tests/integracao/test_concorrencia.py` | 6 threads disputando a última vaga | 1 |  
| | **Total** | **172** |  

São **593 linhas de código-fonte** e **677 linhas de teste** (não vazias, com comentários e docstrings). O tempo da suíte inteira é de 3,4 s local e 10,1 s no contêiner (com cobertura).  

### 4.2 Padrão AAA e valores-limite  

O teste de valor-limite é **parametrizado**: uma linha por valor, nos dois lados do limite. Cada linha é um caso que falha sozinho com uma mensagem clara.  

`tests/unitarios/test_regras.py`  

```python  
# --- CT-08, CT-09, CT-10 · RN-04 · taxa de cancelamento ----------------------  
SAIDA = AGORA + timedelta(hours=5)  


@pytest.mark.parametrize("antecedencia", [timedelta(hours=5), timedelta(minutes=61),  
                                          timedelta(minutes=60)])  
def test_ct08_ate_60_min_antes_nao_ha_taxa(antecedencia):  
    agora = SAIDA - antecedencia  
    assert d.taxa_de_cancelamento(Decimal("6.00"), SAIDA, agora) == Decimal("0.00")  


@pytest.mark.parametrize("antecedencia, taxa", [  
    (timedelta(minutes=59, seconds=59), "3.00"),  
    (timedelta(minutes=30), "3.00"),  
    (timedelta(seconds=1), "3.00"),  
])  
def test_ct09_com_menos_de_60_min_a_taxa_e_metade_do_valor(antecedencia, taxa):  
    assert d.taxa_de_cancelamento(Decimal("6.00"), SAIDA, SAIDA - antecedencia) == Decimal(taxa)  


def test_ct09_taxa_arredonda_meio_centavo_para_cima():  
    assert d.taxa_de_cancelamento(Decimal("3.01"), SAIDA, SAIDA - timedelta(minutes=10)) == Decimal("1.51")  


@pytest.mark.parametrize("depois", [timedelta(0), timedelta(seconds=1), timedelta(hours=2)])  
def test_ct10_na_hora_da_saida_ou_depois_nao_cancela(depois):  
    with pytest.raises(d.ConflitoDeRegra):  
        d.taxa_de_cancelamento(Decimal("6.00"), SAIDA, SAIDA + depois)  
```  

Note os três valores de cada lado: `60 min` (**no** limite), `61 min` e `5 h` sem taxa; `59 min 59 s`, `30 min` e `1 s` com taxa. E o `meio centavo` (`R$ 3,01 → R$ 1,51`) que prova o arredondamento. Arrange (os valores), Act (`taxa_de_cancelamento`) e Assert (`==`) cabem em duas linhas.  

### 4.3 Relógio injetado: como testar "60 minutos" sem esperar 60 minutos  

Nenhuma regra chama `datetime.now()`. O "agora" entra como parâmetro, e o teste usa um relógio que só anda quando o teste manda:  

`tests/conftest.py`  

```python  
class RelogioFixo:  
    """Relógio de teste: o tempo só anda quando o teste manda."""  

    def __init__(self, agora: datetime = AGORA):  
        self.agora = agora  

    def __call__(self) -> datetime:  
        return self.agora  

    def avancar(self, **kw) -> None:  
        self.agora += timedelta(**kw)  
```  

Isso transforma "59 min 59 s" em `relogio.avancar(seconds=...)`: instantâneo e repetível. Uma regra de teste do `AGENTS.md` garante que ninguém reintroduza o relógio real: `test_arq_relogio_real_so_aparece_na_api`.  

### 4.4 Dublês: isolar o que se quer testar  

No teste do serviço, o repositório é um `Mock`. Assim verificamos não só o que o serviço **faz**, mas o que ele **não chama** quando uma regra recusa:  

`tests/unitarios/test_servico_com_dubles.py`  

```python  
def test_ct06_ultima_vaga_e_aceita_e_a_seguinte_e_recusada(servico, repo):  
    repo.reservas_ativas.return_value = [d.Reserva(1, 10, 3, Decimal("3.00"))]   # 1 de 2  
    servico.reservar(10, passageiro_id=2)  
    repo.reservas_ativas.return_value = [d.Reserva(1, 10, 3, Decimal("3.00")),  
                                         d.Reserva(2, 10, 4, Decimal("3.00"))]   # 2 de 2  
    repo.reservar_se_ha_vaga.reset_mock()  
    with pytest.raises(d.ConflitoDeRegra, match="lotada"):  
        servico.reservar(10, passageiro_id=2)  
    repo.reservar_se_ha_vaga.assert_not_called()  


def test_ct06_se_o_repositorio_diz_que_lotou_na_hora_o_servico_recusa(servico, repo):  
    """A checagem do serviço passou, mas outra requisição pegou a vaga antes (corrida)."""  
    repo.reservas_ativas.return_value = []  
    repo.reservar_se_ha_vaga.side_effect = None  
    repo.reservar_se_ha_vaga.return_value = None  
    with pytest.raises(d.ConflitoDeRegra, match="lotada"):  
        servico.reservar(10, passageiro_id=2)  
```  

O segundo teste simula o que o banco real só mostraria numa corrida: o serviço checou, havia vaga, e **entre a checagem e a gravação outra requisição levou a vaga**. Sem o dublê, esse cenário seria impossível de montar de forma determinística.  

### 4.5 O teste que achou um defeito de verdade  

O teste de concorrência dispara 6 threads ao mesmo tempo sobre uma viagem de **1 vaga**:  

`tests/integracao/test_concorrencia.py`  

```python  
def test_ct06_duas_reservas_simultaneas_para_a_ultima_vaga_so_uma_vence(servico, relogio):  
    v = servico.criar_viagem(1, "A", "B", relogio() + timedelta(hours=2), 1, Decimal("6"))  
    largada, resultados = Barrier(6), []  

    def tentar(passageiro):  
        largada.wait()  
        try:  
            servico.reservar(v.id, passageiro)  
            resultados.append("ok")  
        except d.ConflitoDeRegra:  
            resultados.append("lotada")  

    fios = [Thread(target=tentar, args=(p,)) for p in (2, 3, 4, 5, 6, 7)]  
    [f.start() for f in fios]  
    [f.join() for f in fios]  
    assert sorted(resultados) == ["lotada"] * 5 + ["ok"]  
    assert len(servico.repo.reservas_ativas(v.id)) == 1  
```  

Com a primeira versão do serviço (contar as reservas e **depois** gravar) ele falhou nas 5 execuções do pytest. Num script avulso com as mesmas 6 threads, as reservas aceitas foram 1, 6, 5, 6 e 6 para uma única vaga. É o **DEF-001**. A correção move a contagem e a gravação para a mesma transação do banco:  

`servico.py` — antes (DEF-001)  

```python  
        if len(ativas) >= v.vagas:  
            raise ConflitoDeRegra("viagem lotada")  
        return self.repo.inserir_reserva(Reserva(0, viagem_id, passageiro_id, v.valor_por_vaga))  
```  

`servico.py` — depois  

```python  
        if len(ativas) >= v.vagas:  
            raise ConflitoDeRegra("viagem lotada")  
        # A checagem acima é só para dar a mensagem cedo; quem garante é o repositório.  
        reserva = self.repo.reservar_se_ha_vaga(  
            Reserva(0, viagem_id, passageiro_id, v.valor_por_vaga), v.vagas)  
        if reserva is None:  
            raise ConflitoDeRegra("viagem lotada")  
```  

A correção foi verificada com **200 execuções seguidas** do teste, sem falha. Repare que a primeira rodada do script avulso "teve sorte" (aceitou só 1): **um teste de concorrência é probabilístico**, e foi por isso que a mutação (seção 4.7) acrescentou um teste determinístico depois.  

### 4.6 Cobertura, e o que ela não diz  

| Arquivo | Instruções | Ramos | Cobertura |  
|---|---:|---:|---:|  
| `api.py` | 117 | 12 | 100% |  
| `app.py` | 13 | 4 | 100% |  
| `dominio.py` | 92 | 20 | 100% |  
| `repositorio.py` | 99 | 2 | 100% |  
| `servico.py` | 112 | 56 | 100% |  
| **Total** | **433** | **94** | **100%** |  

`app.main()` é excluída (`# pragma: no cover`: só sobe o servidor) e os métodos de `Protocol` também. **A cobertura de 100% não prova nada sobre a qualidade dos testes:** ela diz que as linhas rodaram, não que alguém percebe se estiverem erradas. Antes de acrescentar o teste determinístico da lotação, a cobertura já era de 100% e **um defeito ainda passava**. É para isso que existe a próxima seção.  

### 4.7 Quebrar o código de propósito: teste de mutação manual  

`scripts/mutacao.py` aplica **um mutante por vez** (um `>=` vira `>`, uma constante muda, uma verificação some) e roda a suíte. Se nenhum teste fica vermelho, o mutante **sobrevive**: há uma lacuna.  

| # | Arquivo | Mutação | Resultado | Primeiro teste que falhou |  
|---|---|---|---|---|  
| M01 | `dominio.py` | limite de 60 min: >= vira > | detectado | `test_repositorio_sqlite.py::test_ct08_ct09_taxa_no_limite_de_60_minutos` |  
| M02 | `dominio.py` | cancela na hora exata da saída | detectado | `test_repositorio_sqlite.py::test_ct10_apos_a_saida_nao_cancela_e_a_reserva_continua_ativa` |  
| M03 | `dominio.py` | preço arredonda para baixo | detectado | `test_api.py::test_ct01_criar_e_consultar_viagem` |  
| M04 | `dominio.py` | esquece de contar o motorista | detectado | `test_api.py::test_ct01_criar_e_consultar_viagem` |  
| M05 | `dominio.py` | máximo de vagas 6 -> 7 | detectado | `test_api.py::test_ct01_dados_invalidos_devolvem_400_com_erro_padronizado` |  
| M06 | `dominio.py` | antecedência de 30 min exclusiva | detectado | `test_regras.py::test_ct03_saida_nos_limites_inclusivos_e_aceita` |  
| M07 | `dominio.py` | janela de 14 dias exclusiva | detectado | `test_regras.py::test_ct03_saida_nos_limites_inclusivos_e_aceita` |  
| M08 | `dominio.py` | média exata 3,0 suspende | detectado | `test_repositorio_sqlite.py::test_ct12_ct13_media_exatamente_3_nao_suspende` |  
| M09 | `dominio.py` | só suspende com 6 avaliações | detectado | `test_api.py::test_ct12_reputacao_e_ct13_suspensao_ponta_a_ponta` |  
| M10 | `dominio.py` | taxa de 50% -> 40% | detectado | `test_api.py::test_ct08_ct09_cancelar_reserva_com_e_sem_taxa_pela_api` |  
| M11 | `dominio.py` | teto de R$ 25 -> 26 | detectado | `test_regras.py::test_ct04_preco_por_vaga` |  
| M12 | `dominio.py` | nota 1 vira inválida | detectado | `test_api.py::test_ct12_reputacao_e_ct13_suspensao_ponta_a_ponta` |  
| M13 | `servico.py` | lotação na checagem do serviço | detectado | `test_servico_com_dubles.py::test_ct06_ultima_vaga_e_aceita_e_a_seguinte_e_recusada` |  
| M14 | `servico.py` | motorista reserva a própria viagem | detectado | `test_api.py::test_ct14_so_o_motorista_altera_e_viagem_com_reserva_e_imutavel` |  
| M15 | `servico.py` | avalia antes da saída | detectado | `test_api.py::test_ct11_avaliar_exige_ter_viajado` |  
| M16 | `servico.py` | qualquer um cancela reserva alheia | detectado | `test_api.py::test_ct10_cancelar_reserva_de_outro_ou_inexistente` |  
| M17 | `repositorio.py` | lotação dentro da transação | detectado | `test_concorrencia.py::test_ct06_duas_reservas_simultaneas_para_a_ultima_vaga_so_uma_vence` |  
| M18 | `repositorio.py` | esquece de fechar a conexão | detectado | `test_api.py::test_ct15_login_invalido_devolve_401` |  
| M19 | `api.py` | conflito devolve 400 | detectado | `test_api.py::test_ct14_so_o_motorista_altera_e_viagem_com_reserva_e_imutavel` |  
| M20 | `api.py` | autenticação desligada | detectado | `test_api.py::test_ct15_todo_endpoint_protegido_exige_token` |  
| M21 | `servico.py` | alteração aceita origem/destino vazios | detectado | `test_api.py::test_ct14_alteracao_com_origem_ou_destino_vazios_e_recusada` |  

**21 mutantes, 21 detectados**, em 49 s. Três observações do processo, todas reais:  

1. **Na primeira rodada completa, o mutante M17 sobreviveu** (`>=` virou `>` na lotação dentro do repositório). Ele só morria quando o teste de concorrência "tinha sorte". Foi acrescentado o teste determinístico `test_ct06_repositorio_recusa_a_reserva_alem_das_vagas...`, que mata o M17 sozinho, todas as vezes (**DEF-T4**).  
2. **O M21 nasceu de um defeito** (DEF-003): a alteração de viagem aceitava origem e destino vazios. O defeito apareceu ao **portar o sistema para Java**, que validava; nenhum teste Python cobria o caminho. Quatro testes novos falham no código antigo.  
3. **A coluna "Primeiro teste que falhou"** é o argumento da defesa oral: para cada defeito simulado, existe um teste que o percebe, e dá para apontá-lo.  

### 4.8 Rastreabilidade conferida por script  

`scripts/matriz_rastreabilidade.py` lê os casos (`casos-de-teste.md`), os nomes dos testes (`test_ct07_...`) e os nomes dos requests do Postman (`CT-07 · ...`). Ele **falha** se um caso não tem teste ou se um teste cita um caso que não existe:  

| Caso | Requisitos | Testes pytest | Requests Postman |  
|---|---|---:|---:|  
| CT-01 | RF-01, RF-02 | 10 | 2 |  
| CT-02 | RN-01 | 3 | 2 |  
| CT-03 | RN-02 | 2 | 2 |  
| CT-04 | RN-03, RNF-03 | 3 | 3 |  
| CT-05 | RF-03 | 3 | 1 |  
| CT-06 | RN-01, RNF-05 | 7 | 3 |  
| CT-07 | RF-03 | 6 | 2 |  
| CT-08 | RN-04 | 4 | 1 |  
| CT-09 | RN-04, RNF-03 | 5 | 3 |  
| CT-10 | RN-04, RF-04 | 5 | 2 |  
| CT-11 | RF-05 | 8 | 15 |  
| CT-12 | RN-05, RF-06 | 8 | 7 |  
| CT-13 | RN-05, RF-01 | 5 | 1 |  
| CT-14 | RF-02 | 11 | 5 |  
| CT-15 | RF-08, RNF-02 | 9 | 6 |  
| CT-16 | RF-07 | 4 | 1 |  

Os 16 casos têm teste automatizado. Rastreabilidade inventada é pior que nenhuma; por isso o script roda também no pipeline.  

---  

## 5. Testes de API  

### 5.1 A coleção  

`postman/carona.postman_collection.json` (v2.1) tem **58 requests e 95 asserções**, organizados em 8 pastas na ordem em que o estado evolui:  

| Pasta | Requests | Casos |  
|---|---:|---|  
| 0. Saúde | 1 | — |  
| 1. Autenticação | 6 | CT-15 |  
| 2. Viagens — CRUD | 12 | CT-01 a CT-04, CT-14 |  
| 3. Reservas | 8 | CT-05 a CT-07 |  
| 4. Cancelamento de reserva | 6 | CT-08 a CT-10 |  
| 5. Avaliações e reputação | 17 | CT-11, CT-12 |  
| 6. Relatório | 1 | CT-16 |  
| 7. Suspensão do motorista (por último: o estado não volta) | 7 | CT-12, CT-13 |  

Nenhuma URL é fixa: todas usam `{{base_url}}`. O login guarda o token em variável da coleção. As datas de saída são calculadas num **script de pré-requisição** (`Date.now() + 31 min`), porque a regra RN-02 exige saída no futuro.  

### 5.2 Um request, por dentro  

Cancelar com menos de 60 minutos de antecedência. Veja o que cada asserção verifica (status **e** conteúdo):  

```json  
{  
  "name": "CT-09 · Cancelar com menos de 60 min: taxa de 50% (R$ 1,50)",  
  "event": [  
    {  
      "listen": "test",  
      "script": {  
        "type": "text/javascript",  
        "exec": [  
          "pm.test('status 200', () => pm.response.to.have.status(200));",  
          "pm.test('taxa = ' + \"'1.50'\".toString(), () => pm.expect(pm.response.json().taxa).to.eql('1.50'));"  
        ]  
      }  
    }  
  ],  
  "request": {  
    "method": "DELETE",  
    "header": [  
      {  
        "key": "Content-Type",  
        "value": "application/json"  
      },  
      {  
        "key": "Authorization",  
        "value": "Bearer {{token_carla}}"  
      }  
    ],  
    "url": {  
      "raw": "{{base_url}}/reservas/{{reserva_carla_id}}",  
      "host": [  
        "{{base_url}}"  
      ],  
      "path": [  
        "reservas",  
        "{{reserva_carla_id}}"  
      ]  
    }  
  }  
}  
```  

### 5.3 Execução no Newman, em Docker  

```bash  
docker compose --profile api up --build --abort-on-container-exit --exit-code-from newman  
```  

Resultado: **58 requests, 95 asserções, 0 falhas**, em 1,2 s. O serviço `api-teste` roda a mesma imagem em **modo de teste** (`CARONA_MODO_TESTE=1`), que expõe `POST /teste/avancar-relogio`. Essa porta dos fundos só existe nesse serviço (o teste `test_modo_de_teste_desligado_nao_expoe_o_relogio` e o `ct15_montar_em_modo_normal...` garantem que o serviço normal devolve 404), e é uma **decisão de testabilidade** registrada como risco R-01 no plano.  

### 5.4 E a coleção, detecta defeitos?  

O mesmo exercício da mutação, agora com o Newman rodando contra a API mutante. De 9 mutantes testados, **6 foram detectados e 3 sobreviveram**:  

| Mutante | Newman |  
|---|---|  
| M04 esquece de contar o motorista no preço | detectado |  
| M09 só suspende com 6 avaliações | detectado |  
| M10 taxa de 50% → 40% | detectado |  
| M11 teto de R$ 25 → 26 | detectado |  
| M19 conflito devolve 400 em vez de 409 | detectado |  
| M20 autenticação desligada | detectado |  
| **M01 limite de 60 min (`>=` → `>`)** | **sobreviveu** |  
| **M02 cancela na hora exata da saída** | **sobreviveu** |  
| **M08 média exata 3,0 suspende** | **sobreviveu** |  

Os três sobreviventes são **valores-limite exatos**. Pela API com relógio real, acertar o segundo exato é impraticável. Isso confirma a divisão de trabalho do plano: **limites nos testes unitários e de integração com relógio injetado; a coleção cobre partições e contrato.** Quem removesse os testes unitários "porque a coleção já cobre" perderia exatamente a proteção dos limites.  

---  

## 6. Pipeline CI/CD e Docker  

### 6.1 Docker: a suíte roda em qualquer máquina  

`Dockerfile`  

```dockerfile  
FROM python:3.12-slim  

ENV PYTHONDONTWRITEBYTECODE=1 \  
    PYTHONUNBUFFERED=1 \  
    PYTHONPATH=/app/src  

WORKDIR /app  
COPY requirements.txt .  
RUN pip install --no-cache-dir -r requirements.txt  
COPY . .  

EXPOSE 5000  
CMD ["python", "-m", "carona.app"]  
```  

`docker-compose.yml`  

```yaml  
# docker compose up --build                                  -> sobe a API em http://localhost:5000  
# docker compose run --rm testes                             -> pytest + cobertura (falha abaixo de 70%)  
# docker compose --profile api up --build \  
#     --abort-on-container-exit --exit-code-from newman      -> testes de API (Newman) contra a API em modo de teste  
services:  
  api:  
    build: .  
    image: carona-campus:1.0.0  
    ports: ["5000:5000"]  
    environment:  
      CARONA_DB: /data/carona.db  
    volumes: ["dados:/data"]  
    healthcheck:  
      test: ["CMD", "python", "-c", "import urllib.request; urllib.request.urlopen('http://localhost:5000/saude')"]  
      interval: 3s  
      timeout: 3s  
      retries: 10  

  testes:  
    image: carona-campus:1.0.0  
    profiles: ["testes"]  
    command: ["pytest", "--cov", "--cov-config=.coveragerc", "--cov-fail-under=70"]  

  # API em MODO DE TESTE (expõe POST /teste/avancar-relogio). Banco descartável em memória do contêiner.  
  api-teste:  
    image: carona-campus:1.0.0  
    profiles: ["api"]  
    ports: ["5001:5000"]  
    environment:  
      CARONA_DB: /tmp/carona-teste.db  
      CARONA_MODO_TESTE: "1"  
    healthcheck:  
      test: ["CMD", "python", "-c", "import urllib.request; urllib.request.urlopen('http://localhost:5000/saude')"]  
      interval: 2s  
      timeout: 3s  
      retries: 15  

  newman:  
    image: postman/newman:latest  
    profiles: ["api"]  
    depends_on:  
      api-teste:  
        condition: service_healthy  
    volumes: ["./postman:/etc/newman:ro"]  
    command: ["run", "/etc/newman/carona.postman_collection.json",  
              "--env-var", "base_url=http://api-teste:5000", "--color", "off"]  

volumes:  
  dados:  
```  

| Comando | O que faz |  
|---|---|  
| `docker compose up --build` | Sobe a API em `localhost:5000` |  
| `docker compose run --rm testes` | pytest + cobertura; **falha abaixo de 70%** |  
| `docker compose --profile api up --build --abort-on-container-exit --exit-code-from newman` | Coleção Postman contra a API em modo de teste |  

> **Cuidado real, vivido na construção deste exemplo:** os testes rodam **contra a imagem**. Ao rodar os mutantes pelo Newman, o script reconstruiu a mesma tag `carona-campus:1.0.0` com o último mutante, e a execução seguinte do `docker compose run --rm testes` **falhou em 11 testes**, porque a imagem estava com a autenticação desligada. Depois de mudar o código, reconstruam a imagem (`docker compose build`, ou `run --build`).  

### 6.2 O workflow  

`.github/workflows/ci.yml`  

```yaml  
name: CI  

on:  
  push:  
    branches: [main]  
  pull_request:  
  workflow_dispatch:  

jobs:  
  testes:  
    name: Testes, cobertura e rastreabilidade  
    runs-on: ubuntu-latest  
    steps:  
      - uses: actions/checkout@v4  
      - uses: actions/setup-python@v5  
        with:  
          python-version: "3.12"  
          cache: pip  
      - run: pip install -r requirements.txt  
      - name: Rastreabilidade (cada caso tem teste; nenhum teste cita caso inexistente)  
        run: python scripts/matriz_rastreabilidade.py  
      - name: Testes com cobertura (falha abaixo de 70%)  
        run: pytest --cov --cov-config=.coveragerc --cov-fail-under=70 --cov-report=xml --junitxml=junit.xml  
      - uses: actions/upload-artifact@v4  
        if: always()  
        with:  
          name: relatorios-pytest  
          path: |  
            coverage.xml  
            junit.xml  

  api:  
    name: Testes de API (Newman)  
    needs: testes  
    runs-on: ubuntu-latest  
    steps:  
      - uses: actions/checkout@v4  
      - uses: actions/setup-python@v5  
        with:  
          python-version: "3.12"  
          cache: pip  
      - run: pip install -r requirements.txt  
      - uses: actions/setup-node@v4  
        with:  
          node-version: "20"  
      - name: Sobe a API em modo de teste  
        run: |  
          CARONA_DB=/tmp/carona-ci.db CARONA_MODO_TESTE=1 PYTHONPATH=src python -m carona.app &  
          for i in $(seq 1 20); do curl -fs http://localhost:5000/saude && break; sleep 1; done  
      - name: Coleção Postman  
        run: npx --yes newman@6 run postman/carona.postman_collection.json --env-var base_url=http://localhost:5000 --reporters cli,junit --reporter-junit-export newman.xml  
      - uses: actions/upload-artifact@v4  
        if: always()  
        with:  
          name: relatorio-newman  
          path: newman.xml  

  docker:  
    name: Entrega em contêiner  
    needs: testes  
    runs-on: ubuntu-latest  
    steps:  
      - uses: actions/checkout@v4  
      - name: Testes dentro do contêiner  
        run: docker compose run --rm --build testes  

  mutacao:  
    name: Teste de mutação (manual)  
    if: github.event_name == 'workflow_dispatch'  
    needs: testes  
    runs-on: ubuntu-latest  
    steps:  
      - uses: actions/checkout@v4  
      - uses: actions/setup-python@v5  
        with:  
          python-version: "3.12"  
          cache: pip  
      - run: pip install -r requirements.txt  
      - name: 21 mutantes (falha se algum sobreviver)  
        run: python scripts/mutacao.py  
```  

Quatro jobs: `testes` (rastreabilidade, pytest e cobertura com mínimo de 70%), `api` (sobe a API em modo de teste e roda o Newman), `docker` (a mesma suíte dentro do contêiner) e `mutacao` (só sob demanda: `workflow_dispatch`). Os relatórios são publicados como artefatos. Nenhum passo usa `continue-on-error`.  

### 6.3 O que foi executado e o que não foi  

| Passo do workflow | Executado? | Como |  
|---|---|---|  
| `pip install` + `matriz_rastreabilidade.py` | Sim | Localmente, mesmo comando |  
| `pytest --cov ... --cov-fail-under=70` | Sim | Local e no contêiner `python:3.12-slim`: 172 passed, 100% |  
| `api` (Newman fora do Docker) | Sim | API em `localhost:5000` + `npx newman@6`: 58 requests, 95 asserções, 0 falhas |  
| `docker compose run --rm --build testes` | Sim | Mesmo comando, em cópia limpa |  
| `mutacao.py` | Sim | 21/21 |  
| **Execução no GitHub Actions** | **Não** | O Actions só lê `.github/workflows` da raiz do repositório; o exemplo vive numa subpasta |  

A validade da sintaxe do YAML foi conferida por `yaml.safe_load`. **Isso não substitui uma execução verde**, e é por isso que o enunciado exige o link dela na capa.  

---  

## 7. Relatório de Testes  

*Arquivo `relatorio-testes.md` do repositório, na íntegra.*  

### 7.1 Escopo executado  

Foi executado tudo o que o `plano-de-teste.md` prevê: os 16 casos de teste (`casos-de-teste.md`), nos níveis unitário, integração e API, mais o teste de mutação e a medição do RNF-01.  

**Não foi executado (e por quê):**  

| Item | Motivo |  
|---|---|  
| Workflow no GitHub Actions | O repositório de referência vive numa subpasta de outro repositório, e o Actions só lê `.github/workflows` da raiz. Os **mesmos comandos** do workflow foram executados à mão (seção 8). **O seu grupo precisa de uma execução verde na aba Actions** |  
| Desempenho sob carga | Fora do escopo do plano (só o RNF-01 foi medido, em processo, sem rede) |  
| Expiração de token, força bruta, OWASP | Fora do escopo; risco R-04 aceito (seção 6) |  
| SonarQube / Quality Gate | Opcional na A3; não configurado neste exemplo |  

### 7.2 Resumo da execução  

| Nível | Executados | Aprovados | Reprovados | Bloqueados | Tempo |  
|---|---:|---:|---:|---:|---|  
| Unitários (`tests/unitarios/`) | 105 | 105 | 0 | 0 | — |  
| Integração (`tests/integracao/`) | 67 | 67 | 0 | 0 | — |  
| **pytest (total)** | **172** | **172** | **0** | **0** | 3,4 s local · 10,6 s no contêiner (com cobertura) |  
| API (Newman): requests | 58 | 58 | 0 | 0 | 1,3 s |  
| API (Newman): asserções | 95 | 95 | 0 | 0 | — |  
| **Casos de teste (16)** | **16** | **16** | **0** | **0** | — |  

Taxa de aprovação: 100%. A taxa de reprovação **na última execução** é 0%, mas a suíte já reprovou antes: os defeitos da seção 4 foram achados por testes vermelhos.  

#### Rastreabilidade: caso de teste × testes automatizados  

Gerada por `scripts/matriz_rastreabilidade.py` (que falha se um caso não tem teste, ou se um teste cita um caso que não existe):  

| Caso | Requisitos | Testes pytest | Requests Postman |  
|---|---|---:|---:|  
| CT-01 | RF-01, RF-02 | 10 | 2 |  
| CT-02 | RN-01 | 3 | 2 |  
| CT-03 | RN-02 | 2 | 2 |  
| CT-04 | RN-03, RNF-03 | 3 | 3 |  
| CT-05 | RF-03 | 3 | 1 |  
| CT-06 | RN-01, RNF-05 | 7 | 3 |  
| CT-07 | RF-03 | 6 | 2 |  
| CT-08 | RN-04 | 4 | 1 |  
| CT-09 | RN-04, RNF-03 | 5 | 3 |  
| CT-10 | RN-04, RF-04 | 5 | 2 |  
| CT-11 | RF-05 | 8 | 15 |  
| CT-12 | RN-05, RF-06 | 8 | 7 |  
| CT-13 | RN-05, RF-01 | 5 | 1 |  
| CT-14 | RF-02 | 11 | 5 |  
| CT-15 | RF-08, RNF-02 | 9 | 6 |  
| CT-16 | RF-07 | 4 | 1 |  

Um caso é aprovado quando **todos** os seus testes (pytest e Postman) passam.  

### 7.3 Cobertura  

| Arquivo | Instruções | Ramos | Cobertura |  
|---|---:|---:|---:|  
| `api.py` | 117 | 12 | 100% |  
| `app.py` | 13 | 4 | 100% |  
| `dominio.py` | 92 | 20 | 100% |  
| `repositorio.py` | 99 | 2 | 100% |  
| `servico.py` | 112 | 56 | 100% |  
| **Total** | **433** | **94** | **100%** (linha e ramo) |  

**O que NÃO está nos números:** `app.main()` está excluída da cobertura (`# pragma: no cover`): ela só sobe o servidor, e foi exercitada pelo `docker compose up` e pelo Newman. Os métodos de `Protocol` (`...`) também são excluídos.  

**100% de cobertura não é 100% de qualidade.** A cobertura diz quais linhas rodaram, não se os testes **percebem** um defeito nelas. Por isso a seção 5.  

### 7.4 Defeitos encontrados  

#### 7.4.1 Defeitos do produto  

| ID | Descrição | Severidade | Prioridade | Estado | Como foi achado | Solução |  
|---|---|---|---|---|---|---|  
| **DEF-001** | Reservas simultâneas ultrapassam as vagas: o serviço lia a contagem e gravava em seguida, sem transação. Com 6 threads disputando **1 vaga**, foram aceitas 1, 6, 5, 6 e 6 reservas em 5 execuções | **Alta** (viola RN-01 e o RNF-05) | Alta | Corrigido | Teste CT-06 de concorrência (vermelho nas 5 execuções do pytest) | `reservar_se_ha_vaga`: contagem e gravação na mesma transação (`BEGIN IMMEDIATE`) e índice único parcial contra reserva duplicada. 200 execuções seguidas sem falha |  
| **DEF-002** | Conexões SQLite nunca fechadas: `with sqlite3.connect()` só confirma a transação. Em servidor de longa duração vaza descritores de arquivo | Média | Média | Corrigido | `ResourceWarning: unclosed database` aos montes ao rodar a suíte (884 avisos na execução em que foi notado) | `contextmanager` que confirma, desfaz e **fecha**; `filterwarnings = error` no `pytest.ini` para nunca mais passar despercebido |  

**Passos de reprodução do DEF-001** (antes da correção): criar viagem de 1 vaga; 6 passageiros chamam `POST /viagens/{id}/reservas` ao mesmo tempo; contar `reservas ativas > 1`. Reprodução automatizada em `tests/integracao/test_concorrencia.py`.  

| **DEF-003** | A **alteração** de viagem (`PUT`) aceitava `origem` e `destino` vazios ou só com espaços (e não removia os espaços das pontas): só a criação validava | Baixa | Média | Corrigido | **Revisão comparativa ao portar o sistema para Java**: o serviço Java validava, o Python não. Nenhum teste cobria o caminho. 4 testes novos falham no código antigo | `atualizar_viagem` passou a aplicar `strip()` e a exigir origem e destino não vazios; mutante M21 protege |  

Observação: o teste falhou nas 5 execuções do pytest, mas num script avulso com as mesmas 6 threads uma das 5 rodadas teve sorte (só 1 reserva aceita) — **um teste de concorrência é probabilístico**, e isso motivou o DEF-T4.  

#### 7.4.2 Falhas e lacunas dos próprios testes  

Também são achados de qualidade: um teste errado reprova código certo, e um teste fraco aprova código errado.  

| ID | Descrição | Tipo | Estado | Solução |  
|---|---|---|---|---|  
| **DEF-T1** | Expectativa errada: `10,01 ÷ (4+1)` esperado como 2,51 (é 2,002 → **2,01**) | Erro no teste | Corrigido | Conta refeita à mão; o código estava certo |  
| **DEF-T2** | Linha da suspensão: 6 avaliações `[4,4,4,1,1,1]` (média 2,5) marcadas como "não suspende" | Erro no teste | Corrigido | Valor esperado passou a `True` |  
| **DEF-T3** | Teste de suspensão pela API avançava o relógio do servidor, mas o relógio do teste não andava: as viagens seguintes nasciam no passado e davam 400 | Erro no teste | Corrigido | Os dois relógios passaram a ser o mesmo objeto |  
| **DEF-T4** | Mutante M17 (`>=` → `>` na lotação do repositório) **sobreviveu** em uma rodada de mutação: o teste de concorrência é probabilístico | Lacuna de teste | Corrigido | Teste determinístico direto no repositório; M17 morre sempre |  

#### 7.4.3 Densidade  

- Defeitos do produto: **3** em 593 linhas não vazias de código-fonte → **5,1 defeitos/KLOC**  
- Defeitos de teste: 4 em 677 linhas de teste → 5,9 por KLOC de teste  
- Relação teste/código: 677 / 593 = **1,14**  

Dos 3 defeitos do produto, 2 foram achados **por testes** (DEF-001, DEF-002) e 1 por **revisão comparativa** (DEF-003); nenhum chegou à "produção".  

### 7.5 Qualidade dos testes: teste de mutação  

`scripts/mutacao.py` aplica **um mutante por vez** (um operador trocado, uma constante alterada, uma verificação removida) e roda a suíte. Mutante que a suíte não percebe **sobrevive**.  

| # | Arquivo | Mutação | Resultado | Primeiro teste que falhou |  
|---|---|---|---|---|  
| M01 | `dominio.py` | limite de 60 min: >= vira > | detectado | `test_repositorio_sqlite.py::test_ct08_ct09_taxa_no_limite_de_60_minutos` |  
| M02 | `dominio.py` | cancela na hora exata da saída | detectado | `test_repositorio_sqlite.py::test_ct10_apos_a_saida_nao_cancela_e_a_reserva_continua_ativa` |  
| M03 | `dominio.py` | preço arredonda para baixo | detectado | `test_api.py::test_ct01_criar_e_consultar_viagem` |  
| M04 | `dominio.py` | esquece de contar o motorista | detectado | `test_api.py::test_ct01_criar_e_consultar_viagem` |  
| M05 | `dominio.py` | máximo de vagas 6 -> 7 | detectado | `test_api.py::test_ct01_dados_invalidos_devolvem_400_com_erro_padronizado` |  
| M06 | `dominio.py` | antecedência de 30 min exclusiva | detectado | `test_regras.py::test_ct03_saida_nos_limites_inclusivos_e_aceita` |  
| M07 | `dominio.py` | janela de 14 dias exclusiva | detectado | `test_regras.py::test_ct03_saida_nos_limites_inclusivos_e_aceita` |  
| M08 | `dominio.py` | média exata 3,0 suspende | detectado | `test_repositorio_sqlite.py::test_ct12_ct13_media_exatamente_3_nao_suspende` |  
| M09 | `dominio.py` | só suspende com 6 avaliações | detectado | `test_api.py::test_ct12_reputacao_e_ct13_suspensao_ponta_a_ponta` |  
| M10 | `dominio.py` | taxa de 50% -> 40% | detectado | `test_api.py::test_ct08_ct09_cancelar_reserva_com_e_sem_taxa_pela_api` |  
| M11 | `dominio.py` | teto de R$ 25 -> 26 | detectado | `test_regras.py::test_ct04_preco_por_vaga` |  
| M12 | `dominio.py` | nota 1 vira inválida | detectado | `test_api.py::test_ct12_reputacao_e_ct13_suspensao_ponta_a_ponta` |  
| M13 | `servico.py` | lotação na checagem do serviço | detectado | `test_servico_com_dubles.py::test_ct06_ultima_vaga_e_aceita_e_a_seguinte_e_recusada` |  
| M14 | `servico.py` | motorista reserva a própria viagem | detectado | `test_api.py::test_ct14_so_o_motorista_altera_e_viagem_com_reserva_e_imutavel` |  
| M15 | `servico.py` | avalia antes da saída | detectado | `test_api.py::test_ct11_avaliar_exige_ter_viajado` |  
| M16 | `servico.py` | qualquer um cancela reserva alheia | detectado | `test_api.py::test_ct10_cancelar_reserva_de_outro_ou_inexistente` |  
| M17 | `repositorio.py` | lotação dentro da transação | detectado | `test_concorrencia.py::test_ct06_duas_reservas_simultaneas_para_a_ultima_vaga_so_uma_vence` |  
| M18 | `repositorio.py` | esquece de fechar a conexão | detectado | `test_api.py::test_ct15_login_invalido_devolve_401` |  
| M19 | `api.py` | conflito devolve 400 | detectado | `test_api.py::test_ct14_so_o_motorista_altera_e_viagem_com_reserva_e_imutavel` |  
| M20 | `api.py` | autenticação desligada | detectado | `test_api.py::test_ct15_todo_endpoint_protegido_exige_token` |  
| M21 | `servico.py` | alteração aceita origem/destino vazios | detectado | `test_api.py::test_ct14_alteracao_com_origem_ou_destino_vazios_e_recusada` |  

**21 mutantes, 21 detectados (100%)**, em 49 s.  

#### 7.5.1 E a coleção Postman, detecta?  

O mesmo exercício, dessa vez rodando o Newman contra a API mutante (`docker compose --profile api up ...`):  

| # | Mutação | Newman |  
|---|---|---|  
| M01 | limite de 60 min: >= vira > | **sobreviveu** |  
| M02 | cancela na hora exata da saída | **sobreviveu** |  
| M04 | esquece de contar o motorista | detectado |  
| M08 | média exata 3,0 suspende | **sobreviveu** |  
| M09 | só suspende com 6 avaliações | detectado |  
| M10 | taxa de 50% -> 40% | detectado |  
| M11 | teto de R$ 25 -> 26 | detectado |  
| M19 | conflito devolve 400 | detectado |  
| M20 | autenticação desligada | detectado |  

**Achado:** o Newman detecta 6 de 9 mutantes. Os três que sobrevivem (M01, M02, M08) são **valores-limite exatos** (exatamente 60 min, exatamente a hora da saída, média exatamente 3,0). Pela API com relógio real é impraticável acertar o segundo exato. Isso confirma a estratégia do plano: **limites vivem nos testes unitários e de integração com relógio injetado; a coleção cobre partições e contrato**. Cada nível pega o que o outro não consegue.  

### 7.6 Métricas  

| Métrica | Valor |  
|---|---|  
| Requisitos atendidos | 8/8 RF · 5/5 RN · 5/5 RNF (RNF-01 medido: seção 6.1) |  
| Casos aprovados | 16/16 (100%) |  
| Cobertura de instrução / de ramo | 100% / 100% |  
| Mutantes detectados | 21/21 (pytest) · 6/9 (Newman, amostra) |  
| Defeitos do produto: encontrados / corrigidos / abertos | 3 / 3 / 0 |  
| Defeitos de teste: encontrados / corrigidos | 4 / 4 |  
| Densidade de defeitos do produto | 5,1 /KLOC |  
| Tempo da suíte pytest (local / contêiner) | 3,4 s / 10,6 s |  
| Tempo do Newman | 1,3 s |  

#### 7.6.1 RNF-01 (desempenho)  

`scripts/medir_desempenho.py`: `GET /viagens` com 1.000 viagens, 200 repetições, em processo, sobre SQLite: **mediana 5,4 ms · p95 6,3 ms · máximo 19,5 ms**. Limite do requisito: 300 ms (p95) → **atendido**. A medida não inclui rede nem servidor de produção; vale para comparar versões, não como promessa de latência.  

#### 7.6.2 Riscos residuais  

| Risco | Estado |  
|---|---|  
| R-01 instabilidade por relógio | Mitigado: relógio injetado |  
| R-02 corrida na última vaga | Mitigado e verificado (200 execuções) |  
| R-03 cobertura alta com testes fracos | Mitigado: 21 mutantes, 100% detectados |  
| R-04 segurança fora do escopo | **Aceito**: tokens não expiram; o hash de senha usa sal fixo (suficiente para a demonstração, não para produção) |  
| R-05 dublê diferente do SQLite | Mitigado: os mesmos cenários rodam nos dois |  

### 7.7 Conclusão  

**O sistema está apto a ser liberado como versão de demonstração (v1.0.0), com duas ressalvas.**  

1. Os 16 casos passam, a cobertura é de 100% e os 21 mutantes são detectados. Os três defeitos do produto (corrida na última vaga, vazamento de conexões e alteração sem validação) foram corrigidos; nenhum está aberto.  
2. **Ressalvas para um uso real:** (a) a autenticação é simples demais (token sem expiração, sal de senha fixo) e deve ser trocada antes de qualquer dado real; (b) a coleção Postman **não** prova os valores-limite de tempo, que dependem dos testes unitários: não removam esses testes achando que a API os cobre. Além disso, o workflow ainda precisa de uma execução verde no GitHub.  

**Recomendação:** liberar para o piloto no campus com a lista de ressalvas acima; tratar a autenticação como o primeiro item da próxima versão.  

### 7.8 Como reproduzir cada número  

| Número | Comando |  
|---|---|  
| 172 testes e 100% de cobertura | `docker compose run --rm testes` |  
| 58 requests / 95 asserções | `docker compose --profile api up --build --abort-on-container-exit --exit-code-from newman` |  
| 21 mutantes | `python scripts/mutacao.py` (pytest instalado) |  
| Matriz de rastreabilidade | `python scripts/matriz_rastreabilidade.py` |  
| RNF-01 | `docker compose run --rm testes python scripts/medir_desempenho.py` |  
| Mutantes na coleção Newman | aplicar o mutante de `scripts/mutacao.py` numa cópia e rodar o comando do Newman acima (a tag `carona-campus:1.0.0` é compartilhada: **reconstruam a imagem depois**, ou a próxima execução usa o mutante) |  

Ambiente das medições: Linux, Docker 29, Compose 5, `python:3.12-slim`, pytest 9.1, pytest-cov 7.1, Flask 3.1, Newman (imagem `postman/newman`).  

---  

## 8. Versionamento  

O repositório tem **30 commits**, 4 autores, 6 branches (`feat/*` e `fix/*`, integradas com `merge --no-ff`) e a tag `v1.0.0`. Repare que os **defeitos aparecem no histórico**: o commit da corrida na última vaga, o teste que a reproduz (vermelho) e a correção.  

```  
* f9242ec 2026-10-22 Fred Silva  docs: relatório de testes, README com Uso de IA e CHANGELOG 1.0.0  
* 4d13974 2026-10-21 Betty Costa  chore(ia): AGENTS.md e opencode.json com push negado  
*   0cae098 2026-10-21 Barney Lima  Merge branch 'fix/validacao-alteracao'  
|\  
| * 74f2753 2026-10-21 Barney Lima  fix(servico): alteração de viagem valida origem e destino (DEF-003, achado ao portar para Java)  
|/  
*   c4db171 2026-10-20 Betty Costa  Merge branch 'feat/qualidade'  
|\  
| * 9f8e1c3 2026-10-20 Barney Lima  test: teste determinístico da lotação no repositório (DEF-T4), guardas de arquitetura, matriz e desempenho  
| * d9b0aeb 2026-10-19 Barney Lima  test(mutacao): 20 mutantes aplicados um a um  
| * ec703fc 2026-10-17 Wilma Souza  ci: workflow com testes, Newman, contêiner e mutação manual  
| * 75f71b0 2026-10-16 Wilma Souza  build(docker): Dockerfile e compose (api, testes, api-teste, newman)  
| * 6d63c35 2026-10-15 Betty Costa  test(api): coleção Postman com 58 requests e 95 asserções  
|/  
*   9f7550a 2026-10-13 Wilma Souza  Merge branch 'feat/api'  
|\  
| * 0459071 2026-10-13 Fred Silva  test(integracao): montagem do app, reserva duplicada no banco e cobertura de 100%  
| * dbe4ff9 2026-10-12 Fred Silva  fix(repositorio): fecha as conexões e transforma avisos em erro nos testes (DEF-002)  
| * 9191393 2026-10-11 Wilma Souza  fix(test): relógio do teste e do servidor passam a ser o mesmo (DEF-T3)  
| * fca1d38 2026-10-10 Wilma Souza  feat(api): endpoints Flask com autenticação por token e erros padronizados  
|/  
*   849106d 2026-10-08 Barney Lima  Merge branch 'fix/reserva-atomica'  
|\  
| * 1e96237 2026-10-08 Barney Lima  fix(reserva): contagem e gravação na mesma transação e índice único (DEF-001)  
|/  
*   605a6b4 2026-10-07 Fred Silva  Merge branch 'feat/persistencia-e-servico'  
|\  
| * 6f16277 2026-10-07 Betty Costa  test(concorrencia): 6 threads disputando 1 vaga (falha: reproduz DEF-001)  
| * b9e5134 2026-10-05 Fred Silva  test(integracao): serviço com SQLite real, relógio controlado e limites de 60 min  
| * 2b33f12 2026-10-03 Wilma Souza  test(servico): serviço isolado com Mock do repositório  
| * 0ec795e 2026-10-02 Wilma Souza  feat(servico): casos de uso de viagens, reservas, avaliações e relatório  
| * 8a8cbf2 2026-09-30 Fred Silva  feat(repositorio): porta e adaptador SQLite  
|/  
*   af8ae91 2026-09-26 Barney Lima  Merge branch 'feat/dominio'  
|\  
| * d403b62 2026-09-26 Barney Lima  fix(test): corrige expectativas erradas de preço e suspensão (DEF-T1, DEF-T2)  
| * 8943728 2026-09-25 Barney Lima  test(unitarios): valores-limite de vagas, saída, preço, taxa e suspensão  
| * 4a3fe59 2026-09-23 Barney Lima  feat(dominio): regras RN-01 a RN-05 como funções puras e entidades  
|/  
* 94afed9 2026-09-21 Betty Costa  docs(casos): 16 casos de teste com técnica e requisito  
* 61001fb 2026-09-16 Fred Silva  docs(plano): plano de teste com escopo, estratégia, critérios e riscos  
* d5725e4 2026-09-14 Wilma Souza  chore: estrutura inicial do repositório  
```  

Convenções: Conventional Commits (`feat`, `fix`, `test`, `docs`, `build`, `ci`, `chore`), um commit por mudança lógica, `fix(...)` citando o defeito (`DEF-001`), `CHANGELOG.md` por versão e `.gitignore` (`*.db`, `__pycache__`, `.env`).  

---  

## 9. Uso de IA (obrigatório na A3)  

### 9.1 Configuração versionada  

`opencode.json`  

```json  
{  
  "$schema": "https://opencode.ai/config.json",  
  "model": "openrouter/nvidia/nemotron-3-ultra-550b-a55b:free",  
  "permission": {  
    "bash": {  
      "*": "ask",  
      "git status*": "allow",  
      "git diff*": "allow",  
      "git log*": "allow",  
      "git push*": "deny"  
    }  
  }  
}  
```  

`AGENTS.md`  

```markdown  
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
```  

### 9.2 Registro de uso (seção do README)  

**Ferramenta e modelo:** OpenCode + Nemotron 3 Ultra (`:free`) via OpenRouter, na configuração da Aula 01B. **Skills:** `grilling`, `to-spec`, `tdd`, `caveman-review`.  

### Registros (9 registros, 6 tipos de artefato)  

| # | Artefato | Pedido (resumo) | O que o agente errou ou omitiu | O que o grupo fez |  
|---|---|---|---|---|  
| 1 | `plano-de-teste.md` | `grilling` + `to-spec` sobre escopo e riscos | Listou só o que seria testado; **não** disse o que ficou fora nem deu mitigação aos riscos | Acrescentou a tabela "Fora do escopo" e a coluna de mitigação |  
| 2 | `servico.py` (reserva) | Skill `tdd` para a regra de lotação (RN-01) | Implementou "contar as reservas e depois gravar" e afirmou que a lotação estava garantida; o teste com 6 threads aceitou **até 6 reservas para 1 vaga** (DEF-001) | Moveu contagem e gravação para a mesma transação (`BEGIN IMMEDIATE`) no repositório e criou índice único; repetiu o teste 200 vezes sem falha |  
| 3 | `repositorio.py` | Adaptador SQLite | Usou `with sqlite3.connect()` achando que fechava a conexão; ela só confirma a transação (DEF-002). Centenas de `ResourceWarning` | Criou um `contextmanager` que fecha; `filterwarnings = error` no `pytest.ini`; mutante M18 protege |  
| 4 | `tests/unitarios/test_regras.py` | Valores-limite do preço (RN-03) e da suspensão (RN-05) | Calculou `10,01 ÷ (4+1)` como 2,5025 (é 2,002 → 2,01) e marcou "não suspende" para 6 avaliações com média 2,5 | Refez as contas à mão, conferiu linha por linha; os dois testes erravam, o código estava certo |  
| 5 | `postman/` | Traduzir os casos em requests | Gerou requests só com `status 200`, sem asserções sobre o corpo, e com `localhost` fixo | Pediu asserções por campo e `{{base_url}}`; quebrou a API de propósito (mutantes) e viu o Newman falhar |  
| 6 | `.github/workflows/ci.yml` | Rascunho com build, teste e cobertura | Colocou `continue-on-error: true` no passo de testes | Removeu; quebrou um teste, fez push e viu o job vermelho |  
| 7 | `relatorio-testes.md` | Rascunho da conclusão | Concluiu "100% de cobertura, portanto sem defeitos" | Reescreveu a conclusão com mutação, defeitos e risco residual |  
| 8 | `tests/integracao/` | Revisar se a lotação estava protegida | Disse que o teste de concorrência bastava; numa rodada de mutação o mutante M17 (`>=` → `>` no repositório) **sobreviveu**: o teste é probabilístico | Acrescentou teste determinístico direto no repositório; M17 passou a morrer sempre |  
| 9 | `servico.py` (alterar viagem) | Caso de uso de alteração (`PUT`) | Validou origem e destino só na **criação**; na alteração aceitava vazios (DEF-003). Nenhum teste cobria o caminho | Achado ao portar o sistema para Java (a versão Java validava); 4 testes novos falham no código antigo; mutante M21 protege |  

**Testes escritos pelo agente que o grupo quebrou de propósito:** todos os de regra (RN-01 a RN-05), pelos 21 mutantes de `scripts/mutacao.py`.  

### Reflexão do grupo  

- **Onde a IA mais ajudou:** gerar a grade de valores-limite e a coleção Postman — trabalho repetitivo que o grupo só revisou.  
- **Onde mais errou:** concorrência e recursos (itens 2 e 3), **aritmética** nos valores esperados (item 4) e uma validação esquecida num caminho que ninguém testou (item 9). Em nenhum caso o erro apareceu sem execução ou revisão.  
- **O que faria diferente:** pedir o teste de concorrência **antes** do código da reserva, e rodar a mutação mais cedo.  
- **Regra que ficou no `AGENTS.md`:** "teste novo só vale se falhar quando o código é quebrado de propósito".  

---  

## 10. Demonstração (12 min) e Defesa Oral  

### 10.1 Roteiro  

| Parte | Tempo | O que acontece na tela |  
|---|---|---|  
| **Plano de Teste** | 2 min | "É uma API de caronas com 5 regras de negócio." Mostra o escopo **e o que ficou de fora**, e o risco R-02 (corrida na última vaga) |  
| **Casos de Teste** | 2 min | CT-04 (preço, arredondamento **para cima**), CT-09 (taxa em 59 min 59 s) e CT-06 (lotação). Para cada um: técnica e requisito |  
| **Testes automatizados** | 3 min | `docker compose run --rm testes`: 172 verdes, 100%. **Quebra de propósito:** troca `>=` por `>` em `taxa_de_cancelamento`, roda de novo, aponta o teste vermelho, desfaz |  
| **Testes de API** | 2 min | O Newman: 58 requests, 95 asserções. Mostra um caso negativo (CT-07, 409) e diz o que a coleção **não** prova |  
| **Pipeline** | 2 min | A execução verde na aba Actions e o que cada um dos 4 jobs faz |  
| **Relatório e IA** | 1 min | A conclusão ("liberar com 2 ressalvas") e o DEF-001, que o agente escreveu e o teste achou |  

### 10.2 Perguntas da defesa, com as respostas esperadas  

**"Por que estes 16 casos, e não outros?"**  
Cada regra de negócio tem um caso por **valor-limite** (CT-02, 03, 04, 08, 09, 10, 12), as reservas inválidas formam uma **tabela de decisão** (CT-07), os estados da reserva e do motorista seguem **transição de estados** (CT-10, CT-13), e os dois casos de maior risco (concorrência e autenticação) vieram da **análise de risco** do plano (CT-06, CT-15). A matriz `scripts/matriz_rastreabilidade.py` prova que nenhum requisito importante ficou sem caso.  

**"O que este teste detectaria se quebrasse?"** *(aponta `test_ct09_com_menos_de_60_min_a_taxa_e_metade_do_valor`)*  
Se alguém trocasse `>=` por `>` no limite de 60 minutos, o caso `faltando exatamente 60 min` passaria a cobrar taxa. É o mutante M01, e o teste que o pega é `test_ct08_ct09_taxa_no_limite_de_60_minutos`.  

**"Sua cobertura é 100%. Isso quer dizer que o sistema está certo?"**  
Não. Quer dizer que todas as linhas rodaram. Com 100% de cobertura ainda passava o defeito de lotação (M17 sobreviveu numa rodada). Por isso medimos também a mutação: 21 de 21. E `app.main()` está fora da conta, com o motivo registrado.  

**"Este teste é unitário ou de integração?"** *(aponta `test_ct08_ct09_taxa_no_limite_de_60_minutos`, em `tests/integracao/`)*  
De integração: usa o serviço **e** o SQLite real, e confere que a taxa foi **gravada** no banco. O equivalente unitário é `test_ct09_com_menos_de_60_min...`, que testa só a função pura, sem banco.  

**"O que aconteceria se este mock não existisse?"** *(aponta o `Mock` do repositório)*  
O teste de `reservar` teria de usar o SQLite: ficaria mais lento e **não conseguiria simular** que outra requisição levou a vaga entre a checagem e a gravação. O mock é o que torna esse cenário determinístico.  

**"O agente escreveu este teste? O que você mudou nele?"** *(aponta `test_ct04_preco_por_vaga`)*  
A grade de valores foi proposta pelo agente, e ele errou a conta de uma linha: `10,01 ÷ (4+1)` esperado como 2,51, quando é 2,002 → 2,01. O teste falhou, a dupla refez a conta à mão e viu que o erro era do **teste**, não do código. Está no registro de uso de IA (linha 4) e como DEF-T1.  

---  

## 11. Autoavaliação pela Rubrica (40 pontos)  

| Critério | Pts | Nota | Justificativa |  
|---|---:|---:|---|  
| Plano de Teste | 8 | 8 | Escopo com "fora de escopo", RF/RN/RNF mensuráveis, estratégia por nível e técnica, critérios de entrada/saída/suspensão e 5 riscos com mitigação, todos ligados ao sistema |  
| Casos de Teste | 8 | 8 | 16 casos com técnica, valores-limite exatos, negativos e matriz de rastreabilidade conferida por script |  
| Testes Automatizados | 8 | 8 | 172 testes em AAA, unitários e de integração, 100% de cobertura **lida**, 21/21 mutantes, roda com um comando em Docker |  
| Testes de API | 5 | 5 | CRUD, 95 asserções de status e conteúdo, negativos, `{{base_url}}`, Newman no compose. A limitação (limites exatos) está documentada |  
| Pipeline | 5 | **3** | Workflow completo e todos os passos executados à mão, mas **sem execução no GitHub Actions**. Na rubrica, "workflow nunca executado" é nota parcial |  
| Relatório | 2 | 2 | Seis seções, defeitos reais, métricas e conclusão com ressalvas |  
| Uso de IA | 2 | 2 | 9 registros, 6 tipos de artefato, 5 erros reais, reflexão, `AGENTS.md` coerente |  
| Apresentação | 2 | — | Depende da apresentação; o roteiro está na seção 10 |  
| **Total (sem a apresentação)** | **38** | **36** | **36 de 38**; o que falta é a execução verde no Actions |  

> **Atenção:** a autoavaliação serve para o grupo se corrigir **antes** da entrega. Um grupo com a nota de pipeline em 3 tem uma tarefa clara: rodar o workflow.  

---  

## 12. Versão em Java  

O mesmo sistema, em **Java 21 + JDK `HttpServer` + SQLite/JDBC**, com **JUnit 5, Mockito e JaCoCo**: [`a3-caronacampus-java`](https://github.com/giovanebarcelos/gqs-sandbox/tree/main/class29/a3-caronacampus-java). Mesmas regras, mesmos 16 casos e a **mesma coleção Postman**, que passa sem alteração contra a API em Java (**58 requests, 95 asserções, 0 falhas**: o contrato HTTP é o mesmo).  

| Medida | Python | Java |  
|---|---|---|  
| Testes | 172 (105 unitários, 67 de integração) | 149 (83 unitários, 66 de integração) |  
| Cobertura | 100% (instrução e ramo) | 95,3% de instruções · 87,7% de ramos |  
| Mutação manual | 21/21 (49 s) | 20/20 (6 min 29 s, dentro do contêiner) |  
| Newman | 58 requests · 95 asserções · 0 falhas | 58 requests · 95 asserções · 0 falhas |  
| Teste da API | Cliente de teste do Flask, em processo | Servidor real em porta livre + `HttpClient` |  
| `docker compose run --rm testes` | pytest, ≈ 10 s | `mvn -o verify`, ≈ 20 s (a primeira vez baixa o Maven) |  

**O que a versão Java ensina de diferente:**  

- **Aprender com o Python.** O Java nasceu com a transação única na reserva, as conexões fechadas por `try-with-resources` e a validação na alteração. Por isso **não achou os 3 defeitos do produto**, e os testes equivalentes provam que as correções valem também aqui. Isso é "defeito evitado", não "código sem defeito".  
- **A revisão comparativa achou um defeito do Python** (DEF-003). Escrever o mesmo sistema duas vezes é uma forma de inspeção.  
- **Armadilha do Mockito:** `when(repo.metodo(any())).thenReturn(...)` **executa o stub anterior** com argumentos nulos e dá `NullPointerException`; use `doReturn(...).when(repo).metodo(...)`.  
- **O JaCoCo não chegou a 100%** e o relatório explica o que está nos 5%: `App.main`, os `catch (SQLException)` e o ramo 500 da API. Cobertura menor e explicada vale mais que 100% sem leitura.  

**O que a versão Java não tem:** um pipeline próprio (`ci.yml`). **Uma entrega em Java precisa do seu workflow (`actions/setup-java@v4` e `mvn verify`) e do seu relatório.**  

---  

## Apêndice A — Como reproduzir  

```bash  
# Python  
git clone <repositório>            # ou descompacte a-caronacampus-python.zip  
cd a3-caronacampus  
docker compose up --build                       # API em http://localhost:5000  
docker compose run --rm testes                  # 172 testes, 100% de cobertura  
docker compose --profile api up --build --abort-on-container-exit --exit-code-from newman  
python scripts/mutacao.py                       # 21 mutantes (precisa de pytest)  
python scripts/matriz_rastreabilidade.py        # casos x testes  

# Java  
cd ../a3-caronacampus-java  
docker compose run --rm testes                  # 149 testes JUnit + JaCoCo  
docker compose --profile api up --build --abort-on-container-exit --exit-code-from newman  
docker compose run --rm testes bash scripts/mutacao.sh   # 20 mutantes  
```  

---  

## Apêndice B — Checklist de entrega (Aula 29, Slide 10)  

- [x] Plano de Teste: escopo positivo e negativo, critérios de entrada e saída, riscos com mitigação  
- [x] Casos de Teste: 16 casos, pré-condição, dados, passos, resultado esperado, técnica e requisito  
- [x] Testes automatizados: 172 testes, cobertura de 100% (mínimo 70%), padrão AAA  
- [x] Testes de API: coleção com CRUD, asserções, `{{base_url}}` e Newman  
- [x] `Dockerfile` e `docker-compose.yml` na raiz; os comandos funcionam numa cópia limpa  
- [ ] **Pipeline: execução verde na aba Actions** (no exemplo, passos executados à mão)  
- [x] Relatório de testes com as 6 seções e conclusão  
- [x] Uso de IA: ferramenta, modelo, skills, 9 registros, 5 erros reais, reflexão  
- [x] `AGENTS.md` e `opencode.json` versionados e coerentes  
- [x] Todo teste escrito pelo agente foi quebrado de propósito e falhou  
- [x] Commits ao longo das semanas, de todos, em Conventional Commits; `.gitignore`; `CHANGELOG.md`; tag  
- [x] Nenhuma credencial no repositório  

---  

## Referências  

- Material da disciplina: Aulas 1-30 e Aula 01B (IA assistida).  
- MYERS, Glenford J. *The Art of Software Testing*. 3. ed. Wiley, 2011.  
- ISTQB. *Certified Tester Foundation Level Syllabus* (CTFL 4.0).  
- IEEE Std 829-2008. *Standard for Software and System Test Documentation*.  
- Enunciado: [A3 — Projeto Integrador de Garantia da Qualidade de Software](A3_Projeto_GQS.md).  
