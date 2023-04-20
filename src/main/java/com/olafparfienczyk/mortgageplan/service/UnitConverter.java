package com.olafparfienczyk.mortgageplan.service;

public interface UnitConverter {

    /**
     * @return Amount of months in a year
     */
    int getMonthsInYear();

    /**
     * Converts cents to fractional monetary value.
     *
     * @param cents Cents to convert
     * @return Cents expressed as a fraction
     */
    double centsToFraction(long cents);

    /**
     * Converts fractional monetary value to cents.
     * It's guaranteed to retain the value of first 2 fractional digits.
     *
     * @param wholeValue Whole monetary value e.g. 11.44
     * @return Monetary value expressed in cents
     */
    long fractionToCents(double wholeValue);

    /**
     * Converts base points to ratio.
     *
     * @param basePoints Base points to convert
     * @return Ratio representation of given base points
     */
    double basePointsToRatio(int basePoints);

    /**
     * Converts base points to percentage.
     *
     * @param basePoints Base points to convert
     * @return Percentage representation of given base points
     */
    double basePointsToPercentage(int basePoints);

    /**
     * Converts percentage to base points.
     * It's guaranteed to retain the value of first 2 fractional digits.
     *
     * @param percentage Percentage to convert
     * @return Base points' representation of given percentage
     */
    int percentageToBasePoints(double percentage);

}