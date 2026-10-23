package com.bank.atm_monolithic.core;

import com.bank.atm_monolithic.account.Account;
import com.bank.atm_monolithic.account.AccountRepository;
import com.bank.atm_monolithic.admin.Admin;
import com.bank.atm_monolithic.admin.AdminRepository;
import com.bank.atm_monolithic.atm.AtmTerminal;
import com.bank.atm_monolithic.atm.AtmTerminalRepository;
import com.bank.atm_monolithic.card.Card;
import com.bank.atm_monolithic.card.CardRepository;
import com.bank.atm_monolithic.customer.Customer;
import com.bank.atm_monolithic.customer.CustomerRepository;
import com.bank.atm_monolithic.transaction.Transaction;
import com.bank.atm_monolithic.transaction.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Configuration
@Profile("dev")
public class DataInitializer {

    @Bean
    CommandLineRunner initializeData(
            CustomerRepository customerRepository,
            AccountRepository accountRepository,
            CardRepository cardRepository,
            AtmTerminalRepository atmTerminalRepository,
            AdminRepository adminRepository,
            TransactionRepository transactionRepository) {

        return args -> {

            /* create default admin if none exists */
            if (adminRepository.count() == 0) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                Admin defaultAdmin = new Admin();
                defaultAdmin.setUsername("admin");
                defaultAdmin.setPasswordHash(encoder.encode("1234"));
                defaultAdmin.setEmail("admin@atm.com");

                adminRepository.save(defaultAdmin);
                System.out.println("Default admin user initialized (username: admin, password: 1234).");
                System.out.println("id : " + defaultAdmin.getId());
                System.out.println("username: " + defaultAdmin.getUsername() + " | email:" + defaultAdmin.getEmail());
            } else {
                System.out.println("Admin user already exists. Skipping.");
            }

            /* avoid duplicated customer data */
            if (customerRepository.count() > 0) {
                System.out.println(
                        "Customers already exist. Skipping sample data."
                );
                return;
            }

            /* create customers */
            Customer john = new Customer();
            john.setFirstName("John");
            john.setLastName("Smith");
            john.setEmail("john.smith@example.com");
            john.setPhoneNumber("+994501112233");
            john.setDateOfBirth(LocalDate.of(1995, 5, 15));
            john.setNationalId("DEMO-NAT-001");
            john.setPasswordHash(
                    "$2a$12$REPLACE_WITH_A_REAL_BCRYPT_HASH"
            );
            john.setStatus(Customer.CustomerStatus.ACTIVE);

            Customer emma = new Customer();
            emma.setFirstName("Emma");
            emma.setLastName("Wilson");
            emma.setEmail("emma.wilson@example.com");
            emma.setPhoneNumber("+994502223344");
            emma.setDateOfBirth(LocalDate.of(1998, 8, 22));
            emma.setNationalId("DEMO-NAT-002");
            emma.setPasswordHash(
                    "$2a$12$REPLACE_WITH_A_REAL_BCRYPT_HASH"
            );
            emma.setStatus(Customer.CustomerStatus.ACTIVE);

            Customer alex = new Customer();
            alex.setFirstName("Alex");
            alex.setLastName("Brown");
            alex.setEmail("alex.brown@example.com");
            alex.setPhoneNumber("+994503334455");
            alex.setDateOfBirth(LocalDate.of(2000, 2, 10));
            alex.setNationalId("DEMO-NAT-003");
            alex.setPasswordHash(
                    "$2a$12$REPLACE_WITH_A_REAL_BCRYPT_HASH"
            );
            alex.setStatus(Customer.CustomerStatus.BLOCKED);

            customerRepository.saveAll(List.of(john, emma, alex));

            /* create accounts */
            Account johnChecking = new Account(
                    "AZ100000000000000001",
                    new BigDecimal("1250.00"),
                    new BigDecimal("2000.00"),
                    Account.AccountType.CHECKING,
                    john
            );

            Account johnSavings = new Account(
                    "AZ100000000000000002",
                    new BigDecimal("340.00"),
                    new BigDecimal("1000.00"),
                    Account.AccountType.SAVINGS,
                    john
            );

            Account emmaChecking = new Account(
                    "AZ100000000000000003",
                    new BigDecimal("8500.50"),
                    new BigDecimal("1000.50"),
                    Account.AccountType.CHECKING,
                    emma
            );

            Account alexChecking = new Account(
                    "AZ100000000000000004",
                    new BigDecimal("8500.00"),
                    new BigDecimal("1000.00"),
                    Account.AccountType.CHECKING,
                    alex
            );

            accountRepository.saveAll(List.of(
                    johnChecking,
                    johnSavings,
                    emmaChecking,
                    alexChecking
            ));

            /* create cards */
            Card johnDebit = new Card(
                    "4532678912348812",
                    "1234",
                    Card.CardType.DEBIT,
                    john,
                    johnChecking
            );

            Card johnSavingsDebit = new Card(
                    "5412009354673309",
                    "6789",
                    Card.CardType.DEBIT,
                    john,
                    johnSavings
            );

            Card emmaDebit = new Card(
                    "3782435111120055",
                    "2004",
                    Card.CardType.DEBIT,
                    emma,
                    emmaChecking
            );

            Card alexDebit = new Card(
                    "4000000000000004",
                    "1111",
                    Card.CardType.DEBIT,
                    alex,
                    alexChecking
            );

            Card alexCredit = new Card(
                    "5000000000000005",
                    "2222",
                    Card.CardType.CREDIT,
                    alex,
                    alexChecking
            );

            cardRepository.saveAll(List.of(
                    johnDebit,
                    johnSavingsDebit,
                    emmaDebit,
                    alexDebit,
                    alexCredit
            ));

            System.out.println("Demo customers, accounts, and cards initialized.");

            /* create atm terminals */
            if (atmTerminalRepository.count() == 0) {
                AtmTerminal atm1 = new AtmTerminal();
                atm1.setTerminalCode("ATM-BAKU-01");
                atm1.setLocation("Nizami Street 24, Baku");
                atm1.setStatus(AtmTerminal.AtmTerminalStatus.ACTIVE);
                atm1.setIpAddress("192.168.1.101");
                atm1.setSoftwareVersion("v2.1.0");
                atm1.setCassette1Count(100);
                atm1.setCassette5Count(100);
                atm1.setCassette10Count(200);
                atm1.setCassette20Count(200);
                atm1.setCassette50Count(100);
                atm1.setCassette100Count(50);
                atm1.setCassette200Count(20);
                atm1.setCassette500Count(10);

                AtmTerminal atm2 = new AtmTerminal();
                atm2.setTerminalCode("ATM-BAKU-02");
                atm2.setLocation("28 May Mall, Baku");
                atm2.setStatus(AtmTerminal.AtmTerminalStatus.ACTIVE);
                atm2.setIpAddress("192.168.1.102");
                atm2.setSoftwareVersion("v2.1.0");
                atm2.setCassette1Count(50);
                atm2.setCassette5Count(50);
                atm2.setCassette10Count(100);
                atm2.setCassette20Count(100);
                atm2.setCassette50Count(50);
                atm2.setCassette100Count(25);
                atm2.setCassette200Count(10);
                atm2.setCassette500Count(5);

                AtmTerminal atm3 = new AtmTerminal();
                atm3.setTerminalCode("ATM-GANJA-01");
                atm3.setLocation("Javad Khan Street, Ganja");
                atm3.setStatus(AtmTerminal.AtmTerminalStatus.MAINTENANCE);
                atm3.setIpAddress("192.168.2.50");
                atm3.setSoftwareVersion("v2.0.5");
                atm3.setCassette1Count(0);
                atm3.setCassette5Count(0);
                atm3.setCassette10Count(0);
                atm3.setCassette20Count(0);
                atm3.setCassette50Count(0);
                atm3.setCassette100Count(0);
                atm3.setCassette200Count(0);
                atm3.setCassette500Count(0);

                atmTerminalRepository.saveAll(List.of(atm1, atm2, atm3));
                System.out.println("Demo ATM terminals initialized.");
            } else {
                System.out.println("ATM terminals already exist. Skipping.");
            }

            /* create transactions for testing history */
            if (transactionRepository.count() == 0) {
                Transaction tx1 = new Transaction(
                        "mock-idemp-key-001",
                        Transaction.TransactionType.CREDIT,
                        new BigDecimal("500.00"),
                        johnChecking
                );

                Transaction tx2 = new Transaction(
                        "mock-idemp-key-002",
                        Transaction.TransactionType.DEBIT,
                        new BigDecimal("100.00"),
                        johnChecking
                );

                Transaction tx3 = new Transaction(
                        "mock-idemp-key-003",
                        Transaction.TransactionType.DEBIT,
                        new BigDecimal("1200.00"),
                        emmaChecking
                );

                transactionRepository.saveAll(List.of(tx1, tx2, tx3));
                System.out.println("Demo transactions initialized.");
            } else {
                System.out.println("Transactions already exist. Skipping.");
            }
        };
    }
}