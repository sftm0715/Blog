package me.jeongdahee.springbootdeveloper.controller;

import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/* 로그인/회원가입 경로 접근 시, 뷰파일 연결 컨트롤러 */
@Controller
public class UserViewController {

    @GetMapping("/login")
    public String login() { // "/login" 경로 접근 시,
        return "login";     // login.html 반환.
    }

    @GetMapping("/signup")
    public String signup() { // "/signup" 경로 접근 시,
        return "signup";      // signup.html 반환.
    }
}
