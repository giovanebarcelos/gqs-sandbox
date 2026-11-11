from selenium.webdriver.common.by import By

from pages.base_page import URL_BASE, BasePage


class LoginPage(BasePage):
    # Localizadores: os mesmos que o Selenium IDE gravou, agora num lugar so
    USUARIO = (By.ID, "user-name")
    SENHA = (By.CSS_SELECTOR, "[data-test='password']")
    BOTAO_LOGIN = (By.ID, "login-button")
    ERRO = (By.CSS_SELECTOR, "[data-test='error']")

    def abrir(self):
        self.driver.get(URL_BASE + "/")
        self._visivel(self.USUARIO)
        return self

    def login(self, usuario, senha):
        self._digitar(self.USUARIO, usuario)
        self._digitar(self.SENHA, senha)
        self._clicavel(self.BOTAO_LOGIN).click()

    def mensagem_erro(self):
        return self._visivel(self.ERRO).text
