package me.jeongdahee.springbootdeveloper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest           // 테스트용 애플리케이션 컨텍스트 생성
@AutoConfigureMockMvc     // MockMvc 생성/자동구성 : 어플리케이션을 서버에 배포하지않고 테스트 MVC 환경을 만들어 요청/전송/응답
class TestControllerTest {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach                     // 테스트 실행 전, 실행하는 메서드
    public void mockMvcSetUp() {    // : 해당 메서드를 통해, MockMcv 설정
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
    }

    @AfterEach                      // 테스트 실행 후, 실행하는 메서드
    public void cleanUp() {         // : member 테이블에 있는 데이터를 모두 삭제.
        memberRepository.deleteAll();
    }


    @DisplayName("getAllMembers: 아티클 조회에 성공한다.")
    @Test
    public void getAllMembers() throws Exception {

        // given : '테스트 실행 준비' 단계
        // → 멤버 저장
        final String url = "/test";
        Member savedMember = memberRepository.save(new Member(1L, "홍길동"));

        // when : '테스트 진행' 단계
        // → 멤버 리스트 조회 API 호출
        final ResultActions result = mockMvc.perform(get(url)
                        .accept(MediaType.APPLICATION_JSON));

        // then : '테스트 결과' 검증
        // → 응답코드 200, 반환값의 id, name 이 저장값과 같은지 확인.
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(savedMember.getId()))
                .andExpect(jsonPath("$[0].name").value(savedMember.getName()));
    }
}