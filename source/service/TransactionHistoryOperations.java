package source.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import source.data.TransactionHistoryData;
import source.model.TransactionHistory;
import source.util.Database;

final public class TransactionHistoryOperations {
    final private TransactionHistoryData transactionHistoryData;

    public TransactionHistoryOperations (TransactionHistoryData transactionHistoryData) {
        this.transactionHistoryData = transactionHistoryData;
    }

    public List<TransactionHistory> retrieveTransactionHistories(long accountId) {
        try (Connection conn = Database.getConnection()) {
            List<TransactionHistory> temp = transactionHistoryData.fetchTransactions(conn, accountId, 0);
            if (temp.isEmpty()) throw new IllegalArgumentException("No recorded history.");
            return temp;

        } catch (SQLException e) {
            throw new RuntimeException("Database error: Could not retrieve history", e);
        }
    }

    public TransactionHistory retrieveReceipt(long accountId) {
        try (Connection conn = Database.getConnection()) {
            List<TransactionHistory> list = transactionHistoryData.fetchTransactions(conn, accountId, 1);
            return list.isEmpty() ? null : list.get(0);
        } catch (SQLException e) {
            throw new RuntimeException("Database error: Could not retrieve receipt", e);
        }
    }
}
