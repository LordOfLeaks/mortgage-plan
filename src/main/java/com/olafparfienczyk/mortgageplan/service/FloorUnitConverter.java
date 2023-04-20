package com.olafparfienczyk.mortgageplan.service;

import org.springframework.stereotype.Component;

@Component
public class FloorUnitConverter implements UnitConverter {

    @Override
    public int getMonthsInYear() {
        return 12;
    }

    @Override
    public double centsToFraction(long cents) {
        return (double) cents / 100;
    }

    @Override
    public long fractionToCents(double wholeValue) {
        return (long) (wholeValue * 100);
    }

    @Override
    public double basePointsToRatio(int basePoints) {
        return (double) basePoints / 10_000;
    }

    @Override
    public double basePointsToPercentage(int basePoints) {
        return (double) basePoints / 100;
    }

    @Override
    public int percentageToBasePoints(double percentage) {
        return (int) (percentage * 100);
    }
}
