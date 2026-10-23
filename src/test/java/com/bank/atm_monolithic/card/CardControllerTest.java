package com.bank.atm_monolithic.card;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CardService cardService;

    @Test
    void getAllCards_ShouldReturnList() throws Exception {
        CardDto cardDto = new CardDto(1L, "****-****-****-3456", Card.CardType.DEBIT, Card.CardStatus.ACTIVE, null, null);
        when(cardService.getAllCards()).thenReturn(List.of(cardDto));

        mockMvc.perform(get("/card/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].maskedCardNumber").value("****-****-****-3456"));
    }

    @Test
    void getCardById_ShouldReturnCard() throws Exception {
        CardDto cardDto = new CardDto(1L, "****-****-****-3456", Card.CardType.DEBIT, Card.CardStatus.ACTIVE, null, null);
        when(cardService.getCardById(1L)).thenReturn(cardDto);

        mockMvc.perform(get("/card/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void addCard_ShouldReturnCreatedCard() throws Exception {
        CardController.CreateCardRequest request = new CardController.CreateCardRequest(
                "1234567890123456", "1234", Card.CardType.DEBIT, 1L, 1L
        );
        CardDto cardDto = new CardDto(1L, "****-****-****-3456", Card.CardType.DEBIT, Card.CardStatus.ACTIVE, null, null);

        when(cardService.addCard(anyString(), anyString(), any(), anyLong(), anyLong())).thenReturn(cardDto);

        mockMvc.perform(post("/card/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void verifyPin_ShouldReturnBoolean() throws Exception {
        CardController.PinVerifyRequest request = new CardController.PinVerifyRequest("1234");
        when(cardService.verifyPin(eq(1L), eq("1234"))).thenReturn(true);

        mockMvc.perform(post("/card/1/verify-pin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void changePin_ShouldReturnSuccessMessage() throws Exception {
        PinChangeDto request = new PinChangeDto();
        request.setOldPin("1234");
        request.setNewPin("5678");

        doNothing().when(cardService).changePin(eq(1L), eq("1234"), eq("5678"));

        mockMvc.perform(post("/card/1/pin-change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Card PIN changed successfully"));
    }
}