package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "card_block_requests") // 👈 вот тут ошибка
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardBlockRequest {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private Card card;

    private LocalDateTime requestedAt;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    public enum RequestStatus {
        ACTIVE,
        COMPLETED
    }
}
