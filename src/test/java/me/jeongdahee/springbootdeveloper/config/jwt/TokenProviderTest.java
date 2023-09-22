package me.jeongdahee.springbootdeveloper.config.jwt;


/* TokenProvider 테스트*/

import io.jsonwebtoken.Jwts;
import me.jeongdahee.springbootdeveloper.domain.User;
import me.jeongdahee.springbootdeveloper.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.StatusResultMatchersExtensionsKt.isEqualTo;

@SpringBootTest
class TokenProviderTest {

    @Autowired
    private TokenProvider tokenProvider; // 토큰 생성/유효성검사, 토큰기반 정보조회 클래스
    @Autowired
    private UserRepository userRepository; // email로 사용자 정보를 가져오는 클래스
    @Autowired
    private JwtProperties jwtProperties; // jwt 프로퍼티(속성)값 가져오는 클래스


    /* 1. generateToken() 검증테스트 */
    @DisplayName("generateToken() : 유저정보와 만료기간을 전달해 토큰을 만들 수 있다.")
    @Test
    void generateToken() {
        // given : 테스트 유저 생성(생성)
        User testUser = userRepository.save(User.builder()
                .email("user@email.com")
                .password("test")
                .build());
        // when : 토큰 생성
        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));
        // then : jjwt 라이브러리를 통해 토큰 복호화 / '클레임 id = 유저ID' 인지 확인
        Long userId = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);

        assertThat(userId).isEqualTo(testUser.getId());
    }


    /* 2-1. validToken() 검증테스트 */
    @DisplayName("validToken() : 만료된 토큰인 때, 유효성검증에 실패한다.")
    @Test
    void validToken_invalidToken() {
        // given : jjwt 라이브러리를 통해 만료된 토큰 생성 / 만료시간 : 1970.1.1 - 현재시간(밀리초단위) - 1000
        String token = JwtFactory.builder()
                .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build()
                .createToken(jwtProperties);
        // when : validToken 호출해 유효한지 검사
        boolean result = tokenProvider.validToken(token);
        // then : 반환값 false 확인
        assertThat(result).isFalse();
    }


    /* 2-2. validToken() 검증테스트 */
    @DisplayName("validToken() : 유효한 토큰인 때, 유효성검증에 성공한다.")
    @Test
    void validToken_validToken() {
        // given : jjwt 라이브러리 통해 토큰 생성 / 만료시간 : 현재 - 14일뒤
        String token = JwtFactory.withDefaultValues()
                .createToken(jwtProperties);
        // when : validToken 호출해 유효한지 검사
        boolean result = tokenProvider.validToken(token);
        // then : 반환값 true 확인
        assertThat(result).isTrue();
    }


    /* 3. getAuthentication() 검증테스트 */
    @DisplayName("getAuthentication() : 토큰을 기반으로 인증정보를 가져올 수 있다.")
    @Test
    void getAuthentication() {
        // given : jjwt 라이브러리 통해 토큰 생성 / subject : "user@email.com" 사용
        String userEmail = "user@email.com";
        String token = JwtFactory.builder()
                .subject(userEmail)
                .build()
                .createToken(jwtProperties);
        // when : getAuthentication 호출 → 인증 객체 반환
        Authentication authentication = tokenProvider.getAuthentication(token);
        // then : 인증객체 유저 이름 = user@emamil.com 인지 확인.
        assertThat(((UserDetails)authentication.getPrincipal()).getUsername())
                .isEqualTo(userEmail);
    }


    /* 4. getUserId() 검증테스트 */
    @DisplayName("getUserId() : 토큰으로 유저ID를 가져올 수 있다.")
    @Test
    void getUserId() {
        // given : jjwt 라이브러리 통해 토큰 생성 / 클레임 추가 : 키-"id", 값-1(유저id)
        Long userId = 1L;
        String token = JwtFactory.builder()
                .claims(Map.of("id", userId))
                .build()
                .createToken(jwtProperties);
        // when : getUserId() 호출 → 유저 ID 반환
        Long userIdByToken = tokenProvider.getUserId(token);
        // then : 유저ID = 1(값) 인지 확인.
        assertThat(userIdByToken).isEqualTo(userId);


    }
}