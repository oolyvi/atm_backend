package com.bank.atm_monolithic.card;

import com.bank.atm_monolithic.account.AccountDto;
import com.bank.atm_monolithic.customer.CustomerDto;

public record CardDto(
        Long id,
        String maskedCardNumber,
        Card.CardType cardType,
        Card.CardStatus status,
        CustomerDto customer,
        AccountDto account
) {
    public static CardDto fromEntity(Card card) {
        if (card == null) return null;
        return new CardDto(
                card.getId(),
                card.getCardNumber(),
                card.getCardType(),
                card.getStatus(),
                CustomerDto.fromEntity(card.getCustomer()),
                AccountDto.fromEntity(card.getAccount())
        );
    }
}