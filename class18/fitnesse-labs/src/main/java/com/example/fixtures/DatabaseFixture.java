package com.example.fixtures;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Lab 3 - Script Table (connect / executeUpdate / close) + metodo estatico de consulta
 * usado pela Query Table (UsuariosDb). A conexao e estatica porque cada tabela do
 * Slim cria uma instancia nova da fixture; ja o SetUp e as paginas de teste rodam na mesma JVM.
 */
public class DatabaseFixture {
    private static Connection conn;

    public void connect(String url, String user, String pass) throws Exception {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(url, user, pass);
        }
    }

    public void executeUpdate(String sql) throws Exception {
        try (Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
        }
    }

    public int countRows(String table) throws Exception {
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public void close() throws Exception {
        if (conn != null) conn.close();
        conn = null;
    }

    /** Linhas no formato de Query Table do Slim: lista de linhas; cada linha = lista de pares [coluna, valor]. */
    static List<Object> select(String sql) throws Exception {
        List<Object> rows = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            ResultSetMetaData md = rs.getMetaData();
            while (rs.next()) {
                List<Object> row = new ArrayList<>();
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    List<String> pair = new ArrayList<>();
                    pair.add(md.getColumnLabel(i).toLowerCase());
                    pair.add(rs.getString(i));
                    row.add(pair);
                }
                rows.add(row);
            }
        }
        return rows;
    }
}
