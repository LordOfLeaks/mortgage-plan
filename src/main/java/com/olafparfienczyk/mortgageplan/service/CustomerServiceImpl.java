package com.olafparfienczyk.mortgageplan.service;

import com.olafparfienczyk.mortgageplan.entity.Customer;
import com.olafparfienczyk.mortgageplan.entity.User;
import com.olafparfienczyk.mortgageplan.entity.dto.CustomerDTO;
import com.olafparfienczyk.mortgageplan.entity.dto.NewCustomerDTO;
import com.olafparfienczyk.mortgageplan.repository.CustomerRepository;
import com.olafparfienczyk.mortgageplan.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final DemoCustomersProvider demoCustomersProvider;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final CustomerConverter customerConverter;

    public CustomerServiceImpl(DemoCustomersProvider demoCustomersProvider,
                               CustomerRepository customerRepository,
                               UserRepository userRepository,
                               CustomerConverter customerConverter) {
        this.demoCustomersProvider = demoCustomersProvider;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.customerConverter = customerConverter;
    }

    @Transactional
    @Override
    public void addCustomer(NewCustomerDTO customerDTO, OidcUser principal) {
        if (principal == null) {
            throw BusinessException.unauthenticated();
        }
        User user =
                userRepository
                        .findByAuthId(principal.getName())
                        .orElseThrow(BusinessException::userNotExists);

        Customer customer =
                customerConverter
                        .newCustomerDTOToCustomerWithOwner(user, customerDTO);
        customerRepository.save(customer);
    }

    @Transactional
    @Override
    public void deleteCustomer(UUID customerId, OidcUser principal) {
        if (principal == null) {
            throw BusinessException.unauthenticated();
        }
        User user =
                userRepository
                        .findByAuthId(principal.getName())
                        .orElseThrow(BusinessException::userNotExists);

        customerRepository.deleteByIdAndUser(customerId, user);
    }

    @Override
    public List<CustomerDTO> getCustomers(OidcUser principal) {
        if (principal == null) {
            throw BusinessException.unauthenticated();
        }
        User user =
                userRepository
                        .findByAuthId(principal.getName())
                        .orElseGet(() -> createNewUser(principal.getName()));

        return customerRepository
                .findAllByUser_Id(user.getId())
                .stream()
                .map(customerConverter::customerToDTO)
                .toList();
    }

    @Override
    public List<CustomerDTO> getDemoCustomers() {
        return demoCustomersProvider
                .getDemoCustomers()
                .stream()
                .map(customerConverter::newCustomerDTOToOwnerlessCustomer)
                .map(customerConverter::customerToDTO)
                .toList();
    }

    @Transactional
    public User createNewUser(String authId) {
        User user = new User();
        user.setAuthId(authId);

        final User createdUser = userRepository.save(user);
        createDemoCustomers(createdUser);
        return createdUser;
    }

    private void createDemoCustomers(User owner) {
        customerRepository.saveAll(
                demoCustomersProvider
                        .getDemoCustomers()
                        .stream()
                        .map(newCustomerDTO ->
                                customerConverter
                                        .newCustomerDTOToCustomerWithOwner(
                                                owner,
                                                newCustomerDTO))
                        .toList());
    }
}
