from selenium.webdriver.common.by import By

from pages.base_page import BasePage


class CartPage(BasePage):
    LISTA = (By.CSS_SELECTOR, "[data-test='cart-list']")
    NOMES = (By.CSS_SELECTOR, "[data-test='inventory-item-name']")

    def produtos(self):
        self._visivel(self.LISTA)
        return [e.text for e in self.driver.find_elements(*self.NOMES)]
