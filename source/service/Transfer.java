package source.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

import source.data.AccountData;
import source.data.TransactionHistoryData;

import source.model.Account;

final public class Transfer {
    final private AccountData accountData;
    final private TransactionHistoryData transactionHistoryData;

    public Transfer (AccountData accountData, TransactionHistoryData transactionHistoryData) {
        this.accountData = accountData;
        this.transactionHistoryData = transactionHistoryData;
    }
    
    public BigDecimal main (
        Connection extension,
        Account senderAccount,
        Account receiverAccount,
        BigDecimal amount,
        BigDecimal fee,
        Account.TransactionType transactionType 
    ) throws SQLException {
        BigDecimal total = BigDecimal.ZERO;
        try {
            // START
            extension.setAutoCommit(false);

            // sender Deduction
            BigDecimal currentAmount = senderAccount.getAmount().subtract(amount.add(fee));
            if (currentAmount.compareTo(BigDecimal.ZERO) < 0)
                throw new IllegalArgumentException("Insufficient balance.");

            // Update sender data
            accountData.updateAccountData(extension, currentAmount, senderAccount.getCpNumber());
 

            BigDecimal walletLimit = accountData.findWalletLimit(extension, receiverAccount.getCpNumber());
            if (walletLimit == null) 
                throw new IllegalArgumentException("Receiver account does not exist.");
            
            // Receiver additional
            BigDecimal newReceiverBalance = receiverAccount.getAmount().add(amount);
            if (newReceiverBalance.compareTo(walletLimit) > 0) 
                throw new IllegalArgumentException("Over wallet limitation.");

            // Update receiver data
            accountData.updateAccountData(extension, amount.add(receiverAccount.getAmount()), receiverAccount.getCpNumber());
                
            // Update local value
            total = amount.add(fee); // Return the total amount deducted from the sender.

            // i-record sa transactions table
            transactionHistoryData.recordTransaction(
                extension,
                senderAccount.getAccountID(),
                receiverAccount.getAccountID(), 
                amount,
                fee,
                transactionType
            );

            extension.commit();  // END

        }catch (SQLException e) {
            extension.rollback();
            throw new RuntimeException("Database error while transfering the money ", e);
        } catch (IllegalArgumentException e) {
            extension.rollback();
            throw e;  //
        }
        return total;
    }
}
