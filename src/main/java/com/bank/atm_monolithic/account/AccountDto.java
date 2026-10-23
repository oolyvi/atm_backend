package com.bank.atm_monolithic.account;

import com.bank.atm_monolithic.customer.CustomerDto;

import java.math.BigDecimal;

public record AccountDto(
        Long id,
        String accountNumber,
        BigDecimal balance,
        BigDecimal dailyLimit,
        Account.AccountType accountType,
        Account.AccountStatus accountStatus,
        CustomerDto customer
) {
    public static AccountDto fromEntity(Account account) {
        if (account == null) return null;
        return new AccountDto(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getDailyLimit(),
                account.getAccountType(),
                account.getStatus(),
                CustomerDto.fromEntity(account.getCustomer())
        );
    }
}
