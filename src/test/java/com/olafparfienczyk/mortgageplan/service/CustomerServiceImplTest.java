package com.olafparfienczyk.mortgageplan.service;

import com.olafparfienczyk.mortgageplan.entity.Customer;
import com.olafparfienczyk.mortgageplan.entity.User;
import com.olafparfienczyk.mortgageplan.entity.dto.CustomerDTO;
import com.olafparfienczyk.mortgageplan.entity.dto.NewCustomerDTO;
import com.olafparfienczyk.mortgageplan.repository.CustomerRepository;
import com.olafparfienczyk.mortgageplan.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    private static final String OAUTH_SUB = "oauth|abcdef";

    @Mock
    private DemoCustomersProvider demoCustomersProvider;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CustomerConverter customerConverter;
    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void testAddCustomerThrowsWithoutAuthentication() {
        NewCustomerDTO newCustomerDTO = new NewCustomerDTO()
                .setName("John")
                .setTotalLoan(10000)
                .setInterestPercent(6)
                .setLoanDurationYears(2);
        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> customerService.addCustomer(newCustomerDTO, null));
        assertEquals(BusinessException.unauthenticated().getMessage(), exception.getMessage());

        verify(userRepository, never()).findByAuthId(anyString());
        verify(customerRepository, never()).save(any());
    }

    @Test
    void testAddCustomerThrowsWhenAuthenticatedButUserNotExists() {
        OidcUser principal = mockAuthentication();

        NewCustomerDTO newCustomerDTO = new NewCustomerDTO()
                .setName("John")
                .setTotalLoan(10000)
                .setInterestPercent(6)
                .setLoanDurationYears(2);

        when(userRepository.findByAuthId(OAUTH_SUB)).thenReturn(Optional.empty());

        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> customerService.addCustomer(newCustomerDTO, principal));
        assertEquals(BusinessException.userNotExists().getMessage(), exception.getMessage());

        verify(customerRepository, never()).save(any());
    }

    @Test
    void testAddCustomerWhenAuthenticatedAndUserExists() {
        OidcUser principal = mockAuthentication();

        NewCustomerDTO newCustomerDTO = new NewCustomerDTO()
                .setName("John")
                .setTotalLoan(10000)
                .setInterestPercent(6)
                .setLoanDurationYears(2);
        Customer customer = new Customer();
        customer.setName("John");

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setAuthId(OAUTH_SUB);
        when(userRepository.findByAuthId(OAUTH_SUB))
                .thenReturn(Optional.of(user));
        when(customerConverter.newCustomerDTOToCustomerWithOwner(user, newCustomerDTO))
                .thenReturn(customer);

        customerService.addCustomer(newCustomerDTO, principal);

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerCaptor.capture());

        Customer addedCustomer = customerCaptor.getValue();
        assertEquals(customer, addedCustomer);
    }

    @Test
    void testDeleteCustomerThrowsWithoutAuthentication() {
        UUID customerToDelete = UUID.randomUUID();

        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> customerService.deleteCustomer(customerToDelete, null));
        assertEquals(BusinessException.unauthenticated().getMessage(), exception.getMessage());

        verify(userRepository, never()).findByAuthId(anyString());
        verify(customerRepository, never()).deleteByIdAndUser(any(), any());
    }

    @Test
    void testDeleteCustomerThrowsWhenAuthenticatedButUserNotExists() {
        OidcUser principal = mockAuthentication();

        UUID customerToDelete = UUID.randomUUID();
        when(userRepository.findByAuthId(OAUTH_SUB)).thenReturn(Optional.empty());

        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> customerService.deleteCustomer(customerToDelete, principal));
        assertEquals(BusinessException.userNotExists().getMessage(), exception.getMessage());

        verify(customerRepository, never()).deleteByIdAndUser(any(), any());
    }

    @Test
    void testDeleteCustomerWhenAuthenticatedAndUserExists() {
        OidcUser principal = mockAuthentication();

        UUID customerToDelete = UUID.randomUUID();

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setAuthId(OAUTH_SUB);
        when(userRepository.findByAuthId(OAUTH_SUB)).thenReturn(Optional.of(user));

        customerService.deleteCustomer(customerToDelete, principal);

        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(customerRepository).deleteByIdAndUser(idCaptor.capture(), userCaptor.capture());

        UUID deletedId = idCaptor.getValue();
        User deletedForOwner = userCaptor.getValue();

        assertEquals(customerToDelete, deletedId);
        assertEquals(user, deletedForOwner);
    }

    @Test
    void testGetDemoCustomersReturnsDemoData() {
        NewCustomerDTO newCustomerDTO1 =
                new NewCustomerDTO()
                        .setName("demo1")
                        .setTotalLoan(15000.55)
                        .setInterestPercent(1.22)
                        .setLoanDurationYears(3);
        NewCustomerDTO newCustomerDTO2 =
                new NewCustomerDTO()
                        .setName("demo2")
                        .setTotalLoan(12000.99)
                        .setInterestPercent(4)
                        .setLoanDurationYears(2);

        Customer customer1 = new Customer();
        customer1.setId(UUID.randomUUID());
        customer1.setName("demo1");
        customer1.setTotalLoanCents(1500055);
        customer1.setInterestBasePoints(122);
        customer1.setYears(3);

        Customer customer2 = new Customer();
        customer2.setId(UUID.randomUUID());
        customer2.setName("demo2");
        customer2.setTotalLoanCents(1200099);
        customer2.setInterestBasePoints(4);
        customer2.setYears(2);

        CustomerDTO customerDTO1 =
                new CustomerDTO()
                        .setName("demo1");
        CustomerDTO customerDTO2 =
                new CustomerDTO()
                        .setName("demo2");

        when(demoCustomersProvider.getDemoCustomers())
                .thenReturn(
                        List.of(
                                newCustomerDTO1,
                                newCustomerDTO2));
        when(customerConverter.newCustomerDTOToOwnerlessCustomer(newCustomerDTO1))
                .thenReturn(customer1);
        when(customerConverter.newCustomerDTOToOwnerlessCustomer(newCustomerDTO2))
                .thenReturn(customer2);
        when(customerConverter.customerToDTO(customer1))
                .thenReturn(customerDTO1);
        when(customerConverter.customerToDTO(customer2))
                .thenReturn(customerDTO2);

        List<CustomerDTO> customers = customerService.getDemoCustomers();

        verify(userRepository, never()).findByAuthId(anyString());
        verify(customerRepository, never()).findAllByUser_Id(any());

        assertEquals(customerDTO1, customers.get(0));
        assertEquals(customerDTO2, customers.get(1));
    }

    @Test
    void testGetCustomersWithoutAuthenticationThrows() {
        BusinessException thrown = assertThrows(BusinessException.class,
                () -> customerService.getCustomers(null));

        verify(customerRepository, never()).findAllByUser_Id(any());
        verify(userRepository, never()).findByAuthId(anyString());

        assertEquals("Unauthenticated", thrown.getMessage());
    }

    @Test
    void testGetCustomersWithAuthenticationAndUserNotCreatedCreatesUserAndReturnsUserScopedData() {
        OidcUser principal = mockAuthentication();
        UUID userId = UUID.randomUUID();
        NewCustomerDTO newCustomerDTO1 =
                new NewCustomerDTO()
                        .setName("demo1")
                        .setTotalLoan(15000.55)
                        .setInterestPercent(1.22)
                        .setLoanDurationYears(3);
        NewCustomerDTO newCustomerDTO2 =
                new NewCustomerDTO()
                        .setName("demo2")
                        .setTotalLoan(12000.99)
                        .setInterestPercent(4)
                        .setLoanDurationYears(2);

        Customer customer1 = new Customer();
        customer1.setId(UUID.randomUUID());
        customer1.setName("demo1");
        customer1.setTotalLoanCents(1500055);
        customer1.setInterestBasePoints(122);
        customer1.setYears(3);

        Customer customer2 = new Customer();
        customer2.setId(UUID.randomUUID());
        customer2.setName("demo2");
        customer2.setTotalLoanCents(1200099);
        customer2.setInterestBasePoints(4);
        customer2.setYears(2);

        CustomerDTO customerDTO1 =
                new CustomerDTO()
                        .setName("demo1");
        CustomerDTO customerDTO2 =
                new CustomerDTO()
                        .setName("demo2");

        final ArgumentMatcher<User> userMatcher = user -> user != null && user.getAuthId().equals(OAUTH_SUB);
        when(demoCustomersProvider.getDemoCustomers())
                .thenReturn(List.of(newCustomerDTO1, newCustomerDTO2));
        when(userRepository.findByAuthId(OAUTH_SUB))
                .thenReturn(Optional.empty());
        when(userRepository.save(argThat(userMatcher)))
                .thenAnswer(inv -> {
                    User user = inv.getArgument(0, User.class);
                    user.setId(userId);
                    return user;
                });
        when(customerRepository.findAllByUser_Id(userId))
                .thenReturn(List.of(customer1, customer2));
        when(customerConverter.newCustomerDTOToCustomerWithOwner(argThat(userMatcher), eq(newCustomerDTO1)))
                .thenReturn(customer1);
        when(customerConverter.newCustomerDTOToCustomerWithOwner(argThat(userMatcher), eq(newCustomerDTO2)))
                .thenReturn(customer2);
        when(customerConverter.customerToDTO(customer1))
                .thenReturn(customerDTO1);
        when(customerConverter.customerToDTO(customer2))
                .thenReturn(customerDTO2);

        List<CustomerDTO> returnedCustomers = customerService.getCustomers(principal);

        verify(customerRepository).saveAll(
                argThat(iterable -> {
                    final Iterator<Customer> iterator = iterable.iterator();
                    return iterator.next().getName().equals("demo1")
                            && iterator.next().getName().equals("demo2");
                }));

        assertEquals(customerDTO1, returnedCustomers.get(0));
        assertEquals(customerDTO2, returnedCustomers.get(1));
    }

    @Test
    void testGetCustomersWithAuthenticationAndUserCreatedReturnsUserScopedData() {
        OidcUser principal = mockAuthentication();

        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setAuthId(OAUTH_SUB);

        Customer customer1 = new Customer();
        customer1.setId(UUID.randomUUID());
        customer1.setName("demo1");
        customer1.setTotalLoanCents(1500055);
        customer1.setInterestBasePoints(122);
        customer1.setYears(3);

        Customer customer2 = new Customer();
        customer2.setId(UUID.randomUUID());
        customer2.setName("demo2");
        customer2.setTotalLoanCents(1200099);
        customer2.setInterestBasePoints(4);
        customer2.setYears(2);

        CustomerDTO customerDTO1 =
                new CustomerDTO()
                        .setName("demo1");
        CustomerDTO customerDTO2 =
                new CustomerDTO()
                        .setName("demo2");

        when(userRepository.findByAuthId(OAUTH_SUB))
                .thenReturn(Optional.of(user));
        when(customerRepository.findAllByUser_Id(userId))
                .thenReturn(List.of(customer1, customer2));
        when(customerConverter.customerToDTO(customer1))
                .thenReturn(customerDTO1);
        when(customerConverter.customerToDTO(customer2))
                .thenReturn(customerDTO2);

        List<CustomerDTO> returnedCustomers = customerService.getCustomers(principal);

        verify(userRepository, never()).save(any());
        verify(customerRepository, never()).saveAll(any());

        assertEquals(customerDTO1, returnedCustomers.get(0));
        assertEquals(customerDTO2, returnedCustomers.get(1));
    }

    private OidcUser mockAuthentication() {
        OidcUser oidcUser = mock(OidcUser.class);
        when(oidcUser.getName()).thenReturn(OAUTH_SUB);
        return oidcUser;
    }
}