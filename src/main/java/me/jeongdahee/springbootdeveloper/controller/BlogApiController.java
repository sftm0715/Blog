package me.jeongdahee.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import me.jeongdahee.springbootdeveloper.domain.Article;
import me.jeongdahee.springbootdeveloper.dto.AddArticleRequest;
import me.jeongdahee.springbootdeveloper.dto.ArticleResponse;
import me.jeongdahee.springbootdeveloper.dto.UpdateArticleRequest;
import me.jeongdahee.springbootdeveloper.service.BlogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/* 컨트롤러 : Api 요청 수신 후, 서비스에 전달 & 응답을 클라이언트에게 전달. */

@RequiredArgsConstructor
@RestController
public class BlogApiController {

    private final BlogService blogService;


    // 글 생성 핸들러 메서드
    @PostMapping("/api/articles")
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request) {
        Article savedArticle = blogService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedArticle);
    }

    // 글 목록 조회 핸들러 메서드
    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles() {
        List<ArticleResponse> articles = blogService.findAll()
                .stream()
                .map(ArticleResponse::new) // ArticleResponse 각 정보를 새로운 스트림으로 생성.
                .toList();

        return ResponseEntity.ok()
                .body(articles);
    }

    // 글 조회 핸들러 메서드
    @GetMapping("api/articles/{id}")
    public ResponseEntity<ArticleResponse> findArticle (@PathVariable long id) {
        Article article = blogService.findById(id);

        return ResponseEntity.ok()
                .body(new ArticleResponse(article));
    }

    // 글 삭제 핸들러 메서드
    @DeleteMapping("api/articles/{id}")
    public ResponseEntity<Void> deleteArticle (@PathVariable long id) {
        blogService.delete(id);

        return ResponseEntity.ok()
                .build();
    }

    // 글 수정 핸들러 메서드
    @PutMapping("/api/articles/{id}")
    public ResponseEntity<Article> updateArticle (@PathVariable long id, @RequestBody UpdateArticleRequest request) {
        Article updateArticle = blogService.update(id, request);

        return ResponseEntity.ok()
                .body(updateArticle);
    }
}
