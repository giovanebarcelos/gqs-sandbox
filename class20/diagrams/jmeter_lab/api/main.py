from fastapi import FastAPI, Depends, HTTPException, status
from fastapi.security import OAuth2PasswordRequestForm
from sqlalchemy.orm import Session
from database import get_db, init_db
from models import Produto, Cliente, Pedido
from auth import get_current_user
import uvicorn

app = FastAPI(title="API de Testes JMeter")

# Usuários de exemplo para autenticação
fake_users = {
    "dickvigarista": {"username": "dickvigarista", "password": "12345"},
    "medinho": {"username": "medinho", "password": "12345"},
    "penelopecharmosa": {"username": "penelopecharmosa", "password": "12345"}
}

# Inicializa banco com dados
init_db()

@app.get("/api/produtos")
def listar_produtos(db: Session = Depends(get_db)):
    return db.query(Produto).all()

@app.get("/api/produtos/{produto_id}")
def buscar_produto(produto_id: int, db: Session = Depends(get_db)):
    produto = db.query(Produto).filter(Produto.id == produto_id).first()
    if not produto:
        raise HTTPException(status_code=404, detail="Produto não encontrado")
    return produto

# -----------------------
# ROTAS DE LOGIN / AUTH
# -----------------------
@app.post("/api/token")
def login_token(form_data: OAuth2PasswordRequestForm = Depends()):
    user = fake_users.get(form_data.username)
    if not user or user["password"] != form_data.password:
        raise HTTPException(status_code=400, detail="Credenciais inválidas")
    return {"access_token": form_data.username, "token_type": "bearer"}


@app.post("/api/login")
def login(user=Depends(get_current_user)):
    return {"msg": "Login OK", "usuario": user.username}

# -----------------------
# ROTAS DE PEDIDOS
# -----------------------
@app.get("/api/pedidos")
def listar_pedidos(db: Session = Depends(get_db), user=Depends(get_current_user)):
    return db.query(Pedido).all()

# -----------------------
# ROTAS DE UPLOAD / DOWNLOAD
# -----------------------
@app.post("/api/upload")
def upload_arquivo(user=Depends(get_current_user)):
    return {"msg": "Arquivo recebido"}


@app.get("/api/arquivos/{file_id}")
def download_arquivo(file_id: int, user=Depends(get_current_user)):
    return {"file_id": file_id, "conteudo": "dados..."}

if __name__ == "__main__":
    print("\nAPI disponível em http://127.0.0.1:8000\nAcesse a documentação automática em http://127.0.0.1:8000/docs\n")
    uvicorn.run("main:app", host="127.0.0.1", port=8000, reload=True)