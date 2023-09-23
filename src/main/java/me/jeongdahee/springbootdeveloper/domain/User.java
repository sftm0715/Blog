package me.jeongdahee.springbootdeveloper.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;


@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
// UserDetails : 사용자 인증정보를 담아두는 인터페이스
public class User implements UserDetails { // UserDetails 를 상속 받아 인증 객체로 사용

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "nickname", unique = true)
    private String nickname;

    @Builder
    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    /* 사용자이름(nickname) 변경 메서드 */
    public User update(String nickname) {
        this.nickname = nickname;

        return this;
    }

    /* 필수 오버라이드 메서드 8개 */

    @Override // 권환 반환 : 사용자가 가진 권한 목록 반환 (여기선 user 밖에 없으므로 user 만 반환)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override // 사용자의 id 반환 (고유값) : 사용자를 식별가능한 고유값 반환.
    public String getUsername() {
        return email;
    }

    @Override // 사용자 패스워드 반환 : 패스워드는 암호화 되어야함.
    public String getPassword() {
        return password;
    }

    @Override // 계정 만료 여부 반환 : 만료O-false / 만료X - true 반환
    public boolean isAccountNonExpired() {
        // 만료됐는지 확인하는 로직
        return true; // true : 만료 X
    }

    @Override // 계정 잠금 여부 반환 : 잠금O-false / 잠금X - true 반환
    public boolean isAccountNonLocked() {
        // 계정 잠금됐는지 확인하는 로직
        return true; // true : 잠금 X
    }

    @Override // 패스워드 만료 여부 반환 : 만료O-false / 만료X - true 반환
    public boolean isCredentialsNonExpired() {
        // 패스워드가 만료됐는지 확인하는 로직
        return true;
    }

    @Override // 계정 사용 가능 여부 반환 : 사용불가능-false / 사용가능 - true 반환
    public boolean isEnabled() {
        // 계정이 사용 가능한지 확인하는 로직
        return true; // true : 사용가능
    }

}
