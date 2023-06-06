import java.util.*;

public class BankingApp {

    Map<String, Customer> customerIds = new HashMap<>(); // in-memory map

    /*
    * Assuming Transaction data looks as follows
        id    date    debitAcctId creditAcctId    amount
        ------------------------------------------------
        1   2021-12-02     1           2              1000
    *
    * */
    public void setUp() {
        List<Transaction> transactions = getTransactionDetailsFromAPI();
        transactions.sort(Comparator.comparing(Transaction::getDate)); // sorting by date

        for (Transaction transaction : transactions) {
            String debitCustomerId = transaction.getDebitCustomerId();
            String creditCustomerId = transaction.getCreditCustomerId();
            double amount  = transaction.getAmount();
            String date = transaction.getDate();

            Customer debitCustomer = customerIds.computeIfAbsent(debitCustomerId, Customer::new);
            debitCustomer.setAmount(date, amount, true);
            customerIds.put(debitCustomerId, debitCustomer); // do this by one line

            Customer creditCustomer = customerIds.computeIfAbsent(creditCustomerId, Customer::new);
            creditCustomer.setAmount(date, amount, false);
            customerIds.put(creditCustomerId, creditCustomer);

        }
    }

    public List<Transaction> getTransactionDetailsFromAPI() {
        return new ArrayList<>();
    }

    public double getMonthlyBalance(String customerId, String month, String year) {
        Customer customer = customerIds.get(customerId);
        return customer.getMonthlyBalance(year + "-" + month);
    }

    public double getCumulativeBalance(String customerId) {
        Customer customer = customerIds.get(customerId);
        return customer.getCurrentAmount();
    }

}
