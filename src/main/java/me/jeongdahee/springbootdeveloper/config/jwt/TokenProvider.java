package me.jeongdahee.springbootdeveloper.config.jwt;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.jeongdahee.springbootdeveloper.domain.User;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

/* 토큰생성 & 토큰 유효성 검사 & 토큰 기반 정보 조회 클래스 */

@RequiredArgsConstructor
@Service
public class TokenProvider {

    private final JwtProperties jwtProperties;


    // 1. JWT 토큰 (생성) 메서드
    private String makeToken(Date expiry, User user) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setSubject(user.getEmail())
                .claim("id",user.getId())
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

    // 2. JWT 토큰 (유효성 검증) 메서드

    // 3. 토큰 기반으로 (인증 정보)를 가져오는 메서드

    // 4. 토큰 기반으로 (유저 ID)를 가져오는 메서드

}
