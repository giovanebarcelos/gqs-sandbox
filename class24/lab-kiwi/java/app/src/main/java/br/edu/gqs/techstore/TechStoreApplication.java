package br.edu.gqs.techstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/** TechStore - API de carrinho usada no laboratorio de Kiwi TCMS (Aula 24). */
@SpringBootApplication
public class TechStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(TechStoreApplication.class, args);
    }

    /** As regras de negocio sao Java puro; o Spring so' as injeta no controller. */
    @Bean
    CalculadoraCarrinho calculadoraCarrinho() {
        return new CalculadoraCarrinho();
    }
}
