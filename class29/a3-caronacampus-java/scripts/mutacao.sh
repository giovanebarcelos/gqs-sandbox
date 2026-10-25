#!/usr/bin/env bash
# Teste de mutação manual (Java): aplica UM mutante por vez numa cópia do projeto e roda a suíte.
# Roda dentro do contêiner:  docker compose run --rm testes bash scripts/mutacao.sh
# Saída: tabela em Markdown; código de saída 1 se algum mutante sobreviver.
set -u
RAIZ="$(cd "$(dirname "$0")/.." && pwd)"
P=src/main/java/br/edu/carona

# id ~~ arquivo ~~ trecho original ~~ trecho mutado ~~ o que a mutação simula
MUTANTES=(
"M01~~$P/Regras.java~~compareTo(LIMITE_SEM_TAXA) >= 0~~compareTo(LIMITE_SEM_TAXA) > 0~~limite de 60 min: >= vira >"
"M02~~$P/Regras.java~~if (!agora.isBefore(saida)) {~~if (agora.isAfter(saida)) {~~cancela na hora exata da saída"
"M03~~$P/Regras.java~~2, RoundingMode.CEILING)~~2, RoundingMode.FLOOR)~~preço arredonda para baixo"
"M04~~$P/Regras.java~~BigDecimal.valueOf(vagas + 1L)~~BigDecimal.valueOf((long) vagas)~~esquece de contar o motorista"
"M05~~$P/Regras.java~~VAGAS_MAX = 6;~~VAGAS_MAX = 7;~~máximo de vagas 6 -> 7"
"M06~~$P/Regras.java~~saida.isBefore(agora.plus(ANTECEDENCIA_MIN))~~!saida.isAfter(agora.plus(ANTECEDENCIA_MIN))~~antecedência de 30 min exclusiva"
"M07~~$P/Regras.java~~saida.isAfter(agora.plus(JANELA_MAX))~~!saida.isBefore(agora.plus(JANELA_MAX))~~janela de 14 dias exclusiva"
"M08~~$P/Regras.java~~soma < MEDIA_MINIMA * notas.size()~~soma <= MEDIA_MINIMA * notas.size()~~média exata 3,0 suspende"
"M09~~$P/Regras.java~~notas.size() < AVALIACOES_PARA_SUSPENDER~~notas.size() <= AVALIACOES_PARA_SUSPENDER~~só suspende com 6 avaliações"
"M10~~$P/Regras.java~~new BigDecimal(\"0.50\")~~new BigDecimal(\"0.40\")~~taxa de 50% -> 40%"
"M11~~$P/Regras.java~~new BigDecimal(\"25.00\")~~new BigDecimal(\"26.00\")~~teto de R\$ 25 -> 26"
"M12~~$P/Regras.java~~if (nota < 1 || nota > 5)~~if (nota < 2 || nota > 5)~~nota 1 vira inválida"
"M13~~$P/ServicoCarona.java~~if (ativas.size() >= v.vagas()) {~~if (ativas.size() > v.vagas()) {~~lotação na checagem do serviço"
"M14~~$P/ServicoCarona.java~~if (v.motoristaId() == passageiroId) {~~if (v.motoristaId() != passageiroId) {~~motorista reserva a própria viagem"
"M15~~$P/ServicoCarona.java~~if (!v.iniciada(relogio.get())) {~~if (false) {~~avalia antes da saída"
"M16~~$P/ServicoCarona.java~~if (r.passageiroId() != usuarioId) {~~if (false) {~~qualquer um cancela reserva alheia"
"M17~~$P/RepositorioSqlite.java~~if (ativas >= vagas) {~~if (ativas > vagas) {~~lotação dentro da transação"
"M18~~$P/RepositorioSqlite.java~~CREATE UNIQUE INDEX IF NOT EXISTS~~CREATE INDEX IF NOT EXISTS~~índice único de reserva ativa vira comum"
"M19~~$P/Erros.java~~super(\"conflito_de_regra\", 409, m)~~super(\"conflito_de_regra\", 400, m)~~conflito devolve 400"
"M20~~$P/Api.java~~cab == null || !cab.startsWith(\"Bearer \") || !tokens.containsKey(cab.substring(7))~~false~~autenticação desligada"
)

# Confere, antes de rodar, que cada trecho original aparece exatamente uma vez.
for linha in "${MUTANTES[@]}"; do
  IFS=$'\x1f' read -r id arq orig mut desc <<< "${linha//\~\~/$'\x1f'}"
  n=$(grep -cF -- "$orig" "$RAIZ/$arq" || true)
  [ "$n" = 1 ] || { echo "$id: trecho aparece $n vez(es) em $arq" >&2; exit 2; }
done

echo "| # | Arquivo | Mutação | Resultado | Primeiro teste que falhou |"
echo "|---|---|---|---|---|"
sobreviventes=()
for linha in "${MUTANTES[@]}"; do
  IFS=$'\x1f' read -r id arq orig mut desc <<< "${linha//\~\~/$'\x1f'}"
  tmp="$(mktemp -d)"
  cp -r "$RAIZ/pom.xml" "$RAIZ/src" "$tmp/"
  ORIG="$orig" MUT="$mut" perl -0pi -e 's/\Q$ENV{ORIG}\E/$ENV{MUT}/' "$tmp/$arq"
  saida="$(cd "$tmp" && mvn -B -o test -Dsurefire.skipAfterFailureCount=1 2>&1)" && sobreviveu=1 || sobreviveu=0
  quem="$(grep -m1 -E '^\[ERROR\] +[A-Za-z]+Test\.' <<< "$saida" | sed -E 's/^\[ERROR\] +//; s/:[0-9]+.*//' || true)"
  if [ "$sobreviveu" = 1 ]; then
    echo "| $id | \`$(basename "$arq")\` | $desc | **SOBREVIVEU** | — |"; sobreviventes+=("$id")
  else
    echo "| $id | \`$(basename "$arq")\` | $desc | detectado | \`${quem:-erro de compilação ou de execução}\` |"
  fi
  rm -rf "$tmp"
done
total=${#MUTANTES[@]}
echo
echo "Mutantes: $total · detectados: $((total - ${#sobreviventes[@]})) · sobreviventes: ${#sobreviventes[@]} ${sobreviventes[*]:-}"
[ ${#sobreviventes[@]} -eq 0 ]
