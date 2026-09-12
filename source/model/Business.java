package source.model;

import java.math.BigDecimal;


final public class Business extends Account{
    private String businessName;

    public Business(
        String name,
        String cpNumber,
        AccountType type,
        String businessName
    ) {
        super(
            name,
            cpNumber,
            type
        );
        this.businessName = businessName;
    }

    public Business (
        long accountID,
        String name,
        String cpNumber,
        BigDecimal balance,
        String createdAt,
        String businessName
    ) {
        super(
            accountID,
            name,
            cpNumber,
            balance,
            AccountType.BUSINESS,
            createdAt,
            new BigDecimal(10_000_000)
        );
        this.businessName = businessName;
    }

    public String getBusinessName () {
        return businessName;
    }


    @Override
    public  BigDecimal getSendFee(BigDecimal givenAmount) {
        return super.getSendFee(givenAmount)
                    .multiply(new BigDecimal("0.90"));
    }


    @Override
    public  BigDecimal getBillersFee() {
        return BigDecimal.ZERO;
    }
}
