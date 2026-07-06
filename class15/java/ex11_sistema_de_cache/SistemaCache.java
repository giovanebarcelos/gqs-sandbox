package tdd20ex.ex11_sistema_de_cache;

public class SistemaCache {
    private Cache cache;
    private RepositorioDados repositorio;

    public SistemaCache(Cache cache, RepositorioDados repositorio) {
        this.cache = cache;
        this.repositorio = repositorio;
    }

    public String obterDados(String id) {
        String chaveCache = "dados_" + id;

        if (cache.containsKey(chaveCache)) {
            return (String) cache.get(chaveCache);
        }

        String dados = repositorio.buscarDados(id);
        if (dados != null) {
            cache.put(chaveCache, dados);
        }

        return dados;
    }
}
