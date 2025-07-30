package com.example.bankcards.util;

public class CardMaskUtil {

    private CardMaskUtil() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static String maskCardNumber(String fullNumber) {
        if (fullNumber == null || fullNumber.length() < 4) return "****";
        return "**** **** **** " + fullNumber.substring(fullNumber.length() - 4);
    }
}
