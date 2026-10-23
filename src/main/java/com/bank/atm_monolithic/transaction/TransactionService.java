package com.bank.atm_monolithic.transaction;

import com.bank.atm_monolithic.account.Account;
import com.bank.atm_monolithic.account.AccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Transaction processTransaction(String idempotencyKey, Long accountId, BigDecimal amount, Transaction.TransactionType type) {

        Optional<Transaction> existingTransaction = transactionRepository.findByIdempotencyKey(idempotencyKey);
        if (existingTransaction.isPresent()) {
            return existingTransaction.get();
        }

        Account account = transactionRepository.findAccountByIdWithLock(accountId)
                .orElseThrow(() -> new TransactionNotFoundException(accountId));

        if (type == Transaction.TransactionType.DEBIT) {
            if (account.getBalance().compareTo(amount) < 0) {
                throw new IllegalStateException("Insufficient funds for debit operation");
            }
            account.setBalance(account.getBalance().subtract(amount));
        } else if (type == Transaction.TransactionType.CREDIT) {
            account.setBalance(account.getBalance().add(amount));
        }

        accountRepository.save(account);

        Transaction transaction = new Transaction(idempotencyKey, type, amount, account);

        return transactionRepository.save(transaction);
    }

    public Page<Transaction> getTransactions(Long accountId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return transactionRepository.findTransactions(accountId, startDate, endDate, pageable);
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
    }

    @Transactional
    public Transaction refundTransaction(String idempotencyKey, Long originalTransactionId) {
        Optional<Transaction> existing = transactionRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            return existing.get();
        }

        Transaction original = getTransactionById(originalTransactionId);

        System.out.println("DEBUG: Original ID = " + original.getId() + ", Type = " + original.getType() + ", Amount = " + original.getAmount());

        Account account = transactionRepository.findAccountByIdWithLock(original.getAccount().getId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Transaction.TransactionType reversalType;

        if (original.getType() == Transaction.TransactionType.DEBIT) {
            reversalType = Transaction.TransactionType.CREDIT;
            account.setBalance(account.getBalance().add(original.getAmount()));
        } else {
            reversalType = Transaction.TransactionType.DEBIT;
            if (account.getBalance().compareTo(original.getAmount()) < 0) {
                throw new IllegalStateException("Insufficient funds to issue refund debit");
            }
            account.setBalance(account.getBalance().subtract(original.getAmount()));
        }

        accountRepository.save(account);

        Transaction refundTransaction = new Transaction(idempotencyKey, reversalType, original.getAmount(), account);
        return transactionRepository.save(refundTransaction);
    }

    /* transaction receipt */
    public record TransactionReceiptDto(
            Long transactionId,
            String idempotencyKey,
            Long accountId,
            String type,
            BigDecimal amount,
            LocalDateTime timestamp,
            BigDecimal currentAccountBalance
    ) {}

    @Transactional(readOnly = true)
    public TransactionReceiptDto generateReceipt(Long transactionId) {
        Transaction tx = getTransactionById(transactionId);
        return new TransactionReceiptDto(
                tx.getId(),
                tx.getIdempotencyKey(),
                tx.getAccount().getId(),
                tx.getType().name(),
                tx.getAmount(),
                tx.getTimestamp(),
                tx.getAccount().getBalance()
        );
    }

    @Transactional
    public Transaction transferMoney(String idempotencyKey, Long sourceAccountId, Long destinationAccountId, BigDecimal amount) {
        Optional<Transaction> existingTransaction = transactionRepository.findByIdempotencyKey(idempotencyKey);
        if (existingTransaction.isPresent()) {
            return existingTransaction.get();
        }

        if (sourceAccountId.equals(destinationAccountId)) {
            throw new IllegalArgumentException("Source and destination accounts cannot be the same");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }

        Long firstLockId = Math.min(sourceAccountId, destinationAccountId);
        Long secondLockId = Math.max(sourceAccountId, destinationAccountId);

        Account firstAccount = transactionRepository.findAccountByIdWithLock(firstLockId)
                .orElseThrow(() -> new TransactionNotFoundException(firstLockId));

        Account secondAccount = transactionRepository.findAccountByIdWithLock(secondLockId)
                .orElseThrow(() -> new TransactionNotFoundException(secondLockId));

        Account sourceAccount = (sourceAccountId.equals(firstLockId)) ? firstAccount : secondAccount;
        Account destinationAccount = (destinationAccountId.equals(firstLockId)) ? firstAccount : secondAccount;

        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds in source account for transfer");
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        destinationAccount.setBalance(destinationAccount.getBalance().add(amount));

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        Transaction transaction = new Transaction(idempotencyKey, Transaction.TransactionType.TRANSFER, amount, sourceAccount);

        return transactionRepository.save(transaction);
    }
}