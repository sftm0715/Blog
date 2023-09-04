package me.jeongdahee.springbootdeveloper.config;

import lombok.RequiredArgsConstructor;
import me.jeongdahee.springbootdeveloper.service.UserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
@Configuration
// 실제 인증처리를 하는 시큐리티 설정 파일
public class WebSecurityConfig {

    private final UserDetailService userService;
    // 1. 스프링 시큐리티의 모든 기능 비활성화
    // : 인증, 인가 서비스를 모든 곳에 적용 X
    // - 일반적으로 정적 리소스에만 시큐리티 비활성화 설정 (이미지, HTML 파일)

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                // requestMachers() : 특정 요청과 일치하는 url에 대한 액세스 설정.
                .requestMatchers(toH2Console())            // h2-console(h2데이터 확인에 사용) 하위 url,
                .requestMatchers("/static/**");    // static 하위 경로에 있는 리소스를 대상으로 ignoring() 사용
    }

    // 2. 특정 HTTP 요청에 대한 웹 기반 보안 구성.
    // : 인증/인가, 로그인/로그아웃 관련 설정.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeRequests()  /* 1) 인증/인가 설정 */
                .requestMatchers("/login", "/signup", "/user").permitAll() // permitAll() : [누구나 접근 가능] → "/login", "/signup", "/user" 로 요청이 오면 인증/인가없이 누구나 접근 가능
                .anyRequest().authenticated() // anyRequest() : 위 설정 url 이외 요청
                .and()                        // authenticated() : 인증 후 접근 가능 (인가는 필요없음)
                .formLogin()  /* 2) 폼 기반 로그인 설정 */
                .loginPage("/login") // 로그인 페이지 경로
                .defaultSuccessUrl("/articles") // 로그인 완료시 이동 경로
                .and()
                .logout()  /* 3) 로그아웃 설정 */
                .logoutSuccessUrl("/login") // 로그아웃 완료시 이동 경로
                .invalidateHttpSession(true) // 로그아웃 후, 세션 전체 삭제 여부
                .and()
                .csrf().disable()  /* 4) csrf 비활성화 */
                .build();
    }

        // 3. 인증 관리자 관련 설정.
        // - 사용자 정보 서비스 재정의 시,
        // - 인증 방법 설정 시 사용 (LDAP, JDBC 기반 인증 등)
        @Bean
        public AuthenticationManager authenticationManager(HttpSecurity http,
                                                           BCryptPasswordEncoder bCryptPasswordEncoder,
                                                           UserDetailService userDetailService)
                throws Exception {
            return http.getSharedObject(AuthenticationManagerBuilder.class)
                    .userDetailsService(userService)        // 1) 사용자 서비스 설정 : 사용자 정보를 가져올 서비스를 서비스 설정 (서비스 클래스는 반드시 UserDetailsService를 상속)
                    .passwordEncoder(bCryptPasswordEncoder) // 2) 비밀번호 암호화 설정 : 비밀번호를 암호화하기위한 인코더 설정
                    .and()
                    .build();

        }

        // 4. 패스워드 인코더로 사용할 빈 등록.
        // : 비밀번호 인코더를 빈으로 등록
        @Bean
        public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
        }
}
