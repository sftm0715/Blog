package me.jeongdahee.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.jeongdahee.springbootdeveloper.domain.RefreshToken;
import me.jeongdahee.springbootdeveloper.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    /* 파라미터로 받은 리프레시 토큰으로 리프레시토큰 리포지토리 검색 */
    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected token"));
    }
}
