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

import source.util.Database;
import source.util.TimeFormat;

final public class TransactionHistoryData {

    public void recordTransaction(
        Connection extension,
        long senderId,
        long receiverId,
        BigDecimal amount,
        BigDecimal fee,
        Account.TransactionType transactionType
    ) throws SQLException {
        // System.out.println(BLUE + "recordTransaction()" + END);

        String insertDataTransactions = 
            "INSERT INTO transactions(sender_id, receiver_id, amount, fee, transaction_type, created_at, reference_number) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement request = extension.prepareStatement(insertDataTransactions)) {
            request.setLong(1, senderId);
            request.setLong(2, receiverId);
            request.setString(3, amount.toPlainString());
            request.setString(4, fee.toPlainString());
            request.setString(5, transactionType.toString());
            request.setString(6, TimeFormat.currentTimeStamp());

            String generatedReference = UUID.randomUUID()
                .toString()
                .substring(0, 12)
                .toUpperCase();
            request.setString(7, generatedReference);

            request.executeUpdate();
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

        try (PreparedStatement request = extension.prepareStatement(fetch.toString())) {

            request.setLong(1, loggedAccountId);
            request.setLong(2, loggedAccountId);

            ResultSet response = request.executeQuery();

            while (response.next()) {
                long senderId = response.getLong("sender_id");
                String senderName = response.getString("sender_name");
                String senderCp = response.getString("sender_cp");
                String receiverName = response.getString("receiver_name");
                String receiverCp = response.getString("receiver_cp");

                String otherName, otherCp, role;
                if (senderId == loggedAccountId) {
                    otherName = receiverName;
                    otherCp = receiverCp;
                    role = "SEND";
                } else {
                    otherName = senderName;
                    otherCp = senderCp;
                    role = "RECEIVE";
                }

                temp.add(new TransactionHistory(
                    role,
                    otherName,
                    otherCp,
                    response.getBigDecimal("amount"),
                    response.getBigDecimal("fee"),
                    response.getString("transaction_type"),
                    response.getString("created_at"),
                    response.getString("reference_number")
                ));
            }

            return temp;

        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }
}