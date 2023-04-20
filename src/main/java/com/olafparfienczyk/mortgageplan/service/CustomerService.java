package com.olafparfienczyk.mortgageplan.service;

import com.olafparfienczyk.mortgageplan.entity.dto.CustomerDTO;
import com.olafparfienczyk.mortgageplan.entity.dto.NewCustomerDTO;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.List;
import java.util.UUID;

public interface CustomerService {

    /**
     * Adds new customer for given authentication.
     *
     * @param customerDTO Source DTO
     * @param principal   OIDC authentication principal
     */
    void addCustomer(NewCustomerDTO customerDTO, OidcUser principal);

    /**
     * Deletes customer for given authentication.
     *
     * @param customerId Customer to delete
     * @param principal  OIDC authentication principal
     */
    void deleteCustomer(UUID customerId, OidcUser principal);

    /**
     * Retrieves customers for given authentication.
     *
     * @param principal OIDC authentication principal
     * @return Customers for given authentication
     */
    List<CustomerDTO> getCustomers(OidcUser principal);

    /**
     * Retrieves customer for unauthenticated audience.
     *
     * @return Customers for unauthenticated audience
     */
    List<CustomerDTO> getDemoCustomers();

}