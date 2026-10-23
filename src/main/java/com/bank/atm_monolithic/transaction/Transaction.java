package com.bank.atm_monolithic.transaction;

import com.bank.atm_monolithic.account.Account;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "atm")
public class Transaction {

    /* transaction type */
    public enum TransactionType {
        DEBIT,
        CREDIT,
        TRANSFER
    }


    /* transfer request */
    public record TransferRequest(
            Long sourceAccountId,
            Long destinationAccountId,
            BigDecimal amount
    ) {}

    /* transaction response */
    public record TransactionResponseDto(
            Long id,
            String idempotencyKey,
            Transaction.TransactionType type,
            BigDecimal amount,
            Long accountId,
            LocalDateTime timestamp
    ) {
        public static TransactionResponseDto from(Transaction tx) {
            return new TransactionResponseDto(
                    tx.getId(),
                    tx.getIdempotencyKey(),
                    tx.getType(),
                    tx.getAmount(),
                    tx.getAccount().getId(),
                    tx.getTimestamp()
            );
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idempotency_key", unique = true, nullable = false, length = 100)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public Transaction() {}

    public Transaction(String idempotencyKey, TransactionType type, BigDecimal amount, Account account) {
        this.idempotencyKey = idempotencyKey;
        this.type = type;
        this.amount = amount;
        this.account = account;
    }

    public Long getId() { return id; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    @JsonIgnore
    public Account getAccount() { return account; }
    public LocalDateTime getTimestamp() { return timestamp; }
}