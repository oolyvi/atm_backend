package com.bank.atm_monolithic.admin;

import com.bank.atm_monolithic.account.Account;
import com.bank.atm_monolithic.account.AccountRepository;
import com.bank.atm_monolithic.atm.AtmTerminal;
import com.bank.atm_monolithic.atm.AtmTerminalRepository;
import com.bank.atm_monolithic.card.Card;
import com.bank.atm_monolithic.card.CardRepository;
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
class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AtmTerminalRepository terminalRepository;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private AdminService adminService;

    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    void authenticateAdmin_Success() {
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPasswordHash(passwordEncoder.encode("1234"));

        when(adminRepository.findByUsername("admin")).thenReturn(Optional.of(admin));

        boolean result = adminService.authenticateAdmin("admin", "1234");

        assertTrue(result);
        verify(adminRepository, times(1)).findByUsername("admin");
    }

    @Test
    void authenticateAdmin_InvalidPassword() {
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPasswordHash(passwordEncoder.encode("1234"));

        when(adminRepository.findByUsername("admin")).thenReturn(Optional.of(admin));

        boolean result = adminService.authenticateAdmin("admin", "wrongpassword");

        assertFalse(result);
    }

    @Test
    void authenticateAdmin_UserNotFound() {
        when(adminRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        boolean result = adminService.authenticateAdmin("unknown", "1234");

        assertFalse(result);
    }

    @Test
    void updateAdminProfile_Success() {
        Admin admin = new Admin();
        admin.setId(1L);
        admin.setUsername("oldUser");
        admin.setEmail("old@atm.com");

        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(adminRepository.save(any(Admin.class))).thenReturn(admin);

        adminService.updateAdminProfile(1L, "newUser", "new@atm.com");

        assertEquals("newUser", admin.getUsername());
        assertEquals("new@atm.com", admin.getEmail());
        verify(adminRepository, times(1)).save(admin);
    }

    @Test
    void updateAdminProfile_NotFound() {
        when(adminRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                adminService.updateAdminProfile(99L, "newUser", "new@atm.com")
        );
        verify(adminRepository, never()).save(any());
    }

    @Test
    void updateAccountLimit_Success() {
        Account account = new Account();
        account.setId(1L);
        account.setDailyLimit(BigDecimal.valueOf(500));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        adminService.updateAccountLimit(1L, BigDecimal.valueOf(1000));

        assertEquals(BigDecimal.valueOf(1000), account.getDailyLimit());
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void changeTerminalStatus_Success() {
        AtmTerminal terminal = new AtmTerminal();
        terminal.setId(1L);
        terminal.setStatus(AtmTerminal.AtmTerminalStatus.ACTIVE);

        when(terminalRepository.findById(1L)).thenReturn(Optional.of(terminal));
        when(terminalRepository.save(any(AtmTerminal.class))).thenReturn(terminal);

        adminService.changeTerminalStatus(1L, AtmTerminal.AtmTerminalStatus.OFFLINE);

        assertEquals(AtmTerminal.AtmTerminalStatus.OFFLINE, terminal.getStatus());
        verify(terminalRepository, times(1)).save(terminal);
    }

    @Test
    void manualUnblockCard_Success() {
        Card card = new Card();
        card.setId(1L);
        card.setStatus(Card.CardStatus.BLOCKED);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        adminService.manualUnblockCard(1L);

        assertEquals(Card.CardStatus.ACTIVE, card.getStatus());
        verify(cardRepository, times(1)).save(card);
    }

    @Test
    void manualBlockCard_Success() {
        Card card = new Card();
        card.setId(1L);
        card.setStatus(Card.CardStatus.ACTIVE);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        adminService.manualBlockCard(1L);

        assertEquals(Card.CardStatus.BLOCKED, card.getStatus());
        verify(cardRepository, times(1)).save(card);
    }

    @Test
    void forceResetCardPin_Success() {
        Card card = new Card();
        card.setId(1L);
        card.setStatus(Card.CardStatus.BLOCKED);
        card.setPinHash("oldHash");

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        adminService.forceResetCardPin(1L, "newTempHash");

        assertEquals("newTempHash", card.getPinHash());
        assertEquals(Card.CardStatus.ACTIVE, card.getStatus());
        verify(cardRepository, times(1)).save(card);
    }
}