package com.bank.atm_monolithic.atm;

import com.bank.atm_monolithic.card.CardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AtmController.class)
class AtmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AtmTerminalService atmTerminalService;

    @MockitoBean
    private CardService cardService;

    @Test
    @DisplayName("POST /atm/login - Should return 200 OK with success message when credentials are valid")
    void login_Success_Returns200() throws Exception {
        AtmLoginRequest loginRequest = new AtmLoginRequest("4000000000000001", "12345");
        doNothing().when(atmTerminalService).authenticateAtmUser("4000000000000001", "12345");

        mockMvc.perform(post("/atm/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("[ATM_DEBUG]: Login successful! PIN verified and card is active."));

        verify(atmTerminalService, times(1)).authenticateAtmUser("4000000000000001", "12345");
    }

    @Test
    @DisplayName("GET /atm/balance - Should return balance for specified card number")
    void getAtmBalance_ReturnsBalance() throws Exception {
        String cardNumber = "4000000000000001";
        BigDecimal mockBalance = new BigDecimal("1500.50");

        when(cardService.getBalanceByCardNumber(cardNumber)).thenReturn(mockBalance);

        mockMvc.perform(get("/atm/balance")
                        .param("cardNumber", cardNumber))
                .andExpect(status().isOk())
                .andExpect(content().string("1500.50"));

        verify(cardService, times(1)).getBalanceByCardNumber(cardNumber);
    }
}