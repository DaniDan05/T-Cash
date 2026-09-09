package source.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.crypto.Data;

import source.data.AccountData;

import source.model.Account;
import source.model.Basic;
import source.model.Business;
import source.model.Account.AccountType;

import source.util.Database;

final public class AccountOperations {
    final private AccountData accountData;

    public AccountOperations (AccountData accountData) {
        this.accountData = accountData;
    }
 
    //check
    public void generateAccount(
        String accountName,
        String cpNumber,
        String mpin,
        String businessName,
        boolean isBusinessAccount
    ) {
        try (Connection link = Database.getConnection()) {

            boolean isCpNumberExist = accountData.readAccountData(link, cpNumber) != null;

            if(isCpNumberExist)
                throw new  IllegalArgumentException("Number is already existed.");

            boolean isGeneratingSuccess = accountData.createAccountData(
                link,
                accountName,
                cpNumber,
                BCrypt.hashpw(mpin, BCrypt.gensalt()),
                isBusinessAccount ? "BUSINESS" : "BASIC",
                businessName,
                isBusinessAccount ? "10000000" : "10000"
            );       

            if (!isGeneratingSuccess)
                throw new  IllegalArgumentException("Generating account failed.");
        } catch (SQLException e) {
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
}
