package br.edu.gqs.biblioteca;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Regras de negocio da Biblioteca (Aula 23B - laboratorio SonarQube).
 *
 * <p>Java puro, sem Spring, para poder ser testada por JUnit sem subir
 * servidor. O {@link BibliotecaController} so' traduz HTTP para estes metodos.
 *
 * <p>ATENCAO - ESTE CODIGO CONTEM DEFEITOS PROPOSITAIS. Cada um dispara uma
 * regra real do SonarQube; o tutorial mostra como encontra-los no dashboard
 * e como corrigi-los ate o Quality Gate passar.
 *
 * <pre>
 *   Regra   Qualidade (impacto)        Onde                O problema
 *   S4973   Confiabilidade (media)     emprestar           String comparada com == (quebra o emprestimo!)
 *   S2095   Confiabilidade (bloqueio)  contarNoCatalogo    Connection e Statement nunca fechados (2 issues)
 *   S6437   Seguranca (bloqueio)       contarNoCatalogo    senha fixa no codigo-fonte (SENHA_CATALOGO)
 *   S2077   Seguranca (media)          contarNoCatalogo    SQL montado por concatenacao (injecao de SQL)
 *   S2245   Seguranca (media)          gerarCodigo         Random previsivel gerando codigo de emprestimo
 *   S3776   Manutenibilidade (alta)    calcularMulta       complexidade cognitiva 35 (limite: 15)
 *   S1854   Manutenibilidade (media)   calcularMulta       atribuicoes mortas (2 issues)
 *   S1481   Manutenibilidade (baixa)   calcularMulta       variavel local nunca usada
 *   S108    Manutenibilidade (media)   lerDias             bloco catch vazio (excecao engolida)
 *   (dup)   Duplicacao                 relatorioTexto/Csv  bloco de codigo copiado e colado
 *   (cob.)  Cobertura                  -                   a suite deixa a maior parte do codigo sem teste
 * </pre>
 *
 * <p>Varias regras so' existem nos perfis "Sonar way extended" e
 * "Sonar way comprehensive" - o tutorial mostra como trocar o perfil.
 */
public class Biblioteca {

    /** DEFEITO S6437: credencial fixa no codigo - vai para o repositorio junto. */
    private static final String SENHA_CATALOGO = "biblioteca123";

    private static final String URL_CATALOGO = "jdbc:postgresql://catalogo:5432/acervo";

    /** Token exigido no cabecalho X-Admin-Token do relatorio. */
    private static final String TOKEN_ADMIN = "relatorio-2026";

    /** Multa diaria em reais por dia de atraso. */
    private static final double MULTA_DIARIA = 1.50;

    /** DEFEITO S2245: java.util.Random e' previsivel - nao serve para codigos. */
    private final Random random = new Random();

    private final List<String> acervo = new ArrayList<>(List.of(
            "Engenharia de Software Moderna",
            "Clean Code",
            "Test Driven Development",
            "Refactoring"));
    private final List<String> emprestados = new ArrayList<>();

    public List<String> listarDisponiveis() {
        return List.copyOf(acervo);
    }

    public List<String> listarEmprestados() {
        return List.copyOf(emprestados);
    }

    /**
     * Empresta um livro do acervo e devolve o codigo do emprestimo.
     *
     * <p>DEFEITO S4973: compara Strings com == (identidade) em vez de equals()
     * (conteudo). Um titulo que chega pela API e' um objeto String novo, entao
     * a comparacao NUNCA e' verdadeira e qualquer emprestimo falha.
     */
    public String emprestar(String titulo, String matricula) {
        if (titulo == null || matricula == null || matricula.isBlank()) {
            throw new IllegalArgumentException("Titulo e matricula sao obrigatorios");
        }
        for (String livro : acervo) {
            if (livro == titulo) {
                acervo.remove(livro);
                emprestados.add(livro);
                return gerarCodigo();
            }
        }
        throw new LivroNaoEncontradoException("Livro nao encontrado");
    }

    /** Devolve um livro emprestado ao acervo. */
    public void devolver(String titulo) {
        if (!emprestados.remove(titulo)) {
            throw new LivroNaoEncontradoException("Livro nao encontrado");
        }
        acervo.add(titulo);
    }

    private String gerarCodigo() {
        return "EMP-" + (100000 + random.nextInt(900000));
    }

    /**
     * Calcula a multa por atraso na devolucao.
     *
     * <p>DEFEITOS: S3776 (ifs aninhados demais), S1481 (variavel {@code hoje}
     * nunca usada) e S1854 (o primeiro valor de {@code multa} e' descartado).
     */
    public double calcularMulta(int diasAtraso, String tipoUsuario, boolean livroRaro) {
        String hoje = "2026-10-28";
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
            }
            if (multa > 100) {
                multa = 100;
            }
        } else {
            multa = 0;
        }
        return multa;
    }

    /**
     * Conta quantos exemplares do titulo existem no catalogo central (banco
     * externo, fora deste laboratorio - por isso nenhum endpoint chama).
     *
     * <p>DEFEITOS: S6437 (senha fixa), S2077 (SQL por concatenacao) e
     * S2095 (conexao e statement nunca fechados).
     */
    public int contarNoCatalogo(String titulo) {
        try {
            Connection conexao = DriverManager.getConnection(URL_CATALOGO, "biblioteca", SENHA_CATALOGO);
            Statement stmt = conexao.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM exemplares WHERE titulo = '" + titulo + "'");
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            return -1;
        }
        return 0;
    }

    /** Converte o parametro "dias" da URL. DEFEITO S108: catch vazio. */
    public int lerDias(String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
        }
        return 0;
    }

    /** Confere o token do cabecalho X-Admin-Token. */
    public boolean tokenValido(String token) {
        return TOKEN_ADMIN.equals(token);
    }

    /** Relatorio em texto. DEFEITO (dup): praticamente igual ao relatorioCsv. */
    public String relatorioTexto() {
        StringBuilder sb = new StringBuilder();
        int total = acervo.size() + emprestados.size();
        int disponiveis = acervo.size();
        int ocupados = emprestados.size();
        double taxa = total == 0 ? 0 : (ocupados * 100.0) / total;
        sb.append("RELATORIO DA BIBLIOTECA\n");
        sb.append("Total de livros: ").append(total).append("\n");
        sb.append("Disponiveis: ").append(disponiveis).append("\n");
        sb.append("Emprestados: ").append(ocupados).append("\n");
        sb.append("Taxa de ocupacao: ").append(String.format("%.1f", taxa)).append("%\n");
        for (String livro : acervo) {
            sb.append("disponivel").append(" - ").append(livro).append("\n");
        }
        for (String livro : emprestados) {
            sb.append("emprestado").append(" - ").append(livro).append("\n");
        }
        return sb.toString();
    }

    /** Relatorio em CSV. DEFEITO (dup): copia e cola do relatorioTexto. */
    public String relatorioCsv() {
        StringBuilder sb = new StringBuilder();
        int total = acervo.size() + emprestados.size();
        int disponiveis = acervo.size();
        int ocupados = emprestados.size();
        double taxa = total == 0 ? 0 : (ocupados * 100.0) / total;
        sb.append("indicador;valor\n");
        sb.append("Total de livros;").append(total).append("\n");
        sb.append("Disponiveis;").append(disponiveis).append("\n");
        sb.append("Emprestados;").append(ocupados).append("\n");
        sb.append("Taxa de ocupacao;").append(String.format("%.1f", taxa)).append("%\n");
        for (String livro : acervo) {
            sb.append("disponivel").append(";").append(livro).append("\n");
        }
        for (String livro : emprestados) {
            sb.append("emprestado").append(";").append(livro).append("\n");
        }
        return sb.toString();
    }

}
