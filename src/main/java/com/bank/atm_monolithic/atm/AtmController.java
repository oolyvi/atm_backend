package com.bank.atm_monolithic.atm;

import com.bank.atm_monolithic.card.CardService;
import com.bank.atm_monolithic.transaction.Transaction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/atm")
public class AtmController {

    /* transaction request */
    public record TransactionRequest(
            Long accountId,
            BigDecimal amount,
            Transaction.TransactionType type) {}

    private final CardService cardService;
    private final AtmTerminalService atmTerminalService;

    public AtmController(CardService cardService, AtmTerminalService atmTerminalService) {
        this.cardService = cardService;
        this.atmTerminalService = atmTerminalService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AtmLoginRequest request) {
        /*
        http://localhost:8081/atm/login

        {
            "cardNumber": "4532678912348812",
            "pin": "1234"
        }
         */
        atmTerminalService.authenticateAtmUser(request.cardNumber(), request.pin());
        return ResponseEntity.ok("[ATM_DEBUG]: Login successful! PIN verified and card is active.");
    }

    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getAtmBalance(@RequestParam String cardNumber) {
         /*
         http://localhost:8081/atm/balance?cardNumber=4532678912348812
         */
        BigDecimal balance = cardService.getBalanceByCardNumber(cardNumber);

        System.out.println("[ATM_DEBUG]: atm balance is " + balance + " for card number: " + cardNumber);

        return ResponseEntity.ok(balance);
    }
}