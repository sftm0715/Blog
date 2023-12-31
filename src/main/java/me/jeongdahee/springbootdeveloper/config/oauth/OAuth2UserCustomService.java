package me.jeongdahee.springbootdeveloper.config.oauth;

import lombok.RequiredArgsConstructor;
import me.jeongdahee.springbootdeveloper.domain.User;
import me.jeongdahee.springbootdeveloper.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    /* loadUser() */
    // : 리소스 서버에서 보내주는 사용자 정보를 기반으로 사용자 조회, (정보가있다면)이름 업데이트, (없다면)saveOrUpdate() 를 실행해 테이블에 회원데이터 추가.
    // - DefaultOAuth2UserService(부모클래스)에서 제공하는 메서드
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 요청을 바탕으로 유저 정보를 담은 객체 반환
        OAuth2User user = super.loadUser(userRequest);
        saveOrUpdate(user);
        return user; // 사용자 객체 반환 : 식별자, 이름, 이메일, 프사링크 등 정보를 담고있음
    }

    /* 유저가 있으면 → 업데이트
     유저가 없으면 → 유저 생성해서 DB에 저장하는 메서드 */
    private User saveOrUpdate(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        User user = userRepository.findByEmail(email)
                .map(entity -> entity.update(name))
                .orElse(User.builder()
                        .email(email)
                        .nickname(name)
                        .build());
        return userRepository.save(user);

    }
}
