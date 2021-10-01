package com.mingcraft.minglib.number;


import java.util.List;

public class Roman {

    public static String toRoman(int arabicNumber) {
        if ((arabicNumber <= 0) || (arabicNumber > 4000)) {
            throw new IllegalArgumentException(arabicNumber + " is not in range (0,4000]");
        }

        List<RomanNumeral> romanNumerals = RomanNumeral.getReverseSortedValues();

        int i = 0;
        StringBuilder sb = new StringBuilder();

        while ((arabicNumber > 0) && (i < romanNumerals.size())) {
            RomanNumeral currentSymbol = romanNumerals.get(i);
            if (currentSymbol.getValue() <= arabicNumber) {
                sb.append(currentSymbol.name());
                arabicNumber -= currentSymbol.getValue();
            } else {
                i++;
            }
        }

        return sb.toString();
    }

    public static int toArabic(String romanNumber) {
        String romanNumeral = romanNumber.toUpperCase();
        int result = 0;

        List<RomanNumeral> romanNumerals = RomanNumeral.getReverseSortedValues();

        int i = 0;

        while ((romanNumeral.length() > 0) && (i < romanNumerals.size())) {
            RomanNumeral symbol = romanNumerals.get(i);
            if (romanNumeral.startsWith(symbol.name())) {
                result += symbol.getValue();
                romanNumeral = romanNumeral.substring(symbol.name().length());
            } else {
                i++;
            }
        }

        if (romanNumeral.length() > 0) {
            throw new IllegalArgumentException(romanNumber + " cannot be converted to a Roman Numeral");
        }

        return result;
    }

}
