package source.model;

import java.util.List;

import java.math.BigDecimal;
import java.math.RoundingMode;




public abstract class Account{

    private long accountID;
    private String username;
    private String cpNumber;
    private BigDecimal amount;
    private AccountType type;
    private String createdAt;


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
        String username,
        String cpNumber,
        AccountType type
    ) {
        this.username = username;
        this.cpNumber = cpNumber;
        this.type = type;
    }

    // For getting info
    public Account(
        long accountID,
        String username,
        String cpNumber,
        BigDecimal amount,
        AccountType type,
        String createdAt
    ) {
        this.accountID = accountID;
        this.username = username;
        this.cpNumber = cpNumber;
        this.amount = amount;
        this.type = type;
        this.createdAt = createdAt;
    }

   


    protected BigDecimal getSendFee(BigDecimal givenAmount) {

        return givenAmount.divide(
            new BigDecimal("100.00"),
            0,
            RoundingMode.CEILING).multiply(new BigDecimal("2")
        );
    }


    protected BigDecimal getBillersFee() {
        return new BigDecimal("30");
    }

    public long getAccountID(){
        return accountID;
    }

    public String getUsername(){
        return username;
    }

    public String getCpNumber(){
        return cpNumber;
    }

    public BigDecimal getAmount(){
        return amount;
    }

    public AccountType getAccountType() {
        return type;
    }

    public String getCreatedAt() {
        return createdAt;
    }


    private Account findAccount(List <Account> accounts, String targetName) {
        for (Account account : accounts) {
            if (account.getUsername().equalsIgnoreCase(targetName))
                return account;
        }
        return null;
    }


    private boolean isInvalidAmount(BigDecimal inputAmount) {
        if (inputAmount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Cancelled: Invalid Value.");
            return true;
        }

        return false;
    }

    
    private boolean isInsufficientAmount(BigDecimal inputAmount) {
        if (this.amount.compareTo(inputAmount) < 0) {
            System.out.println("Cancelled: Insufficient amount.");
            return true;
        }

        return false;
    }
}