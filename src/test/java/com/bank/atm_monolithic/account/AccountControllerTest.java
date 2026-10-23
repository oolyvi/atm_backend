package com.bank.atm_monolithic.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccountService accountService;

    @Test
    @WithMockUser
    void getAccounts_ShouldReturnListOfAccounts() throws Exception {
        AccountDto dto = new AccountDto(
                1L, "AZ123456789012345678", new BigDecimal("300.00"),
                new BigDecimal("1000.00"), Account.AccountType.CHECKING,
                Account.AccountStatus.ACTIVE, null
        );
        given(accountService.getAccounts()).willReturn(List.of(dto));

        mockMvc.perform(get("/account/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].accountNumber").value("AZ123456789012345678"));
    }

    @Test
    @WithMockUser
    void getAccountById_WhenFound_ShouldReturnAccount() throws Exception {
        AccountDto dto = new AccountDto(
                1L, "AZ123456789012345678", new BigDecimal("300.00"),
                new BigDecimal("1000.00"), Account.AccountType.CHECKING,
                Account.AccountStatus.ACTIVE, null
        );
        given(accountService.getAccountById(1L)).willReturn(Optional.of(dto));

        mockMvc.perform(get("/account/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.accountNumber").value("AZ123456789012345678"));
    }

    @Test
    @WithMockUser
    void getAccountById_WhenNotFound_ShouldReturn404() throws Exception {
        given(accountService.getAccountById(99L)).willReturn(Optional.empty());

        mockMvc.perform(get("/account/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void addAccount_ShouldReturnCreatedStatus() throws Exception {
        Account account = new Account();
        account.setAccountNumber("AZ123456789012345678");

        AccountDto dto = new AccountDto(
                1L, "AZ123456789012345678", new BigDecimal("300.00"),
                new BigDecimal("1000.00"), Account.AccountType.CHECKING,
                Account.AccountStatus.ACTIVE, null
        );

        given(accountService.addAccount(any(Account.class))).willReturn(dto);

        mockMvc.perform(post("/account/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(account)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void replaceAccount_ShouldReturnUpdatedAccount() throws Exception {
        Account account = new Account();
        account.setAccountNumber("AZ123456789012345678");

        AccountDto dto = new AccountDto(
                1L, "AZ123456789012345678", new BigDecimal("300.00"),
                new BigDecimal("1000.00"), Account.AccountType.CHECKING,
                Account.AccountStatus.ACTIVE, null
        );

        given(accountService.replaceAccount(eq(1L), any(Account.class))).willReturn(dto);

        mockMvc.perform(put("/account/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(account)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void deleteAccount_ShouldReturnNoContent() throws Exception {
        doNothing().when(accountService).deleteAccount(1L);

        mockMvc.perform(delete("/account/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void manualUnblockAccount_WithAdminRole_ShouldSucceed() throws Exception {
        doNothing().when(accountService).manualUnblockAccount(1L);

        mockMvc.perform(post("/account/1/unblock").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Account has been manually unblocked."));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void manualBlockAccount_WithAdminRole_ShouldSucceed() throws Exception {
        doNothing().when(accountService).manualBlockAccount(1L);

        mockMvc.perform(post("/account/1/block").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Account has been manually blocked."));
    }
}