package source.data;

import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import source.model.Account;
import source.model.Account.AccountType;
import source.model.Agent;
import source.model.Basic;
import source.model.Biller;
import source.model.Business;

import source.util.TimeFormat;

final public class AccountData {
    // Account Operations // check
    public boolean createAccountData(
            Connection extension,
            String accountName,
            String cpNumber,
            String mpinHashed,
            String accountType,
            String businessName
    ) throws SQLException {
        String qInsertDataAccounts = 
            "INSERT INTO accounts(account_name, cp_number, mpin_hashed, account_type, business_name, created_at) " +
            "VALUES (?, ?, ?, ?, ?, ?);";

        try (PreparedStatement statement = extension.prepareStatement(qInsertDataAccounts)) {

            statement.setString(1, accountName);
            statement.setString(2, cpNumber);
            statement.setString(3, mpinHashed);
            statement.setString(4, accountType);
            statement.setString(5, businessName);
            statement.setString(6, TimeFormat.currentTimeStamp());

            return statement.executeUpdate() == 1;
        }
    }

    public String findAccountName (Connection extension, String accountName) throws SQLException {
        String qFindAccountName = 
            "SELECT account_name " +
            "FROM accounts WHERE account_name=?";
        try (PreparedStatement statement = extension.prepareStatement(qFindAccountName)
        ) {
            statement.setString(1, accountName);

            ResultSet result = statement.executeQuery();

            return result.next() ? result.getString("account_name") : null;
        }
    }

    public String findMpin (Connection extension, String cpNumber) throws SQLException {
        String qFindMPin = 
            "SELECT mpin_hashed " +
            "FROM accounts WHERE cp_number=?";
        try (PreparedStatement statement = extension.prepareStatement(qFindMPin)
        ) {
            statement.setString(1, cpNumber);

            ResultSet result = statement.executeQuery();

            return result.next() ? result.getString("mpin_hashed") : null;
        }
    }

    // Transfer
    public void updateAccountData(Connection extension, BigDecimal balance, String cpNumber) throws SQLException {
        String qEdit = "UPDATE accounts SET balance = ? WHERE cp_number = ?;";

        try (PreparedStatement statement = extension.prepareStatement(qEdit)) {
            statement.setString(1, balance.toPlainString());
            statement.setString(2, cpNumber);

            // Exactly one account should be updated by this statement.
            if(statement.executeUpdate() == 0)
                throw new SQLException("Database failed: Failed to update data in database");
        } 
    }

    // TransactionOperation, Transfer
    public Account readAccountData(Connection extension, String cpNumber) throws SQLException {
        final String qShowData = "SELECT * FROM accounts WHERE cp_number=?;";
        
        try (PreparedStatement statement = extension.prepareStatement(qShowData)) {
            statement.setString(1, cpNumber);
            
            ResultSet result = statement.executeQuery();

            long id = 0;
            String username = null, account_type = null, created_at = null,
                business_name = null, cpNum = null;
            
            BigDecimal balance = null;

            if (result.next()) {
                id = result.getLong("id");
                username = result.getString("account_name");
                cpNum = result.getString("cp_number");
                balance = result.getBigDecimal("balance");
                account_type = result.getString("account_type");
                created_at = result.getString("created_at");
                business_name = result.getString("business_name");
            } else 
                return null;

            switch (AccountType.valueOf(account_type)) {
                case BASIC:
                    return new Basic(id, username, cpNum, balance, created_at);

                case BUSINESS:
                    return new Business(id, username, cpNum, balance, created_at, business_name);

                case AGENT:
                    return new Agent(id, username, cpNum);

                case BILLER:
                    return new Biller(id, username,cpNum, balance);
            
                default:
                    return null;
            }
        }
    }

    // Transaction Operations
    public List<Account> getAccountsByType(Connection extension, AccountType type) throws SQLException {
        List<Account> temp = new ArrayList<>();
        String qGetAccountsByType = "SELECT cp_number FROM accounts WHERE account_type = ? ";
        try (PreparedStatement statement = extension.prepareStatement(qGetAccountsByType)) {
            statement.setString(1, type.name());   
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Account account = readAccountData(extension, result.getString("cp_number"));
                if (account != null) {
                    temp.add(account);
                }
            }

        } 
        return temp;
    }
}
