package com.bank.atm_monolithic.transaction;

import com.bank.atm_monolithic.account.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    @Test
    void executeTransaction_Success() throws Exception {
        String idempotencyKey = "test-guid-key";

        // Request payload matching AtmController.TransactionRequest structure
        String requestJson = """
                {
                  "accountId": 1,
                  "amount": 150.00,
                  "type": "DEBIT"
                }
                """;

        Account mockAccount = new Account();
        mockAccount.setBalance(new BigDecimal("350.00"));

        Transaction mockTransaction = new Transaction(
                idempotencyKey,
                Transaction.TransactionType.DEBIT,
                new BigDecimal("150.00"),
                mockAccount
        );

        when(transactionService.processTransaction(eq(idempotencyKey), eq(1L), any(BigDecimal.valueOf(150.00).getClass()), eq(Transaction.TransactionType.DEBIT)))
                .thenReturn(mockTransaction);

        mockMvc.perform(post("/transaction/execute")
                        .header("Idempotency-Key", idempotencyKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idempotencyKey").value(idempotencyKey))
                .andExpect(jsonPath("$.amount").value(150.00))
                .andExpect(jsonPath("$.type").value("DEBIT"));
    }

    @Test
    void getTransactionReceipt_Success() throws Exception {
        Long transactionId = 1L;
        TransactionService.TransactionReceiptDto receiptDto = new TransactionService.TransactionReceiptDto(
                transactionId,
                "key-xyz",
                1L,
                "DEBIT",
                new BigDecimal("50.00"),
                LocalDateTime.now(),
                new BigDecimal("450.00")
        );

        when(transactionService.generateReceipt(transactionId)).thenReturn(receiptDto);

        mockMvc.perform(get("/transaction/{id}/receipt", transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(transactionId))
                .andExpect(jsonPath("$.type").value("DEBIT"))
                .andExpect(jsonPath("$.amount").value(50.00))
                .andExpect(jsonPath("$.currentAccountBalance").value(450.00));
    }

    @Test
    void getTransactionById_NotFound() throws Exception {
        Long transactionId = 99L;

        when(transactionService.getTransactionById(transactionId))
                .thenThrow(new TransactionNotFoundException(transactionId));

        mockMvc.perform(get("/transaction/{id}", transactionId))
                .andExpect(result -> assertInstanceOf(TransactionNotFoundException.class, result.getResolvedException()));
    }
}