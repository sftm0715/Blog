package me.jeongdahee.springbootdeveloper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.jeongdahee.springbootdeveloper.domain.Article;
import me.jeongdahee.springbootdeveloper.domain.User;
import me.jeongdahee.springbootdeveloper.dto.AddArticleRequest;
import me.jeongdahee.springbootdeveloper.dto.UpdateArticleRequest;
import me.jeongdahee.springbootdeveloper.repository.BlogRepository;
import me.jeongdahee.springbootdeveloper.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.NestedTestConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest       // 테스트용 어플리케이션 컨텍스트 생성
@AutoConfigureMockMvc // MockMvc 생성
class BlogApiControllerTest {

    @Autowired // MockMvc : 어플을 서버에 배포하지않고, 테스트용 MVC 환경을 만들어 요청/전송/응답 기능을 지원.
    protected MockMvc mockMvc;

    @Autowired // 직렬화(자바객체→JSON) / 역직렬화(JSON→자바객체)를 위한 클래스.
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    UserRepository userRepository;

    User user;

    //----------------------------------------------

    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
        blogRepository.deleteAll();
    }


    @BeforeEach
    void setSecurityContext() {
        userRepository.deleteAll();
        user = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities()));
    }

    //----------------------------------------------

    @DisplayName ("addArticle : 블로그 글 추가에 성공한다.")
    @Test
    public void addArticle() throws Exception {

        // given : 준비 (저장)
        final String url = "/api/articles";
        final String title = "title";
        final String content = "content";
        final AddArticleRequest userRequest = new AddArticleRequest(title,content);

        // 객체 직렬화 : 자바객체 → JSON
        final String requestBody = objectMapper.writeValueAsString(userRequest);

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("username");

        // when : 실행 (요청전송)
        ResultActions result =
                mockMvc.perform(post(url)                       // 요청을 보낼 엔드포인트 URL
                .contentType(MediaType.APPLICATION_JSON_VALUE)  // 요청컨텐츠 타입 설정
                        .principal(principal)
                .content(requestBody));                        // 요청본문(content)에 데이터 추가 (JSON 타입)


        // then : 검증 (확인)
        result.andExpect(status().isCreated());

        List<Article> articles = blogRepository.findAll();

        assertThat(articles.size()).isEqualTo(1);
        assertThat(articles.get(0).getTitle()).isEqualTo(title);
        assertThat(articles.get(0).getContent()).isEqualTo(content);
    }

    //----------------------------------------------

    @DisplayName("findAllAricles: 블로그 글 목록 조회에 성공한다.")
    @Test
    public void findAllArticles() throws Exception {

        // given
        final String url = "/api/articles";
        Article savedArticle = createDefaultArticle();

        // when
         final ResultActions resultActions =
                 mockMvc.perform(get(url)              // 요청을 보낼 엔트포인트 URL
                .accept(MediaType.APPLICATION_JSON));  // 응답 컨텐츠 타입 설정 (JSON)

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(savedArticle.getContent()))
                .andExpect(jsonPath("$[0].title").value(savedArticle.getTitle()));
    }

    //----------------------------------------------

    @DisplayName("findArticle: 블로그 글 조회에 성공한다.")
    @Test
    public void findArticle() throws Exception {

        // given
        final String url = "/api/articles/{id}";
        Article savedArticle = createDefaultArticle();

        // when
        final ResultActions resultActions =
                mockMvc.perform(get(url, savedArticle.getId()));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(savedArticle.getContent()))
                .andExpect(jsonPath("$.title").value(savedArticle.getTitle()));
    }

    //----------------------------------------------

    @DisplayName("deleteArticle: 블로그 글 삭제에 성공한다.")
    @Test
    public void deleteArticle() throws Exception {

        // given
        final String url = "/api/articles/{id}";
        Article savedArticle = createDefaultArticle();

        // when
        mockMvc.perform(delete(url, savedArticle.getId())) // 요청을 보낼 엔드포인트 URL와 id(URL에 포함될 파라미터값)
                .andExpect(status().isOk());

        // then
        List<Article> articles = blogRepository.findAll();
        assertThat(articles).isEmpty();

    }

    //----------------------------------------------

    @DisplayName("updateArticle: 블로그 글 수정에 성공한다.")
    @Test
    public void updateArticle() throws Exception {

        // given
        final String url = "/api/articles/{id}";
        Article savedArticle = createDefaultArticle();

        final String newTitle = "new title";
        final String newContent = "new content";

        UpdateArticleRequest request = new UpdateArticleRequest(newTitle, newContent);


        // when
        ResultActions result = mockMvc.perform(put(url, savedArticle.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))); // 요청본문(content)에 직렬화된 데이터 추가


        // then
        result.andExpect(status().isOk());

        Article article = blogRepository.findById(savedArticle.getId()).get();

        assertThat(article.getTitle()).isEqualTo(newTitle);
        assertThat(article.getContent()).isEqualTo(newContent);
    }

    private Article createDefaultArticle() {
        return blogRepository.save(Article.builder()
                .title("title")
                .author(user.getUsername())
                .content("content")
                .build());
    }
}