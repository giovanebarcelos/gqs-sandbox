from database import init_db, SessionLocal
from models import Produto, Cliente, Pedido
import random

def main():
    # Inicializa tabelas
    init_db()
    db = SessionLocal()

    # Popula Produtos (100)
    if db.query(Produto).count() == 0:
        produtos = [Produto(nome=f"Produto {i}", preco=10.0 + i) for i in range(1, 101)]
        db.add_all(produtos)
        print("Produtos criados.")

    # Popula Clientes (50)
    if db.query(Cliente).count() == 0:
        clientes = [Cliente(nome=f"Cliente {i}", email=f"cli{i}@teste.com") for i in range(1, 51)]
        db.add_all(clientes)
        print("Clientes criados.")

    # Popula Pedidos (200)
    if db.query(Pedido).count() == 0:
        pedidos = [
            Pedido(cliente_id=random.randint(1, 50), produto_id=random.randint(1, 100))
            for _ in range(200)
        ]
        db.add_all(pedidos)
        print("Pedidos criados.")

    db.commit()
    db.close()
    print("Banco SQLite jmeter_lab.db criado e populado com sucesso!")


if __name__ == "__main__":
    main()