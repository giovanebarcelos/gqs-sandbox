# Boletim API em Python (Aula 26 - Gitea Actions). A imagem e' gerada pelo job
# "imagem" do pipeline; localmente, "docker build -t boletim-python ."
FROM python:3.12-slim

WORKDIR /srv
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY boletim/ boletim/

RUN useradd --create-home boletim
USER boletim

EXPOSE 5000
CMD ["gunicorn", "--bind", "0.0.0.0:5000", "--workers", "1", "--access-logfile", "-", "boletim.app:app"]
