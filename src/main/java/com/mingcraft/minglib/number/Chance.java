package com.mingcraft.minglib.number;

import java.util.List;

public class Chance {

    public enum Decimal {

        ONE(10),
        TWO(100),
        THREE(1000),
        FOUR(10000),
        FIVE(100000),
        SIX(1000000),
        SEVEN(10000000),
        EIGHT(100000000),
        NINE(1000000000)
        ;

        private final int places;

        Decimal(int places) {
            this.places = places;
        }

        public int getPlaces() {
            return places;
        }

        public static int parseValue(double value, Decimal decimal) {
            return (int) (value * decimal.getPlaces());
        }

    }

    public static int randomInteger() {
        return (int) (Math.random() * Integer.MAX_VALUE);
    }

    public static int randomInteger(int max) {
        return (int) (Math.random() * max);
    }

    public static int randomInteger(Decimal decimal) {
        return (int) (Math.random() * decimal.getPlaces());
    }

    public static int randomInteger(int min, int max) {
        return Math.min((int) ((Math.random() * max) + min), max);
    }

    public static boolean isValueCanAppear(double value, Decimal decimal) {
        return randomInteger(decimal) < Decimal.parseValue(value, decimal);
    }

    public static boolean isValueCanAppear(double value, int min, Decimal decimal) {
        return randomInteger(Math.min(min, decimal.getPlaces()), decimal.getPlaces()) < Decimal.parseValue(value, decimal);
    }

    public static double percentageToValue(double percentage) {
        return percentage / 100;
    }

    public static int getAppearIndex(List<Double> values, Decimal decimal) {
        int random = randomInteger(decimal);
        int compare = 0;
        int count = 0;
        for (double value : values) {
            compare += Decimal.parseValue(value, decimal);
            if (random < compare) {
                return count;
            }
            count++;
        }
        return values.size() - 1;
    }

}
