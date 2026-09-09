package source.model;

import java.math.BigDecimal;

final public class Basic extends Account{

    
    public Basic(
        String username,
        String cpNumber,
        AccountType type
    ) {
        super(
            username,
            cpNumber,
            type
        );
    }

    public Basic (
        long accountID,
        String username,
        String cpNumber,
        BigDecimal amount,
        String createdAt
    ) {
        super(
            accountID,
            username,
            cpNumber,
            amount,
            AccountType.BASIC,
            createdAt
        );
    }
    
}