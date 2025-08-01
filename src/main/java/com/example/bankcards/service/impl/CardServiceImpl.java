package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CardFilter;
import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockRequest;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.*;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardBlockRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardSpecification;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.CardMapperFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardBlockRequestRepository cardBlockRequestRepository;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public CardResponse createCard(CardRequest cardRequest) {
        UUID userId = cardRequest.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Card saved = cardRepository.save(CardMapperFactory.toCard(user, cardRequest.getBalance()));
        return CardMapperFactory.toCardResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CardResponse getCardById(UUID id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
        return CardMapperFactory.toCardResponse(card);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Page<CardResponse> getAllCards(CardFilter cardFilter) {
        return cardRepository.findAll(
                CardSpecification.withFilter(cardFilter),
                PageRequest.of(cardFilter.getPageNumber(), cardFilter.getPageSize())
        ).map(CardMapperFactory::toCardResponse);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public CardResponse blockCard(UUID cardId) {
        return updateCardStatus(cardId, CardStatus.BLOCKED);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public CardResponse activateCard(UUID cardId) {
        return updateCardStatus(cardId, CardStatus.ACTIVE);
    }

    private CardResponse updateCardStatus(UUID cardId, CardStatus status) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));
        String cardNumber = card.getNumber();

        if (card.getStatus() == status) {
            throw new StatusAlreadySetException(cardNumber, status);
        }

        card.setStatus(status);
        Card updated = cardRepository.save(card);
        return CardMapperFactory.toCardResponse(updated);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteCard(UUID id) {
        if (!cardRepository.existsById(id)) {
            throw new CardNotFoundException(id);
        }
        cardRepository.deleteById(id);
    }

    @Override
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Transactional(readOnly = true)
    public Page<CardResponse> getAllMyCards(UUID currentUserId, CardFilter cardFilter) {
        cardFilter.setUserId(currentUserId);
        return cardRepository.findAll(
                CardSpecification.withFilter(cardFilter),
                PageRequest.of(cardFilter.getPageNumber(), cardFilter.getPageSize())
        ).map(CardMapperFactory::toCardResponse);
    }

    @Override
    @PreAuthorize("hasRole('USER')")
    @Transactional
    public void requestCardBlock(UUID cardId, UUID currentUserId) {
        Card card = getCardByIdRaw(cardId);

        if (!card.getUser().getId().equals(currentUserId)) {
            throw new CardNotFoundException(cardId);
        }

        boolean requestExists = cardBlockRequestRepository.existsByCardIdAndStatus(
                cardId,
                CardBlockRequest.RequestStatus.ACTIVE
        );
        if (requestExists) {
            throw new DuplicateBlockRequestException();
        }

        cardBlockRequestRepository.save(CardBlockRequest.builder()
                .user(card.getUser())
                .card(card)
                .requestedAt(LocalDateTime.now())
                .status(CardBlockRequest.RequestStatus.ACTIVE)
                .build());
    }



    @Override
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Transactional
    public void transferBetweenMyCards(TransferRequest request, UUID currentUserId) {
        Card fromCard = getCardByIdAndUserId(request.getFromCardId(), currentUserId);
        Card toCard = getCardByIdAndUserId(request.getToCardId(), currentUserId);

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
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Transactional(readOnly = true)
    public BigDecimal getMyCardBalance(UUID cardId, UUID currentUserId) {
        Card card = getCardByIdAndUserId(cardId, currentUserId);
        return card.getBalance();
    }

    private Card getCardByIdAndUserId(UUID cardId, UUID userId) {
        return cardRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> new CardNotFoundException(cardId));
    }


    private Card getCardByIdRaw(UUID id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
    }
}
