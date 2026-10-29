"""
Conexao com o Kiwi TCMS compartilhada pelos scripts deste laboratorio.

Credenciais NUNCA vao no codigo. Duas formas aceitas, nesta ordem:

  1. variaveis de ambiente
         export KIWI_URL=https://kiwi.localhost/xml-rpc/
         export KIWI_USERNAME=admin
         export KIWI_PASSWORD=sua-senha

  2. arquivo ~/.tcms.conf (forma oficial do tcms-api), com chmod 600 -
     modelo em lab-kiwi/tcms.conf.example (o mesmo arquivo serve para Java e Python)
"""

import os
import ssl
import sys

from tcms_api import TCMS

# --------------------------------------------------------------------------- #
# APENAS EM LABORATORIO: o Traefik responde em https://kiwi.localhost com um
# certificado autoassinado gerado por ele mesmo. Em producao remova esta
# linha e use um certificado valido (Let's Encrypt, por exemplo).
# --------------------------------------------------------------------------- #
ssl._create_default_https_context = ssl._create_unverified_context


def credenciais_do_ambiente():
    """Devolve (url, usuario, senha) se as tres variaveis existirem."""
    valores = [os.environ.get(v) for v in ("KIWI_URL", "KIWI_USERNAME", "KIWI_PASSWORD")]
    return valores if all(valores) else (None, None, None)


def conectar():
    """Abre a conexao XML-RPC e devolve o objeto `rpc`."""
    url, usuario, senha = credenciais_do_ambiente()
    try:
        return TCMS(url=url, username=usuario, password=senha).exec
    except RuntimeError as erro:
        print(f"ERRO: {erro}")
        print("Configure ~/.tcms.conf ou exporte KIWI_URL/KIWI_USERNAME/KIWI_PASSWORD.")
        sys.exit(1)
