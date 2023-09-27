package me.jeongdahee.springbootdeveloper.config;

import lombok.RequiredArgsConstructor;
import me.jeongdahee.springbootdeveloper.config.jwt.TokenProvider;
import me.jeongdahee.springbootdeveloper.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import me.jeongdahee.springbootdeveloper.config.oauth.OAuth2SuccessHandler;
import me.jeongdahee.springbootdeveloper.config.oauth.OAuth2UserCustomService;
import me.jeongdahee.springbootdeveloper.repository.RefreshTokenRepository;
import me.jeongdahee.springbootdeveloper.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor // 해당 클래스의 멤버 변수를 사용하여 객체를 초기화
@Configuration // 스프링 컨텍스트에 해당 클래스가 구성(Configuration) 클래스임을 나타내며, 스프링 빈으로 등록
public class WebOAuthSecurityConfig {

    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;


    // '스프링 시큐리티' 기능 비활성화
    @Bean // @Bean 어노테이션이 지정된 메소드들은 Spring 컨테이너에 빈 객체로 등록
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring() // (web): 'web'이라는 매개변수 1개를 받음.  /  web.ignoring() : 웹 객체를 받고 해당 객체의 ignoring 메서드를 호출해 특정 요청(보안검사)을 무시
                .requestMatchers(toH2Console()) //  H2 데이터베이스 콘솔에 대한 요청을 무시
                .requestMatchers("/img/**", "/css/**", "/js/**"); // 정적 자원 (이미지, CSS 파일, JavaScript 파일)에 대한 요청을 무시
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 1. filterChain()
        // : 토큰 방식으로 인증을 하기때문에 기존에 있던 '폼로그인/세션' 비활성화
        http.csrf().disable() // http 객체를 통해 CSRF(Cross-Site Request Forgery) 보안을 비활성화 (CSRF 보안은 웹 애플리케이션의 요청 위조 공격을 방지하기 위한 보안 메커니즘 중 하나)
                .httpBasic().disable()
                .logout().disable();
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 2. addFilterBefore()
        // : 헤더를 확인할 커스텀 필터 추가
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 3. authorizeRequests()
        // : 토큰 재발급 URL은 인증없이 접근 가능 설정 (나머지 API URL은 인증 필요)
        http.authorizeHttpRequests() // http.authorizeHttpRequests(): HTTP 요청에 대한 보안 규칙을 구성하는 메서드 호출
                .requestMatchers("/api/token").permitAll() // permitAll() : 모든 사용자에 대한 접근을 허용 (인증 없이 접근 가능)
                .requestMatchers("/api/**").authenticated() // authenticated() : 인증된 사용자에 대한 접근을 허용 ("/api/**" 패턴에 일치하는 모든 요청은 인증된 사용자만 접근가능)
                .anyRequest().permitAll(); // "/api/token"과 "/api/"로 시작하지 않는 모든 요청은 모든 사용자에 대해 접근이 허용

        http.oauth2Login()// OAuth2 로그인을 구성
                .loginPage("/login") // 로그인 페이지를 /login으로 설정
                .authorizationEndpoint() // OAuth2 인증 요청과 관련된 설정을 구성

                // 4. oauth2Login() 이후 체인 메서드 수정 (1)
                // : Authorization 요청과 관련된 상태 저장
                .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()) // OAuth2 인증 요청과 관련된 상태를 저장하고 검색하기 위한 저장소를 설정
                .and()

                // 5. oauth2Login() 이후 체인 메서드 수정 (2)
                // : 인증 성공시 실행할 핸들러 설정
                .successHandler(oAuth2SuccessHandler())
                .userInfoEndpoint() // 사용자 정보 엔드포인트에 대한 설정
                .userService(oAuth2UserCustomService); // OAuth2 사용자 정보를 처리하는 사용자 정의 서비스를 설정

        http.logout()
                .logoutSuccessUrl("/login"); // 로그아웃 후에 사용자를 /login 페이지로 리다이렉트


        // 6. exceptionHandling() 예외 처리 설정
        // : /api 로 시작하는 url인 경우 인증 실패 시, 401 상태 코드(Unauthorized)를 반환하도록 예외 처리
        http.exceptionHandling()
                .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        new AntPathRequestMatcher("/api/**"));


        return http.build();
    }

    // OAuth2SuccessHandler를 반환하는 빈 메소드로, OAuth2 인증 성공 시 실행할 핸들러를 생성
    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(tokenProvider,
                refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userService);
    }

    // TokenAuthenticationFilter 를 반환하는 빈 메소드로, 사용자 정의 토큰 인증 필터를 생성
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    // OAuth2AuthorizationRequestBasedOnCookieRepository를 반환하는 빈 메소드로, OAuth2 인증 요청과 관련된 정보를 쿠키에 저장하고 검색하는 데 사용되는 저장소를 생성
    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    // BCryptPasswordEncoder를 반환하는 빈 메소드로, BCrypt 암호화 방식을 사용하는 비밀번호 인코더를 생성
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}


























