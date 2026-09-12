package source.data;

import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import source.model.Account;
import source.model.TransactionHistory;

import source.util.TimeFormat;

final public class TransactionHistoryData {

    public void recordTransaction(
        Connection extension,
        long senderId,
        long receiverId,
        BigDecimal balance,
        BigDecimal fee,
        Account.TransactionType transactionType
    ) throws SQLException {
        // System.out.println(BLUE + "recordTransaction()" + END);

        String insertDataTransactions = 
            "INSERT INTO transactions(sender_id, receiver_id, amount, fee, transaction_type, created_at, reference_number) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = extension.prepareStatement(insertDataTransactions)) {
            statement.setLong(1, senderId);
            statement.setLong(2, receiverId);
            statement.setString(3, balance.toPlainString());
            statement.setString(4, fee.toPlainString());
            statement.setString(5, transactionType.toString());
            statement.setString(6, TimeFormat.currentTimeStamp());

            String generatedReference = UUID.randomUUID()
                .toString()
                .substring(0, 12)
                .toUpperCase();
            statement.setString(7, generatedReference);

            statement.executeUpdate();
        }
    }

    public List<TransactionHistory> fetchTransactions(Connection extension, long loggedAccountId, int maxRows) {
        List<TransactionHistory> temp = new ArrayList<>();

        StringBuilder fetch = new StringBuilder(
            "SELECT " +
                "transactions.id, " +
                "transactions.sender_id, " +
                "transactions.receiver_id, " +
                "transactions.amount, " +
                "transactions.fee, " +
                "transactions.transaction_type, " +
                "transactions.created_at, " +
                "transactions.reference_number, " +
                "sender_account.account_name AS sender_name, " +
                "sender_account.cp_number AS sender_cp, " +
                "receiver_account.account_name AS receiver_name, " +
                "receiver_account.cp_number AS receiver_cp " +
            "FROM transactions " +
            "JOIN accounts AS sender_account ON transactions.sender_id = sender_account.id " +
            "JOIN accounts AS receiver_account ON transactions.receiver_id = receiver_account.id " +
            "WHERE transactions.sender_id = ? OR transactions.receiver_id = ? " +
            "ORDER BY transactions.id DESC"
        );

        if (maxRows > 0) {
            fetch.append(" LIMIT ").append(maxRows);
        }

        try (PreparedStatement statement = extension.prepareStatement(fetch.toString())) {

            statement.setLong(1, loggedAccountId);
            statement.setLong(2, loggedAccountId);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                long senderId = result.getLong("sender_id");
                String senderName = result.getString("sender_name");
                String senderCp = result.getString("sender_cp");
                String receiverName = result.getString("receiver_name");
                String receiverCp = result.getString("receiver_cp");

                String otherName, otherCp, role;
                if (senderId == loggedAccountId) {
                    otherName = receiverName;
                    otherCp = receiverCp;
                    role = "SENT";
                } else {
                    otherName = senderName;
                    otherCp = senderCp;
                    role = "RECEIVED";
                }

                temp.add(new TransactionHistory(
                    role,
                    otherName,
                    otherCp,
                    result.getBigDecimal("amount"),
                    result.getBigDecimal("fee"),
                    result.getString("transaction_type"),
                    result.getString("created_at"),
                    result.getString("reference_number")
                ));
            }

            return temp;

        } catch (SQLException e) {
            throw new RuntimeException("Database error, can't fetch transaction history...");
        }
    }
}