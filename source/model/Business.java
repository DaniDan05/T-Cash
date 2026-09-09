package source.model;

import java.math.BigDecimal;


final public class Business extends Account{
    private String businessName;

    public Business(
        String username,
        String cpNumber,
        AccountType type,
        String businessName
    ) {
        super(
            username,
            cpNumber,
            type
        );
        this.businessName = businessName;
    }

    public Business (
        long accountID,
        String username,
        String cpNumber,
        BigDecimal amount,
        String createdAt,
        String businessName
    ) {
        super(
            accountID,
            username,
            cpNumber,
            amount,
            AccountType.BUSINESS,
            createdAt
        );
        this.businessName = businessName;
    }

    public String getBusinessName () {
        return businessName;
    }


    @Override
    protected BigDecimal getSendFee(BigDecimal givenAmount) {
        return super.getSendFee(givenAmount)
                    .multiply(new BigDecimal("0.90"));
    }


    @Override
    protected BigDecimal getBillersFee() {
        return BigDecimal.ZERO;
    }
}
