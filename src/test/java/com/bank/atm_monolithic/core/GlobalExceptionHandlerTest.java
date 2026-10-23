package com.bank.atm_monolithic.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @RestController
    static class TestController {
        @GetMapping("/test-security")
        public void throwSecurity() {
            throw new SecurityException("Access Denied");
        }

        @GetMapping("/test-illegal")
        public void throwIllegal() {
            throw new IllegalArgumentException("Invalid argument provided");
        }

        @GetMapping("/test-generic")
        public void throwGeneric() {
            throw new RuntimeException("Unexpected failure");
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void handleSecurityException_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/test-security"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Access Denied"));
    }

    @Test
    void handleIllegalArgumentException_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/test-illegal"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid argument provided"));
    }

    @Test
    void handleAllExceptions_ShouldReturnInternalServerError() throws Exception {
        mockMvc.perform(get("/test-generic"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("[ATM_ERROR]:An unexpected error occurred: Unexpected failure"));
    }
}