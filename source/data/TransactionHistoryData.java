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
        BigDecimal amount,
        BigDecimal fee,
        Account.TransactionType transactionType
    ) throws SQLException {

        String insertDataTransactions = 
            "INSERT INTO `transaction`(sender_id, receiver_id, amount, fee, transaction_type, created_at, reference_number) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = extension.prepareStatement(insertDataTransactions)) {
            statement.setLong(1, senderId);
            statement.setLong(2, receiverId);
            statement.setBigDecimal(3, amount);
            statement.setBigDecimal(4, fee);
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

    public List<TransactionHistory> fetchTransactions(
        Connection extension,
        long loggedAccountId,
        int maxRows
    ) throws SQLException {

        List<TransactionHistory> temp = new ArrayList<>();

        StringBuilder fetch = new StringBuilder(
            "SELECT " +
                "`transaction`.id, " +
                "`transaction`.sender_id, " +
                "`transaction`.receiver_id, " +
                "`transaction`.amount, " +
                "`transaction`.fee, " +
                "`transaction`.transaction_type, " +
                "`transaction`.created_at, " +
                "`transaction`.reference_number, " +
                "sender_account.account_name AS sender_name, " +
                "sender_account.cp_number AS sender_cp, " +
                "receiver_account.account_name AS receiver_name, " +
                "receiver_account.cp_number AS receiver_cp " +
            "FROM `transaction` " +
            "JOIN account AS sender_account ON `transaction`.sender_id = sender_account.id " +
            "JOIN account AS receiver_account ON `transaction`.receiver_id = receiver_account.id " +
            "WHERE `transaction`.sender_id = ? OR `transaction`.receiver_id = ? " +
            "ORDER BY `transaction`.id DESC"
        );

        if (maxRows > 0) 
            fetch.append(" LIMIT ").append(maxRows);

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

        } 
    }
}