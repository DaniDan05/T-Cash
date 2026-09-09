package source.model;

import java.math.BigDecimal;


final public class Agent extends Account{

    public Agent (long accountID, String username, String cpNumber) {
        super(
            accountID,
            username,
            cpNumber,
            new BigDecimal(999999999999999L),
            AccountType.AGENT,
            "2026-08-10 00:00:00"
        );
    }
}
