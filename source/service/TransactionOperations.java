package source.service;

import java.util.List;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

import source.data.AccountData;

import source.model.Account;
import source.model.Account.AccountType;
import source.util.Database;


final public class TransactionOperations {
    final private AccountData accountData;
    final private Transfer transfer;

    public TransactionOperations (AccountData accountData, Transfer transfer) {
        this.accountData = accountData;
        this.transfer = transfer;
    }
        

    public Account findAccount(List <Account> accounts, String targetName) {
        for (Account account : accounts) {
            if (account.getName().equalsIgnoreCase(targetName))
                return account;
        }
        throw new IllegalArgumentException("Does not exist.");
    }


    public Account checkSendDataAndGetReceiverAccount(Account sender, String receiverCpNumber, BigDecimal amount, BigDecimal fee) {
        try (Connection connection = Database.getConnection()) {
            if (isInsufficient(sender.getBalance(), amount.add(fee)))
                throw new IllegalArgumentException("Insufficient balance.");

            Account receiverAccount = accountData.readAccountData(connection, receiverCpNumber);
            if (receiverAccount == null)
                throw new IllegalArgumentException("Account does not exist.");

            if (isOverWalletLimit(receiverAccount.getBalance(), amount, receiverAccount.getWalletLimit()))
                throw new IllegalArgumentException("The account is in balance limit.");

            if (sender.getCpNumber().equals(receiverCpNumber))
                throw new IllegalArgumentException("Invalid target cellphone number.");

            return receiverAccount;

        } catch (SQLException e) {
            throw new RuntimeException("Database error, reading database failed.", e);
        }
    }

    public List<Account> retrieveAccounts(AccountType type) {
        try (Connection link = Database.getConnection()) {
            List<Account> accounts = accountData.getAccountsByType(link, type);
            if (accounts == null || accounts.isEmpty())
                throw new IllegalArgumentException("Does not exist or no account available.");
            return accounts;
        } catch (SQLException e) {
            throw new RuntimeException("Database error, retrieving accounts failed.");
        }
    }

    public void getTransfer(
        Account senderAccount,
        Account receiverAccount,
        BigDecimal amount,
        BigDecimal fee,
        Account.TransactionType transactionType 
    ) {
        try (Connection link = Database.getConnection()) {
            transfer.main(link, senderAccount, receiverAccount, amount, fee, transactionType);
        } catch (SQLException e) {
            throw new RuntimeException("Transfer error, commit and rollback failed...");
        }
    }

    public void validateSufficientBalance(BigDecimal balance, BigDecimal totalAmount) {
    if (isInsufficient(balance, totalAmount))
        throw new IllegalArgumentException("Insufficient balance.");
}

    public void validateWalletLimit(BigDecimal currentBalance, BigDecimal amount, BigDecimal walletLimit) {
        if (isOverWalletLimit(currentBalance, amount, walletLimit))
            throw new IllegalArgumentException("Over wallet limitation.");
    }

    private static boolean isInsufficient(BigDecimal balance, BigDecimal totalAmount) {
        return balance.compareTo(totalAmount) < 0;
    }

    private static boolean isOverWalletLimit(BigDecimal currentBalance, BigDecimal amount, BigDecimal walletLimit) {
        return walletLimit.compareTo(currentBalance.add(amount)) < 0;
    }

    /*
    BigDecimal walletLimit = accountData.findWalletLimit(extension, receiverAccount.getCpNumber());
            if (walletLimit == null) 
                throw new IllegalArgumentException("Receiver account does not exist.");
            
            // Receiver additional
            BigDecimal newReceiverBalance = receiverAccount.getAmount().add(amount);
            if (newReceiverBalance.compareTo(walletLimit) > 0) 
                throw new IllegalArgumentException("Over wallet limitation."); */
}