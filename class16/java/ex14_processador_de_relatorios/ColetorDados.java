package tdd20ex.ex14_processador_de_relatorios;

import java.util.Map;

interface ColetorDados {
    Map<String, Object> coletarDados(String periodo);
}
