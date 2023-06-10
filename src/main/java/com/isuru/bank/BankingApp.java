package com.isuru.bank;

import com.isuru.bank.exceptions.CustomerNotFoundException;
import org.apache.logging.log4j.*;

import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;


public class BankingApp {

    BankingAppController bankingAppController;
    Map<String, Customer> customerIds = new HashMap<>(); // in-memory map
    public static final Logger log = LogManager.getLogger(BankingApp.class);


    public BankingApp(BankingAppController bankingAppController) {
        this.bankingAppController = bankingAppController;
    }


    /*
    * Assuming Transaction data looks as follows
        id    date    debitAcctId creditAcctId    amount
        ------------------------------------------------
        1   2021-12-02     1           2              1000
    *
    * */
    public void setUp() {
        log.info("Setting up the application");
        List<Transaction> transactions = bankingAppController.getTransactionDetailsFromAPI();
        transactions.sort(Comparator.comparing(Transaction::getDate)); // sorting by date

        for (Transaction transaction : transactions) {
            String debitCustomerId = transaction.getDebitCustomerId();
            String creditCustomerId = transaction.getCreditCustomerId();
            double amount  = transaction.getAmount();
            String date = transaction.getDate();

            if (debitCustomerId != null && !debitCustomerId.isEmpty()) {
                Customer debitCustomer = customerIds.computeIfAbsent(debitCustomerId, Customer::new);
                debitCustomer.setAmount(date, amount, true);
                customerIds.put(debitCustomerId, debitCustomer);
            }

            if (creditCustomerId != null && !creditCustomerId.isEmpty()) {
                Customer creditCustomer = customerIds.computeIfAbsent(creditCustomerId, Customer::new);
                creditCustomer.setAmount(date, amount, false);
                customerIds.put(creditCustomerId, creditCustomer);
            }
        }
    }


    public double getMonthlyBalance(String customerId, String month, String year) throws CustomerNotFoundException {
        Customer customer = customerIds.get(customerId);
        if (customer == null) throw new CustomerNotFoundException("Not A Customer");
        return customer.getMonthlyBalance(year + "-" + month);
    }

    public double getCumulativeBalance(String customerId) throws CustomerNotFoundException {
        Customer customer = customerIds.get(customerId);
        if (customer == null) throw new CustomerNotFoundException("Not A Customer");
        return customer.getCurrentAmount();
    }

}
