package source.util;

import java.math.BigDecimal;

final public class Validator {
    private Validator() {}

    public static boolean isInvalidName(String name) {
        return name == null || name.length() < 4 || 50 < name.length();
    }

    public static boolean isInvalidCpNumber(String cpNumber) {
        return cpNumber == null || cpNumber.length() != 11;
    }

    public static boolean isInvalidMpin(String mpin) {
        return mpin == null || mpin.length() != 6;
    }

    public static boolean isInvalidBusinessName(String businessName) {
        return businessName == null || businessName.length() < 4 || 50 < businessName.length();
    }

    public static boolean isInvalidAmount(BigDecimal amount) {
        return amount == null || amount.compareTo(BigDecimal.ZERO) <= 0;
    }
}
