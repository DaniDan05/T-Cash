package source.model;

import java.math.BigDecimal;

final public class Biller extends Account{

    public Biller (long accountID, String name, String cpNumber, BigDecimal amount) {
        super(
            accountID,
            name,
            cpNumber,
            amount,
            AccountType.BILLER,
            "2026-08-10 00:00:00"
        );
    }
}
