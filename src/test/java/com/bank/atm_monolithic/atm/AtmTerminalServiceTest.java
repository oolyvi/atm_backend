package com.bank.atm_monolithic.atm;

import com.bank.atm_monolithic.card.CardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtmTerminalServiceTest {

    @Mock
    private AtmTerminalRepository atmTerminalRepository;

    @InjectMocks
    private AtmTerminalService atmTerminalService;

    @Mock
    private CardService cardService;

    @Test
    @DisplayName("Should save and return terminal when adding terminal")
    void addTerminal_SavesAndReturnsTerminal() {
        AtmTerminal terminal = new AtmTerminal();
        terminal.setTerminalCode("ATM-01");

        AtmTerminal savedTerminal = new AtmTerminal();
        savedTerminal.setId(1L);
        savedTerminal.setTerminalCode("ATM-01");

        when(atmTerminalRepository.save(terminal)).thenReturn(savedTerminal);

        AtmTerminal result = atmTerminalService.addTerminal(terminal);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ATM-01", result.getTerminalCode());
        verify(atmTerminalRepository, times(1)).save(terminal);
    }

    @Test
    @DisplayName("Should return all terminals")
    void getAllTerminals_ReturnsList() {
        AtmTerminal t1 = new AtmTerminal();
        t1.setId(1L);
        AtmTerminal t2 = new AtmTerminal();
        t2.setId(2L);

        when(atmTerminalRepository.findAll()).thenReturn(List.of(t1, t2));

        List<AtmTerminal> result = atmTerminalService.getAllTerminals();

        assertEquals(2, result.size());
        verify(atmTerminalRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return terminal by ID when it exists")
    void getTerminalById_Found_ReturnsOptionalWithTerminal() {
        AtmTerminal terminal = new AtmTerminal();
        terminal.setId(1L);

        when(atmTerminalRepository.findById(1L)).thenReturn(Optional.of(terminal));

        Optional<AtmTerminal> result = atmTerminalService.getTerminalById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(atmTerminalRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty optional when terminal ID does not exist")
    void getTerminalById_NotFound_ReturnsEmptyOptional() {
        when(atmTerminalRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<AtmTerminal> result = atmTerminalService.getTerminalById(99L);

        assertTrue(result.isEmpty());
        verify(atmTerminalRepository, times(1)).findById(99L);
    }

    @Test
    void shouldAuthenticateAtmUserSuccessfully() {
        String cardNumber = "1234-5678-9876-5432";
        String pin = "1234";

        atmTerminalService.authenticateAtmUser(cardNumber, pin);

        verify(cardService).authenticateCard(cardNumber, pin);
    }
}