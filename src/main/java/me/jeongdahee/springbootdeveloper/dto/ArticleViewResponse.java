package me.jeongdahee.springbootdeveloper.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.jeongdahee.springbootdeveloper.domain.Article;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class ArticleViewResponse {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime created_at;

    public ArticleViewResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.created_at = article.getCreatedAt();
    }
}
