  
  
A seguir, apresento **a versão equivalente em Python**, **com o mesmo conteúdo, estrutura e nível de detalhe** — adaptada para `pytest` + `selenium` + `webdriver-manager`, o padrão moderno e simples no ecossistema Python.  
  
---  
  
# 🧪 **Laboratórios de Selenium com Python (equivalente ao GSQ0500 em Java)**  
  
---  
  
## **🔹 Laboratório 1 – Instalação e Primeiro Teste no Selenium**  
  
### 🎯 Objetivo  
  
Configurar o ambiente Selenium em Python e abrir o primeiro site automaticamente.  
  
### 🧰 Pré-requisitos  
  
- Python 3.10+  
  
- pip  
  
- Navegador **Google Chrome** instalado  
  
---  
  
### 1️⃣ Instalar dependências  
  
```bash  
pip install selenium webdriver-manager pytest  
```  
  
---  
  
### 2️⃣ Criar o teste `test_open_google.py`  
  
```python  
from selenium import webdriver  
from selenium.webdriver.common.by import By  
from webdriver_manager.chrome import ChromeDriverManager  
  
def test_abrir_google():  
    driver = webdriver.Chrome(ChromeDriverManager().install())  
    driver.get("https://www.google.com")  
    assert "Google" in driver.title  
    driver.quit()  
```  
  
---  
  
### 3️⃣ Executar o teste  
  
```bash  
pytest -v test_open_google.py  
```  
  
> 🟢 **Resultado esperado:** O Chrome abre o Google, verifica o título e fecha automaticamente.  
  
---  
  
## **🔹 Laboratório 2 – Localizando Elementos e Fazendo Interações**  
  
### 🎯 Objetivo  
  
Aprender a localizar elementos (`id`, `name`, `css selector`, `xpath`) e interagir com campos e botões.  
  
---  
  
### 1️⃣ Criar o teste `test_login_form.py`  
  
```python  
from selenium import webdriver  
from selenium.webdriver.common.by import By  
from webdriver_manager.chrome import ChromeDriverManager  
import pytest  
  
@pytest.fixture  
def driver():  
    driver = webdriver.Chrome(ChromeDriverManager().install())  
    yield driver  
    driver.quit()  
  
def test_login_sucesso(driver):  
    driver.get("https://www.saucedemo.com/")  
    driver.find_element(By.ID, "user-name").send_keys("standard_user")  
    driver.find_element(By.ID, "password").send_keys("secret_sauce")  
    driver.find_element(By.ID, "login-button").click()  
    assert "inventory" in driver.current_url  
```  
  
---  
  
### 2️⃣ Executar  
  
```bash  
pytest -v test_login_form.py  
```  
  
🟢 **Resultado esperado:** o navegador faz login e muda para a tela de produtos.  
  
---  
  
## **🔹 Laboratório 3 – Esperas, Alerts e Dropdowns**  
  
### 🎯 Objetivo  
  
Lidar com **esperas explícitas**, **alertas JavaScript** e **menus suspensos (Select)**.  
  
---  
  
### 1️⃣ Criar o teste `test_waits_alerts.py`  
  
```python  
from selenium import webdriver  
from selenium.webdriver.common.by import By  
from selenium.webdriver.support.ui import WebDriverWait, Select  
from selenium.webdriver.support import expected_conditions as EC  
from webdriver_manager.chrome import ChromeDriverManager  
import pytest  
  
@pytest.fixture  
def driver():  
    driver = webdriver.Chrome(ChromeDriverManager().install())  
    yield driver  
    driver.quit()  
  
def test_alert(driver):  
    driver.get("https://the-internet.herokuapp.com/javascript_alerts")  
    driver.find_element(By.XPATH, "//button[text()='Click for JS Alert']").click()  
    WebDriverWait(driver, 10).until(EC.alert_is_present())  
    alert = driver.switch_to.alert  
    assert alert.text == "I am a JS Alert"  
    alert.accept()  
  
def test_dropdown(driver):  
    driver.get("https://the-internet.herokuapp.com/dropdown")  
    select = Select(driver.find_element(By.ID, "dropdown"))  
    select.select_by_visible_text("Option 2")  
    assert select.first_selected_option.text == "Option 2"  
```  
  
🟢 **Resultado esperado:**  
O primeiro teste aceita um alerta, e o segundo seleciona uma opção do dropdown corretamente.  
  
---  
  
## **🔹 Laboratório 4 – Page Object Model (POM)**  
  
### 🎯 Objetivo  
  
Organizar o código separando **páginas** de **testes**.  
  
---  
  
### 1️⃣ Estrutura de diretórios  
  
```  
selenium_python_pom/  
 ├── pages/  
 │    ├── login_page.py  
 │    └── inventory_page.py  
 └── tests/  
      └── test_sauce_demo_pom.py  
```  
  
---  
  
### 2️⃣ `pages/login_page.py`  
  
```python  
from selenium.webdriver.common.by import By  
  
class LoginPage:  
    def __init__(self, driver):  
        self.driver = driver  
        self.username = (By.ID, "user-name")  
        self.password = (By.ID, "password")  
        self.login_btn = (By.ID, "login-button")  
  
    def open(self):  
        self.driver.get("https://www.saucedemo.com/")  
  
    def login(self, user, pwd):  
        self.driver.find_element(*self.username).send_keys(user)  
        self.driver.find_element(*self.password).send_keys(pwd)  
        self.driver.find_element(*self.login_btn).click()  
```  
  
---  
  
### 3️⃣ `pages/inventory_page.py`  
  
```python  
from selenium.webdriver.common.by import By  
  
class InventoryPage:  
    def __init__(self, driver):  
        self.driver = driver  
        self.title = (By.CLASS_NAME, "title")  
  
    def is_displayed(self):  
        return self.driver.find_element(*self.title).is_displayed()  
```  
  
---  
  
### 4️⃣ `tests/test_sauce_demo_pom.py`  
  
```python  
import pytest  
from selenium import webdriver  
from webdriver_manager.chrome import ChromeDriverManager  
from pages.login_page import LoginPage  
from pages.inventory_page import InventoryPage  
  
@pytest.fixture  
def driver():  
    driver = webdriver.Chrome(ChromeDriverManager().install())  
    yield driver  
    driver.quit()  
  
def test_login_pom(driver):  
    login = LoginPage(driver)  
    inventory = InventoryPage(driver)  
    login.open()  
    login.login("standard_user", "secret_sauce")  
    assert inventory.is_displayed()  
```  
  
🟢 **Resultado esperado:**  
O código segue o padrão POM (Page Object Model), mais limpo e reutilizável.  
  
---  
  
## **🔹 Laboratório 5 – Screenshots e Captura de Erros**  
  
### 🎯 Objetivo  
  
Salvar **evidências automáticas (screenshots)** em caso de falha.  
  
---  
  
### 1️⃣ `test_screenshot_on_fail.py`  
  
```python  
import pytest  
from selenium import webdriver  
from selenium.webdriver.common.by import By  
from webdriver_manager.chrome import ChromeDriverManager  
  
@pytest.fixture  
def driver():  
    driver = webdriver.Chrome(ChromeDriverManager().install())  
    yield driver  
    driver.quit()  
  
def test_screenshot(driver):  
    driver.get("https://www.saucedemo.com/")  
    try:  
        driver.find_element(By.ID, "user-name").send_keys("erro")  
        assert "Inventário" in driver.title  
    except AssertionError:  
        driver.save_screenshot("screenshot_fail.png")  
        raise  
```  
  
🟢 **Resultado esperado:**  
Quando o teste falhar, o arquivo `screenshot_fail.png` será criado com a captura de tela.  
  
---  
  
## **🔹 Laboratório 6 – Execução Headless, Paralela e CI/CD**  
  
### 🎯 Objetivo  
  
Rodar os testes **sem interface gráfica (headless)**, **em paralelo** e integrá-los com **GitHub Actions ou Jenkins**.  
  
---  
  
### 1️⃣ Configurar o modo **headless**  
  
Crie o fixture `driver()` genérico:  
  
```python  
import pytest  
from selenium import webdriver  
from selenium.webdriver.chrome.options import Options  
from webdriver_manager.chrome import ChromeDriverManager  
  
@pytest.fixture  
def driver():  
    options = Options()  
    options.add_argument("--headless=new")  
    options.add_argument("--no-sandbox")  
    options.add_argument("--disable-dev-shm-usage")  
    driver = webdriver.Chrome(ChromeDriverManager().install(), options=options)  
    yield driver  
    driver.quit()  
```  
  
---  
  
### 2️⃣ Rodar testes em paralelo com `pytest-xdist`  
  
```bash  
pip install pytest-xdist  
pytest -n 4  
```  
  
> Executa até 4 testes simultaneamente.  
  
---  
  
### 3️⃣ Integração com **GitHub Actions**  
  
Arquivo: `.github/workflows/selenium.yml`  
  
```yaml  
name: Selenium Tests  
on: [push, pull_request]  
jobs:  
  selenium:  
    runs-on: ubuntu-latest  
    steps:  
      - uses: actions/checkout@v4  
      - name: Set up Python  
        uses: actions/setup-python@v5  
        with:  
          python-version: "3.11"  
      - name: Install dependencies  
        run: |  
          pip install selenium webdriver-manager pytest pytest-xdist  
      - name: Run Selenium tests (headless)  
        run: pytest -n 4 -v  
```  
  
---  
  
### 4️⃣ Jenkinsfile (opcional)  
  
```groovy  
pipeline {  
  agent any  
  stages {  
    stage('Checkout') { steps { checkout scm } }  
    stage('Install') { steps { sh 'pip install selenium webdriver-manager pytest pytest-xdist' } }  
    stage('Test') { steps { sh 'pytest -n 4 --maxfail=1 --disable-warnings -v' } }  
    stage('Archive Screenshots') { steps { archiveArtifacts artifacts: '**/*.png' } }  
  }  
}  
```  
  
🟢 **Resultado esperado:**  
Os testes executam headless, em paralelo e com relatórios automáticos no CI.  
  
---  
  
## ✅ **Resumo dos 6 Laboratórios (Python)**  
  
| Nº  | Tema                        | Principais Conceitos                     |  
| --- | --------------------------- | ---------------------------------------- |  
| 1   | Instalação e primeiro teste | `webdriver`, `get_title`, `pytest`       |  
| 2   | Interação com elementos     | `find_element`, `send_keys`, `click`     |  
| 3   | Esperas e alerts            | `WebDriverWait`, `EC`, `Select`, `Alert` |  
| 4   | Page Object Model           | Reutilização, estrutura modular          |  
| 5   | Evidências e erros          | `save_screenshot`, logs                  |  
| 6   | Headless e CI/CD            | Execução paralela, pipelines CI          |  
  
---  
  
  
