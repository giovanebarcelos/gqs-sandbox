.  
  
---  
  
# ✅ **Tabela de Comandos do SikuliX**  
  
| **Comando**                      | **Descrição**                                | **Exemplo**                                    |  
| -------------------------------- | -------------------------------------------- | ---------------------------------------------- |  
| `click(img)`                     | Clica na imagem indicada na tela.            | `click("botao_login.png")`                     |  
| `doubleClick(img)`               | Dá duplo clique na imagem.                   | `doubleClick("icone.png")`                     |  
| `rightClick(img)`                | Clica com botão direito na imagem.           | `rightClick("arquivo.png")`                    |  
| `hover(img)`                     | Move o mouse até a imagem.                   | `hover("menu.png")`                            |  
| `type(text)`                     | Digita texto no foco atual.                  | `type("admin")`                                |  
| `type(img, text)`                | Clica na imagem e digita o texto.            | `type("campo_usuario.png", "admin")`           |  
| `paste(text)`                    | Cola texto (com suporte UTF-8).              | `paste("José da Silva")`                       |  
| `paste(img, text)`               | Clica na imagem e cola o texto.              | `paste("campo_nome.png", "José")`              |  
| `wait(img, sec)`                 | Aguarda a imagem aparecer até X segundos.    | `wait("loading.png", 10)`                      |  
| `exists(img)`                    | Verifica se a imagem existe na tela.         | `if exists("ok.png"):`                         |  
| `find(img)`                      | Encontra a imagem e retorna um objeto Match. | `m = find("icone.png")`                        |  
| `findAll(img)`                   | Encontra todas as ocorrências da imagem.     | `for m in findAll("estrela.png"):`             |  
| `sleep(sec)`                     | Pausa o script por X segundos.               | `sleep(2)`                                     |  
| `dragDrop(src, dst)`             | Arrasta imagem origem para destino.          | `dragDrop("arquivo.png", "pasta.png")`         |  
| `drag(img)`                      | Inicia drag de um elemento.                  | `drag("arq.png")`                              |  
| `dropAt(loc)`                    | Solta o elemento arrastado em um ponto.      | `dropAt("pasta.png")`                          |  
| `wheel(img, steps)`              | Rola o scroll na imagem (positivo sobe).     | `wheel("lista.png", -3)`                       |  
| `Key`                            | Conjunto de teclas especiais.                | `type(Key.ENTER)`                              |  
| `KeyModifier`                    | Usado para combinações de teclas.            | `type("a", KeyModifier.CTRL)`                  |  
| `SCREEN`                         | Acessa a tela padrão.                        | `SCREEN.capture()`                             |  
| `Region(x,y,w,h)`                | Cria uma região da tela.                     | `reg = Region(100,100,200,200)`                |  
| `reg.click(img)`                 | Clica dentro da região.                      | `reg.click("ok.png")`                          |  
| `reg.exists(img)`                | Busca somente naquela região.                | `if reg.exists("alerta.png"):`                 |  
| `capture()`                      | Captura screenshot de toda a tela.           | `f = capture()`                                |  
| `capture(img)`                   | Captura área ao redor de uma imagem.         | `capture("campo.png")`                         |  
| `App.open(name)`                 | Abre uma aplicação.                          | `App.open("notepad.exe")`                      |  
| `App.focus(name)`                | Foca aplicação pelo nome.                    | `App.focus("Chrome")`                          |  
| `App.close(name)`                | Fecha uma aplicação.                         | `App.close("notepad.exe")`                     |  
| `Pattern(img).similar(x)`        | Ajusta tolerância de reconhecimento.         | `click(Pattern("icon.png").similar(0.7))`      |  
| `Pattern(img).targetOffset(x,y)` | Clica em posição relativa à imagem.          | `click(Pattern("btn.png").targetOffset(20,0))` |  
| `Settings.MoveMouseDelay`        | Ajusta velocidade do mouse.                  | `Settings.MoveMouseDelay = 0`                  |  
| `Settings.MinSimilarity`         | Define similaridade padrão.                  | `Settings.MinSimilarity = 0.7`                 |  
| `popup(text)`                    | Exibe popup simples.                         | `popup("Teste finalizado")`                    |  
| `print(msg)`                     | Imprime no console.                          | `print("Iniciando teste...")`                  |  
  
---  
  
  
