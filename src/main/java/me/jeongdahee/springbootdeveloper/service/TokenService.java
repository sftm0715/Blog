package me.jeongdahee.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.jeongdahee.springbootdeveloper.config.jwt.TokenProvider;
import me.jeongdahee.springbootdeveloper.domain.RefreshToken;
import me.jeongdahee.springbootdeveloper.domain.User;
import org.springframework.stereotype.Service;

import java.time.Duration;

/* 전달받은 리프레시토큰으로 유효성검사 후, 리프레시토큰으로 사용자 ID를 찾고, 새로운 엑세스 토큰 생성*/

@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public String createNewAccessToken(String refreshToken) {

        // 토큰 유효성 검사 시, 실패하면 예외 발생
        if(!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected token");
        }

        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        User user = userService.findById(userId);

        return tokenProvider.generateToken(user, Duration.ofHours(2));
    }
}
