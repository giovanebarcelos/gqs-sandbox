package br.edu.gqs.frete;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/** LojaOnline - API de frete usada no laboratorio de CI/CD (Aula 25). */
@SpringBootApplication
public class FreteApplication {

    public static void main(String[] args) {
        SpringApplication.run(FreteApplication.class, args);
    }

    @Bean
    CalculadoraFrete calculadoraFrete() {
        return new CalculadoraFrete();
    }
}
