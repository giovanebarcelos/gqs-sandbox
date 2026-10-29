# Biblioteca API em Python (Aula 23B). Imagem pequena, usuario sem root.
FROM python:3.12-slim

WORKDIR /srv
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY biblioteca/ biblioteca/

RUN useradd --create-home biblioteca
USER biblioteca

EXPOSE 5000
CMD ["gunicorn", "--bind", "0.0.0.0:5000", "--workers", "1", "--access-logfile", "-", "biblioteca.app:app"]
