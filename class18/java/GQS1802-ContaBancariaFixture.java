import java.util.*;

public class ContaBancoFixture {
    private Map<String, Double> contas = new HashMap<>();
    private Map<String, List<String>> historicoDeTransacoes = new HashMap<>();

    public void criarConta(String codigoConta, double saldoInicial) {
        contas.put(codigoConta, saldoInicial);
        historicoDeTransacoes.put(codigoConta, new ArrayList<>());
        historicoDeTransacoes.get(codigoConta).add(saldoInicial + " saldo inicial");
    }

    public double mostrarSaldoDaConta(String codigoConta) throws Exception {
        if (!contas.containsKey(codigoConta)) {
            throw new Exception("Conta não existe");
        }
        return contas.get(codigoConta);
    }

    public void depositar(String codigoConta, double saldo) {
        double currentBalance = contas.get(codigoConta);
        contas.put(codigoConta, currentBalance + saldo);
        historicoDeTransacoes.get(codigoConta).add(saldo + " depósito");

    }

    public void sacar(String codigoConta, double saldo) throws Exception {
        double saldoAtual = contas.get(codigoConta);
        
        if (saldoAtual < saldo) {
           throw new Exception("Falha");
        } else { 
          contas.put(codigoConta, saldoAtual - saldo);
        } 
        historicoDeTransacoes.get(codigoConta).add(saldo + " saque");

    }

    public void transferir(String contaOrigem, String contaDestino, double valor) throws Exception {
        double saldoOrigem = contas.get(contaOrigem);
        if (saldoOrigem < valor) {
           throw new Exception("Falha");
        }
        double saldoDestino = contas.get(contaDestino);
        contas.put(contaOrigem, saldoOrigem - valor);
        contas.put(contaDestino, saldoDestino + valor);
    }

    public void fecharConta(String accountNumber) {
        contas.remove(accountNumber);
    }

    public List<String> mostrarHistoricoDeTransacoes(String accountNumber) {
        return historicoDeTransacoes.get(accountNumber);
    }
}
