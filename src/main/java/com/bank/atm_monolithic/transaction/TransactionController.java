package com.bank.atm_monolithic.transaction;

import com.bank.atm_monolithic.atm.AtmController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/execute")
    public ResponseEntity<Transaction> executeTransaction(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody AtmController.TransactionRequest request) {
        /*
         http://localhost:8081/transaction/execute

         on body:
         {
          "accountId": 1,
          "amount": 150.00,
          "type": "DEBIT"
         }

         on header:
         Idempotency-Key: {{$guid}}  for dynamic keys that one key can handle multiple requests

         */
        Transaction transaction = transactionService.processTransaction(
                idempotencyKey, request.accountId(), request.amount(), request.type()
        );

        System.out.println("[ATM_DEBUG]: " + request.amount() + " is removed from for id: " + request.accountId());

        return ResponseEntity.ok(transaction);
    }

    @GetMapping
    public ResponseEntity<Page<Transaction.TransactionResponseDto>> getTransactionHistory(
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
         /*
         http://localhost:8081/transaction?accountId=1&startDate=2026-01-01T00:00:00&page=0&size=10
         */
        Page<Transaction> page = transactionService.getTransactions(accountId, startDate, endDate, pageable);
        Page<Transaction.TransactionResponseDto> responsePage = page.map(Transaction.TransactionResponseDto::from);

        System.out.println("[ATM_DEBUG]: transaction history got from account_id: [" + accountId + "] starts from: [" + startDate + "] to end date: [" + endDate + "]");

        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction.TransactionResponseDto> getTransactionById(@PathVariable Long id) {
        /*
        http://localhost:8081/transaction/1
         */
        Transaction tx = transactionService.getTransactionById(id);

        System.out.println("[ATM_DEBUG]: get transaction by id: "+ id);

        return ResponseEntity.ok(Transaction.TransactionResponseDto.from(tx));
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<Transaction> refundTransaction(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @PathVariable Long id) {
        /*
        http://localhost:8081/transaction/1/refund

        header:
        add that transaction idempotency key. get idempotency key from all transaction endpoint request
         */
        Transaction refund = transactionService.refundTransaction(idempotencyKey, id);

        System.out.println("[ATM_DEBUG]: refund requested for id: " + id);

        return ResponseEntity.ok(refund);
    }

    @GetMapping("/{id}/receipt")
    public ResponseEntity<TransactionService.TransactionReceiptDto> getTransactionReceipt(@PathVariable Long id) {
         /*
         http://localhost:8081/transaction/1/receipt
         */

        System.out.println("[ATM_DEBUG]: got receipt for id: " + id);

        return ResponseEntity.ok(transactionService.generateReceipt(id));
    }

    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transferFunds(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody Transaction.TransferRequest request) {
        /*
         http://localhost:8081/transaction/transfer

         body:
         {
          "sourceAccountId": 1,
          "destinationAccountId": 2,
          "amount": 50.00
         }

         header:
         Idempotency-Key: {{$guid}}

         */
        Transaction transaction = transactionService.transferMoney(
                idempotencyKey,
                request.sourceAccountId(),
                request.destinationAccountId(),
                request.amount()
        );

        System.out.println("[ATM_DEBUG]: " + request.amount() + " transferred from account " + request.sourceAccountId() + " to " + request.destinationAccountId());

        return ResponseEntity.ok(transaction);
    }
}