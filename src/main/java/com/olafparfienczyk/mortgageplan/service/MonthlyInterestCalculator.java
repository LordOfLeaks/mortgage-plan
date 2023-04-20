package com.olafparfienczyk.mortgageplan.service;

public interface MonthlyInterestCalculator {

    /**
     * Calculates fixed monthly payment needed to repay the loan with given parameters.
     *
     * @param totalLoanInCents           Total loan to repay in cents
     * @param yearlyInterestInBasePoints Yearly interest rate expressed in base points
     * @param loanDurationInYears        Loan duration expressed in years
     * @return Fixed monthly payment expressed in cents
     */
    int calculateFixedMonthlyPayment(
            long totalLoanInCents,
            int yearlyInterestInBasePoints,
            int loanDurationInYears);

}