import random
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, declarative_base
import os

DATABASE_URL = "sqlite:///./jmeter_lab.db"

engine = create_engine(DATABASE_URL, connect_args={"check_same_thread": False})
SessionLocal = sessionmaker(bind=engine, autoflush=False, autocommit=False)
Base = declarative_base()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

def init_db():
    from models import Produto, Cliente, Pedido
    Base.metadata.create_all(bind=engine)

    # Popular dados se vazio
    db = SessionLocal()
    if db.query(Produto).count() == 0:
        # Popular produtos
        produtos = [Produto(nome=f"Produto {i}", preco=10.0 + i) for i in range(1, 101)]
        print(produtos)
        db.add_all(produtos)

    if db.query(Cliente).count() == 0:
        clientes = [
            Cliente(nome=f"Cliente {i}", email=f"cli{i}@teste.com") for i in range(1, 51)
        ]
        db.add_all(clientes)

    if db.query(Pedido).count() == 0:
        pedidos = [
            Pedido(cliente_id=random.randint(1, 50), produto_id=random.randint(1, 100))
            for _ in range(200)
        ]
        db.add_all(pedidos)

    db.commit()
    db.close()