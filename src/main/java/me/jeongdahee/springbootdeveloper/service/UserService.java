package me.jeongdahee.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.jeongdahee.springbootdeveloper.domain.User;
import me.jeongdahee.springbootdeveloper.dto.AddUserRequest;
import me.jeongdahee.springbootdeveloper.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    // todo : BCryptPasswordEncoder 삭제 후,
    //  BCryptPasswordEncoder 생성자를 사용해 직접 생성해서 패스워드를 암호화할 수 있게 코드 수정 후,
    //  findByEmail() 메서드 추가.
//    private final BCryptPasswordEncoder bCryptPasswordEncoder;
//
//    /* 회원정보 추가 메서드 */
//    public Long save(AddUserRequest dto) {
//        return userRepository.save(User.builder()
//                .email(dto.getEmail())
//                // 패스워드 암호화 : 비밀번호 저장시 시큐리티 설정 : 패스워드 인코딩용으로 등록한 빈 사용해 암호화
//                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
//                .build()).getId();

    public Long save(AddUserRequest dto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return userRepository.save(User.builder()
                .email(dto.getEmail())
                .password(encoder.encode(dto.getPassword()))
                .build()).getId();
    }

    /* 유저 ID로 유저를 찾는 메서드 */
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    /* 이메일로 유저를 찾는 메서드 */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }
}