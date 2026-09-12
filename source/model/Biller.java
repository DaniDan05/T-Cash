package source.model;

import java.math.BigDecimal;

final public class Biller extends Account{

    public Biller (long accountID, String name, String cpNumber, BigDecimal balance) {
        super(
            accountID,
            name,
            cpNumber,
            balance,
            AccountType.BILLER,
            "2026-08-10 00:00:00",
            new BigDecimal(10)
        );
    }
}
