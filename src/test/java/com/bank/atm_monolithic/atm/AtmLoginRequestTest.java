package com.bank.atm_monolithic.atm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AtmLoginRequestTest {

    @Test
    @DisplayName("Should correctly map record components and check equality")
    void testRecordFieldsAndEquals() {
        AtmLoginRequest request1 = new AtmLoginRequest("4000000000000001", "12345");
        AtmLoginRequest request2 = new AtmLoginRequest("4000000000000001", "12345");

        assertEquals("4000000000000001", request1.cardNumber());
        assertEquals("12345", request1.pin());
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }
}