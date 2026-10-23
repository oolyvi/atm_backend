package com.bank.atm_monolithic.cash;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cash")
public class CashController {

    private final CashService cashService;

    public CashController(CashService cashService) {
        this.cashService = cashService;
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Map<Integer, Integer>> withdrawCash(
            @RequestParam Long atmId,
            @RequestParam int amount) {
         /*
         http://localhost:8081/cash/withdraw?atmId=1&amount=600
         */
        Map<Integer, Integer> breakdown = cashService.withdrawCash(atmId, amount);

        System.out.println("[ATM_DEBUG]: " + amount + " is withdrawed from atm id: " + atmId);

        return ResponseEntity.ok(breakdown);
    }
}