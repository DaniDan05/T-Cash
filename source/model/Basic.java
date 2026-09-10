package source.model;

import java.math.BigDecimal;

final public class Basic extends Account{

    
    public Basic(
        String name,
        String cpNumber,
        AccountType type
    ) {
        super(
            name,
            cpNumber,
            type
        );
    }

    public Basic (
        long accountID,
        String name,
        String cpNumber,
        BigDecimal amount,
        String createdAt
    ) {
        super(
            accountID,
            name,
            cpNumber,
            amount,
            AccountType.BASIC,
            createdAt
        );
    }
    
}