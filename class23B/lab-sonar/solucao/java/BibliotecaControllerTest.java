package br.edu.gqs.biblioteca;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Testes da camada HTTP com MockMvc - VERSAO COMPLETA (gabarito da Etapa 7).
 *
 * <p>Copie para java/src/test/java/br/edu/gqs/biblioteca/BibliotecaControllerTest.java.
 * Sobe o contexto do Spring sem abrir porta de rede e exercita cada endpoint:
 * e' o que leva a cobertura do controller para perto de 100%.
 */
@SpringBootTest(properties = {"app.token-admin=token-de-teste", "app.versao=teste"})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BibliotecaControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void healthELivros() throws Exception {
        mvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.versao").value("teste"));
        mvc.perform(get("/api/livros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disponiveis.length()").value(4));
    }

    @Test
    void emprestaEDevolve() throws Exception {
        mvc.perform(post("/api/emprestimos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Clean Code\",\"matricula\":\"2026001\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value(matchesPattern("EMP-\\d{6}")));
        mvc.perform(post("/api/devolucoes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Clean Code\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Devolucao registrada: Clean Code"));
    }

    @Test
    void errosViram404E400() throws Exception {
        mvc.perform(post("/api/emprestimos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Nao Existe\",\"matricula\":\"2026001\"}"))
                .andExpect(status().isNotFound());
        mvc.perform(post("/api/emprestimos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Clean Code\",\"matricula\":\"\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/multa").param("dias", "dez").param("tipo", "aluno"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void multa() throws Exception {
        mvc.perform(get("/api/multa").param("dias", "20").param("tipo", "aluno").param("raro", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.multa").value(90.0));
    }

    @Test
    void relatorioExigeToken() throws Exception {
        mvc.perform(get("/api/relatorio"))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/relatorio").header("X-Admin-Token", "token-de-teste"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("RELATORIO DA BIBLIOTECA")));
        mvc.perform(get("/api/relatorio").param("formato", "csv").header("X-Admin-Token", "token-de-teste"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("indicador;valor")));
    }
}
