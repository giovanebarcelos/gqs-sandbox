package br.edu.gqs.boletim;

import java.math.BigDecimal;

/** Resultado do calculo - vira o JSON de resposta da API. */
public record Boletim(String aluno, BigDecimal media, Situacao situacao,
                      double maiorNota, double menorNota, String resumo) {
}
