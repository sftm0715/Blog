package me.jeongdahee.springbootdeveloper.dto;

import lombok.Getter;
import me.jeongdahee.springbootdeveloper.domain.Article;

/* 응답을 위한 DTO */

@Getter
public class ArticleResponse {

    private final String title;
    private final String content;

    public ArticleResponse(Article article) {
        this.title = article.getTitle();
        this.content = article.getContent();
    }
}
