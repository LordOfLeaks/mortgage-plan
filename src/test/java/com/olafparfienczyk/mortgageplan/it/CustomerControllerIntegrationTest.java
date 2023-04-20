package com.olafparfienczyk.mortgageplan.it;

import com.olafparfienczyk.mortgageplan.entity.dto.CustomerDTO;
import com.olafparfienczyk.mortgageplan.entity.dto.NewCustomerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .alwaysDo(print())
                .apply(springSecurity())
                .build();
    }

    @Test
    void testGetHomeReturnsAllowsUnauthenticated() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("customers"));
    }

    @Test
    void testGetHomeReturnsForAuthenticated() throws Exception {
        mockMvc.perform(get("/")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("name", "foo_user"))))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("customers"))
                .andExpect(model().attribute("userName", "foo_user"))
                .andExpect(model().attributeExists("newCustomer"));
    }

    @Test
    void testAddCustomerRequiresCsrf() throws Exception {
        mockMvc.perform(post("/add-customer")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("newCustomer", new NewCustomerDTO()
                                .setName("John")
                                .setTotalLoan(100000)
                                .setInterestPercent(2)
                                .setLoanDurationYears(5)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAddCustomerWithoutAuthRedirectsToLogin() throws Exception {
        mockMvc.perform(post("/add-customer")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("newCustomer", new NewCustomerDTO()
                                .setName("John")
                                .setTotalLoan(100000)
                                .setInterestPercent(2)
                                .setLoanDurationYears(5)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/oauth2/authorization/test"));
    }

    @Test
    void testDeleteCustomerRequiresCsrf() throws Exception {
        mockMvc.perform(post("/delete-customer")
                        .queryParam("id", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteCustomerWithoutAuthRedirectsToLogin() throws Exception {
        mockMvc.perform(post("/delete-customer")
                        .with(csrf())
                        .queryParam("id", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/oauth2/authorization/test"));
    }

    @Test
    void testAddCustomerValidationCustomer() throws Exception {
        mockMvc.perform(get("/")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("name", "foo_user"))))
                .andExpect(status().isOk());
        mockMvc.perform(post("/add-customer")
                        .with(csrf())
                        .with(oidcLogin()
                                .idToken(token -> token.claim("name", "foo_user")))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("newCustomer", new NewCustomerDTO()
                                .setName("")
                                .setTotalLoan(0)
                                .setInterestPercent(0)
                                .setLoanDurationYears(0)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(
                        flash()
                                .attribute("badFields",
                                        Set.of(
                                                "name",
                                                "totalLoan",
                                                "interestPercent",
                                                "loanDurationYears")));
    }

    @Test
    void testAddAndDeleteCustomerWithAuthAddsAndDeletesCustomer() throws Exception {
        mockMvc.perform(get("/")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("name", "foo_user"))))
                .andExpect(status().isOk());
        mockMvc.perform(post("/add-customer")
                        .with(csrf())
                        .with(oidcLogin()
                                .idToken(token -> token.claim("name", "foo_user")))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("newCustomer", new NewCustomerDTO()
                                .setName("John")
                                .setTotalLoan(100000)
                                .setInterestPercent(2)
                                .setLoanDurationYears(5)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        MvcResult homeAfterAdd = mockMvc.perform(get("/")
                        .with(oidcLogin()
                                .idToken(token ->
                                        token.claim("name", "foo_user"))))
                .andExpect(status().isOk())
                .andExpect(model()
                        .attribute(
                                "customers",
                                hasItem(
                                        hasProperty(
                                                "name",
                                                is("John")))))
                .andReturn();
        List<CustomerDTO> customersInModel = (List<CustomerDTO>) homeAfterAdd.getModelAndView().getModel().get("customers");
        UUID customerToRemove = customersInModel.stream()
                .filter(customerDTO -> customerDTO.getName().equals("John"))
                .map(CustomerDTO::getId)
                .findFirst()
                .orElseThrow();
        mockMvc.perform(post("/delete-customer")
                        .with(csrf())
                        .with(oidcLogin()
                                .idToken(token -> token.claim("name", "foo_user")))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .queryParam("id", customerToRemove.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        mockMvc.perform(get("/")
                        .with(oidcLogin()
                                .idToken(token ->
                                        token.claim("name", "foo_user"))))
                .andExpect(status().isOk())
                .andExpect(model()
                        .attribute(
                                "customers",
                                not(hasItem(
                                        hasProperty(
                                                "name",
                                                is("John"))))));
    }
}