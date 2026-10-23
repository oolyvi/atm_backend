package com.bank.atm_monolithic.atm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AtmTerminalRepository extends JpaRepository<AtmTerminal, Long> {
}