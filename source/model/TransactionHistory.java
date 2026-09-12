package source.model;

import java.math.BigDecimal;


public class TransactionHistory {
    private String role;
    private String counterpartyName;
    private String counterpartyCpNumber;
    private BigDecimal amount;
    private BigDecimal fee;
    private String transactionalType;
    private String createdAt;
    private String referenceNumber;


    public TransactionHistory (
        String role,
        String counterpartyName,
        String counterpartyCpNumber,
        BigDecimal amount,
        BigDecimal fee,
        String transactionType,
        String createdAt,
        String referenceNumber
    ) {
        this.role = role;
        this.counterpartyName = counterpartyName;
        this.counterpartyCpNumber = counterpartyCpNumber;
        this.amount = amount ;
        this.fee = fee;
        this.transactionalType = transactionType;
        this.createdAt = createdAt;
        this.referenceNumber = referenceNumber;
    }

    
    public String getRole() {
        return role;
    }

    public String getCounterpartyName() {
        return counterpartyName;
    }

    public String getCounterpartyCpNumber() {
        return counterpartyCpNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public String getTransactionType() {
        return transactionalType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    
    @Override
    public String toString(){
        boolean isSend = "SENT".equals(getRole());
        String sign = isSend ? "-" : "+";
        String fee = isSend ? "\nFee: -₱" + getFee() : "";
        return getRole() +
            "\nName: " + getCounterpartyName() + 
            "\nNumber: " + getCounterpartyCpNumber() + 
            "\nAmount: " + sign + "₱"+ getAmount() + 
            fee + 
            "\nType: " + getTransactionType() + 
            "\nTime Generated: " + getCreatedAt() + 
            "\nReference Number: " + getReferenceNumber() + 
            "\n\n===================\n";
    }


}
