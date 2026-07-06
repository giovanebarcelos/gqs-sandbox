from flask import Flask, jsonify, request
import sqlite3, uuid

app = Flask(__name__)
DB = "database.db"
TOKENS = {}

# Autenticação simples OAuth2
@app.route("/api/token", methods=["POST"])
def token():
    data = request.form
    username = data.get("username")
    password = data.get("password")
    conn = sqlite3.connect(DB)
    cur = conn.cursor()
    cur.execute("SELECT * FROM usuarios WHERE username=? AND password=?", (username, password))
    user = cur.fetchone()
    conn.close()
    if user:
        token = str(uuid.uuid4())
        TOKENS[token] = username
        return jsonify({"access_token": token})
    return jsonify({"error": "Invalid credentials"}), 401

def auth_required(func):
    def wrapper(*args, **kwargs):
        token = request.headers.get("Authorization")
        if not token or token.replace("Bearer ", "") not in TOKENS:
            return jsonify({"error": "Unauthorized"}), 401
        return func(*args, **kwargs)
    wrapper.__name__ = func.__name__
    return wrapper

# Endpoints da API
@app.route("/api/produtos", methods=["GET"])
@auth_required
def get_produtos():
    conn = sqlite3.connect(DB)
    cur = conn.cursor()
    cur.execute("SELECT * FROM produtos")
    produtos = cur.fetchall()
    conn.close()
    return jsonify(produtos)

@app.route("/api/busca", methods=["GET"])
@auth_required
def busca_produtos():
    q = request.args.get("q", "")
    conn = sqlite3.connect(DB)
    cur = conn.cursor()
    cur.execute("SELECT * FROM produtos WHERE nome LIKE ?", (f"%{q}%",))
    produtos = cur.fetchall()
    conn.close()
    return jsonify(produtos)

@app.route("/api/pedidos", methods=["POST"])
@auth_required
def create_pedido():
    data = request.json
    conn = sqlite3.connect(DB)
    cur = conn.cursor()
    cur.execute("INSERT INTO pedidos(usuario_id, produto_id, quantidade) VALUES (?, ?, ?)",
                (data["usuario_id"], data["produto_id"], data["quantidade"]))
    conn.commit()
    conn.close()
    return jsonify({"status": "ok"})

@app.route("/api/relatorio", methods=["GET"])
@auth_required
def relatorio():
    conn = sqlite3.connect(DB)
    cur = conn.cursor()
    cur.execute("""
        SELECT u.username, p.nome, pd.quantidade
        FROM pedidos pd
        JOIN usuarios u ON u.id = pd.usuario_id
        JOIN produtos p ON p.id = pd.produto_id
    """)
    dados = cur.fetchall()
    conn.close()
    return jsonify(dados)

if __name__ == "__main__":
    app.run(debug=True)
