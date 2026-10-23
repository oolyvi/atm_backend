package com.bank.atm_monolithic.core;

import com.bank.atm_monolithic.account.AccountRepository;
import com.bank.atm_monolithic.admin.AdminRepository;
import com.bank.atm_monolithic.atm.AtmTerminalRepository;
import com.bank.atm_monolithic.card.CardRepository;
import com.bank.atm_monolithic.customer.CustomerRepository;
import com.bank.atm_monolithic.transaction.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
class DataInitializerTest {

    @Autowired
    private CommandLineRunner initializeData;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private AtmTerminalRepository atmTerminalRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void initializeData_ShouldPopulateDatabase() throws Exception {
        assertThat(adminRepository.count()).isGreaterThan(0);
        assertThat(customerRepository.count()).isGreaterThan(0);
        assertThat(accountRepository.count()).isGreaterThan(0);
        assertThat(cardRepository.count()).isGreaterThan(0);
        assertThat(atmTerminalRepository.count()).isGreaterThan(0);
        assertThat(transactionRepository.count()).isGreaterThan(0);
    }
}