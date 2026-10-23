package com.bank.atm_monolithic.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    @Test
    void getCustomers_ShouldReturnOkWithList() throws Exception {
        CustomerDto dto = new CustomerDto(
                1L,
                "John",
                "Doe",
                "john@example.com",
                "12345",
                LocalDate.of(1990, 1, 1),
                "NAT1",
                Customer.CustomerStatus.ACTIVE);
        when(customerService.getCustomers()).thenReturn(List.of(dto));

        mockMvc.perform(get("/customer/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("john@example.com"));
    }

    @Test
    void getCustomerById_WhenExists_ShouldReturnOk() throws Exception {
        CustomerDto dto = new CustomerDto(
                1L,
                "John",
                "Doe",
                "john@example.com",
                "12345",
                LocalDate.of(1990, 1, 1),
                "NAT1",
                Customer.CustomerStatus.ACTIVE);
        when(customerService.getCustomerById(1L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getCustomerById_WhenNotFound_ShouldReturnNotFound() throws Exception {
        when(customerService.getCustomerById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/customer/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addCustomer_ShouldReturnCreated() throws Exception {
        Customer customer = new Customer();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john@example.com");
        customer.setPasswordHash("hash");

        CustomerDto dto = new CustomerDto(
                1L,
                "John",
                "Doe", "john@example.com",
                "12345", LocalDate.of(1990, 1, 1),
                "NAT1",
                Customer.CustomerStatus.ACTIVE);
        when(customerService.addCustomer(any(Customer.class))).thenReturn(dto);

        mockMvc.perform(post("/customer/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void replaceCustomer_ShouldReturnOk() throws Exception {
        Customer customer = new Customer();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john@example.com");
        customer.setPasswordHash("hash");

        CustomerDto dto = new CustomerDto(
                1L,
                "John",
                "Doe",
                "john@example.com",
                "12345",
                LocalDate.of(1990, 1, 1),
                "NAT1",
                Customer.CustomerStatus.ACTIVE);
        when(customerService.replaceCustomer(eq(1L), any(Customer.class))).thenReturn(dto);

        mockMvc.perform(put("/customer/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deactivateCustomer_ShouldReturnNoContent() throws Exception {
        doNothing().when(customerService).deleteCustomer(1L);

        mockMvc.perform(delete("/customer/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getCustomerByStatus_ShouldReturnList() throws Exception {
        CustomerDto dto = new CustomerDto(
                1L,
                "John",
                "Doe",
                "john@example.com",
                "12345",
                LocalDate.of(1990, 1, 1),
                "NAT1",
                Customer.CustomerStatus.ACTIVE);
        when(customerService.getCustomerByStatus(Customer.CustomerStatus.ACTIVE)).thenReturn(List.of(dto));

        mockMvc.perform(get("/customer/status/ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }
}