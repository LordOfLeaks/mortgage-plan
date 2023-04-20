package com.olafparfienczyk.mortgageplan.service;

import org.springframework.stereotype.Component;

@Component
public class MonthlyInterestCalculatorImpl implements MonthlyInterestCalculator {

    private final UnitConverter unitConverter;

    public MonthlyInterestCalculatorImpl(UnitConverter unitConverter) {
        this.unitConverter = unitConverter;
    }

    @Override
    public int calculateFixedMonthlyPayment(long totalLoanInCents,
                                            int yearlyInterestInBasePoints,
                                            int loanDurationInYears) {
        if (totalLoanInCents <= 0) {
            throw new IllegalArgumentException("Total loan must be positive");
        }
        if (yearlyInterestInBasePoints <= 0) {
            throw new IllegalArgumentException("Yearly interest must be positive");
        }
        if (loanDurationInYears <= 0) {
            throw new IllegalArgumentException("Loan duration must be positive");
        }

        final double monthlyInterestRatio =
                unitConverter.basePointsToRatio(yearlyInterestInBasePoints)
                        / unitConverter.getMonthsInYear();
        final double totalCompoundedInterestRatio =
                pow(
                        1 + monthlyInterestRatio,
                        loanDurationInYears * unitConverter.getMonthsInYear());
        final double finalMonthlyRatio =
                (monthlyInterestRatio * totalCompoundedInterestRatio)
                        / (totalCompoundedInterestRatio - 1);
        return roundHalfUp(totalLoanInCents * finalMonthlyRatio);
    }

    private double pow(double num, int exponent) {
        double result = 1;
        for (int i = 0; i < exponent; i++) {
            result *= num;
        }
        return result;
    }

    private int roundHalfUp(double value) {
        int floor = (int) value;
        if ((value - floor) >= 0.5) {
            return floor + 1;
        } else {
            return floor;
        }
    }
}
