package com.bank.atm_monolithic.atm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AtmTerminalTest {

    private AtmTerminal terminal;

    @BeforeEach
    void setUp() {
        terminal = new AtmTerminal();
        terminal.setTerminalCode("ATM-BAKU-01");
        terminal.setLocation("Nizami Street 25");
        terminal.setStatus(AtmTerminal.AtmTerminalStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should correctly recalculate total money based on cassette bill counts")
    void recalculateTotal_CalculatesCorrectly() {
        terminal.setCassette1Count(10);
        terminal.setCassette5Count(5);
        terminal.setCassette10Count(2);
        terminal.setCassette20Count(3);
        terminal.setCassette50Count(4);
        terminal.setCassette100Count(1);
        terminal.setCassette200Count(2);
        terminal.setCassette500Count(1);

        LocalDateTime beforeUpdate = terminal.getUpdatedAt();

        terminal.recalculateTotal();

        assertEquals(1315, terminal.getTotalMoney());
        assertNotNull(terminal.getUpdatedAt());
        assertTrue(terminal.getUpdatedAt().isAfter(beforeUpdate) || terminal.getUpdatedAt().isEqual(beforeUpdate));
    }

    @Test
    @DisplayName("Should verify getter and setter field bindings")
    void gettersAndSetters_WorkAsExpected() {
        terminal.setId(1L);
        terminal.setIpAddress("192.168.1.101");
        terminal.setSoftwareVersion("v2.1.1");

        assertEquals(1L, terminal.getId());
        assertEquals("ATM-BAKU-01", terminal.getTerminalCode());
        assertEquals("Nizami Street 25", terminal.getLocation());
        assertEquals(AtmTerminal.AtmTerminalStatus.ACTIVE, terminal.getStatus());
        assertEquals("192.168.1.101", terminal.getIpAddress());
        assertEquals("v2.1.1", terminal.getSoftwareVersion());
        assertNotNull(terminal.getCreatedAt());
    }
}