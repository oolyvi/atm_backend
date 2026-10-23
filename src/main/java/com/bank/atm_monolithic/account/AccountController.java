package com.bank.atm_monolithic.account;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<AccountDto>> getAccounts() {
         /*
         http://localhost:8081/account/all
         */
        System.out.println("[ATM_DEBUG]: get all accounts");

        return ResponseEntity.ok(accountService.getAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable Long id) {
        /*
         http://localhost:8081/account/1
         */
        System.out.println("[ATM_DEBUG]: get acount by id: " + id);

        return accountService.getAccountById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ResponseEntity<AccountDto> addAccount(@RequestBody Account account) {
        /*
         http://localhost:8081/account/add

        {
            "accountNumber": "AZ100000000000000005",
            "balance": "300",
            "dailyLimit": "1000",
            "accountType": "CHECKING",
            "status": "ACTIVE",
            "customer": {
                "id": 1
            }
        }
         */
        AccountDto savedAccount = accountService.addAccount(account);

        System.out.println("[ATM_DEBUG]: add account for id: " + savedAccount.id());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedAccount);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDto> replaceAccount(@PathVariable Long id, @RequestBody Account account) {
        /*
         http://localhost:8081/account/1

         {
            "accountNumber": "AZ100000000000000001",
            "balance": "500",
            "dailyLimit": "1000",
            "accountType": "CHECKING",
            "status": "ACTIVE",
            "customer": {
                "id": 1
            }
        }
         */
        AccountDto savedAccount = accountService.replaceAccount(id, account);

        System.out.println("[ATM_DEBUG]: replace account for id: " + savedAccount.id());

        return ResponseEntity.ok(savedAccount);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
         /*
         http://localhost:8081/account/1
         */
        accountService.deleteAccount(id);

        System.out.println("[ATM_DEBUG]: delete account for id: " + id);

        return ResponseEntity.noContent().build();
    }

    @PostAuthorize("hasRole('ADMIN')")
    @PostMapping("/{accountId}/unblock")
    public ResponseEntity<String> manualUnblockAccount(@PathVariable Long accountId) {
        /*
        http://localhost:8081/account/1/unblock
         */
        accountService.manualUnblockAccount(accountId);

        System.out.println("[ATM_DEBUG]: manually unblock account for id: " + accountId);

        return ResponseEntity.ok("Account has been manually unblocked.");
    }

    @PostAuthorize("hasRole('ADMIN')")
    @PostMapping("/{accountId}/block")
    public ResponseEntity<String> manualBlockAccount(@PathVariable Long accountId) {
        /*
        http://localhost:8081/account/1/block
         */
        accountService.manualBlockAccount(accountId);

        System.out.println("[ATM_DEBUG]: manually block account for id: " + accountId);

        return ResponseEntity.ok("Account has been manually blocked.");
    }
}