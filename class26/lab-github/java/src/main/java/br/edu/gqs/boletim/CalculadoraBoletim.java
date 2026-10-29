package br.edu.gqs.boletim;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Regras do boletim escolar.
 *
 * <pre>
 *   R1  media = media aritmetica das notas, com 1 casa decimal (arredonda para cima no 5)
 *   R2  frequencia abaixo de 75%            -> REPROVADO_POR_FALTA (vale sobre a media)
 *       media >= 7,0                        -> APROVADO
 *       media >= 4,0                        -> EXAME
 *       abaixo disso                        -> REPROVADO
 *   R3  de 1 a 4 notas, cada uma de 0 a 10; frequencia de 0 a 100
 * </pre>
 */
public class CalculadoraBoletim {

    static final BigDecimal MEDIA_APROVACAO = new BigDecimal("7.0");
    static final BigDecimal MEDIA_EXAME = new BigDecimal("4.0");
    static final double FREQUENCIA_MINIMA = 75;

    public Boletim calcular(String aluno, List<Double> notas, double frequencia) {
        validar(aluno, notas, frequencia);

        BigDecimal soma = notas.stream().map(BigDecimal::valueOf).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal media = soma.divide(BigDecimal.valueOf(notas.size()), 1, RoundingMode.HALF_UP);
        Situacao situacao = situacao(media, frequencia);

        List<Double> ordenadas = new ArrayList<>(notas);
        ordenadas.sort(null);
        double menor = ordenadas.getFirst();
        double maior = ordenadas.getLast();

        return new Boletim(aluno.trim(), media, situacao, maior, menor,
                aluno.trim() + ": " + situacao + " com media " + media);
    }

    private static Situacao situacao(BigDecimal media, double frequencia) {
        if (frequencia <= FREQUENCIA_MINIMA) {
            return Situacao.REPROVADO_POR_FALTA;
        }
        if (media.compareTo(MEDIA_APROVACAO) >= 0) {
            return Situacao.APROVADO;
        }
        if (media.compareTo(MEDIA_EXAME) >= 0) {
            return Situacao.EXAME;
        }
        return Situacao.REPROVADO;
    }

    private static void validar(String aluno, List<Double> notas, double frequencia) {
        if (aluno == null || aluno.isBlank()) {
            throw new IllegalArgumentException("Informe o nome do aluno");
        }
        if (notas == null || notas.isEmpty() || notas.size() > 4) {
            throw new IllegalArgumentException("Informe de 1 a 4 notas");
        }
        for (Double nota : notas) {
            if (nota == null || nota < 0 || nota > 10) {
                throw new IllegalArgumentException("Nota fora do intervalo 0 a 10: " + nota);
            }
        }
        if (frequencia < 0 || frequencia > 100) {
            throw new IllegalArgumentException("Frequencia fora do intervalo 0 a 100: " + frequencia);
        }
    }
}
