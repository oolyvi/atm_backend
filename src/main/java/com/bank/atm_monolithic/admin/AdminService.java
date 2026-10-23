package com.bank.atm_monolithic.admin;

import com.bank.atm_monolithic.account.Account;
import com.bank.atm_monolithic.account.AccountRepository;
import com.bank.atm_monolithic.atm.AtmTerminal;
import com.bank.atm_monolithic.atm.AtmTerminalRepository;
import com.bank.atm_monolithic.card.Card;
import com.bank.atm_monolithic.card.CardRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final AccountRepository accountRepository;
    private final AtmTerminalRepository terminalRepository;
    private final CardRepository cardRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AdminService(AdminRepository adminRepository,
                        AccountRepository accountRepository,
                        AtmTerminalRepository terminalRepository,
                        CardRepository cardRepository) {
        this.adminRepository = adminRepository;
        this.accountRepository = accountRepository;
        this.terminalRepository = terminalRepository;
        this.cardRepository = cardRepository;
    }

    public boolean authenticateAdmin(String username, String rawPassword) {
        return adminRepository.findByUsername(username)
                .map(admin -> passwordEncoder.matches(rawPassword, admin.getPasswordHash()))
                .orElse(false);
    }

    @Transactional
    public void updateAdminProfile(Long adminId, String newUsername, String newEmail) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
        admin.setUsername(newUsername);
        admin.setEmail(newEmail);
        adminRepository.save(admin);
    }

    @Transactional
    public void updateAccountLimit(Long accountId, BigDecimal newDailyLimit) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        account.setDailyLimit(newDailyLimit);
        accountRepository.save(account);
    }

    @Transactional
    public void changeTerminalStatus(Long terminalId, AtmTerminal.AtmTerminalStatus status) {
        AtmTerminal terminal = terminalRepository.findById(terminalId)
                .orElseThrow(() -> new IllegalArgumentException("Terminal not found"));
        terminal.setStatus(status);
        terminalRepository.save(terminal);
    }

    @Transactional
    public void manualUnblockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));
        card.setStatus(Card.CardStatus.ACTIVE);
        cardRepository.save(card);
    }

    @Transactional
    public void manualBlockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));
        card.setStatus(Card.CardStatus.BLOCKED);
        cardRepository.save(card);
    }

    @Transactional
    public void forceResetCardPin(Long cardId, String temporaryPinHash) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));
        card.setPinHash(temporaryPinHash);
        card.setStatus(Card.CardStatus.ACTIVE);
        cardRepository.save(card);
    }
}