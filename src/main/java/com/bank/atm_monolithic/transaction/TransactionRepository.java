package com.bank.atm_monolithic.transaction;

import com.bank.atm_monolithic.account.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id = :id")
    Optional<Account> findAccountByIdWithLock(@Param("id") Long id);

    @Query("SELECT t FROM Transaction t WHERE " +
            "(:accountId IS NULL OR t.account.id = :accountId) AND " +
            "(cast(:startDate as timestamp) IS NULL OR t.timestamp >= :startDate) AND " +
            "(cast(:endDate as timestamp) IS NULL OR t.timestamp <= :endDate)")
    Page<Transaction> findTransactions(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}