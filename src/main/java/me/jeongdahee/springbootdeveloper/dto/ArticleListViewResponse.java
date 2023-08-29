package me.jeongdahee.springbootdeveloper.dto;

import lombok.Getter;
import me.jeongdahee.springbootdeveloper.domain.Article;

import java.security.Signature;

@Getter
public class ArticleListViewResponse {
    private final Long id;
    private final String title;
    private final String content;

    public ArticleListViewResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
    }
}
