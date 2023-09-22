package me.jeongdahee.springbootdeveloper.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

import me.jeongdahee.springbootdeveloper.domain.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

/* 토큰생성 & 토큰 유효성 검사 & 토큰 기반 정보 조회 클래스 */

@RequiredArgsConstructor
@Service
public class TokenProvider {

    private final JwtProperties jwtProperties;

    public String generateToken(User user, Duration expiredAt) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }


    // 1. JWT 토큰 (생성) 메서드
    private String makeToken(Date expiry, User user) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // 헤더 typ : JWT
                .setIssuer(jwtProperties.getIssuer()) // 내용 iss : sftm0715@gmail.com (properties 설정값)
                .setIssuedAt(now)                     // 내용 iat : 현재시간
                .setExpiration(expiry)                // 내용 exp : expiry 멤버 변수값
                .setSubject(user.getEmail())          // 내용 sub : 유저 이메일
                .claim("id",user.getId())       // 클레임 id : 유저 id
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey()) // 서명 : 비밀값 & 해시값을 HS256 방식으로 암호화
                .compact();
    }

    // 2. JWT 토큰 (유효성 검증) 메서드
    public boolean validToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey()) // 비밀값으로 복호화
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {   // 복호화 과정에서 에러 발생하면 유효X 토큰
            return false;
        }
    }

    // 3. 토큰 기반으로 (인증 정보)를 가져오는 메서드
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(new org.springframework.
                security.core.userdetails.User(claims.getSubject
                (), "", authorities), token, authorities);
    }



    // 4. 토큰 기반으로 (유저 ID)를 가져오는 메서드
    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }


    // 클레임 가져오는 메서드
    // 클레임이란? : 사용자에 대한 프로퍼티를 담고있는 정보
    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey()) // 프로퍼티즈 파일에 저장한 비밀키로
                .parseClaimsJws(token)                       // 토큰을 복호화한 뒤,
                .getBody();                                   // 클레임을 가져와라.
    }

}
