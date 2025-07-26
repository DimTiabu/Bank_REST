package com.example.bankcards.service;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.entity.Card;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface CardService {
    Card createCard(CardRequest cardRequest);
    Card getCardById(UUID id);
    List<Card> getAllCards();
    Card blockCard(UUID cardId);
    Card activateCard(UUID cardId);
    void deleteCard(UUID id);
}
