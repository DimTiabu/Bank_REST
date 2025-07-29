package com.example.bankcards.repository;

import com.example.bankcards.dto.CardFilter;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public interface CardSpecification {

    String FIELD_BALANCE = "balance";
    String FIELD_EXPIRATION_DATE = "expirationDate";

    static Specification<Card> withFilter(CardFilter cardFilter) {
        Specification<Card> spec = null;

        spec = and(spec, byId(cardFilter.getId()));
        spec = and(spec, byEncryptedNumber(cardFilter.getEncryptedNumber()));
        spec = and(spec, byUserId(cardFilter.getUserId() != null ? cardFilter.getUserId() : null));
        spec = and(spec, byRange(FIELD_EXPIRATION_DATE, cardFilter.getExpirationDateFrom(), cardFilter.getExpirationDateTo()));
        spec = and(spec, byStatus(cardFilter.getStatus()));
        spec = and(spec, byRange(FIELD_BALANCE, cardFilter.getBalanceFrom(), cardFilter.getBalanceTo()));

        return spec;
    }

    private static <T> Specification<T> and(Specification<T> base, Specification<T> addition) {
        return base == null ? addition : base.and(addition);
    }

    static Specification<Card> byId(UUID id) {
        return (root, query, cb) ->
                id == null ? null : cb.equal(root.get("id"), id);
    }

    static Specification<Card> byEncryptedNumber(String encryptedNumber) {
        return (root, query, cb) ->
                encryptedNumber == null ? null : cb.equal(root.get("encryptedNumber"), encryptedNumber);
    }

    static Specification<Card> byUserId(UUID userId) {
        return (root, query, cb) ->
                userId == null ? null : cb.equal(root.get("user").get("id"), userId);
    }

    static Specification<Card> byStatus(CardStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    static <T extends Comparable<? super T>> Specification<Card> byRange(String fieldName, T from, T to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return null;
            if (from != null && to != null)
                return cb.between(root.get(fieldName), from, to);
            if (from != null)
                return cb.greaterThanOrEqualTo(root.get(fieldName), from);
            return cb.lessThanOrEqualTo(root.get(fieldName), to);
        };
    }
}