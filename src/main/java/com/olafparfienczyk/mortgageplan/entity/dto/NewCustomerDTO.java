package com.olafparfienczyk.mortgageplan.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.util.Objects;

/**
 * Representation of new customer, received from client when adding a customer.
 */
public class NewCustomerDTO {

    @JsonProperty("Customer")
    @NotBlank
    @Size(min = 1, max = 64)
    private String name;

    @JsonProperty("Total loan")
    @DecimalMin(value = "0", inclusive = false)
    @DecimalMax(value = "1000000000")
    private double totalLoan;

    @JsonProperty("Interest")
    @DecimalMin(value = "0", inclusive = false)
    @DecimalMax(value = "1000")
    private double interestPercent;

    @JsonProperty("Years")
    @Min(value = 1)
    @Max(value = 50)
    private int loanDurationYears;

    public NewCustomerDTO() {
    }

    public NewCustomerDTO(NewCustomerDTO cloneSource) {
        this.name = cloneSource.getName();
        this.totalLoan = cloneSource.getTotalLoan();
        this.interestPercent = cloneSource.getInterestPercent();
        this.loanDurationYears = cloneSource.getLoanDurationYears();
    }

    public String getName() {
        return name;
    }

    public NewCustomerDTO setName(String name) {
        this.name = name;
        return this;
    }

    public double getTotalLoan() {
        return totalLoan;
    }

    public NewCustomerDTO setTotalLoan(double totalLoan) {
        this.totalLoan = totalLoan;
        return this;
    }

    public double getInterestPercent() {
        return interestPercent;
    }

    public NewCustomerDTO setInterestPercent(double interestPercent) {
        this.interestPercent = interestPercent;
        return this;
    }

    public int getLoanDurationYears() {
        return loanDurationYears;
    }

    public NewCustomerDTO setLoanDurationYears(int loanDurationYears) {
        this.loanDurationYears = loanDurationYears;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewCustomerDTO that = (NewCustomerDTO) o;
        return Objects.equals(name, that.name)
                && Objects.equals(totalLoan, that.totalLoan)
                && Objects.equals(interestPercent, that.interestPercent)
                && Objects.equals(loanDurationYears, that.loanDurationYears);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, totalLoan, interestPercent, loanDurationYears);
    }

    @Override
    public String toString() {
        return "NewCustomerDTO{" +
                "name='" + name + '\'' +
                ", totalLoan='" + totalLoan + '\'' +
                ", interestPercent='" + interestPercent + '\'' +
                ", years=" + loanDurationYears +
                '}';
    }
}