import org.junit.jupiter.api.*;

public class JUnitCycleTest {

    // @BeforeAll : 전체테스트 시작하기 전에 처음 한번만 실행.
    @BeforeAll
    static void beforeAll() {
        System.out.println("@BeforeAll");
    }

    // @BeforeEach : 각 테스트케이스 시작 전에 매번 실행.
    @BeforeEach
    static void beforeEach() {
        System.out.println("@BeforeEach");
    }

    @Test
    public void test1() {
        System.out.println("test1");
    }

    //
    @Test
    public void test2() {
        System.out.println("test2");
    }

    @Test
    public void test3() {
        System.out.println("test3");
    }

    // @AfterAll : 전체테스트 마치고 종료전 한번만 실행
    @AfterAll
    static void afterAll() {
        System.out.println("@AfterAll");
    }

    // @AfterEach : 각 테스트 케이스 종료전 매번실행
    @AfterEach
    static void afterEach() {
        System.out.println("@AfterEach");
    }
}
