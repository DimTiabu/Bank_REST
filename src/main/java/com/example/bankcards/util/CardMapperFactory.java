package com.example.bankcards.util;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;
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

    public Card toCard(User user) {
        return Card.builder()
                .id(UUID.randomUUID())
                .number(generateRandomCardNumber())
                .user(user)
                .expirationDate(LocalDate.now().plusYears(3))
                .status(CardStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .build();
    }

    private String generateRandomCardNumber() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int group = 1000 + random.nextInt(9000); // генерирует число от 1000 до 9999
            cardNumber.append(group);
            if (i < 3) {
                cardNumber.append(" ");
            }
        }
        return cardNumber.toString();
    }
}
