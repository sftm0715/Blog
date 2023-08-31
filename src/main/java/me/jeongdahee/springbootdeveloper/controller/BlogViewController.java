package me.jeongdahee.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import me.jeongdahee.springbootdeveloper.domain.Article;
import me.jeongdahee.springbootdeveloper.dto.ArticleListViewResponse;
import me.jeongdahee.springbootdeveloper.dto.ArticleViewResponse;
import me.jeongdahee.springbootdeveloper.service.BlogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class BlogViewController {

    private final BlogService blogService;

    // 블로그 글 목록 조회
    @GetMapping("/articles")
    public String getArticles(Model model) {
        List<ArticleListViewResponse> articles =
                blogService.findAll().stream()
                        .map(ArticleListViewResponse::new)
                        .toList();
        model.addAttribute("articles", articles);

        return "articleList";
    }

    // 블로그 글 조회
    @GetMapping("/articles/{id}")
    public String getArticle(@PathVariable Long id, Model model) {
        Article article = blogService.findById(id);
        model.addAttribute("article", new ArticleViewResponse(article));

        return "article";
    }
}
