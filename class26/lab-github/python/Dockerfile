# Boletim API em Python (Aula 26). Imagem publicada no GitHub Packages (GHCR)
# pelo workflow release.yml; localmente, "docker compose up --build".
FROM python:3.12-slim

WORKDIR /srv
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY boletim/ boletim/

RUN useradd --create-home boletim
USER boletim

EXPOSE 5000
CMD ["gunicorn", "--bind", "0.0.0.0:5000", "--workers", "1", "--access-logfile", "-", "boletim.app:app"]
