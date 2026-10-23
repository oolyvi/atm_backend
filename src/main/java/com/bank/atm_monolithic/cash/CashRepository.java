package com.bank.atm_monolithic.cash;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CashRepository extends JpaRepository<Cash, Long> {
    List<Cash> findByAtmTerminalIdOrderByDenominationDesc(Long atmTerminalId);
}