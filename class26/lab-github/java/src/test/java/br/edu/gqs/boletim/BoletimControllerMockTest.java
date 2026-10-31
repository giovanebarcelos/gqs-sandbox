package br.edu.gqs.boletim;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * TESTES COM MOCK (Mockito): o controller e' testado SOZINHO, com a
 * CalculadoraBoletim substituida por um dublê. Nao importa se a regra de
 * negocio esta certa ou errada - aqui so' se verifica a conversa entre as duas
 * camadas: o que o controller entrega a calculadora e o que faz com a resposta.
 */
@WebMvcTest(BoletimController.class)
@TestPropertySource(properties = "app.token-admin=segredo-de-teste")
class BoletimControllerMockTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CalculadoraBoletim calculadora;

    private static final String PEDIDO = "{\"aluno\":\"Ana\",\"notas\":[7,9],\"frequencia\":90}";

    @Test
    void entregaOsDadosDoJsonACalculadoraEDevolveOQueElaRetornou() throws Exception {
        // STUB: a calculadora "responde" um valor combinado, sem calcular nada.
        when(calculadora.calcular("Ana", List.of(7.0, 9.0), 90.0))
                .thenReturn(new Boletim("Ana", new BigDecimal("1.0"), Situacao.REPROVADO, 9.0, 7.0, "resposta do mock"));

        mvc.perform(post("/api/boletim").contentType(MediaType.APPLICATION_JSON).content(PEDIDO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resumo").value("resposta do mock"))
                .andExpect(jsonPath("$.situacao").value("REPROVADO"));

        // VERIFY: o controller chamou a calculadora uma vez, com estes argumentos.
        verify(calculadora).calcular("Ana", List.of(7.0, 9.0), 90.0);
    }

    @Test
    void frequenciaAusenteVaiComoMenosUmEACalculadoraDecide() throws Exception {
        when(calculadora.calcular(anyString(), anyList(), anyDouble()))
                .thenThrow(new IllegalArgumentException("Frequencia fora do intervalo 0 a 100: -1.0"));

        mvc.perform(post("/api/boletim").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"aluno\":\"Ana\",\"notas\":[7]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Frequencia fora do intervalo 0 a 100: -1.0"));

        verify(calculadora).calcular("Ana", List.of(7.0), -1.0);
    }

    @Test
    void erroDaCalculadoraViraHttp400ComAMensagemDela() throws Exception {
        when(calculadora.calcular(anyString(), anyList(), anyDouble()))
                .thenThrow(new IllegalArgumentException("Informe de 1 a 4 notas"));

        mvc.perform(post("/api/boletim").contentType(MediaType.APPLICATION_JSON).content(PEDIDO))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Informe de 1 a 4 notas"));
    }

    @Test
    void jsonInvalidoNemChegaNaCalculadora() throws Exception {
        mvc.perform(post("/api/boletim").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(calculadora);
    }

    @Test
    void healthNaoUsaACalculadora() throws Exception {
        mvc.perform(get("/api/health")).andExpect(status().isOk());

        verify(calculadora, never()).calcular(anyString(), anyList(), anyDouble());
    }
}
