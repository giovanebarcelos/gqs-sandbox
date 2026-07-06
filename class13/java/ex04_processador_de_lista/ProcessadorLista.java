package tdd20ex.ex04_processador_de_lista;

import java.util.*;
import java.util.stream.Collectors;

public class ProcessadorLista {

    public List<Integer> filtrarPares(List<Integer> lista) {
        if (lista == null) return new ArrayList<>();
        return lista.stream()
                .filter(n -> n % 2 == 0)
                .collect(Collectors.toList());
    }

    public int somarElementos(List<Integer> lista) {
        if (lista == null) return 0;
        return lista.stream().mapToInt(Integer::intValue).sum();
    }

    public Integer encontrarMaior(List<Integer> lista) {
        if (lista == null || lista.isEmpty()) {
            throw new IllegalArgumentException("Lista não pode ser vazia");
        }
        return Collections.max(lista);
    }

    public boolean ehListaVazia(List<Integer> lista) {
        return lista == null || lista.isEmpty();
    }
}
