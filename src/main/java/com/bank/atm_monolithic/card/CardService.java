package com.bank.atm_monolithic.card;

import com.bank.atm_monolithic.account.Account;
import com.bank.atm_monolithic.account.AccountRepository;
import com.bank.atm_monolithic.customer.Customer;
import com.bank.atm_monolithic.customer.CustomerRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public CardService(CardRepository cardRepository,
                       CustomerRepository customerRepository,
                       AccountRepository accountRepository) {
        this.cardRepository = cardRepository;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public CardDto addCard(String rawCardNumber, String rawPin, Card.CardType cardType, Long customerId, Long accountId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        String hashedPin = passwordEncoder.encode(rawPin);
        String maskedPan = Card.maskCardNumber(rawCardNumber);
        Card card = new Card(maskedPan, hashedPin, cardType, customer, account);
        Card savedCard = cardRepository.save(card);

        return CardDto.fromEntity(savedCard);
    }

    public boolean verifyPin(Long cardId, String rawPin) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        return passwordEncoder.matches(rawPin, card.getPinHash());
    }

    @Transactional(readOnly = true)
    public List<CardDto> getAllCards() {
        return cardRepository.findAll().stream()
                .map(CardDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CardDto getCardById(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        return CardDto.fromEntity(card);
    }

    @Transactional(readOnly = true)
    public void authenticateCard(String rawCardNumber, String rawPin) {
        String maskedCardNumber = Card.maskCardNumber(rawCardNumber);

        Card card = cardRepository.findByCardNumber(maskedCardNumber)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        if (card.getStatus() != Card.CardStatus.ACTIVE) {
            throw new IllegalStateException("Card is not active. Status: " + card.getStatus());
        }

        boolean isPinValid = passwordEncoder.matches(rawPin, card.getPinHash());

        if (!isPinValid) {
            throw new SecurityException("Invalid PIN");
        }

    }

    @Transactional(readOnly = true)
    public BigDecimal getBalanceByCardNumber(String rawCardNumber) {
        String maskedCardNumber = Card.maskCardNumber(rawCardNumber);

        Card card = cardRepository.findByCardNumber(maskedCardNumber)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        if (card.getStatus() != Card.CardStatus.ACTIVE) {
            throw new IllegalStateException("Card is not active. Status: " + card.getStatus());
        }

        return card.getAccount().getBalance();
    }

    @Transactional
    public void changePin(Long cardId, String oldPin, String newPin) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));

        if (!passwordEncoder.matches(oldPin, card.getPinHash())) {
            throw new SecurityException("Invalid current PIN");
        }

        if (newPin == null || !newPin.matches("\\d{4}")) {
            throw new IllegalArgumentException("New PIN must be a 4-digit number");
        }

        if (oldPin.equals(newPin)) {
            throw new IllegalArgumentException("New PIN cannot be the same as the old PIN");
        }

        card.setPinHash(passwordEncoder.encode(newPin));
        cardRepository.save(card);
    }
}