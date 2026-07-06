package tdd20ex.ex16_sistema_de_reservas;

import java.util.Optional;

interface RepositorioQuarto {
    Optional<Quarto> buscarPorNumero(String numero);
    void atualizar(Quarto quarto);
}
