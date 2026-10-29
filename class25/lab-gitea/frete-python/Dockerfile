# LojaOnline - API de frete em Python (Aula 25).
# O pipeline ja' rodou os testes antes do "docker build"; aqui so' empacota.
FROM python:3.12-slim

WORKDIR /srv
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY frete/ frete/

RUN useradd --create-home frete
USER frete

EXPOSE 5000
CMD ["gunicorn", "--bind", "0.0.0.0:5000", "--workers", "2", "--access-logfile", "-", "frete.app:app"]
