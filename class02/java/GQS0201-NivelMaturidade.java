/**
 * GQS0201 - Calculadora de Nível de Maturidade (CMMI).
 * Analisa características da organização e sugere o nível CMMI.
 *
 * Compilação e execução:
 *   javac GQS0201-NivelMaturidade.java
 *   java -cp . GQS0201_NivelMaturidade
 */
import java.util.*;

class NivelMaturidade {
    int nivel;
    String nome;
    String descricao;

    NivelMaturidade(int nivel, String nome, String descricao) {
        this.nivel = nivel;
        this.nome = nome;
        this.descricao = descricao;
    }
}

class GQS0201_NivelMaturidade {

    static List<NivelMaturidade> criarNiveis() {
        return List.of(
            new NivelMaturidade(1, "Inicial", "Processos imprevisíveis, sucesso depende de heróis"),
            new NivelMaturidade(2, "Gerenciado", "Processos planejados e controlados por projeto"),
            new NivelMaturidade(3, "Definido", "Processos padronizados na organização"),
            new NivelMaturidade(4, "Gerenciado Quantitativamente", "Processos controlados com métricas"),
            new NivelMaturidade(5, "Otimizando", "Melhoria contínua baseada em dados")
        );
    }

    /** Calcula o nível de maturidade com base nas respostas. */
    static int avaliarMaturidade(Map<String, Boolean> respostas) {
        int pontos = 0;
        Map<String, Integer> criterios = new LinkedHashMap<>();
        criterios.put("documenta_requisitos", 1);
        criterios.put("planeja_projetos", 1);
        criterios.put("faz_revisoes", 1);
        criterios.put("testa_sistematicamente", 1);
        criterios.put("mede_qualidade", 2);
        criterios.put("melhoria_continua", 2);

        for (Map.Entry<String, Boolean> entrada : respostas.entrySet()) {
            if (entrada.getValue() && criterios.containsKey(entrada.getKey())) {
                pontos += criterios.get(entrada.getKey());
            }
        }

        if (pontos >= 8) return 5;
        else if (pontos >= 6) return 4;
        else if (pontos >= 4) return 3;
        else if (pontos >= 2) return 2;
        return 1;
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  GQS0201 - AVALIADOR DE NÍVEL DE MATURIDADE CMMI");
        System.out.println("=".repeat(60));
        System.out.println("\nResponda com 's' (sim) ou 'n' (não):");

        Map<String, String> perguntas = new LinkedHashMap<>();
        perguntas.put("documenta_requisitos", "Sua organização documenta requisitos dos projetos?");
        perguntas.put("planeja_projetos", "Os projetos têm planejamento com cronograma?");
        perguntas.put("faz_revisoes", "São feitas revisões de código/design?");
        perguntas.put("testa_sistematicamente", "Os testes são sistemáticos e documentados?");
        perguntas.put("mede_qualidade", "Métricas de qualidade são coletadas regularmente?");
        perguntas.put("melhoria_continua", "Existe programa de melhoria contínua?");

        Map<String, Boolean> respostas = new LinkedHashMap<>();
        try (Scanner sc = new Scanner(System.in)) {
            for (Map.Entry<String, String> entrada : perguntas.entrySet()) {
                while (true) {
                    System.out.printf("%n%s (s/n): ", entrada.getValue());
                    String resposta = sc.nextLine().strip().toLowerCase();
                    if (resposta.equals("s") || resposta.equals("n")) {
                        respostas.put(entrada.getKey(), resposta.equals("s"));
                        break;
                    }
                    System.out.println("Responda 's' ou 'n'.");
                }
            }
        }

        int nivel = avaliarMaturidade(respostas);
        NivelMaturidade nivelInfo = criarNiveis().get(nivel - 1);

        System.out.printf("%n%s%n", "=".repeat(60));
        System.out.printf("  NÍVEL DE MATURIDADE: %d - %s%n", nivel, nivelInfo.nome);
        System.out.printf("  %s%n", nivelInfo.descricao);
        System.out.println("=".repeat(60));
    }
}
