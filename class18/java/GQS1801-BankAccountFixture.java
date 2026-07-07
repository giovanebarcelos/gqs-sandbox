import java.util.*;

class BankAccountFixture {
    private Map<String, Double> accounts = new HashMap<>();
    private Map<String, List<String>> transactionHistory = new HashMap<>();


    public void createAccount(String accountNumber, double initialBalance) {
        accounts.put(accountNumber, initialBalance);
        transactionHistory.put(accountNumber, new ArrayList<>());
        transactionHistory.get(accountNumber).add(initialBalance + " initial balance");
    }

    public double showAccountBalance(String accountNumber) throws Exception {
        if (!accounts.containsKey(accountNumber)) {
            throw new Exception("Account not exist");
        }
        return accounts.get(accountNumber);
    }

    public void deposit(String accountNumber, double amount) {
        double currentBalance = accounts.get(accountNumber);
        accounts.put(accountNumber, currentBalance + amount);
        transactionHistory.get(accountNumber).add(amount + " deposit");

    }

    public void withdraw(String accountNumber, double amount) throws Exception {
        double currentBalance = accounts.get(accountNumber);
        
        if (currentBalance < amount) {
           throw new Exception("Fail");
        } else { 
          accounts.put(accountNumber, currentBalance - amount);
        } 
        transactionHistory.get(accountNumber).add(amount + " withdrawal");

    }

    public void transfer(String fromAccount, String toAccount, double amount) throws Exception {
        double fromBalance = accounts.get(fromAccount);
        if (fromBalance < amount) {
           throw new Exception("Fail");
        }
        double toBalance = accounts.get(toAccount);
        accounts.put(fromAccount, fromBalance - amount);
        accounts.put(toAccount, toBalance + amount);
    }

    public void closeAccount(String accountNumber) {
        accounts.remove(accountNumber);
    }

    public List<String> showTransactionHistory(String accountNumber) {
        return transactionHistory.get(accountNumber);
    }
}
