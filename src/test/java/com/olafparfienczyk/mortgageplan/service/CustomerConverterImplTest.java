package com.olafparfienczyk.mortgageplan.service;

import com.olafparfienczyk.mortgageplan.entity.Customer;
import com.olafparfienczyk.mortgageplan.entity.User;
import com.olafparfienczyk.mortgageplan.entity.dto.CustomerDTO;
import com.olafparfienczyk.mortgageplan.entity.dto.NewCustomerDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerConverterImplTest {

    @Mock
    private UnitConverter unitConverter;

    @Mock
    private MonthlyInterestCalculator monthlyInterestCalculator;

    @InjectMocks
    private CustomerConverterImpl customerConverter;

    @Test
    void testConvertsNewCustomerToOwnerlessCustomer() {
        NewCustomerDTO newCustomerDTO =
                new NewCustomerDTO()
                        .setName("John")
                        .setTotalLoan(1000.55)
                        .setInterestPercent(2.55)
                        .setLoanDurationYears(4);

        when(unitConverter.percentageToBasePoints(2.55))
                .thenReturn(255);
        when(unitConverter.fractionToCents(1000.55))
                .thenReturn(100055L);

        Customer convertedCustomer = customerConverter.newCustomerDTOToOwnerlessCustomer(newCustomerDTO);
        assertNull(convertedCustomer.getId());
        assertNull(convertedCustomer.getUser());
        assertEquals(100055, convertedCustomer.getTotalLoanCents());
        assertEquals(255, convertedCustomer.getInterestBasePoints());
        assertEquals(4, convertedCustomer.getYears());
    }

    @Test
    void testConvertsNewCustomerToCustomerWithOwner() {
        User owner = new User();
        owner.setId(UUID.randomUUID());
        owner.setAuthId("abc");

        NewCustomerDTO newCustomerDTO =
                new NewCustomerDTO()
                        .setName("John")
                        .setTotalLoan(1000.55)
                        .setInterestPercent(2.55)
                        .setLoanDurationYears(4);

        when(unitConverter.percentageToBasePoints(2.55))
                .thenReturn(255);
        when(unitConverter.fractionToCents(1000.55))
                .thenReturn(100055L);

        Customer convertedCustomer = customerConverter.newCustomerDTOToCustomerWithOwner(owner, newCustomerDTO);
        assertNull(convertedCustomer.getId());
        assertEquals(owner, convertedCustomer.getUser());
        assertEquals(100055, convertedCustomer.getTotalLoanCents());
        assertEquals(255, convertedCustomer.getInterestBasePoints());
        assertEquals(4, convertedCustomer.getYears());
    }

    @Test
    void testConvertsCustomerToDTO() {
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setUser(null);
        customer.setName("John");
        customer.setTotalLoanCents(100055);
        customer.setInterestBasePoints(522);
        customer.setYears(3);

        when(monthlyInterestCalculator.calculateFixedMonthlyPayment(100055, 522, 3))
                .thenReturn(39955);
        when(unitConverter.centsToFraction(39955))
                .thenReturn(399.55);
        when(unitConverter.centsToFraction(100055))
                .thenReturn(1000.55);
        when(unitConverter.basePointsToPercentage(522))
                .thenReturn(5.22);

        CustomerDTO convertedDTO = customerConverter.customerToDTO(customer);
        assertEquals("John", convertedDTO.getName());
        assertEquals(1000.55, convertedDTO.getTotalLoan());
        assertEquals(5.22, convertedDTO.getInterestPercent());
        assertEquals(399.55, convertedDTO.getMonthlyPayment());
        assertEquals(3, convertedDTO.getLoanDurationYears());
    }

}