package com.olafparfienczyk.mortgageplan.controller;

import com.olafparfienczyk.mortgageplan.entity.dto.NewCustomerDTO;
import com.olafparfienczyk.mortgageplan.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class CustomerController {

    private static final String NEW_CUSTOMER_ATTRIBUTE = "newCustomer";
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/")
    public String getHome(Model model, @AuthenticationPrincipal OidcUser principal) {
        if (principal != null) {
            model.addAttribute("userName", principal.getClaims().get("name"));
            model.addAttribute(NEW_CUSTOMER_ATTRIBUTE, new NewCustomerDTO());
        }

        model.addAttribute(
                "customers",
                principal == null
                        ? customerService.getDemoCustomers()
                        : customerService.getCustomers(principal));
        return "index";
    }

    @PostMapping("/add-customer")
    public String addCustomer(@Valid @ModelAttribute(NEW_CUSTOMER_ATTRIBUTE) NewCustomerDTO customerDTO,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              @AuthenticationPrincipal OidcUser principal) {
        if (bindingResult.hasErrors()) {
            Set<String> fieldErrors =
                    bindingResult
                            .getFieldErrors()
                            .stream()
                            .map(FieldError::getField)
                            .collect(Collectors.toUnmodifiableSet());
            redirectAttributes.addFlashAttribute("badFields", fieldErrors);
        } else {
            customerService.addCustomer(customerDTO, principal);
        }
        return "redirect:/";
    }

    @PostMapping("/delete-customer")
    public String deleteCustomer(@RequestParam("id") UUID customerId,
                                 @AuthenticationPrincipal OidcUser principal) {
        customerService.deleteCustomer(customerId, principal);
        return "redirect:/";
    }
}