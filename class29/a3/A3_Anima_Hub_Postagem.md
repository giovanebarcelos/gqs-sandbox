# A3 — Texto de Postagem no Anima Hub  

> Material para o **professor**: o que colar e anexar ao criar a atividade A3 no Anima Hub. Não é distribuído aos alunos.  

---  

## Configuração sugerida da atividade  

| Campo | Sugestão |  
|---|---|  
| **Título** | A3 — Projeto Integrador: plano de qualidade e automação de testes, com agente de IA |  
| **Valor** | 40 pontos |  
| **Modalidade** | Em dupla (trio só com autorização); uma entrega por dupla |  
| **Tipo de envio** | Arquivo (`.pdf` da capa e `.zip` do repositório) e link (GitHub) |  
| **Tentativas** | 1 envio por dupla (reenvio só antes do prazo) |  
| **Prazo** | Antes da Aula 30 (definir a data no Hub) |  
| **Anexos do professor** | `A3_Projeto_GQS` (enunciado), `A3_Exemplo_CaronaCampus` (exemplo de referência) e **dois zips** de repositório de referência: [`a3-caronacampus-python.zip`](https://raw.githubusercontent.com/giovanebarcelos/gqs-sandbox/main/class29/a3/a3-caronacampus-python.zip) (**Python**) e [`a3-caronacampus-java.zip`](https://raw.githubusercontent.com/giovanebarcelos/gqs-sandbox/main/class29/a3/a3-caronacampus-java.zip) (**Java**) |  

> Ajuste ao que o Anima Hub oferecer para a sua turma (nomes de campos, tentativas, grupos). Este roteiro foi escrito sem acesso à configuração da atividade no portal.  

---  

## Descrição (para colar no campo de texto da atividade)  

**A3 — Projeto Integrador (40 pontos)**  

Em dupla, vocês vão escolher um sistema pequeno, com regras de negócio de verdade, e entregar o que garante a sua qualidade: **plano de teste, casos de teste, testes automatizados, testes de API (Postman/Newman), pipeline no GitHub Actions e relatório de testes com conclusão**. O que se avalia não é o sistema, e sim a evidência de que vocês sabem testá-lo. O trabalho é feito **com o agente de IA da disciplina** (Aula 01B), e o uso é **declarado** e **defendido oralmente** na Aula 30.  

**O sistema é de livre escolha**, desde que tenha pelo menos 3 regras de negócio com valores-limite testáveis (só cadastrar e listar não basta) e uma API HTTP. Mostrem o tema ao professor antes de se comprometerem (até a Aula 4).  

**Critérios (40 pts):** Plano de Teste 8 · Casos de Teste 8 · Testes Automatizados 8 · Testes de API 5 · Pipeline CI/CD 5 · Relatório e Uso de IA 4 (2 + 2) · Apresentação 2.  

**Uso do agente de IA (obrigatório):** configurem o OpenCode conforme a Aula 01B, usem as skills nas etapas indicadas no enunciado e registrem no README, em uma tabela, o que pediram, **o que o agente errou** e o que a dupla corrigiu (mínimo 5 registros e 2 erros reais). **Teste escrito por IA só vale depois de falhar quando o código é quebrado de propósito.** Artefato que a dupla não sabe explicar é avaliado como Insuficiente, com ou sem IA.  

**Linguagem:** **Python ou Java** (uma só por projeto). Há um exemplo de referência em cada uma, com o mesmo sistema e a mesma coleção Postman.  

**Entrega com Docker Compose (obrigatório):** o repositório traz `Dockerfile` e `docker-compose.yml` na raiz, de modo que `docker compose up --build` suba o sistema e `docker compose run --rm testes` rode os testes com cobertura, numa máquina que só tenha o Docker. No exemplo Java, a primeira execução baixa as dependências do Maven e leva alguns minutos.  

**Pipeline:** o workflow do GitHub Actions precisa ter **pelo menos uma execução verde**; o link dela vai na capa da entrega. Workflow que nunca rodou vale zero.  

**Apresentação (Aula 30):** 12 minutos, com demonstração ao vivo (incluindo um teste falhando de propósito) e as perguntas do professor.  

**Material de apoio (em anexo):**  
- Enunciado completo, com rubrica detalhada e checklist  
- Exemplo completo de referência: CaronaCampus (use como guia, não copie)  
- Repositório de referência em **Python** ([`a3-caronacampus`](https://github.com/giovanebarcelos/gqs-sandbox/tree/main/class29/a3-caronacampus)): 172 testes, 100% de cobertura, coleção Postman, `docker-compose.yml`, workflow e relatório  
- Repositório de referência em **Java** ([`a3-caronacampus-java`](https://github.com/giovanebarcelos/gqs-sandbox/tree/main/class29/a3-caronacampus-java)): 149 testes JUnit, JaCoCo, a mesma coleção Postman e `docker-compose.yml`  

---  

## O que a dupla deve postar  

1. **Capa da entrega (PDF, 1 página):** nome do projeto, integrantes (nome e RA), link do repositório, tag da entrega, hash do commit e **link da execução verde** na aba Actions  
2. **Repositório completo (`.zip`)**, incluindo a pasta `.git` e os arquivos `Dockerfile` e `docker-compose.yml`, sem `.venv`, `target` nem `node_modules`  
3. **Link do repositório no GitHub** (público, ou com o professor como colaborador), no campo de texto  

**Teste antes de postar:** descompacte o zip em outra pasta e rode `docker compose up --build` e `docker compose run --rm testes`; peça a um colega de outra dupla para clonar o repositório e rodar a suíte seguindo só o README.  

Mudanças depois do prazo não são consideradas: vale o commit da tag.  

---  

## Mensagem de aviso à turma (opcional)  

> Pessoal, a A3 está publicada no Anima Hub. O sistema a testar é de livre escolha, mas precisa ter regras de negócio com número (valores-limite): tragam o tema para eu aprovar até a Aula 4. Há um exemplo completo (CaronaCampus), em Python e em Java, com plano, casos, testes, coleção Postman, workflow e relatório. A entrega precisa rodar com `docker compose` (Dockerfile e docker-compose na raiz) e o workflow precisa ter uma execução verde no GitHub, então comecem o pipeline cedo, mesmo com um teste só. O uso do agente de IA é obrigatório e vale pontos, mas o que vale mesmo é saber explicar o que entregaram. Dúvidas no fórum da atividade.  
