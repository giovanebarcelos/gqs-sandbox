package com.example.fixtures;

import java.util.List;

/** Query Table: |Query:UsuariosDb| - compara as linhas da tabela USUARIOS com a wiki. */
public class UsuariosDb {
    public List<Object> query() throws Exception {
        return DatabaseFixture.select("SELECT id, nome, email FROM usuarios ORDER BY id");
    }
}
