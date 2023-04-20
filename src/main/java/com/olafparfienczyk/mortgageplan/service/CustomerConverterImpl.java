package com.olafparfienczyk.mortgageplan.service;

import com.olafparfienczyk.mortgageplan.entity.Customer;
import com.olafparfienczyk.mortgageplan.entity.User;
import com.olafparfienczyk.mortgageplan.entity.dto.CustomerDTO;
import com.olafparfienczyk.mortgageplan.entity.dto.NewCustomerDTO;
import org.springframework.stereotype.Component;

@Component
public class CustomerConverterImpl implements CustomerConverter {

    private final UnitConverter unitConverter;
    private final MonthlyInterestCalculator monthlyInterestCalculator;

    public CustomerConverterImpl(UnitConverter unitConverter,
                                 MonthlyInterestCalculator monthlyInterestCalculator) {
        this.unitConverter = unitConverter;
        this.monthlyInterestCalculator = monthlyInterestCalculator;
    }

    @Override
    public Customer newCustomerDTOToOwnerlessCustomer(NewCustomerDTO newCustomerDTO) {
        return dtoToCustomer(null, newCustomerDTO);
    }

    @Override
    public Customer newCustomerDTOToCustomerWithOwner(User owner, NewCustomerDTO newCustomerDTO) {
        return dtoToCustomer(owner, newCustomerDTO);
    }

    @Override
    public CustomerDTO customerToDTO(Customer customer) {
        return new CustomerDTO()
                .setId(customer.getId())
                .setName(customer.getName())
                .setTotalLoan(
                        unitConverter.centsToFraction(
                                customer.getTotalLoanCents()))
                .setInterestPercent(
                        unitConverter.basePointsToPercentage(
                                customer.getInterestBasePoints()))
                .setLoanDurationYears(customer.getYears())
                .setMonthlyPayment(
                        unitConverter.centsToFraction(
                                monthlyInterestCalculator.calculateFixedMonthlyPayment(
                                        customer.getTotalLoanCents(),
                                        customer.getInterestBasePoints(),
                                        customer.getYears())));
    }


    private Customer dtoToCustomer(User owner, NewCustomerDTO newCustomerDTO) {
        Customer customer = new Customer();
        customer.setUser(owner);
        customer.setName(newCustomerDTO.getName());
        customer.setTotalLoanCents(
                unitConverter
                        .fractionToCents(
                                newCustomerDTO.getTotalLoan()));
        customer.setInterestBasePoints(
                unitConverter
                        .percentageToBasePoints(
                                newCustomerDTO.getInterestPercent()));
        customer.setYears(newCustomerDTO.getLoanDurationYears());
        return customer;
    }
}