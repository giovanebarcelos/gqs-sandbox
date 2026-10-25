package br.edu.carona;

import br.edu.carona.Erros.ConflitoDeRegra;
import br.edu.carona.Erros.DadoInvalido;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Regras de negócio do CaronaCampus (RN-01 a RN-05) como funções puras.
 * O "agora" é sempre recebido como parâmetro: nada aqui chama {@code LocalDateTime.now()}.
 */
public final class Regras {
    private Regras() {}

    public static final int VAGAS_MIN = 1;
    public static final int VAGAS_MAX = 6;
    public static final Duration ANTECEDENCIA_MIN = Duration.ofMinutes(30);
    public static final Duration JANELA_MAX = Duration.ofDays(14);
    public static final BigDecimal PRECO_MIN = new BigDecimal("2.00");
    public static final BigDecimal PRECO_MAX = new BigDecimal("25.00");
    public static final Duration LIMITE_SEM_TAXA = Duration.ofMinutes(60);
    public static final BigDecimal TAXA_TARDIA = new BigDecimal("0.50");
    public static final int AVALIACOES_PARA_SUSPENDER = 5;
    public static final int MEDIA_MINIMA = 3;

    /** RN-01: de 1 a 6 vagas. */
    public static int validarVagas(int vagas) {
        if (vagas < VAGAS_MIN || vagas > VAGAS_MAX) {
            throw new DadoInvalido("vagas deve estar entre " + VAGAS_MIN + " e " + VAGAS_MAX);
        }
        return vagas;
    }

    /** RN-02: saída entre 30 min e 14 dias à frente (limites inclusivos). */
    public static LocalDateTime validarSaida(LocalDateTime saida, LocalDateTime agora) {
        if (saida.isBefore(agora.plus(ANTECEDENCIA_MIN))) {
            throw new DadoInvalido("a saída deve ser pelo menos 30 minutos no futuro");
        }
        if (saida.isAfter(agora.plus(JANELA_MAX))) {
            throw new DadoInvalido("a saída deve ser em no máximo 14 dias");
        }
        return saida;
    }

    /** RN-03: custo ÷ (vagas + 1), arredondado para CIMA ao centavo, entre R$ 2,00 e R$ 25,00. */
    public static BigDecimal precoPorVaga(BigDecimal custoTotal, int vagas) {
        validarVagas(vagas);
        if (custoTotal.signum() <= 0) {
            throw new DadoInvalido("custo_total deve ser positivo");
        }
        BigDecimal bruto = custoTotal.divide(BigDecimal.valueOf(vagas + 1L), 2, RoundingMode.CEILING);
        return bruto.max(PRECO_MIN).min(PRECO_MAX);
    }

    /** RN-04: sem taxa até 60 min antes (inclusive); depois, 50% do valor; após a saída, proibido. */
    public static BigDecimal taxaDeCancelamento(BigDecimal valor, LocalDateTime saida, LocalDateTime agora) {
        if (!agora.isBefore(saida)) {
            throw new ConflitoDeRegra("a viagem já começou: não é possível cancelar");
        }
        if (Duration.between(agora, saida).compareTo(LIMITE_SEM_TAXA) >= 0) {
            return new BigDecimal("0.00");
        }
        return valor.multiply(TAXA_TARDIA).setScale(2, RoundingMode.HALF_UP);
    }

    /** RN-05: com 5 ou mais avaliações, média abaixo de 3,0 suspende (comparação exata com inteiros). */
    public static boolean estaSuspenso(List<Integer> notas) {
        if (notas.size() < AVALIACOES_PARA_SUSPENDER) {
            return false;
        }
        int soma = notas.stream().mapToInt(Integer::intValue).sum();
        return soma < MEDIA_MINIMA * notas.size();
    }

    /** Média com 2 casas, ou {@code null} se não houver avaliações. */
    public static BigDecimal media(List<Integer> notas) {
        if (notas.isEmpty()) {
            return null;
        }
        int soma = notas.stream().mapToInt(Integer::intValue).sum();
        return BigDecimal.valueOf(soma).divide(BigDecimal.valueOf(notas.size()), 2, RoundingMode.HALF_UP);
    }

    public static int validarNota(int nota) {
        if (nota < 1 || nota > 5) {
            throw new DadoInvalido("a nota deve ser um inteiro de 1 a 5");
        }
        return nota;
    }
}
