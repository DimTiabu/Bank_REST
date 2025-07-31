package com.example.bankcards.repository;

import com.example.bankcards.entity.CardBlockRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CardBlockRequestRepository extends JpaRepository<CardBlockRequest, UUID> {

    boolean existsByCardIdAndStatus(UUID cardId, CardBlockRequest.RequestStatus status);

}
