package com.bank.atm_monolithic.customer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public List<CustomerDto> getCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(CustomerDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<CustomerDto> getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(CustomerDto::fromEntity);
    }

    @Transactional
    public CustomerDto addCustomer(Customer customer) {
        Customer savedCustomer = customerRepository.save(customer);
        return CustomerDto.fromEntity(savedCustomer);
    }

    @Transactional
    public CustomerDto replaceCustomer(Long id, Customer newCustomer) {
        Customer savedCustomer =  customerRepository.findById(id)
                .map(customer -> {
                    customer.setFirstName(newCustomer.getFirstName());
                    customer.setLastName(newCustomer.getLastName());
                    customer.setEmail(newCustomer.getEmail());
                    customer.setPhoneNumber(newCustomer.getPhoneNumber());
                    customer.setDateOfBirth(newCustomer.getDateOfBirth());
                    customer.setNationalId(newCustomer.getNationalId());
                    customer.setStatus(newCustomer.getStatus());

                    return customerRepository.save(customer);
                })
                .orElseGet(() -> {
                    newCustomer.setId(null);
                    return customerRepository.save(newCustomer);
                });

        return CustomerDto.fromEntity(savedCustomer);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<CustomerDto> getCustomerByStatus(Customer.CustomerStatus status) {
        return customerRepository.findByStatus(status)
                .stream()
                .map(CustomerDto::fromEntity)
                .toList();
    }

    public void deactivateCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.setStatus(Customer.CustomerStatus.CLOSED);
        customerRepository.save(customer);
    }
}