package me.jeongdahee.springbootdeveloper.dto;

import lombok.Data;

@Data
public class CreateAccessTokenRequest {
    private String refreshToken;
}
