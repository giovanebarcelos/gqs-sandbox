package br.edu.gqs.kiwi;

import java.util.Arrays;

/**
 * Ponto de entrada do kiwi-tools.jar.
 *
 * <pre>
 *   java -jar target/kiwi-tools.jar preparar
 *   java -jar target/kiwi-tools.jar publicar arquivo.xml|pasta [...]
 * </pre>
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) throws Exception {
        String comando = args.length > 0 ? args[0] : "";
        String[] resto = args.length > 0 ? Arrays.copyOfRange(args, 1, args.length) : args;
        switch (comando) {
            case "preparar" -> PrepararKiwi.main(resto);
            case "publicar" -> PublicarResultados.main(resto);
            default -> {
                System.out.println("uso: java -jar kiwi-tools.jar preparar");
                System.out.println("     java -jar kiwi-tools.jar publicar resultados.xml|pasta-surefire [...]");
                System.exit(1);
            }
        }
    }
}
