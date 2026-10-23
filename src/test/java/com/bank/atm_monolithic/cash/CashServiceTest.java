package com.bank.atm_monolithic.cash;

import com.bank.atm_monolithic.atm.AtmTerminal;
import com.bank.atm_monolithic.atm.AtmTerminalRepository;
import com.bank.atm_monolithic.notification.NotificationRoutingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CashServiceTest {

    @Mock
    private CashRepository cashRepository;

    @Mock
    private AtmTerminalRepository atmTerminalRepository;

    @Mock
    private NotificationRoutingService routingService;

    @InjectMocks
    private CashService cashService;

    private AtmTerminal terminal;

    @BeforeEach
    void setUp() {
        terminal = new AtmTerminal();
        terminal.setId(1L);
        terminal.setCassette500Count(1);
        terminal.setCassette200Count(1);
        terminal.setCassette100Count(2);
        terminal.setCassette50Count(2);
        terminal.setCassette20Count(2);
        terminal.setCassette10Count(2);
        terminal.setCassette5Count(2);
        terminal.setCassette1Count(5);
        terminal.recalculateTotal(); // Total should be 500+200+200+100+100+50+50+20+20+10+10+5+5+5 = 1275
    }

    @Test
    void withdrawCash_Success() {
        Long terminalId = 1L;
        int amount = 250; // take one 200 and one 50

        when(atmTerminalRepository.findById(terminalId)).thenReturn(Optional.of(terminal));

        Cash cash200 = new Cash();
        cash200.setDenomination(200);
        cash200.setQuantity(5);

        Cash cash50 = new Cash();
        cash50.setDenomination(50);
        cash50.setQuantity(5);

        when(cashRepository.findByAtmTerminalIdOrderByDenominationDesc(terminalId))
                .thenReturn(List.of(cash200, cash50));

        Map<Integer, Integer> breakdown = cashService.withdrawCash(terminalId, amount);

        assertNotNull(breakdown);
        assertEquals(1, breakdown.get(200));
        assertEquals(1, breakdown.get(50));

        verify(atmTerminalRepository, times(1)).save(terminal);
        verify(cashRepository, atLeastOnce()).save(any(Cash.class));
    }

    @Test
    void withdrawCash_ZeroOrNegativeAmount_ThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            cashService.withdrawCash(1L, 0);
        });
        assertEquals("Withdrawal amount must be greater than zero.", exception.getMessage());
    }

    @Test
    void withdrawCash_TerminalNotFound_ThrowsException() {
        when(atmTerminalRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            cashService.withdrawCash(99L, 100);
        });
        assertTrue(exception.getMessage().contains("ATM terminal not found"));
    }

    @Test
    void withdrawCash_InsufficientMoney_TriggersNotificationAndThrowsException() {
        terminal.setCassette500Count(0);
        terminal.setCassette200Count(0);
        terminal.setCassette100Count(0);
        terminal.setCassette50Count(0);
        terminal.setCassette20Count(0);
        terminal.setCassette10Count(0);
        terminal.setCassette5Count(0);
        terminal.setCassette1Count(2);
        terminal.recalculateTotal();

        when(atmTerminalRepository.findById(1L)).thenReturn(Optional.of(terminal));

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            cashService.withdrawCash(1L, 50);
        });

        assertTrue(exception.getMessage().contains("Insufficient cash in ATM terminal"));
        verify(routingService, times(1)).routeNotification(any());
    }
}