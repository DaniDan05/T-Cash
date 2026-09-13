package source.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import source.data.AccountData;

import source.model.Account;
import source.model.Basic;
import source.model.Business;
import source.model.Account.AccountType;

import source.util.Database;

import org.mindrot.jbcrypt.BCrypt;

final public class AccountOperations {
    final private AccountData accountData;

    public AccountOperations (AccountData accountData) {
        this.accountData = accountData;
    }
 
    //check
    public void generateAccount(
        String inputAccountName,
        String inputCpNumber,
        String inputMpin,
        String inputBusinessName,
        boolean isBusinessAccount
    ) {
        try (Connection link = Database.getConnection()) {

            boolean isAccountNameExist = accountData.findAccountName(link, inputAccountName) != null,
                    isCpNumberExist = accountData.readAccountData(link, inputCpNumber) != null;

            if(isAccountNameExist || isCpNumberExist)
                throw new IllegalArgumentException("Name or number is already existed.");

            boolean isGeneratingSuccess = accountData.createAccountData(
                link,
                inputAccountName,
                inputCpNumber,
                BCrypt.hashpw(inputMpin, BCrypt.gensalt()),
                isBusinessAccount ? "BUSINESS" : "BASIC",
                inputBusinessName
            );       

            if (!isGeneratingSuccess)
                throw new  IllegalArgumentException("Generating account failed.");
        } catch (SQLException e) {
            // e.printStackTrace(); Debugging purposes
            throw new RuntimeException("Database error: creating data in database failed...", e);
        }
    }

    // ========== RESERVED ==========
    public boolean isCpNumberExist(String inputCpNumber) {
        try(Connection link = Database.getConnection()) {
            return accountData.readAccountData(link, inputCpNumber) != null;
        }
        catch (SQLException e) {
            throw new RuntimeException("Database error: finding data in database failed...", e);
        }
    }
    // ========== ========== ==========
    
    // check
    public Account authenticate(String inputCpNumber, String inputMPin) {
        
        try (Connection link = Database.getConnection()) {
            String storedHash = accountData.findMpin(link, inputCpNumber);
            if (storedHash == null || !BCrypt.checkpw(inputMPin, storedHash))
                throw new IllegalArgumentException("Number does not exist or wrong password.");

            return accountData.readAccountData(link, inputCpNumber);
        } catch (SQLException e) {
            throw new RuntimeException("Database error: Authentication failed...", e);
        }
    }

    public Account getAccountByCpNumber(String cpNumber) {
    try (Connection conn = Database.getConnection()) {
        return accountData.readAccountData(conn, cpNumber);
    } catch (SQLException e) {
        throw new RuntimeException("Database error while retrieving account", e);
    }
}
}
