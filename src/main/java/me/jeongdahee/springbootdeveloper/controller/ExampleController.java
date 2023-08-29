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
    public String thymeleafExample(Model model) {
        Person person1 = new Person();
        person1.setId(1L);
        person1.setName("홍길동");
        person1.setAge(11);
        person1.setHobbies(List.of("운동", "독서"));

        model.addAttribute("person", person1);
        model.addAttribute("today", LocalDate.now());

        return "example";
    }

    @Data
    class Person {
        private Long id;
        private String name;
        private int age;
        private List<String> hobbies;
    }

}


