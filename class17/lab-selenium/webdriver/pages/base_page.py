"""Base de todos os Page Objects: espera explicita num lugar so."""
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait

URL_BASE = "https://www.saucedemo.com"


class BasePage:
    TIMEOUT = 10

    def __init__(self, driver):
        self.driver = driver
        self.wait = WebDriverWait(driver, self.TIMEOUT)

    def _visivel(self, locator):
        """Espera o elemento ficar visivel e o devolve (nada de time.sleep)."""
        return self.wait.until(EC.visibility_of_element_located(locator))

    def _clicavel(self, locator):
        return self.wait.until(EC.element_to_be_clickable(locator))

    def _digitar(self, locator, texto):
        campo = self._visivel(locator)
        campo.clear()
        campo.send_keys(texto)
