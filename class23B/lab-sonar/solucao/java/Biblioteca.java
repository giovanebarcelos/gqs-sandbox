package br.edu.gqs.biblioteca;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Regras de negocio da Biblioteca - VERSAO CORRIGIDA (gabarito da Etapa 7).
 *
 * <p>Copie para java/src/main/java/br/edu/gqs/biblioteca/Biblioteca.java.
 *
 * <pre>
 *   Defeito          Correcao
 *   S4973            equals() no lugar de ==
 *   S6437/S2077/     contarNoCatalogo removido: nenhum endpoint o chamava
 *   S2095            (codigo morto). Se fosse necessario: PreparedStatement,
 *                    try-with-resources e senha lida do ambiente.
 *   S2245            SecureRandom no lugar de Random
 *   S3776/S1854/     calcularMulta reescrito com tabela de fatores e
 *   S1481            retornos antecipados; sem variaveis mortas
 *   S108             lerDias rejeita entrada invalida (vira HTTP 400)
 *   duplicacao       relatorioTexto e relatorioCsv usam o mesmo montarRelatorio
 *   segredo fixo     TOKEN_ADMIN vem do ambiente (o Sonar NAO apontou este!)
 * </pre>
 */
public class Biblioteca {

    private static final double MULTA_DIARIA = 1.50;
    private static final double MULTA_MAXIMA = 100;
    private static final String NAO_ENCONTRADO = "Livro nao encontrado";
    private static final String ALUNO = "aluno";

    /** Fatores por tipo de usuario: {ate o limite, acima do limite, acima do limite e raro}. */
    private static final Map<String, double[]> FATORES = Map.of(
            ALUNO, new double[] {1, 2, 4},
            "professor", new double[] {0.5, 1.2, 2.4},
            "visitante", new double[] {2, 3, 3});
    private static final Map<String, Integer> LIMITE_DIAS = Map.of(
            ALUNO, 30, "professor", 30, "visitante", 7);

    private final SecureRandom random = new SecureRandom();
    private final String tokenAdmin;

    private final List<String> acervo = new ArrayList<>(List.of(
            "Engenharia de Software Moderna",
            "Clean Code",
            "Test Driven Development",
            "Refactoring"));
    private final List<String> emprestados = new ArrayList<>();

    /** @param tokenAdmin token exigido pelo relatorio; vem da variavel de ambiente TOKEN_ADMIN */
    public Biblioteca(String tokenAdmin) {
        this.tokenAdmin = tokenAdmin;
    }

    public List<String> listarDisponiveis() {
        return List.copyOf(acervo);
    }

    public List<String> listarEmprestados() {
        return List.copyOf(emprestados);
    }

    /** Empresta um livro do acervo e devolve o codigo do emprestimo. */
    public String emprestar(String titulo, String matricula) {
        if (titulo == null || matricula == null || matricula.isBlank()) {
            throw new IllegalArgumentException("Titulo e matricula sao obrigatorios");
        }
        if (!acervo.remove(titulo)) {          // remove(Object) usa equals()
            throw new LivroNaoEncontradoException(NAO_ENCONTRADO);
        }
        emprestados.add(titulo);
        return "EMP-" + (100000 + random.nextInt(900000));
    }

    /** Devolve um livro emprestado ao acervo. */
    public void devolver(String titulo) {
        if (!emprestados.remove(titulo)) {
            throw new LivroNaoEncontradoException(NAO_ENCONTRADO);
        }
        acervo.add(titulo);
    }

    /**
     * Multa = dias x 1,50 x fator do tipo de usuario, limitada a R$ 100,00.
     * Mesmo resultado da versao original, com complexidade cognitiva baixa.
     */
    public double calcularMulta(int diasAtraso, String tipoUsuario, boolean livroRaro) {
        if (diasAtraso <= 0) {
            return 0;
        }
        double base = diasAtraso * MULTA_DIARIA;
        double[] fatores = FATORES.get(tipoUsuario);
        if (fatores == null) {
            return Math.min(base, MULTA_MAXIMA);
        }
        double fator;
        if (diasAtraso <= LIMITE_DIAS.get(tipoUsuario)) {
            fator = fatorAteOLimite(tipoUsuario, diasAtraso, livroRaro, fatores);
        } else {
            fator = fatores[livroRaro ? 2 : 1];
        }
        return Math.min(base * fator, MULTA_MAXIMA);
    }

    /** Aluno tem uma faixa intermediaria (16 a 30 dias); os demais usam o fator base. */
    private static double fatorAteOLimite(String tipoUsuario, int diasAtraso, boolean livroRaro, double[] fatores) {
        if (ALUNO.equals(tipoUsuario) && diasAtraso > 15) {
            return livroRaro ? 3 : 1.5;
        }
        return fatores[0];
    }

    /** Converte o parametro "dias" da URL; texto invalido vira IllegalArgumentException (HTTP 400). */
    public int lerDias(String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("O parametro dias deve ser um numero inteiro", e);
        }
    }

    /** Confere o token do cabecalho X-Admin-Token. */
    public boolean tokenValido(String token) {
        return tokenAdmin != null && !tokenAdmin.isBlank() && tokenAdmin.equals(token);
    }

    public String relatorioTexto() {
        return montarRelatorio("RELATORIO DA BIBLIOTECA", ": ", " - ");
    }

    public String relatorioCsv() {
        return montarRelatorio("indicador;valor", ";", ";");
    }

    private String montarRelatorio(String cabecalho, String separadorIndicador, String separadorLivro) {
        int total = acervo.size() + emprestados.size();
        int ocupados = emprestados.size();
        double taxa = total == 0 ? 0 : (ocupados * 100.0) / total;
        StringBuilder sb = new StringBuilder(cabecalho).append("\n");
        sb.append("Total de livros").append(separadorIndicador).append(total).append("\n");
        sb.append("Disponiveis").append(separadorIndicador).append(acervo.size()).append("\n");
        sb.append("Emprestados").append(separadorIndicador).append(ocupados).append("\n");
        sb.append("Taxa de ocupacao").append(separadorIndicador)
                .append(String.format("%.1f", taxa)).append("%\n");
        acervo.forEach(livro -> sb.append("disponivel").append(separadorLivro).append(livro).append("\n"));
        emprestados.forEach(livro -> sb.append("emprestado").append(separadorLivro).append(livro).append("\n"));
        return sb.toString();
    }
}
