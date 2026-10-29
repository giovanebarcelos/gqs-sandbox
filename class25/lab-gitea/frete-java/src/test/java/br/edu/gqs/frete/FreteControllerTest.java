package br.edu.gqs.frete;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** Testes da camada HTTP com MockMvc - sobem so' o controller, sem servidor. */
@WebMvcTest(FreteController.class)
class FreteControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void healthRespondeOk() throws Exception {
        mvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    void listaAsCincoRegioes() throws Exception {
        mvc.perform(get("/api/regioes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    void calculaFrete() throws Exception {
        mvc.perform(post("/api/frete").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"regiao\":\"SUL\",\"pesoKg\":2.0,\"valorPedido\":100.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.frete").value(17.5))
                .andExpect(jsonPath("$.prazoDias").value(3));
    }

    @Test
    void regiaoInvalidaResponde400() throws Exception {
        mvc.perform(post("/api/frete").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"regiao\":\"LESTE\",\"pesoKg\":1,\"valorPedido\":10}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Regiao invalida: LESTE"));
    }

    @Test
    void jsonQuebradoResponde400() throws Exception {
        mvc.perform(post("/api/frete").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest());
    }
}
