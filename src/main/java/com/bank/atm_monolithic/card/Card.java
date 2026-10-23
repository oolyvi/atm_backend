package com.bank.atm_monolithic.card;

import com.bank.atm_monolithic.account.Account;
import com.bank.atm_monolithic.customer.Customer;
import jakarta.persistence.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "card")
public class Card {

    /* password encoder */
    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    /* card type */
    public enum CardType {
        DEBIT,
        CREDIT
    }

    /* card status */
    public enum CardStatus {
        ACTIVE,
        BLOCKED,
        EXPIRED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number", nullable = false, unique = true, length = 30)
    private String cardNumber;

    @Column(name = "pin_hash", nullable = false, length = 255)
    private String pinHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", nullable = false, length = 20)
    private CardType cardType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CardStatus status = CardStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    public Card() {
    }

    public Card(String rawCardNumber, String rawPin, CardType cardType,
                Customer customer, Account account) {
        this.cardNumber = maskCardNumber(rawCardNumber);
        this.pinHash = PASSWORD_ENCODER.encode(rawPin);
        this.cardType = cardType;
        this.customer = customer;
        this.account = account;
    }

    public static String maskCardNumber(String rawCardNumber) {
        if (rawCardNumber == null || rawCardNumber.length() < 4) {
            return "****";
        }
        String last4 = rawCardNumber.substring(rawCardNumber.length() - 4);
        return "****-****-****-" + last4;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = maskCardNumber(cardNumber); }
    public String getPinHash() { return pinHash; }
    public void setPinHash(String pinHash) { this.pinHash = pinHash; }
    public CardType getCardType() { return cardType; }
    public void setCardType(CardType cardType) { this.cardType = cardType; }
    public CardStatus getStatus() { return status; }
    public void setStatus(CardStatus status) { this.status = status; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
}