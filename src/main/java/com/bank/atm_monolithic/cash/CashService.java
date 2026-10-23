package com.bank.atm_monolithic.cash;

import com.bank.atm_monolithic.atm.AtmTerminal;
import com.bank.atm_monolithic.atm.AtmTerminalRepository;
import com.bank.atm_monolithic.notification.Notification;
import com.bank.atm_monolithic.notification.NotificationRoutingService;
import com.bank.atm_monolithic.notification.NotificationType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CashService {

    private final CashRepository cashRepository;
    private final AtmTerminalRepository atmTerminalRepository;
    private final NotificationRoutingService routingService;

    public CashService(CashRepository cashRepository, AtmTerminalRepository atmTerminalRepository, NotificationRoutingService routingService) {
        this.cashRepository = cashRepository;
        this.atmTerminalRepository = atmTerminalRepository;
        this.routingService = routingService;
    }

    @Transactional
    public Map<Integer, Integer> withdrawCash(Long terminalId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
        }

        AtmTerminal terminal = atmTerminalRepository.findById(terminalId)
                .orElseThrow(() -> new IllegalStateException("ATM terminal not found: " + terminalId));

        if (terminal.getTotalMoney() < amount) {
            sendLowMoneyNotification(terminalId);
            throw new IllegalStateException("Insufficient cash in ATM terminal. Total available: " + terminal.getTotalMoney());
        }

        int remaining = amount;

        Map<Integer, Integer> dispensedBills = new HashMap<>();

        int c500 = terminal.getCassette500Count();
        int c200 = terminal.getCassette200Count();
        int c100 = terminal.getCassette100Count();
        int c50 = terminal.getCassette50Count();
        int c20 = terminal.getCassette20Count();
        int c10 = terminal.getCassette10Count();
        int c5 = terminal.getCassette5Count();
        int c1 = terminal.getCassette1Count();

        if (remaining >= 500 && c500 > 0) {
            int take = Math.min(remaining / 500, c500);
            dispensedBills.put(500, take);
            remaining -= take * 500;
            c500 -= take;
        }

        if (remaining >= 200 && c200 > 0) {
            int take = Math.min(remaining / 200, c200);
            dispensedBills.put(200, take);
            remaining -= take * 200;
            c200 -= take;
        }

        if (remaining >= 100 && c100 > 0) {
            int take = Math.min(remaining / 100, c100);
            dispensedBills.put(100, take);
            remaining -= take * 100;
            c100 -= take;
        }

        if (remaining >= 50 && c50 > 0) {
            int take = Math.min(remaining / 50, c50);
            dispensedBills.put(50, take);
            remaining -= take * 50;
            c50 -= take;
        }

        if (remaining >= 20 && c20 > 0) {
            int take = Math.min(remaining / 20, c20);
            dispensedBills.put(20, take);
            remaining -= take * 20;
            c20 -= take;
        }

        if (remaining >= 10 && c10 > 0) {
            int take = Math.min(remaining / 10, c10);
            dispensedBills.put(10, take);
            remaining -= take * 10;
            c10 -= take;
        }

        if (remaining >= 5 && c5 > 0) {
            int take = Math.min(remaining / 5, c5);
            dispensedBills.put(5, take);
            remaining -= take * 5;
            c5 -= take;
        }

        if (remaining >= 1 && c1 > 0) {
            int take = Math.min(remaining, c1);
            dispensedBills.put(1, take);
            remaining -= take;
            c1 -= take;
        }

        if (remaining > 0) {
            throw new IllegalStateException("Unable to dispense exact amount with available cassette counts. Remaining: " + remaining);
        }

        terminal.setCassette500Count(c500);
        terminal.setCassette200Count(c200);
        terminal.setCassette100Count(c100);
        terminal.setCassette50Count(c50);
        terminal.setCassette20Count(c20);
        terminal.setCassette10Count(c10);
        terminal.setCassette5Count(c5);
        terminal.setCassette1Count(c1);
        terminal.recalculateTotal();

        atmTerminalRepository.save(terminal);

        List<Cash> cassettes = cashRepository.findByAtmTerminalIdOrderByDenominationDesc(terminalId);

        for (Cash cassette : cassettes) {
            if (dispensedBills.containsKey(cassette.getDenomination())) {
                cassette.setQuantity(cassette.getQuantity() - dispensedBills.get(cassette.getDenomination()));
                cashRepository.save(cassette);
            }
        }

        return dispensedBills;
    }

    private void sendLowMoneyNotification(Long terminalId) {
        System.out.println("ALERT: ATM ID " + terminalId + " is low on $");

        Notification notification = new Notification(
                NotificationType.EMAIL,
                "recipient_mail@gmail.com",
                "ATM Low Cash Alert for atm_id: " + terminalId,
                "ATM ID " + terminalId + " is low on $"
        );

        routingService.routeNotification(notification);
    }
}