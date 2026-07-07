/**
 * GQS1201 - Exemplo "Hello Test" de Automação de Testes.
 * Demonstra o padrão AAA (Arrange-Act-Assert) sem depender de um framework
 * de testes; os métodos testeXxx() usam a instrução assert do Java.
 *
 * Compilação e execução:
 *   javac GQS1201-PrimeiroTeste.java
 *   java -ea -cp . GQS1201_PrimeiroTeste
 *
 * (a flag -ea habilita as instruções assert; sem ela os testes não falham)
 *
 * Para um projeto real, este mesmo cenário seria escrito com JUnit 5,
 * como visto na Aula 13.
 */
class GQS1201_PrimeiroTeste {

    /** Função simples a ser testada. */
    static int somar(int a, int b) {
        return a + b;
    }

    /** Exemplo de teste com padrão AAA. */
    static void testeSomaDeveRetornarCinco() {
        // Arrange
        int a = 2, b = 3;
        // Act
        int resultado = somar(a, b);
        // Assert
        assert resultado == 5 : "Esperado 5, obtido " + resultado;
    }

    static void testeSomaComZero() {
        assert somar(0, 5) == 5;
    }

    static void testeSomaComNegativos() {
        assert somar(-1, 1) == 0;
    }

    static void testeSomaValoresGrandes() {
        assert somar(1000000, 2000000) == 3000000;
    }

    /** Demonstra independência de testes. */
    static void testeSomaDuasVezes() {
        assert somar(3, 4) == 7;
        assert somar(10, 20) == 30;
    }

    public static void main(String[] args) {
        System.out.println("GQS1201 - Executando testes manualmente:");
        System.out.printf("  somar(2, 3) = %d%n", somar(2, 3));
        System.out.printf("  somar(0, 5) = %d%n", somar(0, 5));
        System.out.printf("  somar(-1, 1) = %d%n", somar(-1, 1));

        testeSomaDeveRetornarCinco();
        testeSomaComZero();
        testeSomaComNegativos();
        testeSomaValoresGrandes();
        testeSomaDuasVezes();

        System.out.println("\nTodos os testes executados. Rode com 'java -ea' para validar os asserts.");
    }
}
