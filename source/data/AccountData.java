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

import source.util.Database;
import source.util.TimeFormat;

final public class AccountData {
    // Account Operations // check
    public boolean createAccountData(
            Connection extension,
            String accountName,
            String cpNumber,
            String mpinHashed,
            String accountType,
            String businessName,
            String walletLimit
    ) throws SQLException {
        String insertDataAccounts = 
            "INSERT INTO accounts(account_name, cp_number, mpin_hashed, account_type, business_name, created_at, wallet_limit) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?);";

        try (PreparedStatement request = link.prepareStatement(insertDataAccounts)) {

            request.setString(1, accountName);
            request.setString(2, cpNumber);
            request.setString(3, mpinHashed);
            request.setString(4, accountType);
            request.setString(5, businessName);
            request.setString(6, TimeFormat.currentTimeStamp());
            request.setString(7, walletLimit);

            return request.executeUpdate() == 1;
        }
    }

    public String findMpin (Connection extension, String cpNumber) throws SQLException {
        String findMPin = 
            "SELECT mpin_hashed " +
            "FROM accounts WHERE cp_number=?";
        try (PreparedStatement request = extension.prepareStatement(findMPin)
        ) {
            request.setString(1, cpNumber);

            ResultSet response = request.executeQuery();

            return response.next() ? response.getString("mpin_hashed") : null;
        }
    }

    // Transfer
    void checkWalletLimit(Connection extension, String receiverCpNumber, BigDecimal sendingAmount) 
    throws SQLException {
        // System.out.println(BLUE + "checkWalletLimit() call." + END);
        
        String findAmount = 
            "SELECT amount, wallet_limit FROM accounts " + 
            "WHERE cp_number=?";
            
        try (PreparedStatement request = extension.prepareStatement(findAmount)) {
            request.setString(1, receiverCpNumber);

            BigDecimal currentReceiverAmount = null, walletLimit = null;

            ResultSet response = request.executeQuery();
            // yung "if" statement 1 row only kapag while statement 2+ rows
            if (response.next()) {
                currentReceiverAmount = new BigDecimal(response.getString(1));
                walletLimit = new BigDecimal(response.getString(2));
            }
            

            // -1 yung left side mababa, 0 equal, 1 opposite nung -1
            if (sendingAmount.add(currentReceiverAmount).compareTo(walletLimit) > 0)
                throw new SQLException("Over the wallet limitation");
        }                 
    }

    // Transfer
    public void updateAccountData(Connection extension, BigDecimal amount, String cpNumber) throws SQLException {
        // System.out.println(BLUE + "updateData() call." + END);
        String editData = "UPDATE accounts SET amount = ? WHERE cp_number = ?;";

        try (PreparedStatement request = extension.prepareStatement(editData)) {
            request.setString(1, amount.toPlainString());
            request.setString(2, cpNumber);

            // Exactly one account should be updated by this statement.
            if(request.executeUpdate() == 0)
                throw new SQLException("Database failed: Failed to update data in database");
        } 
    }

    // TransactionOperation, Transfer
    public Account readAccountData(Connection extension, String cpNumber) throws SQLException {
        // System.out.println(BLUE + "readAccountData() call." + END);
        final String showData = "SELECT * FROM accounts WHERE cp_number=?;";
        
        try (PreparedStatement request = extension.prepareStatement(showData)) {
            request.setString(1, cpNumber);
            
            ResultSet response = request.executeQuery();

            long id = 0;
            String username = null, account_type = null, created_at = null,
                business_name = null, cpNum = null;
            
            BigDecimal amount = null;

            if (response.next()) {
                id = response.getLong("id");
                username = response.getString("account_name");
                cpNum = response.getString("cp_number");
                amount = response.getBigDecimal("amount");
                account_type = response.getString("account_type");
                created_at = response.getString("created_at");
                business_name = response.getString("business_name");
            } else 
                return null;

            switch (AccountType.valueOf(account_type)) {
                case BASIC:
                    return new Basic(id, username, cpNum, amount, created_at);

                case BUSINESS:
                    return new Business(id, username, cpNum, amount, created_at, business_name);

                case AGENT:
                    return new Agent(id, username, cpNum);

                case BILLER:
                    return new Biller(id, username,cpNum, amount);
            
                default:
                    return null;
            }
        }
    }

    // Transaction Operations
    public List<Account> getAccountsByType(Connection extension, AccountType type) throws SQLException {
        List<Account> temp = new ArrayList<>();
        String readType = "SELECT cp_number FROM accounts WHERE account_type = ? ";
        try (PreparedStatement request = extension.prepareStatement(readType)) {
            request.setString(1, type.name());   
            ResultSet response = request.executeQuery();
            while (response.next()) {
                Account account = readAccountData(extension, response.getString("cp_number"));
                if (account != null) {
                    temp.add(account);
                }
            }

        } 
        return temp;
    }
}
