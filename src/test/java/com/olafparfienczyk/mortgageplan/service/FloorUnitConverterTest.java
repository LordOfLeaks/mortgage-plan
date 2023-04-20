package com.olafparfienczyk.mortgageplan.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FloorUnitConverterTest {

    private FloorUnitConverter floorUnitConverter;

    @BeforeEach
    void setUp() {
        floorUnitConverter = new FloorUnitConverter();
    }

    @Test
    void testThereAreTwelveMonths() {
        assertEquals(12, floorUnitConverter.getMonthsInYear());
    }

    @Test
    void testCentsToFraction() {
        assertEquals(10000000000000.55D, floorUnitConverter.centsToFraction(1000000000000055L));
        assertEquals(100D, floorUnitConverter.centsToFraction(10000));
        assertEquals(12.44D, floorUnitConverter.centsToFraction(1244));
        assertEquals(1D, floorUnitConverter.centsToFraction(100));
        assertEquals(0.01D, floorUnitConverter.centsToFraction(1));
    }

    @Test
    void testFractionToCents() {
        assertEquals(1000000000000000L, floorUnitConverter.fractionToCents(10000000000000D));
        assertEquals(10000, floorUnitConverter.fractionToCents(100D));
        assertEquals(1244, floorUnitConverter.fractionToCents(12.44));
        assertEquals(1244, floorUnitConverter.fractionToCents(12.4455));
        assertEquals(1244, floorUnitConverter.fractionToCents(12.4422));
        assertEquals(100, floorUnitConverter.fractionToCents(1D));
        assertEquals(1, floorUnitConverter.fractionToCents(0.01));
    }

    @Test
    void testBasePointsToRatio() {
        assertEquals(1D, floorUnitConverter.basePointsToRatio(10000));
        assertEquals(0.4422D, floorUnitConverter.basePointsToRatio(4422));
        assertEquals(0.01D, floorUnitConverter.basePointsToRatio(100));
        assertEquals(0.0001D, floorUnitConverter.basePointsToRatio(1));
    }

    @Test
    void testBasePointsToPercentage() {
        assertEquals(100D, floorUnitConverter.basePointsToPercentage(10000));
        assertEquals(12.44D, floorUnitConverter.basePointsToPercentage(1244));
        assertEquals(1D, floorUnitConverter.basePointsToPercentage(100));
        assertEquals(0.01D, floorUnitConverter.basePointsToPercentage(1));
    }

    @Test
    void testPercentageToBasePoints() {
        assertEquals(10000, floorUnitConverter.percentageToBasePoints(100D));
        assertEquals(1244, floorUnitConverter.percentageToBasePoints(12.44));
        assertEquals(1244, floorUnitConverter.percentageToBasePoints(12.4455));
        assertEquals(1244, floorUnitConverter.percentageToBasePoints(12.4422));
        assertEquals(100, floorUnitConverter.percentageToBasePoints(1D));
        assertEquals(1, floorUnitConverter.percentageToBasePoints(0.01));
    }
}