package com.bank.atm_monolithic.admin;

import com.bank.atm_monolithic.atm.AtmTerminal;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticateAdmin(@Valid @RequestBody AdminLoginDto request) {
        /*
        http://localhost:8081/admin/login

        {
            "username": "admin",
            "password": "1234"
        }
         */
        boolean authenticated = adminService.authenticateAdmin(request.getUsername(), request.getPassword());
        if (authenticated) {
            return ResponseEntity.ok("Admin login successful. Token/Session granted.");
        }

        System.out.println("[ATM_DEBUG]: login admin for admin: " + request.getUsername());

        return ResponseEntity.status(401).body("Invalid admin credentials");
    }

    @PutMapping("/{adminId}/profile")
    public ResponseEntity<String> updateAdminProfile(@PathVariable Long adminId, @RequestParam String username, @RequestParam String email) {
        /*
        http://localhost:8081/admin/1/profile?username=admin&email=admin@atm.com
         */
        adminService.updateAdminProfile(adminId, username, email);

        System.out.println("[ATM_DEBUG]: updated admin profile into username: [" + username + "] and email: [" + email + "]");

        return ResponseEntity.ok("Admin profile updated successfully.");
    }

    @PatchMapping("/accounts/{accountId}/limit")
    public ResponseEntity<String> updateAccountLimit(@PathVariable Long accountId, @RequestParam BigDecimal limit) {
        /*
        http://localhost:8081/admin/accounts/1/limit?limit=999
         */
        adminService.updateAccountLimit(accountId, limit);

        System.out.println("[ATM_DEBUG]: update account limit for id: " + accountId);

        return ResponseEntity.ok("Account daily limit updated successfully into " + limit);
    }

    @PatchMapping("/terminals/{terminalId}/status")
    public ResponseEntity<String> changeTerminalStatus(@PathVariable Long terminalId, @RequestParam AtmTerminal.AtmTerminalStatus status) {
        /*
        http://localhost:8081/admin/terminals/1/status?status=OFFLINE
         */
        adminService.changeTerminalStatus(terminalId, status);

        System.out.println("[ATM_DEBUG]: changed status of the terminal for id: " + terminalId);

        return ResponseEntity.ok("Terminal status changed to: " + status);
    }

    @PostAuthorize("hasRole('ADMIN')")
    @PostMapping("/cards/{cardId}/unblock")
    public ResponseEntity<String> manualUnblockCard(@PathVariable Long cardId) {
        /*
        http://localhost:8081/admin/cards/1/unblock
         */
        adminService.manualUnblockCard(cardId);

        System.out.println("[ATM_DEBUG]: manually unblock card for id: " + cardId);

        return ResponseEntity.ok("Card has been manually unblocked.");
    }

    @PostAuthorize("hasRole('ADMIN')")
    @PostMapping("/cards/{cardId}/block")
    public ResponseEntity<String> manualBlockCard(@PathVariable Long cardId) {
        /*
        http://localhost:8081/admin/cards/1/block
         */
        adminService.manualBlockCard(cardId);

        System.out.println("[ATM_DEBUG]: manually block card for id: " + cardId);

        return ResponseEntity.ok("Card has been manually blocked.");
    }

    @PostAuthorize("hasRole('ADMIN')")
    @PostMapping("/cards/{cardId}/reset-pin")
    public ResponseEntity<String> forceResetCardPin(@PathVariable Long cardId, @RequestParam String temporaryPinHash) {
        /*
        http://localhost:8081/admin/cards/2/reset-pin?temporaryPinHash=myHashedPin123
         */
        adminService.forceResetCardPin(cardId, temporaryPinHash);

        System.out.println("[ATM_DEBUG]: reset card pin for id: " + cardId);

        return ResponseEntity.ok("Card pin has been resetted into " + temporaryPinHash);
    }
}