package br.edu.carona;

import br.edu.carona.Erros.ConflitoDeRegra;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import org.sqlite.SQLiteConfig;

/** Adaptador SQLite (JDBC). Abre e FECHA uma conexão por operação (try-with-resources). */
public class RepositorioSqlite implements RepositorioCarona {
    static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private static final String ESQUEMA = """
        CREATE TABLE IF NOT EXISTS usuarios (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            matricula TEXT NOT NULL UNIQUE,
            nome TEXT NOT NULL,
            senha_hash TEXT NOT NULL);
        CREATE TABLE IF NOT EXISTS viagens (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            motorista_id INTEGER NOT NULL REFERENCES usuarios(id),
            origem TEXT NOT NULL,
            destino TEXT NOT NULL,
            saida TEXT NOT NULL,
            vagas INTEGER NOT NULL CHECK (vagas BETWEEN 1 AND 6),
            custo_total TEXT NOT NULL,
            cancelada INTEGER NOT NULL DEFAULT 0);
        CREATE TABLE IF NOT EXISTS reservas (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            viagem_id INTEGER NOT NULL REFERENCES viagens(id),
            passageiro_id INTEGER NOT NULL REFERENCES usuarios(id),
            valor TEXT NOT NULL,
            cancelada INTEGER NOT NULL DEFAULT 0,
            taxa TEXT NOT NULL DEFAULT '0.00');
        CREATE TABLE IF NOT EXISTS avaliacoes (
            viagem_id INTEGER NOT NULL REFERENCES viagens(id),
            passageiro_id INTEGER NOT NULL REFERENCES usuarios(id),
            nota INTEGER NOT NULL CHECK (nota BETWEEN 1 AND 5),
            PRIMARY KEY (viagem_id, passageiro_id));
        CREATE UNIQUE INDEX IF NOT EXISTS ux_reserva_ativa
            ON reservas (viagem_id, passageiro_id) WHERE cancelada = 0;
        """;

    private final String url;

    public RepositorioSqlite(String caminho) {
        this.url = "jdbc:sqlite:" + caminho;
        try (Connection con = abrir(false); Statement st = con.createStatement()) {
            for (String comando : ESQUEMA.split(";")) {
                if (!comando.isBlank()) {
                    st.execute(comando);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("não foi possível criar o esquema", e);
        }
    }

    private Connection abrir(boolean imediata) throws SQLException {
        SQLiteConfig cfg = new SQLiteConfig();
        cfg.enforceForeignKeys(true);
        cfg.setBusyTimeout(10_000);
        if (imediata) {
            cfg.setTransactionMode(SQLiteConfig.TransactionMode.IMMEDIATE);
        }
        Properties p = cfg.toProperties();
        return DriverManager.getConnection(url, p);
    }

    private static IllegalStateException falha(SQLException e) {
        return new IllegalStateException(e);
    }

    // -- usuários -----------------------------------------------------------------
    @Override
    public Usuario criarUsuario(String matricula, String nome, String senhaHash) {
        try (Connection con = abrir(false);
             PreparedStatement ps = con.prepareStatement(
                 "INSERT INTO usuarios (matricula, nome, senha_hash) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, matricula);
            ps.setString(2, nome);
            ps.setString(3, senhaHash);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return new Usuario(rs.getLong(1), matricula, nome);
            }
        } catch (SQLException e) {
            throw falha(e);
        }
    }

    @Override
    public Optional<UsuarioComSenha> usuarioPorMatricula(String matricula) {
        try (Connection con = abrir(false);
             PreparedStatement ps = con.prepareStatement("SELECT * FROM usuarios WHERE matricula = ?")) {
            ps.setString(1, matricula);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                    ? Optional.of(new UsuarioComSenha(
                        new Usuario(rs.getLong("id"), rs.getString("matricula"), rs.getString("nome")),
                        rs.getString("senha_hash")))
                    : Optional.empty();
            }
        } catch (SQLException e) {
            throw falha(e);
        }
    }

    @Override
    public Optional<Usuario> obterUsuario(long usuarioId) {
        try (Connection con = abrir(false);
             PreparedStatement ps = con.prepareStatement("SELECT * FROM usuarios WHERE id = ?")) {
            ps.setLong(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                    ? Optional.of(new Usuario(rs.getLong("id"), rs.getString("matricula"), rs.getString("nome")))
                    : Optional.empty();
            }
        } catch (SQLException e) {
            throw falha(e);
        }
    }

    // -- viagens ------------------------------------------------------------------
    private static Viagem viagem(ResultSet rs) throws SQLException {
        return new Viagem(rs.getLong("id"), rs.getLong("motorista_id"), rs.getString("origem"),
            rs.getString("destino"), LocalDateTime.parse(rs.getString("saida"), FORMATO), rs.getInt("vagas"),
            new BigDecimal(rs.getString("custo_total")), rs.getInt("cancelada") == 1);
    }

    @Override
    public Viagem inserirViagem(Viagem v) {
        try (Connection con = abrir(false);
             PreparedStatement ps = con.prepareStatement(
                 "INSERT INTO viagens (motorista_id, origem, destino, saida, vagas, custo_total) VALUES (?, ?, ?, ?, ?, ?)",
                 Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, v.motoristaId());
            ps.setString(2, v.origem());
            ps.setString(3, v.destino());
            ps.setString(4, v.saida().format(FORMATO));
            ps.setInt(5, v.vagas());
            ps.setString(6, v.custoTotal().toPlainString());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                v.definirId(rs.getLong(1));
            }
            return v;
        } catch (SQLException e) {
            throw falha(e);
        }
    }

    @Override
    public Optional<Viagem> obterViagem(long viagemId) {
        try (Connection con = abrir(false);
             PreparedStatement ps = con.prepareStatement("SELECT * FROM viagens WHERE id = ?")) {
            ps.setLong(1, viagemId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(viagem(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw falha(e);
        }
    }

    @Override
    public List<Viagem> listarViagens() {
        try (Connection con = abrir(false);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM viagens WHERE cancelada = 0 ORDER BY saida, id")) {
            List<Viagem> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(viagem(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw falha(e);
        }
    }

    @Override
    public void atualizarViagem(Viagem v) {
        try (Connection con = abrir(false);
             PreparedStatement ps = con.prepareStatement(
                 "UPDATE viagens SET origem=?, destino=?, saida=?, vagas=?, custo_total=?, cancelada=? WHERE id=?")) {
            ps.setString(1, v.origem());
            ps.setString(2, v.destino());
            ps.setString(3, v.saida().format(FORMATO));
            ps.setInt(4, v.vagas());
            ps.setString(5, v.custoTotal().toPlainString());
            ps.setInt(6, v.cancelada() ? 1 : 0);
            ps.setLong(7, v.id());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw falha(e);
        }
    }

    // -- reservas -----------------------------------------------------------------
    private static Reserva reserva(ResultSet rs) throws SQLException {
        return new Reserva(rs.getLong("id"), rs.getLong("viagem_id"), rs.getLong("passageiro_id"),
            new BigDecimal(rs.getString("valor")), rs.getInt("cancelada") == 1, new BigDecimal(rs.getString("taxa")));
    }

    /** Conta e insere na mesma transação (BEGIN IMMEDIATE): sem isso, duas requisições levam a mesma última vaga. */
    @Override
    public Optional<Reserva> reservarSeHaVaga(Reserva r, int vagas) {
        try (Connection con = abrir(true)) {
            con.setAutoCommit(false);
            try {
                int ativas;
                try (PreparedStatement ps = con.prepareStatement(
                    "SELECT COUNT(*) FROM reservas WHERE viagem_id = ? AND cancelada = 0")) {
                    ps.setLong(1, r.viagemId());
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        ativas = rs.getInt(1);
                    }
                }
                if (ativas >= vagas) {
                    con.rollback();
                    return Optional.empty();
                }
                try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO reservas (viagem_id, passageiro_id, valor) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, r.viagemId());
                    ps.setLong(2, r.passageiroId());
                    ps.setString(3, r.valor().toPlainString());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        rs.next();
                        r.definirId(rs.getLong(1));
                    }
                }
                con.commit();
                return Optional.of(r);
            } catch (SQLException e) {
                con.rollback();
                if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) {
                    throw new ConflitoDeRegra("passageiro já tem reserva nesta viagem");
                }
                throw e;
            }
        } catch (SQLException e) {
            throw falha(e);
        }
    }

    @Override
    public Optional<Reserva> obterReserva(long reservaId) {
        try (Connection con = abrir(false);
             PreparedStatement ps = con.prepareStatement("SELECT * FROM reservas WHERE id = ?")) {
            ps.setLong(1, reservaId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(reserva(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw falha(e);
        }
    }

    @Override
    public List<Reserva> reservasAtivas(long viagemId) {
        try (Connection con = abrir(false);
             PreparedStatement ps = con.prepareStatement(
                 "SELECT * FROM reservas WHERE viagem_id = ? AND cancelada = 0 ORDER BY id")) {
            ps.setLong(1, viagemId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Reserva> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add(reserva(rs));
                }
                return lista;
            }
        } catch (SQLException e) {
            throw falha(e);
        }
    }

    @Override
    public void atualizarReserva(Reserva r) {
        try (Connection con = abrir(false);
             PreparedStatement ps = con.prepareStatement("UPDATE reservas SET cancelada=?, taxa=? WHERE id=?")) {
            ps.setInt(1, r.cancelada() ? 1 : 0);
            ps.setString(2, r.taxa().toPlainString());
            ps.setLong(3, r.id());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw falha(e);
        }
    }

    // -- avaliações e relatório ---------------------------------------------------
    @Override
    public void inserirAvaliacao(long viagemId, long passageiroId, int nota) {
        try (Connection con = abrir(false);
             PreparedStatement ps = con.prepareStatement(
                 "INSERT INTO avaliacoes (viagem_id, passageiro_id, nota) VALUES (?, ?, ?)")) {
            ps.setLong(1, viagemId);
            ps.setLong(2, passageiroId);
            ps.setInt(3, nota);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw falha(e);
        }
    }

    @Override
    public boolean avaliacaoExiste(long viagemId, long passageiroId) {
        try (Connection con = abrir(false);
             PreparedStatement ps = con.prepareStatement(
                 "SELECT 1 FROM avaliacoes WHERE viagem_id = ? AND passageiro_id = ?")) {
            ps.setLong(1, viagemId);
            ps.setLong(2, passageiroId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw falha(e);
        }
    }

    @Override
    public List<Integer> notasDoMotorista(long motoristaId) {
        try (Connection con = abrir(false);
             PreparedStatement ps = con.prepareStatement(
                 "SELECT a.nota FROM avaliacoes a JOIN viagens v ON v.id = a.viagem_id WHERE v.motorista_id = ? ORDER BY a.rowid")) {
            ps.setLong(1, motoristaId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Integer> notas = new ArrayList<>();
                while (rs.next()) {
                    notas.add(rs.getInt(1));
                }
                return notas;
            }
        } catch (SQLException e) {
            throw falha(e);
        }
    }

    @Override
    public List<long[]> ocupacaoPorViagem() {
        try (Connection con = abrir(false);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT v.id, v.vagas, COUNT(r.id) AS ocupadas FROM viagens v "
                 + "LEFT JOIN reservas r ON r.viagem_id = v.id AND r.cancelada = 0 "
                 + "WHERE v.cancelada = 0 GROUP BY v.id ORDER BY v.id")) {
            List<long[]> linhas = new ArrayList<>();
            while (rs.next()) {
                linhas.add(new long[] {rs.getLong(1), rs.getLong(2), rs.getLong(3)});
            }
            return linhas;
        } catch (SQLException e) {
            throw falha(e);
        }
    }
}
