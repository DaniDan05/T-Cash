package source.util;

import java.math.BigDecimal;

final public class Validator {
    private Validator() {}

    public static boolean isNotNumerical(String number) {
        for(int index = 0; index < number.length(); index++) {
            char symbol = number.charAt(index);
            if(symbol < '0' || '9' < symbol)
                return true;
        }
        return false;
    }

    public static boolean isNotFormatName(String name) {
        for(int index = 0; index < name.length(); index++) {
            char symbol = name.charAt(index);
            boolean condition =
                symbol == ' ' || //  whitespace
                symbol == '_' || // underscore
                ('A' <= symbol && symbol <= 'Z') || // A - Z
                ('a' <= symbol && symbol <= 'z') || // a - z
                ('0' <= symbol && symbol <= '9');// 0 - 9
            if(!condition)
                return true;
        }
        return false;
    }
    
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
