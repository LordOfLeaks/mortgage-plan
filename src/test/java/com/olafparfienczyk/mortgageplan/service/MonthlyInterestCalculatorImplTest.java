package com.olafparfienczyk.mortgageplan.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MonthlyInterestCalculatorImplTest {

    @Mock
    private UnitConverter unitConverter;

    @InjectMocks
    private MonthlyInterestCalculatorImpl monthlyInterestCalculator;

    @Test
    void testThrowsIfTotalLoanIsNotPositive() {
        assertThrows(IllegalArgumentException.class, () ->
                monthlyInterestCalculator
                        .calculateFixedMonthlyPayment(0, 100, 10));
        assertThrows(IllegalArgumentException.class, () ->
                monthlyInterestCalculator
                        .calculateFixedMonthlyPayment(-1, 100, 10));
    }

    @Test
    void testThrowsIfYearlyInterestIsNotPositive() {
        assertThrows(IllegalArgumentException.class, () ->
                monthlyInterestCalculator
                        .calculateFixedMonthlyPayment(10000, 0, 10));
        assertThrows(IllegalArgumentException.class, () ->
                monthlyInterestCalculator
                        .calculateFixedMonthlyPayment(10000, -1, 10));
    }

    @Test
    void testThrowsIfLoanDurationIsNotPositive() {
        assertThrows(IllegalArgumentException.class, () ->
                monthlyInterestCalculator
                        .calculateFixedMonthlyPayment(10000, 100, 0));
        assertThrows(IllegalArgumentException.class, () ->
                monthlyInterestCalculator
                        .calculateFixedMonthlyPayment(10000, 100, -1));
    }

    @MethodSource("monthlyInterestInput")
    @ParameterizedTest
    void testMonthlyInterestIsCalculatedCorrectly(final int totalLoanCents,
                                                  final int interestRateBasePoints,
                                                  final double interestRateRatio,
                                                  final int loanDurationInYears,
                                                  final int expectedMonthlyPayment) {
        when(unitConverter.getMonthsInYear())
                .thenReturn(12);
        when(unitConverter.basePointsToRatio(interestRateBasePoints))
                .thenReturn(interestRateRatio);

        final int monthlyPayment =
                monthlyInterestCalculator
                        .calculateFixedMonthlyPayment(
                                totalLoanCents,
                                interestRateBasePoints,
                                loanDurationInYears);

        assertEquals(expectedMonthlyPayment, monthlyPayment);
    }

    static Stream<Arguments> monthlyInterestInput() {
        return Stream.of(
                Arguments.of(
                        400_000_00,
                        6_94,
                        0.0694,
                        30,
                        2_645_11
                ),
                Arguments.of(
                        1_000_000_00,
                        10_11,
                        0.1011,
                        5,
                        21_301_21
                ),
                Arguments.of(
                        1_000_00,
                        1_00,
                        0.01,
                        1,
                        83_79
                )
        );
    }

}