package com.bank.atm_monolithic.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPhoneNumber("123456789");
        customer.setDateOfBirth(LocalDate.of(1990, 1, 1));
        customer.setNationalId("NAT12345");
        customer.setPasswordHash("hashedpassword");
        customer.setStatus(Customer.CustomerStatus.ACTIVE);
    }

    @Test
    void getCustomers_ShouldReturnListOfCustomerDtos() {
        when(customerRepository.findAll()).thenReturn(List.of(customer));

        List<CustomerDto> result = customerService.getCustomers();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().firstName()).isEqualTo("John");
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void getCustomerById_WhenFound_ShouldReturnCustomerDto() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Optional<CustomerDto> result = customerService.getCustomerById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().email()).isEqualTo("john.doe@example.com");
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void getCustomerById_WhenNotFound_ShouldReturnEmpty() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<CustomerDto> result = customerService.getCustomerById(99L);

        assertThat(result).isEmpty();
        verify(customerRepository, times(1)).findById(99L);
    }

    @Test
    void addCustomer_ShouldSaveAndReturnCustomerDto() {
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerDto result = customerService.addCustomer(customer);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void replaceCustomer_WhenExists_ShouldUpdateAndReturnCustomerDto() {
        Customer newDetails = new Customer();
        newDetails.setFirstName("Jane");
        newDetails.setLastName("Doe");
        newDetails.setEmail("jane.doe@example.com");
        newDetails.setStatus(Customer.CustomerStatus.BLOCKED);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerDto result = customerService.replaceCustomer(1L, newDetails);

        assertThat(result).isNotNull();
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void deleteCustomer_ShouldInvokeDeleteById() {
        doNothing().when(customerRepository).deleteById(1L);

        customerService.deleteCustomer(1L);

        verify(customerRepository, times(1)).deleteById(1L);
    }

    @Test
    void getCustomerByStatus_ShouldReturnMatchingCustomers() {
        when(customerRepository.findByStatus(Customer.CustomerStatus.ACTIVE)).thenReturn(List.of(customer));

        List<CustomerDto> result = customerService.getCustomerByStatus(Customer.CustomerStatus.ACTIVE);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().status()).isEqualTo(Customer.CustomerStatus.ACTIVE);
        verify(customerRepository, times(1)).findByStatus(Customer.CustomerStatus.ACTIVE);
    }
}