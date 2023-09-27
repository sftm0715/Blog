package me.jeongdahee.springbootdeveloper.config.oauth;


/* 인증 요청 관련 상태 저장소 */
// : OAuth2 에 필요한 정보를 세션이 아닌 쿠키에 저장해 쓸 수있도록,
// 인증 요청 관련 상태를 저장할 수 있는 저장소 구현.

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.jeongdahee.springbootdeveloper.util.CookieUtil;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.util.WebUtils;

// AuthorizationRequestRepository 인터페이스
// : 권한 인증 흐름에서 클라이언트의 요청을 유지하는 데 사용

// OAuth2AuthorizationRequestBasedOnCookieRepository 클래스
// : 해당 클래스를 구현해 쿠키를 사용해 OAuth의 정보를 저장/조회/삭제하는 로직 작성

public class OAuth2AuthorizationRequestBasedOnCookieRepository implements
        AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    // OAuth 2.0 인증 요청과 관련된 쿠키이름/유효시간
    public final static String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    private final static int COOKIE_EXPIRE_SECONDS = 18000;


    /* 클라이언트의 OAuth2 인증 요청 제거 */
    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
                                                                 HttpServletResponse response) {
        return this.loadAuthorizationRequest(request); // 이전에 저장된 OAuth2 인증 요청을 가져온 후, 해당 정보를 제거하고 반환
    }

    /* 클라이언트의 이전 OAuth2 인증 요청 정보를 쿠키에서 조회 */
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        return CookieUtil.deserialaize(cookie, OAuth2AuthorizationRequest.class); // 쿠키에서 OAuth2 인증 요청 정보를 읽어와서 객체로 역직렬화하고 반환
    }


    /* 클라이언트의 OAuth2 인증 요청 정보를 쿠키에 저장 */
    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {

        // 만약 인증 요청 정보가 null이면 관련된 쿠키 제거
        if (authorizationRequest == null) {
            removeAuthorizationRequestCookies(request, response);
            return;
        }
        // null 아니면, OAuth2 인증 요청 정보를 쿠키에 직렬화하여 저장, 유효 시간 설정.
        CookieUtil.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                CookieUtil.serialize(authorizationRequest), COOKIE_EXPIRE_SECONDS);
    }


    /* 클라이언트의 OAuth2 인증 요청과 관련된 쿠키를 제거 */
    public void removeAuthorizationRequestCookies(HttpServletRequest request,
                                                  HttpServletResponse response) {
        CookieUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        // 클라이언트의 쿠키에서 OAuth2 인증 요청과 관련된 정보를 삭제
    }
}
