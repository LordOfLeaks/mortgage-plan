package com.olafparfienczyk.mortgageplan.service;

import com.olafparfienczyk.mortgageplan.entity.dto.NewCustomerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FromCsvFileDemoCustomersProviderTest {

    private FromCsvFileDemoCustomersProvider customersProvider;

    @BeforeEach
    void setup() throws IOException {
        customersProvider = new FromCsvFileDemoCustomersProvider("classpath:test-prospects.txt");
    }

    @Test
    void testLoadedProperly() {
        List<NewCustomerDTO> demoCustomers = customersProvider.getDemoCustomers();
        assertEquals(2, demoCustomers.size());
        assertEquals(demoCustomers.get(0),
                new NewCustomerDTO()
                .setName("Juhé")
                .setTotalLoan(1000)
                .setInterestPercent(5)
                .setLoanDurationYears(2));
        assertEquals(demoCustomers.get(1),
                new NewCustomerDTO()
                        .setName("Clarencé,Andersson")
                        .setTotalLoan(200000000.66)
                        .setInterestPercent(6.22)
                        .setLoanDurationYears(4));
    }

    @Test
    void testImmutable() {
        List<NewCustomerDTO> modifiedCustomers = customersProvider.getDemoCustomers();
        modifiedCustomers.get(0).setName("newname");
        List<NewCustomerDTO> newCustomers = customersProvider.getDemoCustomers();

        assertNotEquals(newCustomers.get(0), modifiedCustomers.get(0));
    }
}