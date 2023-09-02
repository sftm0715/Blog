package me.jeongdahee.springbootdeveloper.controller;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ExampleController {

    @GetMapping("/thymeleaf/example")
    public String thymeleafExample(Model model) {  // Model : 뷰(html)로 데이터를 넘겨주는 객체.
        Person person1 = new Person();
        person1.setId(1L);
        person1.setName("홍길동");
        person1.setAge(11);
        person1.setHobbies(List.of("운동", "독서"));

        model.addAttribute("person", person1);          // addAttribute() : model에 값 저장 - person(키)에 사람 정보 저장
        model.addAttribute("today", LocalDate.now());   //                : model에 값 저장 - today(키)에 날짜 정보 저장

        return "example";   // example(뷰 이름) 반환 : 반환하는 값의 이름을 가진 뷰 파일을 찾아라 → example.html 뷰 조회
    }

    @Data
    class Person {
        private Long id;
        private String name;
        private int age;
        private List<String> hobbies;
    }

}


