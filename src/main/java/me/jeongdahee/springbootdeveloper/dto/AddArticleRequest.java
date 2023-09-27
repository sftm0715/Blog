package me.jeongdahee.springbootdeveloper.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.jeongdahee.springbootdeveloper.domain.Article;

/* 요청을 위한 DTO */
// DTO : 계층끼리 데이터 교환을 위해 사용하는 객체 (단순한 데이터 전달자 역할)

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddArticleRequest {

    private String title;
    private String content;

    public Article toEntity(String author) {
        return Article.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }
}
