package com.example.bankcards.util;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.Card;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CardMapperFactory {

    public CardResponse toCardResponse(Card card) {
        return CardResponse.builder()
                .id(card.getId())
                .maskedNumber(CardMaskUtil.maskCardNumber(card.getEncryptedNumber()))
                .expirationDate(card.getExpirationDate())
                .status(card.getStatus())
                .balance(card.getBalance())
                .build();
    }
}
