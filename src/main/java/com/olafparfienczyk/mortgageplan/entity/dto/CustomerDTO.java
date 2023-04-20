package com.olafparfienczyk.mortgageplan.entity.dto;

import java.util.Objects;
import java.util.UUID;

/**
 * Customer representation used to communicate customer data to end user.
 */
public class CustomerDTO {

    private UUID id;
    private String name;
    private double totalLoan;
    private double interestPercent;
    private int loanDurationYears;
    private double monthlyPayment;

    public UUID getId() {
        return id;
    }

    public CustomerDTO setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public CustomerDTO setName(String name) {
        this.name = name;
        return this;
    }

    public double getTotalLoan() {
        return totalLoan;
    }

    public CustomerDTO setTotalLoan(double totalLoan) {
        this.totalLoan = totalLoan;
        return this;
    }

    public double getInterestPercent() {
        return interestPercent;
    }

    public CustomerDTO setInterestPercent(double interestPercent) {
        this.interestPercent = interestPercent;
        return this;
    }

    public int getLoanDurationYears() {
        return loanDurationYears;
    }

    public CustomerDTO setLoanDurationYears(int loanDurationYears) {
        this.loanDurationYears = loanDurationYears;
        return this;
    }

    public double getMonthlyPayment() {
        return monthlyPayment;
    }

    public CustomerDTO setMonthlyPayment(double monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerDTO that = (CustomerDTO) o;
        return Double.compare(that.totalLoan, totalLoan) == 0 && Double.compare(that.interestPercent, interestPercent) == 0 && loanDurationYears == that.loanDurationYears && Double.compare(that.monthlyPayment, monthlyPayment) == 0 && Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, totalLoan, interestPercent, loanDurationYears, monthlyPayment);
    }

    @Override
    public String toString() {
        return "CustomerDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", totalLoan=" + totalLoan +
                ", interestPercent=" + interestPercent +
                ", years=" + loanDurationYears +
                ", monthlyPayment=" + monthlyPayment +
                '}';
    }
}