package tdd20ex.ex19_sistema_de_workflow;

import java.util.Map;

class Etapa {
    private String id;
    private String nome;
    private Map<String, Object> parametros;

    public Etapa(String id, String nome, Map<String, Object> parametros) {
        this.id = id;
        this.nome = nome;
        this.parametros = parametros;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public Map<String, Object> getParametros() { return parametros; }
}
