package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CardFilter;
import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.InsufficientFundsException;
import com.example.bankcards.exception.CardIsNotActiveException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.StatusAlreadySetException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardSpecification;
import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Card createCard(CardRequest cardRequest) {
        return cardRepository.save(
                Card.builder()
                        .id(UUID.randomUUID())
                        .encryptedNumber(cardRequest.getCardNumber())
                        .user(cardRequest.getUser())
                        .expirationDate(LocalDate.now().plusYears(3))
                        .status(CardStatus.ACTIVE)
                        .balance(BigDecimal.ZERO)
                        .build());
    }

    @Override
    public Card getCardById(UUID id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Page<Card> getAllCards(CardFilter cardFilter) {
        return cardRepository.findAll(
                CardSpecification.withFilter(cardFilter),
                PageRequest.of(cardFilter.getPageNumber(), cardFilter.getPageSize())
        );
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Card blockCard(UUID cardId) {
        return updateCardStatus(cardId, CardStatus.BLOCKED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Card activateCard(UUID cardId) {
        return updateCardStatus(cardId, CardStatus.ACTIVE);
    }

    public Card updateCardStatus(UUID cardId, CardStatus status) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));
        String cardNumber = card.getEncryptedNumber();

        if (card.getStatus() == status) {
            throw new StatusAlreadySetException(cardNumber, status);
        }

        card.setStatus(status);
        return cardRepository.save(card);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCard(UUID id) {
        if (!cardRepository.existsById(id)) {
            throw new CardNotFoundException(id);
        }
        cardRepository.deleteById(id);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Page<Card> getAllMyCards(UUID currentUserId, CardFilter cardFilter) {
        cardFilter.setUserId(currentUserId);
        return cardRepository.findAll(
                CardSpecification.withFilter(cardFilter),
                PageRequest.of(cardFilter.getPageNumber(), cardFilter.getPageSize())
        );
    }

    @PreAuthorize("hasRole('USER')")
    @Override
    public void requestCardBlock(UUID cardId, UUID currentUserId) {
        Card card = getCardById(cardId);
        if (card.getUser().getId().equals(currentUserId)){
            throw new CardNotFoundException(cardId);
        }
        //TODO: реализация метода
    }

    @Override
    @Transactional
    public void transferBetweenMyCards(TransferRequest request, UUID currentUserId) {
        Card fromCard = getCardByCardNumberAndUserId(request.getFromCardNumber(), currentUserId);
        Card toCard = getCardByCardNumberAndUserId(request.getToCardNumber(), currentUserId);

        if (fromCard.getStatus() != CardStatus.ACTIVE || toCard.getStatus() != CardStatus.ACTIVE) {
            throw new CardIsNotActiveException();
        }

        if (fromCard.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException();
        }

        fromCard.setBalance(fromCard.getBalance().subtract(request.getAmount()));
        toCard.setBalance(toCard.getBalance().add(request.getAmount()));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);
    }

    @Override
    public BigDecimal getMyCardBalance(String cardNumber, UUID currentUserId) {
        Card card = getCardByCardNumberAndUserId(cardNumber, currentUserId);
        return card.getBalance();
    }

    public Card getCardByCardNumberAndUserId(String cardNumber, UUID currentUserId) {
        return cardRepository.findByEncryptedNumberAndUserId(cardNumber, currentUserId)
                .orElseThrow(() -> new CardNotFoundException(cardNumber));
    }
}