# TechStore - API de carrinho (Aula 24). Imagem pequena, usuario sem root.
FROM python:3.12-slim

WORKDIR /srv
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY techstore/ techstore/

RUN useradd --create-home techstore
USER techstore

EXPOSE 5000
CMD ["gunicorn", "--bind", "0.0.0.0:5000", "--workers", "2", "--access-logfile", "-", "techstore.app:app"]
