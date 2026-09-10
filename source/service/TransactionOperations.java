package source.service;

import java.util.List;
import java.util.Scanner;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

import source.data.AccountData;
import source.data.TransactionHistoryData;

import source.model.Account;
import source.model.Account.AccountType;
import source.model.Account.TransactionType;

import source.util.Database;
import source.util.Validator;

final public class TransactionOperations {
    final private AccountData accountData;
    final private TransactionHistoryData transactionHistoryData;
    final private Transfer transfer;

    public TransactionOperations (AccountData accountData, TransactionHistoryData transactionHistoryData, Transfer transfer) {
        this.accountData = accountData;
        this.transactionHistoryData = transactionHistoryData;
        this.transfer = transfer;
    }

    
    public void send(Account sender, Account receiver, BigDecimal amount){

        //─────INPUT AND VALIDATE─────//
        if (receiver == null) {
            throw new IllegalArgumentException("Receiver account does not exist.");
        }
        
        BigDecimal fee = sender.getSendFee(amount);
        BigDecimal totalDeduct = amount.add(fee);
        if (sender.getAmount().compareTo(totalDeduct) < 0)
            throw new IllegalArgumentException("Insufficient balance.");

        
        //─────MAIN─────//
        try (Connection conn = Database.getConnection()) {
            // Account refreshed = accountData.readAccountData(conn, receiver.getCpNumber()); // MySQL / PostgreSQL
            // if (refreshed == null) {
            //    throw new IllegalArgumentException("Receiver account does not exist.");
            //}

            transfer.main(conn, sender, receiver, amount, fee, TransactionType.SEND);
            // success – service can now optionally fetch receipt if needed
        } catch (SQLException e) {
            throw new RuntimeException("Transfer failed", e);
        }
    }

    // TODO: FIX THIS IMMEDIATELY WITH THE checkWalletLimitation in AccountData.java
    public void cashIn(Connection input) {
        try (Connection link = Database.getConnection()) {
            
        } catch (Exception e) {
            // TODO: handle exception
        }
        //─────GENERATE CONTAINER─────//
        List<Account> agents = accountData.getAccountsByType(AccountType.AGENT);
        if (agents.isEmpty()) {
            System.out.println("No active agent available.");
            return;
        }

        //─────DISPLAY─────//
        agents.forEach(x -> System.out.println(x.getName()));

        //─────INPUT AND VALIDATE─────//
        System.out.println("Enter a agent name.");
        Account targetAccount = findAccount(agents, input.nextLine());
        if (targetAccount == null){
            System.out.println("Does not exist...");
            return;
        }

        System.out.println("Input Amount");
        BigDecimal inputAmount = input.nextBigDecimal();
        input.nextLine();
        if (Validator.isInvalidAmount(inputAmount))
            return;

        //─────MAIN─────//
        
            this.amount = this.amount.add(
                Database.transfer(
                    targetAccount,
                    this,
                    inputAmount,
                    BigDecimal.ZERO,
                    TransactionType.CASH_IN
                )
            );
        } catch (SQLException e) { 
            // e.printStackTrace();
            System.out.println("Cash in failed...");
        } 
    }

    
    public void payBills(Scanner input) {
        
        List<Account> billers = Database.getAccountsByType(AccountType.BILLER);
        if (billers.isEmpty()) {
            System.out.println("No active billers available.");
            return;
        }

        billers.forEach(x -> System.out.println(x.getUsername()));

        System.out.println("Enter a biller name.");
        Account targetAccount = findAccount(billers, input.nextLine());
        if (targetAccount == null) {
            System.out.println("Does not exist try again...");
            return;
        }

        System.out.println("Input Amount");
        BigDecimal inputAmount = input.nextBigDecimal();
        input.nextLine();
        if (isInvalidAmount(inputAmount) ||
            isInsufficientAmount(inputAmount.add(getBillersFee())))
            return;

        //─────CORE─────//
        try {
            this.amount = this.amount.subtract(
                Database.transfer(
                    this,
                    targetAccount,
                    inputAmount,
                    getBillersFee(),
                    TransactionType.PAY_BILLS
                )
            );
        } catch (SQLException e) { 
            // e.printStackTrace();
            System.out.println("Paying bills failed...");
        }
    }

    private Account findAccount(List <Account> accounts, String targetName) {
        for (Account account : accounts) {
            if (account.getName().equalsIgnoreCase(targetName))
                return account;
        }
        return null;
    }


    public boolean isReceiverNotExist(String receiverCpNumber) {
        try (Connection connection = Database.getConnection()) {
            Account temp = accountData.readAccountData(connection, receiverCpNumber);
            return temp == null;
        } catch (SQLException e) {
            throw new RuntimeException("Database error, reading database failed ", e);
        }
    }
}