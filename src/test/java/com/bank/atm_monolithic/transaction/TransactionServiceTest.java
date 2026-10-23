package com.bank.atm_monolithic.transaction;

import com.bank.atm_monolithic.account.Account;
import com.bank.atm_monolithic.account.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setBalance(new BigDecimal("500.00"));
    }

    @Test
    void processTransaction_Debit_Success() {
        String idempotencyKey = "key-123";
        BigDecimal amount = new BigDecimal("100.00");

        when(transactionRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());
        when(transactionRepository.findAccountByIdWithLock(1L)).thenReturn(Optional.of(testAccount));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction transaction = transactionService.processTransaction(
                idempotencyKey, 1L, amount, Transaction.TransactionType.DEBIT
        );

        assertNotNull(transaction);
        assertEquals(Transaction.TransactionType.DEBIT, transaction.getType());
        assertEquals(new BigDecimal("400.00"), testAccount.getBalance());
        verify(accountRepository, times(1)).save(testAccount);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void processTransaction_Debit_InsufficientFunds_ThrowsException() {
        String idempotencyKey = "key-123";
        BigDecimal amount = new BigDecimal("600.00");

        when(transactionRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());
        when(transactionRepository.findAccountByIdWithLock(1L)).thenReturn(Optional.of(testAccount));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                transactionService.processTransaction(idempotencyKey, 1L, amount, Transaction.TransactionType.DEBIT)
        );

        assertEquals("Insufficient funds for debit operation", exception.getMessage());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void processTransaction_IdempotentRequest_ReturnsExisting() {
        String idempotencyKey = "key-123";
        Transaction existingTx = new Transaction(idempotencyKey, Transaction.TransactionType.CREDIT, new BigDecimal("50.00"), testAccount);

        when(transactionRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.of(existingTx));

        Transaction transaction = transactionService.processTransaction(
                idempotencyKey, 1L, new BigDecimal("50.00"), Transaction.TransactionType.CREDIT
        );

        assertEquals(existingTx, transaction);
        verify(transactionRepository, never()).findAccountByIdWithLock(anyLong());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void transferMoney_Success() {
        String idempotencyKey = "transfer-key-1";
        Account destinationAccount = new Account();
        destinationAccount.setBalance(new BigDecimal("200.00"));

        BigDecimal transferAmount = new BigDecimal("100.00");

        when(transactionRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());
        when(transactionRepository.findAccountByIdWithLock(1L)).thenReturn(Optional.of(testAccount));
        when(transactionRepository.findAccountByIdWithLock(2L)).thenReturn(Optional.of(destinationAccount));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction transaction = transactionService.transferMoney(idempotencyKey, 1L, 2L, transferAmount);

        assertNotNull(transaction);
        assertEquals(new BigDecimal("400.00"), testAccount.getBalance());
        assertEquals(new BigDecimal("300.00"), destinationAccount.getBalance());
        verify(accountRepository, times(1)).save(testAccount);
        verify(accountRepository, times(1)).save(destinationAccount);
    }
}