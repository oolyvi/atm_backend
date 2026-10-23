package com.bank.atm_monolithic.atm;

import com.bank.atm_monolithic.card.CardService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AtmTerminalService {

    private final AtmTerminalRepository atmTerminalRepository;
    private final CardService cardService;

    public AtmTerminalService(AtmTerminalRepository atmTerminalRepository, CardService cardService) {
        this.atmTerminalRepository = atmTerminalRepository;
        this.cardService = cardService;
    }

    public AtmTerminal addTerminal(AtmTerminal terminal) {
        return atmTerminalRepository.save(terminal);
    }

    public List<AtmTerminal> getAllTerminals() {
        return atmTerminalRepository.findAll();
    }

    public Optional<AtmTerminal> getTerminalById(Long id) {
        return atmTerminalRepository.findById(id);
    }

    public void authenticateAtmUser(String cardNumber, String pin) {
        cardService.authenticateCard(cardNumber, pin);
    }
}