package br.edu.gqs.biblioteca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/** Biblioteca API - aplicacao analisada no laboratorio de SonarQube (Aula 23B). */
@SpringBootApplication
public class BibliotecaApplication {

    public static void main(String[] args) {
        SpringApplication.run(BibliotecaApplication.class, args);
    }

    /** As regras de negocio sao Java puro; o Spring so' as injeta no controller. */
    @Bean
    Biblioteca biblioteca() {
        return new Biblioteca();
    }
}
