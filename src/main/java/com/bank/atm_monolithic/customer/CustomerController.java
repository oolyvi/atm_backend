package com.bank.atm_monolithic.customer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<CustomerDto>> getCustomers() {
        /*
        http://localhost:8081/customer/all
         */

        System.out.println("[ATM_DEBUG]: get all customers");

        return ResponseEntity.ok(customerService.getCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable Long id) {
        /*
        http://localhost:8081/customer/1
         */

        System.out.println("[ATM_DEBUG]: get customer for id: " + id);

        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ResponseEntity<CustomerDto> addCustomer(@RequestBody Customer customer) {
        /*
        http://localhost:8081/customer/add
        {
            "firstName": "name",
            "lastName": "lastname",
            "email": "a@example.com",
            "phoneNumber": "051123123",
            "dateOfBirth": "2024-12-10",
            "nationalId": "128734876",
            "status": "ACTIVE",
            "passwordHash": "best_password_in_the_world"
        }
         */
        CustomerDto savedCustomer = customerService.addCustomer(customer);

        System.out.println("[ATM_DEBUG]: add customer account for id: " + savedCustomer.id());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedCustomer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDto> replaceCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        /*
        http://localhost:8081/customer/1
        {
            "id": "5",
            "firstName": "ad",
            "lastName": "lastname",
            "email": "a@example.com",
            "phoneNumber": "051123123",
            "dateOfBirth": "2024-12-10",
            "nationalId": "128734876",
            "status": "ACTIVE"
        }
         */
        CustomerDto savedCustomer = customerService.replaceCustomer(id, customer);

        System.out.println("[ATM_DEBUG]: replace customer for id: " + id);

        return ResponseEntity.ok(savedCustomer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateCustomer(@PathVariable Long id) {
         /*
         http://localhost:8081/customer/1
         */
        customerService.deactivateCustomer(id);
        System.out.println("[ATM_DEBUG]: deactivated customer for id: " + id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<CustomerDto>> getCustomerByStatus(@PathVariable Customer.CustomerStatus status) {
        /*
         http://localhost:8081/customer/status/ACTIVE
         */

        System.out.println("[ATM_DEBUG]: get customers by status");

        return ResponseEntity.ok(
                customerService.getCustomerByStatus(status));
    }
}