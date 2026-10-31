package br.edu.gqs.boletim;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

/** Testes da camada HTTP com MockMvc. */
@WebMvcTest(BoletimController.class)
@TestPropertySource(properties = "app.token-admin=segredo-de-teste")
class BoletimControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void healthRespondeOk() throws Exception {
        mvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    void calculaBoletim() throws Exception {
        mvc.perform(post("/api/boletim").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"aluno\":\"Ana\",\"notas\":[5,6],\"frequencia\":80}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.media").value(5.5))
                .andExpect(jsonPath("$.situacao").value("EXAME"));
    }

    @Test
    void notaInvalidaResponde400() throws Exception {
        mvc.perform(post("/api/boletim").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"aluno\":\"Ana\",\"notas\":[11],\"frequencia\":80}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Nota fora do intervalo 0 a 10: 11.0"));
    }

    @Test
    void jsonQuebradoResponde400() throws Exception {
        mvc.perform(post("/api/boletim").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void estatisticasSemTokenResponde401() throws Exception {
        mvc.perform(get("/api/admin/estatisticas")).andExpect(status().isUnauthorized());
    }

    @Test
    void estatisticasComTokenResponde200() throws Exception {
        mvc.perform(get("/api/admin/estatisticas").header("X-Admin-Token", "segredo-de-teste"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.boletinsCalculados").isNumber());
    }
}
