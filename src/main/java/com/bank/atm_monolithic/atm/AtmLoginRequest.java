package com.bank.atm_monolithic.atm;

public record AtmLoginRequest(
        String cardNumber,
        String pin
) {}
