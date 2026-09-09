package source.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import source.data.AccountData;
import source.data.TransactionHistoryData;
import source.model.Account;
import source.util.Database;

final public class Transfer {
    final private AccountData accountDB;
    final private TransactionHistoryData recordsDB;

    public Transfer (AccountData accountDB, TransactionHistoryData recordsDB) {
        this.accountDB = accountDB;
        this.recordsDB = recordsDB;
    }
    
    public BigDecimal main (
        Account senderAccount,
        Account receiverAccount,
        BigDecimal amount,
        BigDecimal fee,
        Account.TransactionType transactionType 
    ) throws SQLException {
        BigDecimal total = BigDecimal.ZERO;
        Connection link = Database.getConnection();
        try {
            // Begin the transaction so all balance changes succeed or fail together.
            link.setAutoCommit(false);

            // Deduct the transfer amount and fee from the sender.
            BigDecimal currentAmount = senderAccount.getAmount().subtract(amount.add(fee));
            if (currentAmount.compareTo(BigDecimal.ZERO) < 0)
                throw new SQLException("BAWAL UTANG!!!");

            updateData(link, currentAmount, senderAccount.getCpNumber());

            // Verify that the receiver will remain within the wallet limit.
            checkWalletLimit(link, receiverAccount.getCpNumber(), amount);

            // Add the transfer amount to the receiver's balance.
            currentAmount = amount.add(receiverAccount.getAmount());

            updateData(link, amount.add(receiverAccount.getAmount()), receiverAccount.getCpNumber());
                

            total = amount.add(fee); // Return the total amount deducted from the sender.


            recordTransaction(
                link,
                senderAccount.getAccountID(),
                receiverAccount.getAccountID(), 
                amount,
                fee,
                transactionType
            );

            link.commit();  // Commit only after both balance updates and the history record succeed.

        }         
        catch (SQLException e) {
            // e.printStackTrace();
            link.rollback();
            throw e;
        }
        finally{link.close(); }        
        return total;
    }
}
