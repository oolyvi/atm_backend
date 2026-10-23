package com.bank.atm_monolithic.customer;

import java.time.LocalDate;

public record CustomerDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        LocalDate dateOfBirth,
        String nationalId,
        Customer.CustomerStatus status
) {
    public static CustomerDto fromEntity(Customer customer) {
        if (customer == null) return null;

        return new CustomerDto(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getDateOfBirth(),
                customer.getNationalId(),
                customer.getStatus()
        );
    }
}