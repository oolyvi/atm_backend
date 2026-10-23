package com.bank.atm_monolithic.card;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/card")
public class CardController {

    /* create card request */
    public record CreateCardRequest(
            String cardNumber,
            String pin,
            Card.CardType cardType,
            Long customerId,
            Long accountId
    ) {
    }

    /* pin verify request */
    public record PinVerifyRequest(String pin) {
    }

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<CardDto>> getAllCards() {
        /*
         http://localhost:8081/card/all
         */

        System.out.println("[ATM_DEBUG]: get all cards");

        return ResponseEntity.ok(cardService.getAllCards());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardDto> getCardById(@PathVariable Long id) {
        /*
         http://localhost:8081/card/1
         */

        System.out.println("[ATM_DEBUG]: get card for id: " + id);

        return ResponseEntity.ok(cardService.getCardById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<CardDto> addCard(@RequestBody CreateCardRequest request) {
        /*
         http://localhost:8081/card/add
         {
            "cardNumber": "4532678912341221",
            "pin": "6789",
            "cardType": "DEBIT",
            "customerId": "1",
            "accountId": "1"
         }
         */
        CardDto created = cardService.addCard(
                request.cardNumber(),
                request.pin(),
                request.cardType(),
                request.customerId(),
                request.accountId()
        );

        System.out.println("[ATM_DEBUG]: add card for id: " + created.id());

        return ResponseEntity.ok(created);
    }

    @PostMapping("/{id}/verify-pin")
    public ResponseEntity<Boolean> verifyPin(@PathVariable Long id, @RequestBody PinVerifyRequest request) {
        /*
        http://localhost:8081/card/1/verify-pin

         {
            "pin": "1234"
         }
         */
        boolean isValid = cardService.verifyPin(id, request.pin());

        System.out.println("[ATM_DEBUG]: card pin verified for id: " + id);

        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/{cardId}/pin-change")
    public ResponseEntity<String> changePin(@PathVariable Long cardId, @RequestBody PinChangeDto request) {
        /*
        http://localhost:8081/card/1/pin-change

         {
            "oldPin": "1234",
            "newPin": "4321"
         }
         */
        cardService.changePin(cardId, request.getOldPin(), request.getNewPin());

        System.out.println("[ATM_DEBUG]: old card pin: [" + request.getOldPin() + "]  changed into new pin: [" + request.getNewPin() + "]");

        return ResponseEntity.ok("Card PIN changed successfully");
    }
}