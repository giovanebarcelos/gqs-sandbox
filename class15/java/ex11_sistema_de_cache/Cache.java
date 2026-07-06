package tdd20ex.ex11_sistema_de_cache;

interface Cache {
    Object get(String chave);
    void put(String chave, Object valor);
    boolean containsKey(String chave);
}
