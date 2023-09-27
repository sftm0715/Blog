package me.jeongdahee.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.jeongdahee.springbootdeveloper.domain.Article;
import me.jeongdahee.springbootdeveloper.dto.AddArticleRequest;
import me.jeongdahee.springbootdeveloper.dto.ArticleResponse;
import me.jeongdahee.springbootdeveloper.dto.UpdateArticleRequest;
import me.jeongdahee.springbootdeveloper.repository.BlogRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/* 서비스 : 컨트롤러에서 받은 요청에 따라, DB에서 데이터 생성/조회/수정/삭제 */

@RequiredArgsConstructor // final 붙거나 @NotNull 붙은 필드의 생성자 추가
@Service                 // 빈 등록
public class BlogService {

    private final BlogRepository blogRepository;

    // 블로그 글 추가 메서드
    public Article save(AddArticleRequest request, String userName) {
        return blogRepository.save(request.toEntity(userName));
    }

    // 블로그 글목록 조회 메서드
    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    // 블로그 글 조회 메서드
    public Article findById(long id) {
        return blogRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("not found: + id"));
    }

    // 블로그 글 삭제 메서드
    public void delete(long id) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        authorizeArticleAuthor(article);
        blogRepository.deleteById(id);
    }

    // 블로그 글 수정 메서드
    @Transactional // 트랜잭션 메서드
    public Article update(long id, UpdateArticleRequest request) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        authorizeArticleAuthor(article);
        article.update(request.getTitle(), request.getContent());

        return article;
    }

    // 게시글 작성자 일치 여부 확인
    private static void authorizeArticleAuthor(Article article) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!article.getAuthor().equals(userName)) {
            throw new IllegalArgumentException("not authorized");
        }
    }
}


