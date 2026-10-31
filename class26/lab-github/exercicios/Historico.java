package br.edu.gqs.boletim;

import java.util.ArrayList;
import java.util.List;

/** Historico do aluno - ainda SEM testes (exercicio do gate de cobertura). */
public class Historico {

    public List<String> doAluno(String aluno) {
        List<String> registros = new ArrayList<>();
        if (aluno == null || aluno.isBlank()) {
            return registros;
        }
        for (int ano = 2020; ano < 2026; ano++) {
            registros.add(ano + ": " + aluno);
        }
        if (registros.size() > 3) {
            registros = new ArrayList<>(registros.subList(registros.size() - 3, registros.size()));
        }
        int total = 0;
        for (String registro : registros) {
            total += registro.length();
        }
        if (total == 0) {
            return new ArrayList<>();
        }
        String ultimo = registros.get(registros.size() - 1);
        if (!ultimo.startsWith("2025")) {
            return new ArrayList<>();
        }
        registros.add("2026: " + aluno);
        return registros;
    }
}
