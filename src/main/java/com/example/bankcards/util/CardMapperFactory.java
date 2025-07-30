package com.example.bankcards.util;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@UtilityClass
public class CardMapperFactory {

    public CardResponse toCardResponse(Card card) {
        return CardResponse.builder()
                .id(card.getId())
                .number(CardMaskUtil.maskCardNumber(card.getNumber()))
                .expirationDate(card.getExpirationDate())
                .status(card.getStatus())
                .balance(card.getBalance())
                .build();
    }

    public Card toCard(CardRequest request, User user) {
        return Card.builder()
                .id(UUID.randomUUID())
                .number(request.getCardNumber())
                .user(user)
                .expirationDate(LocalDate.now().plusYears(3))
                .status(CardStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .build();
    }
}
