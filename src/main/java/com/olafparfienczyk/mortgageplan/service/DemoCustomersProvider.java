package com.olafparfienczyk.mortgageplan.service;

import com.olafparfienczyk.mortgageplan.entity.dto.NewCustomerDTO;

import java.util.List;

public interface DemoCustomersProvider {

    /**
     * Provides demonstrative customers.
     *
     * @return Demonstrative customers
     */
    List<NewCustomerDTO> getDemoCustomers();

}