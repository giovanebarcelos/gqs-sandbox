import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * GQS23B01 - Sistema de empréstimo de uma biblioteca.
 *
 * ATENÇÃO — ESTE CÓDIGO CONTÉM DEFEITOS PROPOSITAIS.
 *
 * Cada defeito abaixo dispara uma regra REAL do SonarQube. Ele é o alvo da
 * Etapa 3 do laboratório da Aula 23B: rodar a análise, ler o dashboard e
 * corrigir os problemas até o Quality Gate passar. A versão corrigida está em
 * `diagrams/GQS23B-Lab-Solucao.md`.
 *
 * Mapa dos defeitos plantados (regra Sonar -> onde):
 *   S2068  Credencial fixa no código          -> SENHA_BD
 *   S2077  SQL montado por concatenação       -> buscarPorTitulo
 *   S2095  Recurso (Connection) não fechado   -> buscarPorTitulo
 *   S1166  Exceção engolida no catch          -> buscarPorTitulo
 *   S4973  Comparação de String com ==        -> emprestar
 *   S3776  Complexidade cognitiva alta        -> calcularMulta
 *   S1192  Literal de string duplicado 3x+    -> "Livro não encontrado"
 *   S1481  Variável local não utilizada       -> calcularMulta
 *   S1854  Atribuição morta                   -> calcularMulta
 *   (dup)  Bloco duplicado                    -> relatorioTexto / relatorioCsv
 *
 * Compilação e execução:
 *   mvn clean verify
 *   java -cp target/classes GQS23B01_Biblioteca
 */
class GQS23B01_Biblioteca {

    /** DEFEITO S2068: credencial fixa no código-fonte. */
    private static final String SENHA_BD = "biblioteca123";

    private static final String URL_BD = "jdbc:postgresql://localhost:5432/biblioteca";

    /** Multa diária em reais aplicada a cada dia de atraso. */
    private static final double MULTA_DIARIA = 1.50;

    private final List<String> acervo = new ArrayList<>();
    private final List<String> emprestados = new ArrayList<>();

    GQS23B01_Biblioteca() {
        acervo.add("Engenharia de Software Moderna");
        acervo.add("Clean Code");
        acervo.add("Test Driven Development");
    }

    /**
     * Busca um livro pelo título no banco de dados.
     *
     * DEFEITOS: S2077 (SQL por concatenação — injeção de SQL),
     * S2095 (Connection nunca fechada) e S1166 (exceção engolida).
     */
    String buscarPorTitulo(String titulo) {
        try {
            Connection conexao = DriverManager.getConnection(URL_BD, "admin", SENHA_BD);
            Statement stmt = conexao.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT titulo FROM livros WHERE titulo = '" + titulo + "'");
            if (rs.next()) {
                return rs.getString("titulo");
            }
        } catch (SQLException e) {
        }
        return "Livro não encontrado";
    }

    /**
     * Registra o empréstimo de um livro do acervo.
     *
     * DEFEITO S4973: compara Strings com == em vez de equals().
     */
    String emprestar(String titulo, String matricula) {
        if (titulo == null || matricula == null) {
            throw new IllegalArgumentException("Título e matrícula são obrigatórios");
        }
        for (String livro : acervo) {
            if (livro == titulo) {
                acervo.remove(livro);
                emprestados.add(titulo);
                return "Empréstimo registrado: " + titulo;
            }
        }
        return "Livro não encontrado";
    }

    /**
     * Devolve um livro ao acervo.
     */
    String devolver(String titulo) {
        if (emprestados.remove(titulo)) {
            acervo.add(titulo);
            return "Devolução registrada: " + titulo;
        }
        return "Livro não encontrado";
    }

    /**
     * Calcula a multa por atraso na devolução.
     *
     * DEFEITOS: S3776 (complexidade cognitiva alta — 8 desvios aninhados),
     * S1481 (variável `hoje` declarada e nunca usada) e
     * S1854 (primeira atribuição de `multa` descartada).
     */
    double calcularMulta(int diasAtraso, String tipoUsuario, boolean livroRaro) {
        String hoje = "2026-09-03";
        double multa = 0.0;
        multa = diasAtraso * MULTA_DIARIA;
        if (diasAtraso > 0) {
            if (tipoUsuario.equals("aluno")) {
                if (diasAtraso > 30) {
                    if (livroRaro) {
                        multa = multa * 4;
                    } else {
                        multa = multa * 2;
                    }
                } else if (diasAtraso > 15) {
                    if (livroRaro) {
                        multa = multa * 3;
                    } else {
                        multa = multa * 1.5;
                    }
                }
            } else if (tipoUsuario.equals("professor")) {
                if (diasAtraso > 30) {
                    if (livroRaro) {
                        multa = multa * 2.4;
                    } else {
                        multa = multa * 1.2;
                    }
                } else {
                    multa = multa * 0.5;
                }
            } else if (tipoUsuario.equals("visitante")) {
                if (diasAtraso > 7) {
                    multa = multa * 3;
                } else {
                    multa = multa * 2;
                }
            } else {
                multa = multa * 1;
            }
            if (multa > 100) {
                multa = 100;
            }
        }
        return Math.round(multa * 100.0) / 100.0;
    }

    /**
     * Relatório do acervo em texto.
     *
     * DEFEITO (duplicação): este método e `relatorioCsv` são blocos quase
     * idênticos — o SonarQube os aponta como linhas duplicadas.
     */
    String relatorioTexto() {
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("RELATORIO DO ACERVO - BIBLIOTECA CENTRAL\n");
        sb.append("=========================================\n");
        sb.append("Disponiveis: ").append(acervo.size()).append("\n");
        sb.append("Emprestados: ").append(emprestados.size()).append("\n");
        sb.append("Total geral: ").append(acervo.size() + emprestados.size()).append("\n");
        sb.append("-----------------------------------------\n");
        int contador = 0;
        for (String livro : acervo) {
            contador = contador + 1;
            sb.append(contador).append(". ").append(livro).append(" [DISPONIVEL]\n");
        }
        for (String livro : emprestados) {
            contador = contador + 1;
            sb.append(contador).append(". ").append(livro).append(" [EMPRESTADO]\n");
        }
        sb.append("-----------------------------------------\n");
        sb.append("Itens listados: ").append(contador).append("\n");
        sb.append("Multa diaria vigente: R$ ").append(MULTA_DIARIA).append("\n");
        sb.append("=========================================\n");
        return sb.toString();
    }

    String relatorioCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("RELATORIO DO ACERVO - BIBLIOTECA CENTRAL\n");
        sb.append("=========================================\n");
        sb.append("Disponiveis; ").append(acervo.size()).append("\n");
        sb.append("Emprestados; ").append(emprestados.size()).append("\n");
        sb.append("Total geral; ").append(acervo.size() + emprestados.size()).append("\n");
        sb.append("-----------------------------------------\n");
        int contador = 0;
        for (String livro : acervo) {
            contador = contador + 1;
            sb.append(contador).append("; ").append(livro).append("; DISPONIVEL\n");
        }
        for (String livro : emprestados) {
            contador = contador + 1;
            sb.append(contador).append("; ").append(livro).append("; EMPRESTADO\n");
        }
        sb.append("-----------------------------------------\n");
        sb.append("Itens listados; ").append(contador).append("\n");
        sb.append("Multa diaria vigente; ").append(MULTA_DIARIA).append("\n");
        sb.append("=========================================\n");
        return sb.toString();
    }

    int totalDisponivel() {
        return acervo.size();
    }

    public static void main(String[] args) {
        GQS23B01_Biblioteca biblioteca = new GQS23B01_Biblioteca();
        System.out.println("Acervo inicial: " + biblioteca.totalDisponivel());
        System.out.println(biblioteca.emprestar("Clean Code", "2026001"));
        System.out.println("Multa (10 dias, aluno): R$ " + biblioteca.calcularMulta(10, "aluno", false));
        System.out.print(biblioteca.relatorioTexto());
    }
}
