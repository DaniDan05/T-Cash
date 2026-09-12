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
    
    public void main (
        Connection link,
        Account senderAccount,
        Account receiverAccount,
        BigDecimal amount,
        BigDecimal fee,
        Account.TransactionType transactionType 
    ) throws SQLException {
        try {
            // START
            link.setAutoCommit(false);


            // Update sender balance in local
            BigDecimal 
                totalAmount = amount.add(fee),
                newSenderBalance = senderAccount.getBalance().subtract(totalAmount);
            senderAccount.setBalance(newSenderBalance);

            // Update sender balance in database
            accountData.updateAccountData(link, newSenderBalance, senderAccount.getCpNumber());
 
            // Update receiver data
            BigDecimal newReceiverBalance = amount.add(receiverAccount.getBalance());
            accountData.updateAccountData(link, newReceiverBalance, receiverAccount.getCpNumber());
            receiverAccount.setBalance(newReceiverBalance); // for local receiver account


            // i-record sa transactions table
            transactionHistoryData.recordTransaction(
                link,
                senderAccount.getAccountID(),
                receiverAccount.getAccountID(), 
                amount,
                fee,
                transactionType
            );

            link.commit();  // END

        }catch (SQLException e) {
            link.rollback();
            throw new RuntimeException("Database error while transfering the money ", e);
        }
    }
}
