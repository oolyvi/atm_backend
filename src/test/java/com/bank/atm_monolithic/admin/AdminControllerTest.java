package com.bank.atm_monolithic.admin;

import com.bank.atm_monolithic.atm.AtmTerminal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdminService adminService;

    @Test
    void authenticateAdmin_Success() throws Exception {
        AdminLoginDto loginDto = new AdminLoginDto();
        loginDto.setUsername("admin");
        loginDto.setPassword("1234");

        when(adminService.authenticateAdmin("admin", "1234")).thenReturn(true);

        mockMvc.perform(post("/admin/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Admin login successful. Token/Session granted."));
    }

    @Test
    void authenticateAdmin_Unauthorized() throws Exception {
        AdminLoginDto loginDto = new AdminLoginDto();
        loginDto.setUsername("admin");
        loginDto.setPassword("wrong");

        when(adminService.authenticateAdmin("admin", "wrong")).thenReturn(false);

        mockMvc.perform(post("/admin/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid admin credentials"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateAdminProfile_Success() throws Exception {
        doNothing().when(adminService).updateAdminProfile(1L, "newAdmin", "new@atm.com");

        mockMvc.perform(put("/admin/1/profile")
                        .with(csrf())
                        .param("username", "newAdmin")
                        .param("email", "new@atm.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("Admin profile updated successfully."));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateAccountLimit_Success() throws Exception {
        doNothing().when(adminService).updateAccountLimit(1L, BigDecimal.valueOf(999));

        mockMvc.perform(patch("/admin/accounts/1/limit")
                        .with(csrf())
                        .param("limit", "999"))
                .andExpect(status().isOk())
                .andExpect(content().string("Account daily limit updated successfully into 999"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void changeTerminalStatus_Success() throws Exception {
        doNothing().when(adminService).changeTerminalStatus(1L, AtmTerminal.AtmTerminalStatus.OFFLINE);

        mockMvc.perform(patch("/admin/terminals/1/status")
                        .with(csrf())
                        .param("status", "OFFLINE"))
                .andExpect(status().isOk())
                .andExpect(content().string("Terminal status changed to: OFFLINE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void manualUnblockCard_Success() throws Exception {
        doNothing().when(adminService).manualUnblockCard(1L);

        mockMvc.perform(post("/admin/cards/1/unblock")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Card has been manually unblocked."));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void manualBlockCard_Success() throws Exception {
        doNothing().when(adminService).manualBlockCard(1L);

        mockMvc.perform(post("/admin/cards/1/block")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Card has been manually blocked."));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void forceResetCardPin_Success() throws Exception {
        doNothing().when(adminService).forceResetCardPin(1L, "myHashedPin123");

        mockMvc.perform(post("/admin/cards/1/reset-pin")
                        .with(csrf())
                        .param("temporaryPinHash", "myHashedPin123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Card pin has been resetted into myHashedPin123"));
    }
}