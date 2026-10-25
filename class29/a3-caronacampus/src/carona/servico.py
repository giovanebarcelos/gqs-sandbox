"""Casos de uso do CaronaCampus. Depende só do domínio e da porta `RepositorioCarona`."""
from __future__ import annotations

from collections.abc import Callable
from datetime import datetime
from decimal import Decimal

from .dominio import (
    ConflitoDeRegra, DadoInvalido, MotoristaSuspenso, NaoEncontrado, Reserva,
    SemPermissao, Viagem, esta_suspenso, media, taxa_de_cancelamento,
    validar_nota, validar_saida, validar_vagas,
)
from .repositorio import RepositorioCarona


class ServicoCarona:
    def __init__(self, repo: RepositorioCarona, relogio: Callable[[], datetime]):
        self.repo = repo
        self.relogio = relogio

    # -- viagens (CRUD) -------------------------------------------------
    def criar_viagem(self, motorista_id: int, origem: str, destino: str,
                     saida: datetime, vagas: int, custo_total: Decimal) -> Viagem:
        """RF-01. Aplica RN-01, RN-02 e RN-05 (motorista suspenso não cria viagem)."""
        origem, destino = (origem or "").strip(), (destino or "").strip()
        if not origem or not destino:
            raise DadoInvalido("origem e destino são obrigatórios")
        if origem.casefold() == destino.casefold():
            raise DadoInvalido("origem e destino devem ser diferentes")
        validar_vagas(vagas)
        validar_saida(saida, self.relogio())
        if custo_total <= 0:
            raise DadoInvalido("custo_total deve ser positivo")
        if esta_suspenso(self.repo.notas_do_motorista(motorista_id)):
            raise MotoristaSuspenso("motorista suspenso por avaliação baixa")
        return self.repo.inserir_viagem(
            Viagem(0, motorista_id, origem, destino, saida, vagas, custo_total))

    def obter_viagem(self, viagem_id: int) -> Viagem:
        v = self.repo.obter_viagem(viagem_id)
        if v is None or v.cancelada:
            raise NaoEncontrado("viagem não encontrada")
        return v

    def listar_viagens(self) -> list[Viagem]:
        return self.repo.listar_viagens()

    def atualizar_viagem(self, viagem_id: int, usuario_id: int, **campos) -> Viagem:
        """RF-02. Só o motorista altera, e só enquanto não há reservas ativas."""
        v = self._viagem_do_motorista(viagem_id, usuario_id)
        if self.repo.reservas_ativas(viagem_id):
            raise ConflitoDeRegra("a viagem já tem reservas: não pode ser alterada")
        if "origem" in campos:
            v.origem = (campos["origem"] or "").strip()
        if "destino" in campos:
            v.destino = (campos["destino"] or "").strip()
        if "vagas" in campos:
            v.vagas = validar_vagas(campos["vagas"])
        if "custo_total" in campos:
            if campos["custo_total"] <= 0:
                raise DadoInvalido("custo_total deve ser positivo")
            v.custo_total = campos["custo_total"]
        if "saida" in campos:
            v.saida = validar_saida(campos["saida"], self.relogio())
        if not v.origem or not v.destino:
            raise DadoInvalido("origem e destino são obrigatórios")
        if v.origem.casefold() == v.destino.casefold():
            raise DadoInvalido("origem e destino devem ser diferentes")
        self.repo.atualizar_viagem(v)
        return v

    def excluir_viagem(self, viagem_id: int, usuario_id: int) -> None:
        """RF-02. Cancelamento lógico; recusado se houver reservas ativas."""
        v = self._viagem_do_motorista(viagem_id, usuario_id)
        if self.repo.reservas_ativas(viagem_id):
            raise ConflitoDeRegra("a viagem já tem reservas: cancele-as antes")
        v.cancelada = True
        self.repo.atualizar_viagem(v)

    def _viagem_do_motorista(self, viagem_id: int, usuario_id: int) -> Viagem:
        v = self.obter_viagem(viagem_id)
        if v.motorista_id != usuario_id:
            raise SemPermissao("somente o motorista pode alterar a viagem")
        return v

    # -- reservas -------------------------------------------------------
    def reservar(self, viagem_id: int, passageiro_id: int) -> Reserva:
        """RF-03. RN-01 (vaga disponível), sem reserva dupla, sem reservar a própria viagem."""
        v = self.obter_viagem(viagem_id)
        if v.iniciada(self.relogio()):
            raise ConflitoDeRegra("a viagem já começou")
        if v.motorista_id == passageiro_id:
            raise ConflitoDeRegra("o motorista não pode reservar a própria viagem")
        ativas = self.repo.reservas_ativas(viagem_id)
        if any(r.passageiro_id == passageiro_id for r in ativas):
            raise ConflitoDeRegra("passageiro já tem reserva nesta viagem")
        if len(ativas) >= v.vagas:
            raise ConflitoDeRegra("viagem lotada")
        # A checagem acima é só para dar a mensagem cedo; quem garante é o repositório.
        reserva = self.repo.reservar_se_ha_vaga(
            Reserva(0, viagem_id, passageiro_id, v.valor_por_vaga), v.vagas)
        if reserva is None:
            raise ConflitoDeRegra("viagem lotada")
        return reserva

    def cancelar_reserva(self, reserva_id: int, usuario_id: int) -> Reserva:
        """RF-04. RN-04: taxa de 50% se faltar menos de 60 min para a saída."""
        r = self.repo.obter_reserva(reserva_id)
        if r is None:
            raise NaoEncontrado("reserva não encontrada")
        if r.passageiro_id != usuario_id:
            raise SemPermissao("somente o passageiro pode cancelar a reserva")
        if r.cancelada:
            raise ConflitoDeRegra("reserva já cancelada")
        v = self.repo.obter_viagem(r.viagem_id)
        r.taxa = taxa_de_cancelamento(r.valor, v.saida, self.relogio())
        r.cancelada = True
        self.repo.atualizar_reserva(r)
        return r

    # -- avaliações e reputação ----------------------------------------
    def avaliar(self, viagem_id: int, passageiro_id: int, nota: int) -> None:
        """RF-05. Só quem tinha reserva ativa, depois da saída, uma vez por viagem."""
        validar_nota(nota)
        v = self.obter_viagem(viagem_id)
        if not v.iniciada(self.relogio()):
            raise ConflitoDeRegra("só é possível avaliar depois da saída")
        if not any(r.passageiro_id == passageiro_id for r in self.repo.reservas_ativas(viagem_id)):
            raise SemPermissao("só quem viajou pode avaliar")
        if self.repo.avaliacao_existe(viagem_id, passageiro_id):
            raise ConflitoDeRegra("viagem já avaliada por este passageiro")
        self.repo.inserir_avaliacao(viagem_id, passageiro_id, nota)

    def reputacao(self, motorista_id: int) -> dict:
        """RF-06. Média, quantidade e se o motorista está suspenso (RN-05)."""
        if self.repo.obter_usuario(motorista_id) is None:
            raise NaoEncontrado("usuário não encontrado")
        notas = self.repo.notas_do_motorista(motorista_id)
        m = media(notas)
        return {"motorista_id": motorista_id, "avaliacoes": len(notas),
                "media": None if m is None else str(m), "suspenso": esta_suspenso(notas)}

    # -- relatório -----------------------------------------------------
    def relatorio_ocupacao(self) -> dict:
        """RF-07. Ocupação das viagens abertas."""
        linhas = self.repo.ocupacao_por_viagem()
        vagas = sum(v for _, v, _ in linhas)
        ocupadas = sum(o for _, _, o in linhas)
        return {
            "viagens": len(linhas), "vagas": vagas, "ocupadas": ocupadas,
            "taxa_ocupacao": round(100 * ocupadas / vagas, 1) if vagas else 0.0,
            "por_viagem": [{"viagem_id": i, "vagas": v, "ocupadas": o} for i, v, o in linhas],
        }
