#1. Criar e ativar o ambiente virtual:
python -m venv venv
source venv/bin/activate   # Linux/macOS
#venv\Scripts\activate      # Windows

#2. Instalar as dependências:
pip install -r requirements.txt

#3. Rodar a aplicação:
python api/main.py