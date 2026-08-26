.

---

# ✅ **Tabela de Comandos — Appium e Maestro**

## Appium (Java client / Python client)

| **Comando**                              | **Descrição**                                   | **Exemplo**                                                        |
| ----------------------------------------- | ------------------------------------------------ | ------------------------------------------------------------------- |
| `driver.findElement(AppiumBy.id(x))`      | Localiza elemento por resource-id.               | `driver.findElement(AppiumBy.id("pkg:id/botao"))`                   |
| `driver.findElement(AppiumBy.accessibilityId(x))` | Localiza por rótulo de acessibilidade.    | `AppiumBy.accessibilityId("Login")`                                  |
| `driver.findElement(AppiumBy.xpath(x))`   | Localiza por XPath (último recurso).             | `AppiumBy.xpath("//android.widget.Button[@text='=']")`              |
| `element.click()`                         | Clica/toca no elemento.                          | `driver.findElement(...).click()`                                   |
| `element.sendKeys(texto)` / `.send_keys()`| Digita texto no elemento focado.                 | `campo.sendKeys("admin")`                                            |
| `element.getText()` / `.text`             | Lê o texto exibido no elemento.                  | `String r = driver.findElement(...).getText();`                     |
| `driver.perform(actions)`                 | Executa uma sequência de gestos (W3C Actions).   | `driver.perform(List.of(swipeSequence))`                            |
| `driver.executeScript("mobile: swipeGesture", args)` | Gesto simplificado via mobile command. | `driver.execute_script("mobile: swipeGesture", {...})`               |
| `driver.activateApp(pkg)`                 | Traz o app para primeiro plano.                  | `driver.activateApp("com.google.android.calculator")`                |
| `driver.terminateApp(pkg)`                | Encerra o app.                                   | `driver.terminateApp("com.google.android.calculator")`               |
| `driver.rotate(orientation)`              | Rotaciona a tela (landscape/portrait).           | `driver.rotate(ScreenOrientation.LANDSCAPE)`                         |
| `driver.quit()`                           | Encerra a sessão do driver.                      | `driver.quit()`                                                      |
| `appium driver install <nome>`            | Instala um driver de plataforma (CLI).           | `appium driver install uiautomator2`                                 |
| `appium-doctor --android`                 | Valida o ambiente para Android (CLI).            | `appium-doctor --android`                                            |

---

## Maestro (Flow YAML / CLI)

| **Comando**                | **Descrição**                                     | **Exemplo**                              |
| --------------------------- | -------------------------------------------------- | ----------------------------------------- |
| `launchApp`                 | Abre o app declarado em `appId`.                    | `- launchApp`                             |
| `tapOn`                      | Toca em um elemento (texto ou seletor).             | `- tapOn: "Entrar"`                       |
| `longPressOn`                | Toque longo em um elemento.                         | `- longPressOn: "Item"`                   |
| `inputText`                  | Digita texto no campo focado.                       | `- inputText: "admin"`                    |
| `assertVisible`              | Falha se o elemento não estiver visível.            | `- assertVisible: "Bem-vindo"`            |
| `assertNotVisible`           | Falha se o elemento estiver visível.                | `- assertNotVisible: "Erro"`              |
| `swipe`                      | Realiza um gesto de arraste.                        | `- swipe: {direction: UP}`                |
| `scroll`                     | Rola a tela até o fim.                              | `- scroll`                                |
| `back`                       | Aciona o botão "voltar" do sistema.                 | `- back`                                  |
| `pressKey`                   | Pressiona uma tecla do sistema (Home, Enter...).    | `- pressKey: Enter`                       |
| `takeScreenshot`             | Salva uma captura de tela.                          | `- takeScreenshot: resultado`             |
| `waitForAnimationToEnd`      | Aguarda animações terminarem.                       | `- waitForAnimationToEnd`                 |
| `stopApp`                    | Encerra o app.                                      | `- stopApp`                               |
| `clearState`                 | Limpa dados/estado do app.                          | `- clearState`                            |
| `maestro test <flow>`        | Executa um flow (CLI).                              | `maestro test calculadora.yaml`           |
| `maestro studio`             | Abre inspetor visual de hierarquia (CLI).           | `maestro studio`                          |
| `maestro record <flow>`      | Grava interações e gera um flow (CLI).              | `maestro record calculadora.yaml`         |

---
