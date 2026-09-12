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
        BigDecimal balance,
        String createdAt
    ) {
        super(
            accountID,
            name,
            cpNumber,
            balance,
            AccountType.BASIC,
            createdAt,
            new BigDecimal(10_000)
        );
    }
    
}