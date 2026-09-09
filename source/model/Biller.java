package source.model;

import java.math.BigDecimal;

final public class Biller extends Account{

    public Biller (long accountID, String username, String cpNumber, BigDecimal amount) {
        super(
            accountID,
            username,
            cpNumber,
            amount,
            AccountType.BILLER,
            "2026-08-10 00:00:00"
        );
    }
}
