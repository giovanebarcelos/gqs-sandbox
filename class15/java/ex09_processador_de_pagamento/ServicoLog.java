package tdd20ex.ex09_processador_de_pagamento;

interface ServicoLog {
    void registrarSucesso(String mensagem);
    void registrarErro(String mensagem);
}