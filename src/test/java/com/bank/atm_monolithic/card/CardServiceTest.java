package com.bank.atm_monolithic.card;

import com.bank.atm_monolithic.account.Account;
import com.bank.atm_monolithic.account.AccountRepository;
import com.bank.atm_monolithic.customer.Customer;
import com.bank.atm_monolithic.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private CardService cardService;

    private Customer customer;
    private Account account;
    private Card card;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);

        account = new Account();
        account.setId(1L);
        account.setBalance(new BigDecimal("500.00"));

        card = new Card("1234567890123456", "1234", Card.CardType.DEBIT, customer, account);
        card.setId(1L);
    }

    @Test
    void addCard_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        CardDto result = cardService.addCard("1234567890123456", "1234", Card.CardType.DEBIT, 1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("****-****-****-3456", result.maskedCardNumber());
        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    void addCard_CustomerNotFound_ThrowsException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                cardService.addCard("1234567890123456", "1234", Card.CardType.DEBIT, 1L, 1L)
        );

        assertEquals("Customer not found", exception.getMessage());
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void verifyPin_Success() {
        card.setPinHash(passwordEncoder.encode("1234"));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        boolean isValid = cardService.verifyPin(1L, "1234");

        assertTrue(isValid);
    }

    @Test
    void verifyPin_Failure() {
        card.setPinHash(passwordEncoder.encode("1234"));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        boolean isValid = cardService.verifyPin(1L, "9999");

        assertFalse(isValid);
    }

    @Test
    void authenticateCard_Success() {
        card.setPinHash(passwordEncoder.encode("1234"));
        card.setStatus(Card.CardStatus.ACTIVE);
        when(cardRepository.findByCardNumber("****-****-****-3456")).thenReturn(Optional.of(card));

        assertDoesNotThrow(() -> cardService.authenticateCard("1234567890123456", "1234"));
    }

    @Test
    void authenticateCard_CardInactive_ThrowsException() {
        card.setStatus(Card.CardStatus.BLOCKED);
        when(cardRepository.findByCardNumber("****-****-****-3456")).thenReturn(Optional.of(card));

        Exception exception = assertThrows(IllegalStateException.class, () ->
                cardService.authenticateCard("1234567890123456", "1234")
        );

        assertTrue(exception.getMessage().contains("Card is not active"));
    }

    @Test
    void getBalanceByCardNumber_Success() {
        card.setStatus(Card.CardStatus.ACTIVE);
        when(cardRepository.findByCardNumber("****-****-****-3456")).thenReturn(Optional.of(card));

        BigDecimal balance = cardService.getBalanceByCardNumber("1234567890123456");

        assertEquals(new BigDecimal("500.00"), balance);
    }

    @Test
    void changePin_Success() {
        card.setPinHash(passwordEncoder.encode("1234"));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        assertDoesNotThrow(() -> cardService.changePin(1L, "1234", "5678"));
        verify(cardRepository, times(1)).save(card);
    }

    @Test
    void changePin_InvalidOldPin_ThrowsException() {
        card.setPinHash(passwordEncoder.encode("1234"));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        assertThrows(SecurityException.class, () ->
                cardService.changePin(1L, "0000", "5678")
        );
    }

    @Test
    void changePin_InvalidNewPinFormat_ThrowsException() {
        card.setPinHash(passwordEncoder.encode("1234"));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        assertThrows(IllegalArgumentException.class, () ->
                cardService.changePin(1L, "1234", "123") // too short
        );
    }
}