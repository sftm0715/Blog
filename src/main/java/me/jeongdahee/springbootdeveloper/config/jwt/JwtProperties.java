package me.jeongdahee.springbootdeveloper.config.jwt;

/*jwt 값들(발급자, 비밀키)을 변수로 저장하는 클래스*/

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties("jwt") // 자바 클래스에 jwt 프로퍼티(속성)값을 가져와서 사용하는 애너테이션
public class JwtProperties {
    private String issuer;
    private String secretKey;
}
