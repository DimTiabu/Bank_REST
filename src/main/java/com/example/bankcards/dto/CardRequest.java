package com.example.bankcards.dto;

import com.example.bankcards.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CardRequest {

    private String cardNumber;

    private User user;

}
