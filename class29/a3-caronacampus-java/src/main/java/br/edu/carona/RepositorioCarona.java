package br.edu.carona;

import java.util.List;
import java.util.Optional;

/** Porta de persistência. O serviço depende só desta interface; os testes unitários usam um Mock. */
public interface RepositorioCarona {
    Usuario criarUsuario(String matricula, String nome, String senhaHash);

    /** Usuário e hash da senha, se a matrícula existir. */
    Optional<UsuarioComSenha> usuarioPorMatricula(String matricula);

    Optional<Usuario> obterUsuario(long usuarioId);

    Viagem inserirViagem(Viagem viagem);

    Optional<Viagem> obterViagem(long viagemId);

    List<Viagem> listarViagens();

    void atualizarViagem(Viagem viagem);

    /** Conta as reservas ativas e insere na MESMA transação; {@code Optional.empty()} se lotada. */
    Optional<Reserva> reservarSeHaVaga(Reserva reserva, int vagas);

    Optional<Reserva> obterReserva(long reservaId);

    List<Reserva> reservasAtivas(long viagemId);

    void atualizarReserva(Reserva reserva);

    void inserirAvaliacao(long viagemId, long passageiroId, int nota);

    boolean avaliacaoExiste(long viagemId, long passageiroId);

    List<Integer> notasDoMotorista(long motoristaId);

    /** (viagemId, vagas, reservas ativas) das viagens não canceladas. */
    List<long[]> ocupacaoPorViagem();

    record UsuarioComSenha(Usuario usuario, String senhaHash) {}
}
