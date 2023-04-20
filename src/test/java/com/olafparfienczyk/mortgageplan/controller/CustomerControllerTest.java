package com.olafparfienczyk.mortgageplan.controller;

import com.olafparfienczyk.mortgageplan.entity.dto.CustomerDTO;
import com.olafparfienczyk.mortgageplan.entity.dto.NewCustomerDTO;
import com.olafparfienczyk.mortgageplan.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    private static final String USER_NAME = "user@name";

    @Mock
    private Model model;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController controller;

    @Test
    void testGetHomeUnauthenticatedReturnsDemoData() {
        List<CustomerDTO> demoCustomers =
                List.of(
                        new CustomerDTO()
                                .setName("test"));
        when(customerService.getDemoCustomers())
                .thenReturn(demoCustomers);

        controller.getHome(model, null);

        verify(model).addAttribute("customers", demoCustomers);
    }

    @Test
    void testGetHomeAuthenticatedReturnsUserScopedData() {
        OidcUser principal = mock(OidcUser.class);
        when(principal.getClaims())
                .thenReturn(Map.of("name", USER_NAME));

        List<CustomerDTO> customers =
                List.of(
                        new CustomerDTO()
                                .setName("test"));
        when(customerService.getCustomers(principal))
                .thenReturn(customers);

        controller.getHome(model, principal);

        verify(model).addAttribute("userName", USER_NAME);
        verify(model).addAttribute("customers", customers);
        verify(model).addAttribute(eq("newCustomer"), any(NewCustomerDTO.class));
    }

    @Test
    void testAddCustomerAddsCustomerIfNoValidationErrors() {
        NewCustomerDTO newCustomerDTO =
                new NewCustomerDTO()
                        .setName("John")
                        .setTotalLoan(10000)
                        .setInterestPercent(2)
                        .setLoanDurationYears(10);
        BindingResult bindingResult = mock(BindingResult.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        OidcUser principal = mock(OidcUser.class);
        when(bindingResult.hasErrors())
                .thenReturn(false);

        controller.addCustomer(
                newCustomerDTO,
                bindingResult,
                redirectAttributes,
                principal);

        verify(customerService).addCustomer(newCustomerDTO, principal);
    }

    @Test
    void testAddCustomerDoesntAddCustomerAndSaveErrorsIfValidationErrors() {
        NewCustomerDTO newCustomerDTO =
                new NewCustomerDTO()
                        .setName("John")
                        .setTotalLoan(10000)
                        .setInterestPercent(2)
                        .setLoanDurationYears(10);
        BindingResult bindingResult = mock(BindingResult.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        OidcUser principal = mock(OidcUser.class);
        when(bindingResult.hasErrors())
                .thenReturn(true);
        when(bindingResult.getFieldErrors())
                .thenReturn(
                        List.of(
                                new FieldError("obj1", "field1", "msg1"),
                                new FieldError("obj2", "field2", "msg2")));

        controller.addCustomer(
                newCustomerDTO,
                bindingResult,
                redirectAttributes,
                principal);

        verify(redirectAttributes)
                .addFlashAttribute(
                        "badFields",
                        Set.of("field1", "field2"));
        verify(customerService, never())
                .addCustomer(any(), any());
    }

    @Test
    void testDeleteCustomerDeletesCustomer() {
        UUID customerId = UUID.randomUUID();
        OidcUser principal = mock(OidcUser.class);

        controller.deleteCustomer(customerId, principal);

        verify(customerService).deleteCustomer(customerId, principal);
    }
}