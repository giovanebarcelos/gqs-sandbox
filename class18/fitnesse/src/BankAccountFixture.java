import java.util.*;

/** Script Table Slim do sistema bancario (RF1..RF10). Excecoes viram "EXCEPTION" na wiki. */
public class BankAccountFixture {
    private final Map<String, Double> accounts = new HashMap<>();
    private final Map<String, List<String>> transactionHistory = new HashMap<>();

    public void createAccount(String accountNumber, double initialBalance) {
        accounts.put(accountNumber, initialBalance);
        transactionHistory.put(accountNumber, new ArrayList<>());
        transactionHistory.get(accountNumber).add(initialBalance + " initial balance");
    }

    public double showAccountBalance(String accountNumber) throws Exception {
        return balanceOf(accountNumber);
    }

    public void deposit(String accountNumber, double amount) throws Exception {
        accounts.put(accountNumber, balanceOf(accountNumber) + amount);
        transactionHistory.get(accountNumber).add(amount + " deposit");
    }

    public void withdraw(String accountNumber, double amount) throws Exception {
        double current = balanceOf(accountNumber);
        if (current < amount) {
            throw new Exception("Insufficient funds");
        }
        accounts.put(accountNumber, current - amount);
        transactionHistory.get(accountNumber).add(amount + " withdrawal");
    }

    public void transfer(String fromAccount, String toAccount, double amount) throws Exception {
        double from = balanceOf(fromAccount);
        double to = balanceOf(toAccount);
        if (from < amount) {
            throw new Exception("Insufficient funds");
        }
        accounts.put(fromAccount, from - amount);
        accounts.put(toAccount, to + amount);
    }

    public void closeAccount(String accountNumber) throws Exception {
        balanceOf(accountNumber);
        accounts.remove(accountNumber);
    }

    public List<String> showTransactionHistory(String accountNumber) throws Exception {
        balanceOf(accountNumber);
        return transactionHistory.get(accountNumber);
    }

    public boolean accountExists(String accountNumber) {
        return accounts.containsKey(accountNumber);
    }

    private double balanceOf(String accountNumber) throws Exception {
        Double balance = accounts.get(accountNumber);
        if (balance == null) {
            throw new Exception("Account does not exist");
        }
        return balance;
    }
}
