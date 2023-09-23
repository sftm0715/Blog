package me.jeongdahee.springbootdeveloper.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import java.util.Base64;

public class CookieUtil {

    /* 쿠키 추가 */
    // : 요청값(이름, 값, 만료기간)을 바탕으로 HTTP 응답에 쿠키 추가
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    /* 쿠키 삭제 */
    // : 쿠키의 이름을 입력받아 쿠키 삭제
    // 파라미터로 넘어온 키의 쿠키를 빈값으로 바꾸고,
    // 만료시간을 0으로 설정해 쿠키가 재생성되자마자 만료처리 (실제 삭제 방법은 없음)
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }

        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }

    /* 객체 → 쿠키 (직렬화) */
    // : 객체를 직렬화해 쿠키의 값으로 들어갈 값으로 변환
    public static String serialize(Object obj) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(obj));
    }

    /* 쿠키 → 객체 (역직렬화) */
    // : 쿠키를 역직렬화해 객체로 변환
    public static <T> T deserialaize(Cookie cookie, Class<T> cls) {
        return cls.cast(
                SerializationUtils.deserialize(
                        Base64.getUrlDecoder().decode(cookie.getValue())
                )
        );
    }
}
