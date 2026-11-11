from selenium.webdriver.common.by import By

from pages.base_page import BasePage


class InventoryPage(BasePage):
    TITULO = (By.CSS_SELECTOR, "[data-test='title']")
    LISTA = (By.CSS_SELECTOR, "[data-test='inventory-list']")
    NOMES = (By.CSS_SELECTOR, "[data-test='inventory-item-name']")
    BOTOES_ADICIONAR = (By.CSS_SELECTOR, "[data-test^='add-to-cart']")
    BADGE_CARRINHO = (By.CSS_SELECTOR, "[data-test='shopping-cart-badge']")
    LINK_CARRINHO = (By.CSS_SELECTOR, "[data-test='shopping-cart-link']")

    def carregada(self):
        self._visivel(self.LISTA)
        return self

    def titulo(self):
        return self._visivel(self.TITULO).text

    def nome_do_produto(self, indice=0):
        self.carregada()
        return self.driver.find_elements(*self.NOMES)[indice].text

    def adicionar_ao_carrinho(self, indice=0):
        self.carregada()
        self.driver.find_elements(*self.BOTOES_ADICIONAR)[indice].click()

    def itens_no_carrinho(self):
        return int(self._visivel(self.BADGE_CARRINHO).text)

    def ir_para_carrinho(self):
        self._clicavel(self.LINK_CARRINHO).click()
