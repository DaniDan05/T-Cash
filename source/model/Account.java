package source.model;

import java.util.List;

import java.math.BigDecimal;
import java.math.RoundingMode;




public abstract class Account{

    private long accountID;
    private String name;
    private String cpNumber;
    private BigDecimal balance;
    private AccountType type;
    private String createdAt;
    private BigDecimal walletLimit; // TODO: Remove mo ito ilagay mo sa database as column it can be modified by malicious attacks


    public static enum AccountType {
        BASIC,
        BUSINESS,
        AGENT,
        BILLER
    }

    
    public static enum TransactionType {
        SEND,
        CASH_IN,
        PAY_BILLS
    }
    
     // para sa create ng data
    public Account(
        String name,
        String cpNumber,
        AccountType type
    ) {
        this.name = name;
        this.cpNumber = cpNumber;
        this.type = type;
    }

    // For getting info
    public Account(
        long accountID,
        String name,
        String cpNumber,
        BigDecimal balance,
        AccountType type,
        String createdAt,
        BigDecimal walletLimit
    ) {
        this.accountID = accountID;
        this.name = name;
        this.cpNumber = cpNumber;
        this.balance = balance;
        this.type = type;
        this.createdAt = createdAt;
        this.walletLimit = walletLimit;
    }

   


    public BigDecimal getSendFee(BigDecimal givenAmount) {

        return givenAmount.divide(
            new BigDecimal("100.00"),
            0,
            RoundingMode.CEILING).multiply(new BigDecimal("2")
        );
    }

    public void setBalance(BigDecimal balance){
        this.balance = balance;
    }


    public BigDecimal getBillersFee() {
        return new BigDecimal("30");
    }

    public long getAccountID(){
        return accountID;
    }

    public String getName(){
        return name;
    }

    public String getCpNumber(){
        return cpNumber;
    }

    public BigDecimal getBalance(){
        return balance;
    }

    public AccountType getAccountType() {
        return type;
    }

    public String getCreatedAt() {
        return createdAt;
    }
    
    public BigDecimal getWalletLimit() {
        return walletLimit;
    }
}