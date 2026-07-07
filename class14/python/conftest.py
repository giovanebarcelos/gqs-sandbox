"""conftest.py compartilhado da Aula 14 (pytest).

Este arquivo é carregado automaticamente pelo pytest antes de qualquer teste,
independentemente de onde o comando `pytest` é executado dentro da árvore
`repository/class14/python/`. É o mecanismo padrão do pytest para:

1. Registrar fixtures compartilhadas entre múltiplos arquivos de teste
   (sem precisar importar nada explicitamente nos módulos de teste).
2. Fazer setup de ambiente antes da coleta dos testes — aqui usamos isso
   para adicionar a pasta de implementação de cada exercício (ex.:
   `ex01_calculadora/`) ao `sys.path`, já que cada exercício mantém a
   implementação e o teste em pastas irmãs separadas (mesmo padrão usado
   nos exercícios Java da Aula 13 com Maven, onde src/ e test/ também
   ficam separados).

Rodando `pytest -v` de dentro de `ex01_calculadora_test/`, por exemplo,
o pytest sobe o diretório em busca de `conftest.py` até a raiz do projeto
(`python/`), encontra este arquivo e o executa antes de coletar os testes —
por isso `from calculadora import Calculadora` funciona no teste sem
nenhum `sys.path.append` manual dentro do arquivo de teste.
"""
import os
import sys
import glob

_BASE_DIR = os.path.dirname(os.path.abspath(__file__))

for _impl_dir in sorted(glob.glob(os.path.join(_BASE_DIR, "ex*"))):
    if os.path.isdir(_impl_dir) and not _impl_dir.endswith("_test"):
        if _impl_dir not in sys.path:
            sys.path.insert(0, _impl_dir)
