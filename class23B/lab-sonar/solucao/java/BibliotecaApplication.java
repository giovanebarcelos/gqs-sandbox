package br.edu.gqs.biblioteca;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * VERSAO CORRIGIDA (gabarito da Etapa 7): o token do relatorio vem da
 * propriedade app.token-admin, que le a variavel de ambiente TOKEN_ADMIN.
 *
 * <p>Copie para java/src/main/java/br/edu/gqs/biblioteca/BibliotecaApplication.java.
 */
@SpringBootApplication
public class BibliotecaApplication {

    public static void main(String[] args) {
        SpringApplication.run(BibliotecaApplication.class, args);
    }

    @Bean
    Biblioteca biblioteca(@Value("${app.token-admin}") String tokenAdmin) {
        return new Biblioteca(tokenAdmin);
    }
}
