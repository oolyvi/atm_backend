package com.bank.atm_monolithic.cash;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CashController.class)
class CashControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CashService cashService;

    @Test
    void withdrawCashEndpoint_Success() throws Exception {
        Long atmId = 1L;
        int amount = 100;
        Map<Integer, Integer> mockBreakdown = Map.of(100, 1);

        when(cashService.withdrawCash(eq(atmId), eq(amount))).thenReturn(mockBreakdown);

        mockMvc.perform(post("/cash/withdraw")
                        .param("atmId", "1")
                        .param("amount", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['100']").value(1));
    }
}