package me.jeongdahee.springbootdeveloper.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.jeongdahee.springbootdeveloper.domain.Article;

import java.time.LocalDate;
import java.time.LocalDateTime;

/* 글 상세 페이지 */
@NoArgsConstructor
@Getter
public class ArticleViewResponse {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String author;

    public ArticleViewResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.createdAt = article.getCreatedAt();
        this.author = article.getAuthor();
    }
}
