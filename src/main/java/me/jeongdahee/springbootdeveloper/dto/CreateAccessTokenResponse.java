package me.jeongdahee.springbootdeveloper.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CreateAccessTokenResponse {
    private String accessToken;
}
