package com.olafparfienczyk.mortgageplan.service;

import com.olafparfienczyk.mortgageplan.entity.Customer;
import com.olafparfienczyk.mortgageplan.entity.User;
import com.olafparfienczyk.mortgageplan.entity.dto.CustomerDTO;
import com.olafparfienczyk.mortgageplan.entity.dto.NewCustomerDTO;

public interface CustomerConverter {

    /**
     * Creates new customer entity from {@link NewCustomerDTO} DTO without owning user.
     *
     * @param newCustomerDTO Source DTO
     * @return Customer entity
     */
    Customer newCustomerDTOToOwnerlessCustomer(NewCustomerDTO newCustomerDTO);

    /**
     * Creates new customer entity from {@link NewCustomerDTO} DTO with owning user.
     *
     * @param owner          User entity to which customer will be bound
     * @param newCustomerDTO Source DTO
     * @return Customer entity
     */
    Customer newCustomerDTOToCustomerWithOwner(User owner, NewCustomerDTO newCustomerDTO);

    /**
     * Creates {@link CustomerDTO} from {@link Customer} entity.
     *
     * @param customer Source entity
     * @return Customer DTO
     */
    CustomerDTO customerToDTO(Customer customer);

}