package me.jeongdahee.springbootdeveloper.config.oauth;



import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.jeongdahee.springbootdeveloper.config.jwt.TokenProvider;
import me.jeongdahee.springbootdeveloper.domain.RefreshToken;
import me.jeongdahee.springbootdeveloper.domain.User;
import me.jeongdahee.springbootdeveloper.repository.RefreshTokenRepository;
import me.jeongdahee.springbootdeveloper.service.UserService;
import me.jeongdahee.springbootdeveloper.util.CookieUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

/* 인증 성공 시, 실행할 핸들러*/
// : 스프링 시큐리티의 기본 로직에서는 별도로 authenticationSuccessHandler 를 지정하지 않을시,
// 로그인 성공 이후, SimpleUrlAuthenticationSuccessHandler 를 사용

// 따라서, SimpleUrlAuthenticationSuccessHandler 를 상속받고, onAuthenticationSuccess() 를 오버라이드
// → 일반적인 로직은 동일 사용 + 토큰 관련 작업만 추가

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);
    public static final String REDIRECT_PATH = "/articles";

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserService userService;

    /* onAuthenticationSuccess() */
    // : 일반적인 로직은 동일 사용하고 토큰 관련 작업만 추가
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal(); // principal : 사용자 정보가 저장됨
        User user = userService.findByEmail((String) oAuth2User.getAttributes().get("email"));

        // 1. 리프레시 토큰 생성 → 저장 → 쿠키에 저장
        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);
        saveRefreshToken(user.getId(), refreshToken);
        addRefreshTokenToCookie(request, response, refreshToken);  // 액세스 토큰 만료 시, 재발급 요청하도록 addRefreshTokenToCookie() 호출해, 쿠키에 리프레시 토큰 저장.

        // 2. 액세스 토큰 생성 → 패스에 액세스 토큰 추가
        // : 토큰제공자로 액세스 토큰을 만든 뒤, 쿠키에서 리다이렉트 경로가 담긴 값을 가져와
        // 쿼리 파라미터에 액세스 토큰 추가.
        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
        String targetUrl = getTargetUrl(accessToken);

        // 3. 인증 관련 설정값/쿠키 제거
        // : 인증 프로세스를 진행하며, 세션/쿠키에 임시 저장 인증 관련 데이터 제거
        clearAuthenticationAttributes(request, response);

        // 4. 리타이렉트
        // : 2에서 만든 URL로 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /*생성된 리프레시 토큰을 전달받아 DB에 저장*/
    private void saveRefreshToken(Long userId, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken)) // 리프레시 토큰이 존재하는 경우, 해당 엔티티의 update 메서드를 호출하여 새로운 리프레시 토큰(newRefreshToken)을 업데이트
                .orElse(new RefreshToken(userId, newRefreshToken));
    }

    /*생성된 리프레시 토큰을 쿠키에 저장*/
    private void addRefreshTokenToCookie(HttpServletRequest request,
                                         HttpServletResponse response,
                                         String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }

    /*인증 관련 설정값,쿠키 제거*/
    private void clearAuthenticationAttributes(HttpServletRequest request,
                                               HttpServletResponse response) {
        super.clearAuthenticationAttributes(request); // : 인증 프로세스를 진행하며, 기본 제공 메서드 사용해 세션/쿠키에 임시 저장 인증 관련 데이터 제거
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response); // removeAuthorizationRequestCookies() 추가호출 : OAuth 인증위해 저장된 정보 삭제
    }

    /*액세스 토큰을 패스에 추가*/
    // : 토큰제공자로 액세스 토큰을 만든 뒤, 쿠키에서 리다이렉트 경로가 담긴 값을 가져와
    // 쿼리 파라미터에 액세스 토큰 추가.
    private String getTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                .queryParam("token", token)
                .build()
                .toUriString();
    }
}
