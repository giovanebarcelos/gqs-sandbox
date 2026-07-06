import sqlite3
import csv

conn = sqlite3.connect("database.db")
cur = conn.cursor()

# Criar tabelas
cur.execute("""
CREATE TABLE IF NOT EXISTS produtos(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT,
    preco REAL,
    estoque INTEGER
)
""")

cur.execute("""
CREATE TABLE IF NOT EXISTS usuarios(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT,
    password TEXT
)
""")

cur.execute("""
CREATE TABLE IF NOT EXISTS pedidos(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    usuario_id INTEGER,
    produto_id INTEGER,
    quantidade INTEGER,
    FOREIGN KEY(usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY(produto_id) REFERENCES produtos(id)
)
""")

# Popular a tabela com CSVs
def populate_table_from_csv(table_name, csv_file):
    with open(csv_file, newline='') as f:
        reader = csv.DictReader(f)
        for row in reader:
            keys = ', '.join(row.keys())
            qmarks = ', '.join(['?']*len(row))
            cur.execute(f"INSERT INTO {table_name} ({keys}) VALUES ({qmarks})", tuple(row.values()))
    conn.commit()

populate_table_from_csv('produtos', 'data/produtos.csv')
populate_table_from_csv('usuarios', 'data/usuarios.csv')
populate_table_from_csv('pedidos', 'data/pedidos.csv')

conn.close()