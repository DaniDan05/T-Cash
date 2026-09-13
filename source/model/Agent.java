package source.model;

import java.math.BigDecimal;


final public class Agent extends Account{

    public Agent (long accountID, String name, String cpNumber) {
        super(
            accountID,
            name,
            cpNumber,
            new BigDecimal(999999999999999L),
            AccountType.AGENT,
            "2026-08-10 00:00:00", // TODO: Reserve ka muna
            new BigDecimal(999999999999999L)
        );
    }
}
