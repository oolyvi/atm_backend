package com.bank.atm_monolithic.atm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AtmTerminalController.class)
class AtmTerminalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AtmTerminalService atmTerminalService;

    @Test
    @DisplayName("POST /atm/terminal/add - Should create terminal and return 200 OK")
    void addTerminal_Returns200AndTerminal() throws Exception {
        AtmTerminal inputTerminal = new AtmTerminal();
        inputTerminal.setTerminalCode("ATM-BAKU-01");
        inputTerminal.setLocation("Nizami Street 25");
        inputTerminal.setStatus(AtmTerminal.AtmTerminalStatus.ACTIVE);

        AtmTerminal savedTerminal = new AtmTerminal();
        savedTerminal.setId(1L);
        savedTerminal.setTerminalCode("ATM-BAKU-01");
        savedTerminal.setLocation("Nizami Street 25");
        savedTerminal.setStatus(AtmTerminal.AtmTerminalStatus.ACTIVE);

        when(atmTerminalService.addTerminal(any(AtmTerminal.class))).thenReturn(savedTerminal);

        mockMvc.perform(post("/atm/terminal/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputTerminal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.terminalCode").value("ATM-BAKU-01"))
                .andExpect(jsonPath("$.location").value("Nizami Street 25"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("GET /atm/terminal/all - Should return list of terminals")
    void getAllTerminals_Returns200AndList() throws Exception {
        AtmTerminal terminal = new AtmTerminal();
        terminal.setId(1L);
        terminal.setTerminalCode("ATM-BAKU-01");

        when(atmTerminalService.getAllTerminals()).thenReturn(List.of(terminal));

        mockMvc.perform(get("/atm/terminal/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].terminalCode").value("ATM-BAKU-01"));
    }

    @Test
    @DisplayName("GET /atm/terminal/{id} - Should return terminal when ID exists")
    void getTerminalById_Found_Returns200() throws Exception {
        AtmTerminal terminal = new AtmTerminal();
        terminal.setId(1L);
        terminal.setTerminalCode("ATM-BAKU-01");

        when(atmTerminalService.getTerminalById(1L)).thenReturn(Optional.of(terminal));

        mockMvc.perform(get("/atm/terminal/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.terminalCode").value("ATM-BAKU-01"));
    }

    @Test
    @DisplayName("GET /atm/terminal/{id} - Should return 404 Not Found when ID does not exist")
    void getTerminalById_NotFound_Returns404() throws Exception {
        when(atmTerminalService.getTerminalById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/atm/terminal/99"))
                .andExpect(status().isNotFound());
    }
}